package ifb.sbo.api.infra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EnviarEmail {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarSenhaPorEmail(String para, String senha) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(para);
        mensagem.setSubject("Cadastro no sistema");
        mensagem.setText("Olá! Sua conta foi criada. Sua senha temporária é: " + senha);

        mailSender.send(mensagem);
    }

}
