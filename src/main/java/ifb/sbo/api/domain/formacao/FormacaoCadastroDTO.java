package ifb.sbo.api.domain.formacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormacaoCadastroDTO(
        @NotBlank
        String curso,
        @NotBlank
        String instituicao,
        @NotBlank
        String titulo,
        @NotBlank
        Long anoInicio,
        @NotBlank
        Long anoFim) {

        public FormacaoCadastroDTO {
                if (anoInicio <= 0) {
                        throw new IllegalArgumentException("Ano de início deve ser positivo.");
                }
                if (anoFim != null && anoFim < anoInicio) {
                        throw new IllegalArgumentException("Ano de fim não pode ser anterior ao ano de início.");
                }
        }
}
