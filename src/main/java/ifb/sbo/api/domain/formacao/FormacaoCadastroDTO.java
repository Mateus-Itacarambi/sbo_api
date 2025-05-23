package ifb.sbo.api.domain.formacao;

import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormacaoCadastroDTO(
        @NotBlank
        String curso,
        @NotBlank
        String instituicao,
        @NotBlank
        String titulo,
        @NotNull
        Long anoInicio,
        @NotNull
        Long anoFim) {

        public FormacaoCadastroDTO {
                if (anoInicio <= 0) {
                        throw new ConflitoException("Informe o ano de início corretamente.");
                }
                if (anoFim != null && anoFim < anoInicio) {
                        throw new ConflitoException("Ano de conclusão não pode ser anterior ao ano de início.");
                }
        }
}
