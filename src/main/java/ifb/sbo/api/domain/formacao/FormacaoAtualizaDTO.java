package ifb.sbo.api.domain.formacao;

import jakarta.validation.constraints.NotNull;

public record FormacaoAtualizaDTO(
        String curso,
        String instituicao,
        String titulo,
        Long anoInicio,
        Long anoFim,
        Long professorId) {}