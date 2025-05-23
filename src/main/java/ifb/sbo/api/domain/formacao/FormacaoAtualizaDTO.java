package ifb.sbo.api.domain.formacao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FormacaoAtualizaDTO(
        @NotBlank
        String curso,
        @NotBlank
        String instituicao,
        @NotBlank
        String titulo,
        @NotNull
        Long anoInicio,
        @NotNull
        Long anoFim) {}