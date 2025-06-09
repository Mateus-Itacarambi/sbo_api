package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoNotificacaoDTO;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificacaoService {
    Clock clock = Clock.systemDefaultZone();

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    public void criarNotificacao(Usuario solicitante, Usuario destinatario, String mensagem, Solicitacao solicitacao, String tipo) {
        Notificacao notificacao = new Notificacao();
        notificacao.setSolicitante(solicitante);
        notificacao.setDestinatario(destinatario);
        notificacao.setMensagem(mensagem);
        notificacao.setSolicitacao(solicitacao);
        notificacao.setDataCriacao(LocalDateTime.now(clock));
        notificacao.setLida(false);
        notificacao.setTipo(tipo);

        notificacaoRepository.save(notificacao);

        NotificacaoDTO dto = toDTO(notificacao);
        messagingTemplate.convertAndSend("/topic/notificacoes/" + destinatario.getId(), dto);
    }


    public List<NotificacaoDTO> buscarNaoLidasPorUsuario(Usuario usuario) {
        return notificacaoRepository.findByDestinatarioAndLidaFalseOrderByDataCriacaoDesc(usuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void marcarComoLida(Long notificacaoId, Usuario usuario) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));

        if (!notificacao.getDestinatario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não pode marcar esta notificação como lida");
        }

        notificacao.setLida(true);
        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void marcarTodasComoLidas(Usuario usuario) {
        var notificacoes = notificacaoRepository.findByDestinatarioAndLidaFalse(usuario);
        for (Notificacao notificacao : notificacoes) {
            notificacao.setLida(true);
        }
        notificacaoRepository.saveAll(notificacoes);
    }

    @Transactional
    public void excluirNotificacao(Solicitacao solicitacao, Long idUsuario) {
        notificacaoRepository.findBySolicitacao(solicitacao).ifPresent(notificacao -> {
            notificacaoRepository.delete(notificacao);
            messagingTemplate.convertAndSend(
                    "/topic/notificacoes/" + idUsuario,
                    Map.of("removerNotificacaoId", notificacao.getId())
            );
        });
    }

    public NotificacaoDTO toDTO(Notificacao notificacao) {
        return new NotificacaoDTO(
                notificacao.getId(),
                notificacao.getMensagem(),
                notificacao.isLida(),
                notificacao.getDataCriacao(),
                notificacao.getTipo(),
                notificacao.getSolicitacao() != null ? SolicitacaoNotificacaoDTO.from(notificacao.getSolicitacao()) : null,
                notificacao.getSolicitante() != null ? UsuarioSimplesDTO.from(notificacao.getSolicitante()) : null,
                notificacao.getDestinatario() != null ? UsuarioSimplesDTO.from(notificacao.getDestinatario()) : null
        );
    }

}

