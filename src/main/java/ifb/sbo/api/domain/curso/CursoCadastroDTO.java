package ifb.sbo.api.domain.curso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CursoCadastroDTO(
        @NotBlank
        String nome,
        @NotBlank
        String sigla,
        @NotBlank
        String descricao,
        @NotNull
        Integer semestres,
        @NotBlank
        String cargaHoraria,
        @NotBlank
        String duracaoMax,
        @NotBlank
        String modalidade) {}
