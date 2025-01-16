package ifb.sbo.api.domain.area_interesse;

import jakarta.validation.constraints.NotNull;

public record AreaInteresseAtualizaDTO(
        @NotNull
        Long id,
        String nome) {}
