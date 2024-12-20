package ifb.sbo.api.domain.estudante;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EstudanteCadastroDTO(
        @NotBlank
        String nome,
        @NotNull
        @Past
        LocalDate dataNascimento,
        @NotBlank
        String genero,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String senha,
        @NotBlank
        String matricula,
        @NotNull
        @Min(1)
        Integer semestre,
        @NotNull
        @Min(1)
        Long idCurso) {}
