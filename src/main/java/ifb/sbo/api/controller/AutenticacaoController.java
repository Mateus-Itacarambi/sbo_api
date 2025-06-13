package ifb.sbo.api.controller;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.estudante.EstudanteService;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.professor.ProfessorService;
import ifb.sbo.api.domain.usuario.*;
import ifb.sbo.api.infra.exception.ConflitoException;
import ifb.sbo.api.infra.security.DadosTokenJWT;
import ifb.sbo.api.infra.security.TokenService;
import ifb.sbo.api.infra.service.EmailService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {
    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid DadosAutenticacao dados, HttpServletResponse response) {
        var token = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(token);

        var usuario = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuario);

        ResponseCookie cookie = ResponseCookie.from("token", tokenJWT)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(2 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().body(usuarioService.detalharUsuario(usuario.getId()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logout realizado com sucesso.");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@CookieValue(name = "token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente.");
        }

        try {
            String email = tokenService.getSubject(token);

            Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email);

            if (usuario instanceof Estudante estudante) {
                return ResponseEntity.ok(estudanteService.detalharEstudante(estudante.getId()));
            } else if (usuario instanceof Professor professor) {
                return ResponseEntity.ok(professorService.detalharProfessor(professor.getId()));
            } else if (usuario.getRole() == TipoUsuario.ADMINISTRADOR) {
                return ResponseEntity.ok(usuarioService.detalharUsuario(usuario.getId()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }
    }

    @GetMapping("/resumo")
    public ResponseEntity<?> getUserResumo(@CookieValue(name = "token", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token ausente.");
        }

        try {
            String email = tokenService.getSubject(token);

            Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email);

            if (usuario instanceof Estudante estudante) {
                return ResponseEntity.ok(estudanteService.resumoEstudante(estudante.getId()));
            } else if (usuario instanceof Professor professor) {
                return ResponseEntity.ok(professorService.resumoProfessor(professor.getId()));
            } else if (usuario.getRole() == TipoUsuario.ADMINISTRADOR) {
                return ResponseEntity.ok(usuarioService.detalharUsuario(usuario.getId()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.");
        }
    }

    @PostMapping("/esqueceu-senha")
    public ResponseEntity<?> solicitarRedefinicao(@RequestBody @Valid EmailDTO dto) {
        emailService.enviarEmailRedefinicaoSenha(dto.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<?> redefinirSenha(@RequestBody RedefinirSenhaDTO dto) {
        usuarioService.redefinirSenha(dto.token(), dto.novaSenha());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<?> alterarSenha(
            @RequestBody AlterarSenhaDTO dto,
            @AuthenticationPrincipal Usuario usuario
    ) {
        usuarioService.alterarSenha(usuario, dto.senhaAtual(), dto.novaSenha());
        return ResponseEntity.ok().build();
    }

}
