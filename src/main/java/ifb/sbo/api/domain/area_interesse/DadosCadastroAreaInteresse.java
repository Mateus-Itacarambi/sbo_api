package ifb.sbo.api.domain.area_interesse;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroAreaInteresse (
        @NotBlank
        String nome) {}
