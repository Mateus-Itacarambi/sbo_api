package ifb.sbo.api.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaDTO(
        @NotBlank
        String token,
        @NotBlank
        String novaSenha) {
}
