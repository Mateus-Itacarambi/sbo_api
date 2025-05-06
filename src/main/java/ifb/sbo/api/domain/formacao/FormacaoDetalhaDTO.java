package ifb.sbo.api.domain.formacao;

public record FormacaoDetalhaDTO(
        String curso,
        String faculdade,
        String titulo,
        Long anoInicio,
        Long anoFim) {
}
