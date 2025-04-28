package ifb.sbo.api.domain.usuario;

import java.time.LocalDate;

public record UsuarioListagemDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String role,
        Boolean ativo,
        Boolean cadastroCompleto
        ) {}
