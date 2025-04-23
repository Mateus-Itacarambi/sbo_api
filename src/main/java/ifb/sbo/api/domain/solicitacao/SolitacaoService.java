package ifb.sbo.api.domain.solicitacao;
import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.estudante.EstudanteService;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.tema.StatusTema;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;
import ifb.sbo.api.domain.tema.TemaService;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioService;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SolitacaoService {
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private TemaService temaService;

    @Autowired
    private ProfessorRepository professorRepository;

    private final EstudanteService estudanteService;

    private final ProfessorService professorService;

    private final UsuarioService usuarioService;

    public SolitacaoService(EstudanteService estudanteService, ProfessorService professorService, UsuarioService usuarioService) {
        this.estudanteService = estudanteService;
        this.professorService = professorService;
        this.usuarioService = usuarioService;
    }

    public SolicitacaoListagemDTO solicitarOrientacao(Long estudanteId, Long professorId) {
        var estudante = estudanteService.buscarEstudante(estudanteId);
        estudanteService.estudanteTemTema(estudante);

        estudanteTemSolicitacaoAprovada(estudanteId);

        var tema = estudante.getTema();

        var professor = professorService.buscarProfessor(professorId);

        existeSolicitacaoTemaProfessor(tema.getId(), professorId);
        existeSolicitacaoTema(tema.getId());

        var solicitacao = new Solicitacao();
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDate.now());
        solicitacao.setTema(tema);
        solicitacao.setProfessor(professor);

        solicitacaoRepository.save(solicitacao);

        return detalharSolicitacao(solicitacao.getId());
    }

    @Transactional
    public SolicitacaoListagemDTO rejeitarSolicitacao(Long solicitacaoId, Long usuarioId, SolicitacaoMotivoDTO dados) {
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoRejeitar(usuarioId);
        usuarioService.verificarUsuarioSolicitacao(usuarioId, solicitacao);

        verificarSolicitacao(solicitacao);

        solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        solicitacao.setMotivo(dados.motivo());
        solicitacaoRepository.save(solicitacao);

        return detalharSolicitacao(solicitacaoId);
    }

    @Transactional
    public SolicitacaoListagemDTO aprovarSolicitacao(Long solicitacaoId, Long usuarioId) {
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoAprovar(usuarioId);
        usuarioService.verificarUsuarioSolicitacao(usuarioId, solicitacao);

        verificarSolicitacao(solicitacao);

        var professor = professorService.buscarProfessor(usuarioId);

        maximoOrientacoes(professor);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        if (solicitacao.getEstudante() != null) {
            temaService.adicionarEstudanteAoTema(tema.getId(), professor.getId(), solicitacao.getEstudante().getMatricula());
        }

        tema.setStatus(StatusTema.EM_ANDAMENTO);

        tema.setProfessor(professor);
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        solicitacaoRepository.save(solicitacao);

        List<Solicitacao> solicitacoes = solicitacaoRepository.findAllByTemaAndStatus(solicitacao.getTema(), StatusSolicitacao.PENDENTE);

        solicitacoes.forEach(s -> s.setStatus(StatusSolicitacao.CANCELADA));
        solicitacaoRepository.saveAll(solicitacoes);

        maximoOrientacoesAtingida(usuarioId, solicitacao);

        return detalharSolicitacao(solicitacaoId);
    }

    public void cancelarSolicitacao(Long solicitacaoId, Long usuarioId, SolicitacaoMotivoDTO dados) {
        var solicitacao = buscarSolicitacao(solicitacaoId);
        solicitacaoConcluida(solicitacao);

        var usuario = usuarioService.buscarUsuario(usuarioId);
        usuarioService.verificarUsuarioSolicitacao(usuarioId, solicitacao);

        verificarProfessorCancelar(usuario, solicitacao, dados.motivo());

        verificarSolicitacao(solicitacao);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        tema.setStatus(StatusTema.RESERVADO);
        tema.setProfessor(null);

        if (solicitacao.getEstudante() != null) {
            var professor = professorService.buscarProfessor(solicitacao.getProfessor().getId());

            tema.setStatus(StatusTema.DISPONIVEL);
            tema.setProfessor(professor);

            List<Estudante> estudantes = tema.getEstudantes();
            estudantes.forEach(e -> temaService.removerEstudanteDoTema(tema.getId(), professor.getId(), e.getMatricula()));
        }

        tema.setDataAtualizacao(LocalDate.now());

        solicitacao.setStatus(StatusSolicitacao.CANCELADA);
        solicitacaoRepository.save(solicitacao);

        maximoOrientacoesAtingida(usuarioId, solicitacao);
    }

    @Transactional
    public SolicitacaoListagemDTO concluirSolicitacao(Long solicitacaoId, Long usuarioId) {
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoAprovar(usuarioId);
        usuarioService.verificarUsuarioSolicitacao(usuarioId, solicitacao);

        solicitacaoAprovada(solicitacao);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        tema.setStatus(StatusTema.CONCLUIDO);
        tema.setDataAtualizacao(LocalDate.now());

        solicitacao.setStatus(StatusSolicitacao.CONCLUIDA);
        solicitacao.setDataConclusaoOrientacao(LocalDate.now());
        solicitacaoRepository.save(solicitacao);

        maximoOrientacoesAtingida(usuarioId, solicitacao);

        return detalharSolicitacao(solicitacaoId);
    }

    public SolicitacaoListagemDTO solicitarTema(Long estudanteId, Long temaId) {
        var estudante = estudanteService.buscarEstudante(estudanteId);
        estudanteService.verificarTemaEstudante(estudanteId);

        var tema = temaService.buscarTemaDisponivel(temaId);

        var professor = professorService.buscarProfessor(tema.getProfessor().getId());

        estudanteTemSolicitacaoAprovada(estudanteId);
        existeSolicitacaoTemaProfessorAprovada(tema.getId(), professor.getId());

        var solicitacao = new Solicitacao();
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDate.now());
        solicitacao.setTema(tema);
        solicitacao.setProfessor(professor);
        solicitacao.setEstudante(estudante);

        solicitacaoRepository.save(solicitacao);

        return detalharSolicitacao(solicitacao.getId());
    }

    public Page<SolicitacaoListagemDTO> listarSolicitacoesPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return solicitacaoRepository.findAll(paginacao)
                .map(this::mapearParaDTO);
    }

    public Page<SolicitacaoListagemDTO> listarSolicitacoesPorProfessor(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao, Long professorId) {
        return solicitacaoRepository.findAllByProfessorId(paginacao, professorId)
                .map(this::mapearParaDTO);
    }

    public Page<SolicitacaoListagemDTO> listarSolicitacoesPorAluno(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao, Long estudanteId) {
        estudanteService.buscarEstudante(estudanteId);
        return solicitacaoRepository.findAllByTemaEstudanteId(paginacao, estudanteId)
                .map(this::mapearParaDTO);
    }

    public SolicitacaoListagemDTO detalharSolicitacao(Long solicitacaoId) {
        var solicitacao = buscarSolicitacao(solicitacaoId);
        return mapearParaDTO(solicitacao);
    }

    public Solicitacao buscarSolicitacao(Long solicitacaoId) {
        return solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada!"));
    }

    public void existeSolicitacaoTema(Long temaId) {
        if (solicitacaoRepository.countByTemaAndStatus(temaId, StatusSolicitacao.PENDENTE) >= 3) {
            throw new ConflitoException("Máximo de solicitações alcançada!");
        }
    }

    public void existeSolicitacaoTemaProfessor(Long temaId, Long professorId) {
        if (solicitacaoRepository.countByIdTemaAndIdProfessor(temaId, professorId, StatusSolicitacao.PENDENTE) != 0) {
            throw new ConflitoException("Já existe uma solicitação para esse professor!");
        }
    }

    public void existeSolicitacaoTemaProfessorAprovada(Long temaId, Long professorId) {
        if (solicitacaoRepository.countByIdTemaAndIdProfessor(temaId, professorId, StatusSolicitacao.APROVADA) != 0) {
            throw new ConflitoException("Já existe uma solicitação aprovada para esse tema!");
        }
    }

    private void maximoOrientacoes(Professor professor) {
        if (solicitacaoRepository.countByStatusAndProfessor(StatusSolicitacao.APROVADA, professor) >= 6) {
            throw new ConflitoException("Número máximo de orientações alcançada!");
        }
    }

    private void maximoOrientacoesAtingida(Long usuarioId, Solicitacao solicitacao) {
        var usuario = usuarioService.buscarUsuario(usuarioId);

        if(usuario instanceof Professor professor) {
            if (solicitacaoRepository.countByStatusAndProfessor(StatusSolicitacao.APROVADA, professor) >= 6) {
                professor.setDisponibilidade(Disponibilidade.INDISPONIVEL);
            } else {
                professor.setDisponibilidade(Disponibilidade.DISPONIVEL);
            }
            professorRepository.save(professor);
        } else {
            var professor = solicitacao.getProfessor();

            if (solicitacaoRepository.countByStatusAndProfessor(StatusSolicitacao.APROVADA, professor) >= 6) {
                professor.setDisponibilidade(Disponibilidade.INDISPONIVEL);
            } else {
                professor.setDisponibilidade(Disponibilidade.DISPONIVEL);
            }
            professorRepository.save(professor);
        }
    }

    private void verificarSolicitacao (Solicitacao solicitacao) {
        if (solicitacao.getStatus().equals(StatusSolicitacao.REJEITADA)) {
            throw new ConflitoException("Esta solicitação já foi rejeitada!");
        } else if (solicitacao.getStatus().equals(StatusSolicitacao.CANCELADA)) {
            throw new ConflitoException("Esta solicitação já foi cancelada!");
        }
    }

    private void estudanteTemSolicitacaoAprovada (Long estudanteId) {
        if (solicitacaoRepository.countByStatusAndEstudante(StatusSolicitacao.APROVADA, estudanteId) > 0) {
            throw new ConflitoException("Você já possui uma solicitação aprovada");
        } else if (solicitacaoRepository.countByStatusAndEstudante(StatusSolicitacao.CONCLUIDA, estudanteId) > 0) {
            throw new ConflitoException("Você já possui uma solicitação concluída");
        }
    }

    private void solicitacaoAprovada(Solicitacao solicitacao) {
        if (solicitacao.getStatus() == StatusSolicitacao.CONCLUIDA) {
            throw new ConflitoException("Esta solicitação já foi concluída!");
        } else if (!solicitacao.getStatus().equals(StatusSolicitacao.APROVADA)) {
            throw new ConflitoException("Esta solicitação precisa ser aprovada!");
        }
    }

    private void solicitacaoConcluida(Solicitacao solicitacao) {
        if (solicitacao.getStatus().equals(StatusSolicitacao.CONCLUIDA)) {
            throw new ConflitoException("Não é possível cancelar uma solicitação concluída!");
        }
    }

    private void verificarProfessorCancelar(Usuario usuario, Solicitacao solicitacao, String motivo) {
        if (usuario instanceof Professor) {
            if (solicitacao.getStatus().equals(StatusSolicitacao.PENDENTE)) {
                throw new ConflitoException("Não é possível cancelar esta solicitação!");
            }
            solicitacao.setMotivo(motivo);
        }
    }

    private SolicitacaoListagemDTO mapearParaDTO(Solicitacao solicitacao) {
        List<EstudanteDetalhaDTO> estudantesDTO = solicitacao.getTema().getEstudantes()
                .stream()
                .map(estudante -> new EstudanteDetalhaDTO(
                        estudante.getId(),
                        estudante.getNome())).toList();

        return new SolicitacaoListagemDTO(
                solicitacao.getId(),
                solicitacao.getStatus(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataConclusaoOrientacao(),
                new TemaDetalhaSolicitacaoDTO(
                        solicitacao.getTema().getId(),
                        solicitacao.getTema().getTitulo(),
                        solicitacao.getTema().getDescricao(),
                        solicitacao.getTema().getPalavrasChave(),
                        estudantesDTO
                ),
                new ProfessorDetalhaDTO(
                        solicitacao.getProfessor().getId(),
                        solicitacao.getProfessor().getNome()
                ),
                solicitacao.getEstudante() != null ?  new EstudanteDetalhaDTO(
                        solicitacao.getEstudante().getId(),
                        solicitacao.getEstudante().getNome()
                ) : null,
                solicitacao.getMotivo()
        );
    }
}
