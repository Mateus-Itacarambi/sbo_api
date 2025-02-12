package ifb.sbo.api.controller;

import ifb.sbo.api.domain.usuario.UsuarioAtualizaDTO;
import ifb.sbo.api.domain.usuario.UsuarioCadastroDTO;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import ifb.sbo.api.domain.usuario.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid UsuarioCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var admin = usuarioService.cadastrar(dados);
        var uri = uriBuilder.path("/admin/{id}").buildAndExpand(admin.id()).toUri();

        return ResponseEntity.created(uri).body(admin);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid UsuarioAtualizaDTO dados) {
        var admin = usuarioRepository.getReferenceById(dados.id());
        admin.atualizarInformacoes(dados);
        return ResponseEntity.ok(usuarioService.detalharUsuario(admin.getId()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id) {
        var admin = usuarioRepository.getReferenceById(id);
        admin.desativar();
        return ResponseEntity.noContent().build();
    }
}
