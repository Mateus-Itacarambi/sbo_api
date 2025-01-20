package ifb.sbo.api.domain.tema;


import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.estudante.*;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class TemaService {
    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    private final EstudanteService estudanteService;

    private final ProfessorService professorService;

    public TemaService(EstudanteService estudanteService, ProfessorService professorService) {
        this.estudanteService = estudanteService;
        this.professorService = professorService;
    }

    public EstudanteListagemDTO criarTemaEstudante(Long estudanteId, TemaCadastroDTO dados) {
        buscarTemaTitulo(dados.titulo());
        var estudante = estudanteService.buscarEstudante(estudanteId);

        estudanteService.verificarTemaEstudante(estudanteId);

        var tema = new Tema(dados);
        tema.setStatus("Reservado");

        tema.getEstudantes().add(estudante);
        estudante.setTema(tema);

        temaRepository.save(tema);
        estudanteRepository.save(estudante);

        return estudanteService.detalharEstudante(estudanteId);
    }

    public ProfessorListagemDTO criarTemaProfessor(Long professorId, TemaCadastroDTO dados) {
        buscarTemaTitulo(dados.titulo());
        var professor = professorService.buscarProfessor(professorId);

        var tema = new Tema(dados);
        tema.setStatus("Disponível");

        tema.setProfessor(professor);

        temaRepository.save(tema);
        professorRepository.save(professor);

        return professorService.detalharProfessor(professorId);
    }

    public Page<TemaListagemDTO> listarTemasPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return temaRepository.findAllByStatusDisponivel(paginacao)
                .map(this::mapearParaDTO);
    }

    @Transactional
    public TemaListagemDTO atualizarTema(Long temaId, Long usuarioId, TemaAtualizaDTO dados) {
        System.out.println("ID TEMA: " + temaId);
        var tema = buscarTema(temaId);

        verificarUsuario(usuarioId, tema);

        buscarTemaTitulo(dados.titulo());

        tema.atualizarTema(dados);
        temaRepository.save(tema);

        return detalharTema(temaId);
    }

    @Transactional
    public void adicionarEstudanteAoTema(Long temaId, Long usuarioId, String matricula) {
        var tema = buscarTema(temaId);

        if (tema.getEstudantes().size() >= 2) {
            throw new ConflitoException("Este tema já possui o número máximo de estudantes (2).");
        }

        verificarUsuario(usuarioId, tema);

        var estudante = estudanteService.buscarEstudanteMatricula(matricula);

        estudanteService.verificarTemaEstudante(estudante.getId());

        estudante.setTema(tema);
        tema.getEstudantes().add(estudante);

        estudanteRepository.save(estudante);
    }

    @Transactional
    public void removerEstudanteDoTema(Long temaId, Long usuarioId, Long estudanteId) {
        Tema tema = buscarTema(temaId);

        verificarUsuario(usuarioId, tema);

        Estudante estudante = estudanteService.buscarEstudante(estudanteId);

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
        verificarUsuario(usuarioId, tema);
        temaRepository.delete(tema);
    }

    private TemaListagemDTO mapearParaDTO(Tema tema) {
        List<EstudanteListagemTemaDTO> estudantesDTO = tema.getEstudantes()
                .stream()
                .map(estudante -> new EstudanteListagemTemaDTO(
                        estudante.getId(),
                        estudante.getNome(),
                        estudante.getDataNascimento(),
                        estudante.getGenero(),
                        estudante.getEmail(),
                        estudante.getMatricula(),
                        estudante.getSemestre(),
                        new CursoDetalhaDTO(
                                estudante.getCurso().getNome()
                        )
                ))
                .toList();

        return new TemaListagemDTO(
                tema.getTitulo(),
                tema.getDescricao(),
                tema.getPalavrasChave(),
                tema.getAreaConhecimento(),
                tema.getStatus(),
                tema.getProfessor() != null ? new ProfessorDetalhaDTO(
                        tema.getProfessor().getId(),
                        tema.getProfessor().getNome()
                ) : null,
                estudantesDTO);
    }

    public Tema buscarTema(Long temaId) {
        return temaRepository.findById(temaId)
                .orElseThrow(() -> new EntityNotFoundException("Tema não encontrado!"));
    }

    public Tema buscarTemaEstudante(Long temaId) {
        return temaRepository.findByIdWithEstudantes(temaId)
                .orElseThrow(() -> new EntityNotFoundException("Tema não encontrado!"));
    }

    public void buscarTemaTitulo(String titulo) {
        if (temaRepository.countByTituloAndAtivoTrue(titulo) != 0) {
            throw new ConflitoException("Esse tema já existe!");
        }
    }

    public Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void verificarUsuario(Long usuarioId, Tema tema) {
        var usuario = buscarUsuario(usuarioId);

        if (usuario instanceof Professor professor) {
            if (tema.getProfessor() == null || !tema.getProfessor().getId().equals(professor.getId())) {
                throw new ConflitoException("Permissão negada: Você não é o criador deste tema.");
            }
        } else if (usuario instanceof Estudante estudante) {
            if (tema.getEstudantes() == null || tema.getEstudantes().stream().noneMatch(e -> e.getId().equals(estudante.getId()))) {
                throw new ConflitoException("Permissão negada: Você não é o criador deste tema.");
            }
        } else {
            throw new EntityNotFoundException("Tipo de usuário inválido.");
        }
    }

    private void buscarEstudante(Tema tema, Long estudanteId) {
        if (!tema.getEstudantes().stream().anyMatch(estudante -> estudante.getId().equals(estudanteId))) {
            throw new IllegalStateException("O tema não pertence ao estudante informado.");
        }
    }
}
