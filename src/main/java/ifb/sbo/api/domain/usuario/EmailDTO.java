package ifb.sbo.api.domain.usuario;

import jakarta.validation.constraints.Email;

public record EmailDTO(
        @Email
        String email
) {
}
