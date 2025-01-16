package ifb.sbo.api.domain.curso;

import jakarta.validation.constraints.NotBlank;

public record CursoCadastroDTO(
        @NotBlank
        String nome,
        @NotBlank
        String sigla,
        @NotBlank
        String descricao) {}
