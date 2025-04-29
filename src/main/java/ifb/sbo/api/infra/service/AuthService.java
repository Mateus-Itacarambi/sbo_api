package ifb.sbo.api.infra.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean verificarSenha(String senhaBruta, String senhaCriptografada) {
        return passwordEncoder.matches(senhaBruta, senhaCriptografada);
    }

    public String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }
}

