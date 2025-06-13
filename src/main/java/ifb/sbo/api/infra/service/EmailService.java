package ifb.sbo.api.infra.service;

import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import ifb.sbo.api.infra.exception.ConflitoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public void enviarSenhaPorEmail(String email, String senha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(email);
        mensagem.setSubject("Cadastro no sistema");
        mensagem.setText("Olá! Sua conta foi criada. Sua senha temporária é: " + senha);

        mailSender.send(mensagem);
    }

    public void enviarEmailRedefinicaoSenha(String email) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ConflitoException("E-mail não encontrado"));

        String token = UUID.randomUUID().toString();
        usuario.setTokenRecuperacao(token);
        usuario.setDataExpiracaoToken(LocalDateTime.now().plusHours(1));
        usuarioRepository.save(usuario);

        String link = "http://localhost:3000/redefinir-senha?token=" + token;

        mensagem.setTo(email);
        mensagem.setSubject("Redefinição de Senha");
        mensagem.setText("Para redefinir sua senha clique no link a seguir: " + link);
        mailSender.send(mensagem);
    }

}
