package ifb.sbo.api.domain.professor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;


public record ProfessorAtualizaCadastroDTO(
        @NotNull
        Long id,
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
        String senhaAtual,
        @NotBlank
        String senhaNova,
        @NotBlank
        String senhaConfirmar,
        @NotBlank
        String idLattes) {}

