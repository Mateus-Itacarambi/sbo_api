package ifb.sbo.api.controller;

import ifb.sbo.api.domain.notificacao.NotificacaoDTO;
import ifb.sbo.api.domain.notificacao.NotificacaoRepository;
import ifb.sbo.api.domain.notificacao.NotificacaoService;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @GetMapping("/nao-lidas/{idUsuario}")
    public ResponseEntity<List<NotificacaoDTO>> listarNaoLidas(@PathVariable Long idUsuario) {
        var usuario = usuarioRepository.getReferenceById(idUsuario);
        return ResponseEntity.ok(notificacaoService.buscarNaoLidasPorUsuario(usuario));
    }

    @PutMapping("/{id}/marcar-lida/{idUsuario}")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id, @PathVariable Long idUsuario) {
        var usuario = usuarioRepository.getReferenceById(idUsuario);
        notificacaoService.marcarComoLida(id, usuario);
        return ResponseEntity.noContent().build();
    }
}

