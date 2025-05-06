package ifb.sbo.api.domain.formacao;

import jakarta.validation.constraints.NotNull;

public record FormacaoAtualizaDTO(
        String curso,
        String faculdade,
        String titulo,
        Long anoInicio,
        Long anoFim,
        Long professorId) {}