package ifb.sbo.api.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UsuarioCadastroDTO(
        @NotBlank
        String nome,
        @NotNull
        @Past()
        LocalDate dataNascimento,
        @NotBlank
        String genero,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String senha) {}
