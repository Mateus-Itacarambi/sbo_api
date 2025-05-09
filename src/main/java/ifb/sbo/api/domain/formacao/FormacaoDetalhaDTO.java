package ifb.sbo.api.domain.formacao;

public record FormacaoDetalhaDTO(
        Long id,
        String curso,
        String instituicao,
        String titulo,
        Long anoInicio,
        Long anoFim) {
}
