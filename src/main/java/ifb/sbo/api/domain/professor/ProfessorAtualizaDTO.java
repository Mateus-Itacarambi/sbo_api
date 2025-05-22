package ifb.sbo.api.domain.professor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;


public record ProfessorAtualizaDTO(
        @NotNull
        Long id,
        @NotBlank
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha,
        String idLattes) {}

