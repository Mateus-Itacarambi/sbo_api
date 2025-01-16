package ifb.sbo.api.domain.area_interesse;

import jakarta.validation.constraints.NotBlank;

public record AreaInteresseCadastroDTO(
        @NotBlank
        String nome) {}
