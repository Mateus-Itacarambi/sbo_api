package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoNotificacaoDTO;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacaoService {
    Clock clock = Clock.systemDefaultZone();

    @Autowired
    private NotificacaoRepository notificacaoRepository;

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

