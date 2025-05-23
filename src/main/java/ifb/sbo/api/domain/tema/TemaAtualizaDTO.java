package ifb.sbo.api.domain.tema;

import jakarta.validation.constraints.NotBlank;

public record TemaAtualizaDTO(
        @NotBlank
        String titulo,
        @NotBlank
        String descricao,
        @NotBlank
        String palavrasChave) {}
