package ifb.sbo.api.config;

import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import ifb.sbo.api.domain.usuario.TipoUsuario;
import ifb.sbo.api.infra.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SecurityConfig securityConfig;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail("admin@sbo.com.br").isEmpty()) {
            Usuario admin = new Usuario("Administrador", LocalDate.of(2000, 1 ,1), "Outro", "admin@sbo.com.br", "admin123");
            admin.setRole(TipoUsuario.ADMINISTRADOR);

            usuarioRepository.save(admin);
            System.out.println("Usuário admin criado com sucesso!");
        }
    }
}
