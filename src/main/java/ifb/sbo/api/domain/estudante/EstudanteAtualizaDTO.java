package ifb.sbo.api.domain.estudante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.Period;

public record EstudanteAtualizaDTO(
        @NotNull
        Long id,
        @NotBlank
        String nome,
        @NotNull
        LocalDate dataNascimento,
        @NotBlank
        String genero,
        @NotBlank
        String matricula,
        @NotNull
        Long curso,
        @NotNull
        Integer semestre) {

        public boolean isMaiorDeIdade() {
                return Period.between(this.dataNascimento, LocalDate.now()).getYears() >= 18;
        }
}
