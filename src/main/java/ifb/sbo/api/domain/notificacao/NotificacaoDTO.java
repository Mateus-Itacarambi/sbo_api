package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.solicitacao.SolicitacaoNotificacaoDTO;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;

import java.time.LocalDateTime;

public record NotificacaoDTO(
        Long id,
        String mensagem,
        boolean lida,
        LocalDateTime dataCriacao,
        String tipo,
        SolicitacaoNotificacaoDTO solicitacao,
        UsuarioSimplesDTO solicitante,
        UsuarioSimplesDTO destinatario
) {}
