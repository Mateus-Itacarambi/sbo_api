package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

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

    public NotificacaoDTO toDTO(Notificacao notificacao) {
        return new NotificacaoDTO(
                notificacao.getId(),
                notificacao.getMensagem(),
                notificacao.isLida(),
                notificacao.getDataCriacao(),
                notificacao.getTipo(),
                notificacao.getSolicitacao() != null ? notificacao.getSolicitacao().getId() : null,
                notificacao.getSolicitante() != null ? UsuarioSimplesDTO.from(notificacao.getSolicitante()) : null,
                UsuarioSimplesDTO.from(notificacao.getDestinatario())
        );
    }

}

