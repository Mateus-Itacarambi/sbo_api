package ifb.sbo.api.domain.solicitacao;


import jakarta.validation.constraints.NotBlank;

public record SolicitacaoMotivoDTO(
        @NotBlank
        String motivo) {}
