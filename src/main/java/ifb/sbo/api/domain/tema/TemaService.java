package ifb.sbo.api.domain.tema;


import ifb.sbo.api.domain.estudante.*;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoRepository;
import ifb.sbo.api.domain.solicitacao.StatusSolicitacao;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioService;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
public class TemaService {
    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private UsuarioService usuarioService;

//    public TemaService(EstudanteService estudanteService, ProfessorService professorService, UsuarioService usuarioService) {
//        this.estudanteService = estudanteService;
//        this.professorService = professorService;
//        this.usuarioService = usuarioService;
//    }

    public EstudanteListagemDTO criarTemaEstudante(Long estudanteId, TemaCadastroDTO dados) {
        buscarTemaTitulo(dados.titulo());
        var estudante = estudanteService.buscarEstudante(estudanteId);

        estudanteService.verificarTemaEstudante(estudanteId);

        var tema = new Tema(dados);
        tema.setStatus(StatusTema.RESERVADO);

        tema.getEstudantes().add(estudante);
        temaRepository.save(tema);

        estudante.setTema(tema);
        estudanteRepository.save(estudante);

        return estudanteService.detalharEstudante(estudanteId);
    }

    public ProfessorListagemDTO criarTemaProfessor(Long professorId, TemaCadastroDTO dados) {
        buscarTemaTitulo(dados.titulo());
        var professor = professorService.buscarProfessor(professorId);

        var tema = new Tema(dados);
        tema.setStatus(StatusTema.DISPONIVEL);

        tema.setProfessor(professor);

        temaRepository.save(tema);
        professorRepository.save(professor);

        return professorService.detalharProfessor(professorId);
    }

    public Page<TemaBuscaDTO> listarTemasComFiltros(FiltroTema filtro, Pageable pageable, Usuario usuario) {
        Specification<Tema> spec = TemaSpecification.comFiltros(filtro);

        Long estudanteId;

        if (usuario instanceof Estudante estudante) {
            estudanteId = estudante.getId();
        } else {
            estudanteId = null;
        }

        return temaRepository.findAll(spec, pageable)
                .map(tema -> {
                    var solicitacao = solicitacaoRepository.findByEstudanteIdAndTemaIdAndStatus(estudanteId, tema.getId(), StatusSolicitacao.PENDENTE);

                    return mapearParaListaDTO(tema, solicitacao.isPresent(), solicitacao.map(Solicitacao::getId).orElse(null));
                });
    }

    public Page<TemaListagemDTO> listarTemasPaginadosPorProfessor(Long professorId, @PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return temaRepository.findAllByProfessor_Id(professorId, paginacao)
                .map(this::mapearParaDTO);
    }

    @Transactional
    public TemaListagemDTO atualizarTema(Long temaId, Long usuarioId, TemaAtualizaDTO dados) {
        var tema = buscarTema(temaId);

        if (tema.getStatus().equals(StatusTema.CONCLUIDO)){
            throw new ConflitoException("Este tema já foi concluído.");
        }

        usuarioService.verificarUsuarioTema(usuarioId, tema);


        if (!Objects.equals(tema.getTitulo(), dados.titulo())) {
            buscarTemaTitulo(dados.titulo());
        }

        tema.atualizarTema(dados);
        temaRepository.save(tema);

        return detalharTema(temaId);
    }

    @Transactional
    public void adicionarEstudanteAoTema(Long temaId, Long usuarioId, String matricula) {
        var tema = buscarTema(temaId);

        if (tema.getStatus().equals(StatusTema.CONCLUIDO)) {
            throw new ConflitoException("Este tema já foi concluído.");
        } else if (tema.getStatus().equals(StatusTema.DISPONIVEL)) {
            throw new ConflitoException("Não é possível adicionar estudante ao tema.");
        }

        if (tema.getEstudantes().size() >= 2) {
            throw new ConflitoException("Este tema já possui o número máximo de estudantes (2).");
        }

        usuarioService.verificarUsuarioTema(usuarioId, tema);

        var estudante = estudanteService.buscarEstudanteMatricula(matricula);

        estudanteService.verificarTemaEstudante(estudante.getId());

        estudante.setTema(tema);
        tema.getEstudantes().add(estudante);

        estudanteRepository.save(estudante);
    }

