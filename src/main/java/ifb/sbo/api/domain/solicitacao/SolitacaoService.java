package ifb.sbo.api.domain.solicitacao;
import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.estudante.EstudanteService;
import ifb.sbo.api.domain.notificacao.NotificacaoRepository;
import ifb.sbo.api.domain.notificacao.NotificacaoService;
import ifb.sbo.api.domain.notificacao.TipoNotificacao;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.tema.StatusTema;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;
import ifb.sbo.api.domain.tema.TemaService;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioService;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class SolitacaoService {
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private TemaService temaService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    Clock clock = Clock.systemDefaultZone();

    public SolicitacaoListagemDTO solicitarOrientacao(Usuario usuario, Long professorId) {
        var estudante = estudanteService.buscarEstudante(usuario.getId());
        estudanteService.estudanteTemTema(estudante);

        estudanteTemSolicitacaoAprovada(estudante.getId());

        var tema = estudante.getTema();

        var professor = professorService.buscarProfessor(professorId);

        professorEstaDisponivel(professor);
        existeSolicitacaoTemaProfessor(tema.getId(), professorId);
        existeSolicitacaoTema(tema.getId());

        var solicitacao = new Solicitacao(tema, professor, estudante);
        solicitacao.setTipo(TipoSolicitacao.ORIENTACAO);

        solicitacaoRepository.save(solicitacao);

        notificacaoService.criarNotificacao(estudante, professor, "Nova solicitação de orientação: ", solicitacao, TipoNotificacao.ORIENTACAO.toString());

        SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacao.getId());
        messagingTemplate.convertAndSend(
                "/topic/solicitacoes/" + professor.getId(),
                dto
        );

        return dto;
    }

    @Transactional
    public SolicitacaoListagemDTO rejeitarSolicitacao(Long solicitacaoId, Usuario usuario, SolicitacaoMotivoDTO dados) {
        System.out.println("USUARIO LOGADO: " + usuario.getNome() + " - " + usuario.getId());
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoRejeitar(usuario);
        usuarioService.verificarUsuarioSolicitacao(usuario, solicitacao);

        verificarSolicitacao(solicitacao);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        if (solicitacao.getTipo() == TipoSolicitacao.TEMA) {
            notificacaoService.criarNotificacao(solicitacao.getProfessor(), solicitacao.getEstudante(), "Sua solicitação para o tema “" + tema.getTitulo() + "” foi rejeitada pelo(a) professor(a) ", solicitacao, StatusSolicitacao.REJEITADA.toString());
        }

        solicitacao.setStatus(StatusSolicitacao.REJEITADA);
        solicitacao.setMotivo(dados.motivo());
        solicitacao.setDataAtualizacao(LocalDateTime.now(clock));
        solicitacaoRepository.save(solicitacao);

        tema.getEstudantes().forEach(e -> notificacaoService.criarNotificacao(solicitacao.getProfessor(), e, "Sua solicitação de orientação foi rejeitada pelo(a) professor(a) ", solicitacao, StatusSolicitacao.REJEITADA.toString()));

        SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacaoId);

        tema.getEstudantes().forEach(e -> {
            messagingTemplate.convertAndSend(
                    "/topic/solicitacoes/" + e.getId(),
                    dto
            );
        });

        return dto;
    }

    @Transactional
    public SolicitacaoListagemDTO aprovarSolicitacao(Long solicitacaoId, Usuario usuario) {
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoAprovar(usuario);
        usuarioService.verificarUsuarioSolicitacao(usuario, solicitacao);

        verificarSolicitacao(solicitacao);

        var professor = professorService.buscarProfessor(usuario.getId());

        maximoOrientacoes(professor);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        tema.setStatus(StatusTema.EM_ANDAMENTO);

        if (solicitacao.getTipo() == TipoSolicitacao.TEMA) {
            notificacaoService.criarNotificacao(solicitacao.getProfessor(), solicitacao.getEstudante(), "Sua solicitação para o tema “" + tema.getTitulo() + "” foi aprovada pelo(a) professor(a) ", solicitacao, StatusSolicitacao.APROVADA.toString());
            temaService.adicionarEstudanteAoTema(tema.getId(), professor.getId(), solicitacao.getEstudante().getMatricula());
        } else {
            tema.getEstudantes().forEach(estudante -> notificacaoService.criarNotificacao(solicitacao.getProfessor(), estudante, "Sua solicitação de orientação foi aprovada pelo(a) professor(a) ", solicitacao, StatusSolicitacao.APROVADA.toString()));
        }

        tema.setProfessor(professor);
        solicitacao.setStatus(StatusSolicitacao.APROVADA);
        solicitacao.setDataAtualizacao(LocalDateTime.now(clock));
        solicitacaoRepository.save(solicitacao);

        List<Solicitacao> solicitacoes = solicitacaoRepository.findAllByEstudanteIdAndStatus(solicitacao.getEstudante().getId(), StatusSolicitacao.PENDENTE);

        solicitacoes.stream()
                .filter(s -> !s.getId().equals(solicitacaoId))
                .forEach(s -> {
                    solicitacaoRepository.delete(s);
                    messagingTemplate.convertAndSend(
                            "/topic/solicitacoes/" + s.getProfessor().getId(),
                            Map.of("removerSolicitacaoId", s.getId())
                    );
                    messagingTemplate.convertAndSend(
                            "/topic/solicitacoes/" + s.getEstudante().getId(),
                            Map.of("removerSolicitacaoId", s.getId())
                    );
        });

        maximoOrientacoesAtingida(usuario.getId(), solicitacao);

        SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacaoId);

        tema.getEstudantes().forEach(e -> {
            messagingTemplate.convertAndSend(
                    "/topic/solicitacoes/" + e.getId(),
                    dto
            );
        });

        messagingTemplate.convertAndSend(
                "/topic/solicitacoes/" + solicitacao.getEstudante().getId(),
                dto
        );

        return dto;
    }

    @Transactional
    public SolicitacaoListagemDTO cancelarSolicitacao(Long solicitacaoId, Usuario usuario, SolicitacaoMotivoDTO dados) {
        var solicitacao = buscarSolicitacao(solicitacaoId);
        solicitacaoConcluida(solicitacao);

        if (solicitacao.getStatus().equals(StatusSolicitacao.PENDENTE)) {
            var dto = mapearParaDTO(solicitacao);
            notificacaoService.excluirNotificacao(solicitacao, solicitacao.getProfessor().getId());
            solicitacaoRepository.delete(solicitacao);

            messagingTemplate.convertAndSend(
                        "/topic/solicitacoes/" + solicitacao.getProfessor().getId(),
                        Map.of("removerSolicitacaoId", solicitacao.getId())
                );

            return dto;
        } else {
            usuarioService.verificarUsuarioSolicitacao(usuario, solicitacao);

            verificarProfessorCancelar(usuario, solicitacao, dados.motivo());

            verificarSolicitacao(solicitacao);

            var tema = temaService.buscarTema(solicitacao.getTema().getId());

            tema.setStatus(StatusTema.RESERVADO);
            tema.setProfessor(null);

            if (solicitacao.getTipo() == TipoSolicitacao.TEMA) {
                var professor = professorService.buscarProfessor(solicitacao.getProfessor().getId());

                tema.setStatus(StatusTema.DISPONIVEL);
                tema.setProfessor(professor);

                List<Estudante> estudantes = tema.getEstudantes();
                estudantes.forEach(e -> e.setTema(null));
            }

            tema.setDataAtualizacao(LocalDate.now());

            if (usuario instanceof Estudante estudante) {
                if (solicitacao.getTipo() == TipoSolicitacao.TEMA) {
                    var mensagem = " cancelou a solicitação do tema “" + solicitacao.getTema().getTitulo() + "“.";
                    notificacaoService.criarNotificacao(estudante, solicitacao.getProfessor(), mensagem, solicitacao, TipoSolicitacao.TEMA.toString());

                    tema.getEstudantes().stream()
                            .filter(e -> !e.getId().equals(estudante.getId()))
                            .forEach(outroEstudante ->
                                    notificacaoService.criarNotificacao(
                                            estudante,
                                            outroEstudante,
                                            mensagem,
                                            solicitacao,
                                            TipoSolicitacao.TEMA.toString()
                                    )
                            );
                } else {
                    tema.getEstudantes().stream()
                            .filter(e -> !e.getId().equals(estudante.getId()))
                            .forEach(outroEstudante ->
                                    notificacaoService.criarNotificacao(
                                            estudante,
                                            outroEstudante,
                                            "Solicitação de orientação cancelada por ",
                                            solicitacao,
                                            StatusSolicitacao.CANCELADA.toString()
                                    )
                            );
                }
            } else if (usuario instanceof Professor professor) {
                if (solicitacao.getTipo() == TipoSolicitacao.TEMA) {
                    var mensagem = " cancelou a solicitação do tema “" + solicitacao.getTema().getTitulo() + "“.";
                    tema.getEstudantes()
                            .forEach(estudante ->
                                    notificacaoService.criarNotificacao(
                                            professor,
                                            estudante,
                                            mensagem,
                                            solicitacao,
                                            TipoSolicitacao.TEMA.toString()
                                    )
                            );
                } else {
                    tema.getEstudantes()
                            .forEach(estudante ->
                                    notificacaoService.criarNotificacao(
                                            professor,
                                            estudante,
                                            "Solicitação de orientação cancelada por ",
                                            solicitacao,
                                            StatusSolicitacao.CANCELADA.toString()
                                    )
                            );
                }
            }

            solicitacao.setStatus(StatusSolicitacao.CANCELADA);
            solicitacao.setDataAtualizacao(LocalDateTime.now(clock));
            solicitacaoRepository.save(solicitacao);

            maximoOrientacoesAtingida(usuario.getId(), solicitacao);

            SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacao.getId());

            if (usuario instanceof Professor) {
                tema.getEstudantes().forEach(e -> {
                    messagingTemplate.convertAndSend(
                            "/topic/solicitacoes/" + e.getId(),
                            dto
                    );
                });
            } else {
                messagingTemplate.convertAndSend(
                        "/topic/solicitacoes/" + solicitacao.getProfessor().getId(),
                        dto
                );
            }

            return dto;
        }
    }

    @Transactional
    public SolicitacaoListagemDTO concluirSolicitacao(Long solicitacaoId, Usuario usuario) {
        var solicitacao = buscarSolicitacao(solicitacaoId);

        usuarioService.permissaoAprovar(usuario);
        usuarioService.verificarUsuarioSolicitacao(usuario, solicitacao);

        solicitacaoAprovada(solicitacao);

        var tema = temaService.buscarTema(solicitacao.getTema().getId());

        tema.setStatus(StatusTema.CONCLUIDO);
        tema.setDataAtualizacao(LocalDate.now());

        solicitacao.setStatus(StatusSolicitacao.CONCLUIDA);
        solicitacao.setDataConclusaoOrientacao(LocalDate.now());
        solicitacao.setDataAtualizacao(LocalDateTime.now(clock));
        solicitacaoRepository.save(solicitacao);

        tema.getEstudantes().forEach(estudante -> notificacaoService.criarNotificacao(solicitacao.getProfessor(), estudante, "Sua orientação foi concluída pelo(a) professor(a) ", solicitacao, StatusSolicitacao.CONCLUIDA.toString()));

        maximoOrientacoesAtingida(usuario.getId(), solicitacao);

        SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacaoId);

        tema.getEstudantes().forEach(e -> {
            messagingTemplate.convertAndSend(
                    "/topic/solicitacoes/" + e.getId(),
                    dto
            );
        });

        return dto;
    }

    public SolicitacaoListagemDTO solicitarTema(Long estudanteId, Long temaId) {
        var estudante = estudanteService.buscarEstudante(estudanteId);

        estudanteTemSolicitacaoAprovada(estudanteId);
        estudanteService.verificarTemaEstudante(estudanteId);

        var tema = temaService.buscarTemaDisponivel(temaId);

        var professor = professorService.buscarProfessor(tema.getProfessor().getId());

        professorEstaDisponivel(professor);
        existeSolicitacaoTemaProfessorPendente(temaId, professor.getId());
        existeSolicitacaoTema(tema.getId());

        var solicitacao = new Solicitacao(tema, professor, estudante);
        solicitacao.setTipo(TipoSolicitacao.TEMA);

        solicitacaoRepository.save(solicitacao);

        var mensagem = " tem interesse no tema: " + tema.getTitulo() + ".";

        notificacaoService.criarNotificacao(estudante, professor, mensagem, solicitacao, TipoNotificacao.TEMA.toString());

        SolicitacaoListagemDTO dto = detalharSolicitacao(solicitacao.getId());

        messagingTemplate.convertAndSend(
                "/topic/solicitacoes/" + professor.getId(),
                dto
        );

        return dto;
    }

    public Page<SolicitacaoListagemDTO> buscarSolicitacoesComFiltros(Usuario usuario, FiltroSolicitacao filtro, Pageable pageable) {
        Specification<Solicitacao> spec = Specification.where(SolicitacaoSpecification.comFiltros(filtro));

        if (usuario instanceof Professor professor) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("professor").get("id"), professor.getId()));
        } else if (usuario instanceof Estudante estudante) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estudante").get("id"), estudante.getId()));
        } else {
            throw new AccessDeniedException("Usuário não autorizado.");
        }

        return solicitacaoRepository.findAll(spec, pageable)
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

    public Solicitacao buscarSolicitacaoPorTema(Long temaId) {
        return solicitacaoRepository.findByTemaId(temaId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada!"));
    }

    public void existeSolicitacaoTema(Long temaId) {
        if (solicitacaoRepository.countByTemaAndStatus(temaId, StatusSolicitacao.PENDENTE) >= 5) {
            throw new ConflitoException("Máximo de solicitações alcançada!");
        }
    }

    public void existeSolicitacaoTemaProfessor(Long temaId, Long professorId) {
        if (solicitacaoRepository.countByIdTemaAndIdProfessor(temaId, professorId, StatusSolicitacao.PENDENTE) != 0) {
            throw new ConflitoException("Já existe uma solicitação para esse professor!");
        }
    }

    public void existeSolicitacaoTemaProfessorPendente(Long temaId, Long professorId) {
        if (solicitacaoRepository.countByIdTemaAndIdProfessor(temaId, professorId, StatusSolicitacao.PENDENTE) != 0) {
            throw new ConflitoException("Já existe uma solicitação para esse tema!");
        }
    }

    private void maximoOrientacoes(Professor professor) {
        if (solicitacaoRepository.countByStatusAndProfessor(StatusSolicitacao.APROVADA, professor) >= 6) {
            throw new ConflitoException("Número máximo de orientações alcançada!");
        }
    }

    private void professorEstaDisponivel(Professor professor) {
        if (professor.getDisponibilidade() == Disponibilidade.INDISPONIVEL) {
            throw new ConflitoException("Professor(a) não está mais disponível.");
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
        }
        solicitacao.setMotivo(motivo);
    }

    private SolicitacaoListagemDTO mapearParaDTO(Solicitacao solicitacao) {
        List<UsuarioSimplesDTO> estudantesDTO = solicitacao.getTema().getEstudantes()
                .stream()
                .map(estudante -> new UsuarioSimplesDTO(
                        estudante.getId(),
                        estudante.getNome(),
                        estudante.getRole().toString(),
                        estudante.getMatricula())).toList();

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
                new UsuarioSimplesDTO(
                        solicitacao.getProfessor().getId(),
                        solicitacao.getProfessor().getNome(),
                        solicitacao.getProfessor().getRole().toString(),
                        solicitacao.getProfessor().getIdLattes()
                ),
                solicitacao.getEstudante() != null ?  new UsuarioSimplesDTO(
                solicitacao.getEstudante().getId(),
                solicitacao.getEstudante().getNome(),
                solicitacao.getEstudante().getRole().toString(),
                solicitacao.getEstudante().getMatricula()
                ) : null,
                solicitacao.getMotivo(),
                solicitacao.getTipo()
        );
    }
}
