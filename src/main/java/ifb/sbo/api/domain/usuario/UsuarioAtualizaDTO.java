package ifb.sbo.api.domain.usuario;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UsuarioAtualizaDTO(
        @NotNull
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha) {}
