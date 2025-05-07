package ifb.sbo.api.domain.formacao;

public record FormacaoDetalhaDTO(
        String curso,
        String instituicao,
        String titulo,
        Long anoInicio,
        Long anoFim) {
}