    @Transactional
    public void removerEstudanteDoTema(Long temaId, Long usuarioId, String matricula) {
        Tema tema = buscarTema(temaId);

        if (tema.getStatus().equals(StatusTema.CONCLUIDO)){
            throw new ConflitoException("Este tema já foi concluído.");
        }

        usuarioService.verificarUsuarioTema(usuarioId, tema);

        Estudante estudante = estudanteService.buscarEstudanteMatricula(matricula);

        if (tema.getEstudantes().size() == 1) {
            throw new ConflitoException("Não é possível remover estudante do tema.");
        }

        if (tema.getEstudantes().stream()
                .noneMatch(e -> e.getId().equals(estudante.getId()))) {
            throw new ConflitoException("O estudante não está associado a este tema.");
        }

        estudante.setTema(null);
        estudanteRepository.save(estudante);
    }

    public TemaListagemDTO detalharTema(Long temaId) {
        var tema = buscarTema(temaId);
        return mapearParaDTO(tema);
    }

    public void deletarTema(Long temaId, Long usuarioId) {
        var tema = buscarTema(temaId);
        var usuario = usuarioService.buscarUsuario(usuarioId);

        if (tema.getStatus().equals(StatusTema.CONCLUIDO)){
            throw new ConflitoException("Este tema já foi concluído.");
        }

        usuarioService.verificarUsuarioTema(usuarioId, tema);

        if (usuario instanceof Estudante) {
            if (tema.getProfessor() != null) {
                throw new ConflitoException("Este tema está associado a um professor. Cancele a solicitação associada a este tema.");
            }
        } else if (usuario instanceof Professor) {
            if (!tema.getEstudantes().isEmpty()) {
                throw new ConflitoException("Este tema está associado a um ou mais estudates. Cancele a solicitação associada a este tema.");
            }
        }

        temaRepository.delete(tema);
    }

    private TemaListagemDTO mapearParaDTO(Tema tema) {
        List<EstudanteDetalhaDTO> estudantesDTO = tema.getEstudantes()
                .stream()
                .map(estudante -> new EstudanteDetalhaDTO(
                        estudante.getId(),
                        estudante.getNome()
                ))
                .toList();

        return new TemaListagemDTO(
                tema.getId(),
                tema.getTitulo(),
                tema.getDescricao(),
                tema.getPalavrasChave(),
                tema.getStatus().getDescricao(),
                tema.getProfessor() != null ? new ProfessorDetalhaDTO(
                        tema.getProfessor().getId(),
                        tema.getProfessor().getNome()
                ) : null,
                estudantesDTO);
    }

    private TemaBuscaDTO mapearParaListaDTO(Tema tema, Boolean solicitacaoPendente, Long idSolicitacao) {
        return new TemaBuscaDTO(
                tema.getId(),
                tema.getTitulo(),
                tema.getDescricao(),
                tema.getPalavrasChave(),
                tema.getStatus().getDescricao(),
                tema.getProfessor() != null ? new ProfessorDetalhaDTO(
                        tema.getProfessor().getId(),
                        tema.getProfessor().getNome()
                ) : null,
                solicitacaoPendente,
                idSolicitacao);
    }

    public Tema buscarTema(Long temaId) {
        return temaRepository.findById(temaId)
                .orElseThrow(() -> new EntityNotFoundException("Tema não encontrado!"));
    }

    public Tema buscarTemaDisponivel(Long temaId) {
        return temaRepository.findByIdAndStatus(temaId, StatusTema.DISPONIVEL)
                .orElseThrow(() -> new ConflitoException("Este tema não está disponível!"));
    }

    public void buscarTemaTitulo(String titulo) {
        if (temaRepository.countByTitulo(titulo) != 0) {
            throw new ConflitoException("Esse tema já existe!");
        }
    }
}
