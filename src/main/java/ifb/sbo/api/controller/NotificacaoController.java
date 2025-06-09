package ifb.sbo.api.controller;

import ifb.sbo.api.domain.notificacao.NotificacaoDTO;
import ifb.sbo.api.domain.notificacao.NotificacaoRepository;
import ifb.sbo.api.domain.notificacao.NotificacaoService;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<NotificacaoDTO>> listarNaoLidas(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(notificacaoService.buscarNaoLidasPorUsuario(usuario));
    }

    @PutMapping("/{id}/marcar-lida")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        notificacaoService.marcarComoLida(id, usuario);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/marcar-todas-lidas")
    public ResponseEntity<Void> marcarTodasComoLidas(@AuthenticationPrincipal Usuario usuario) {
        notificacaoService.marcarTodasComoLidas(usuario);
        return ResponseEntity.noContent().build();
    }

}

