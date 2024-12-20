package ifb.sbo.api.domain.estudante;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record EstudanteAtualizaDTO(
        @NotNull
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha,
        String matricula,
        Integer semestre) {
}
