package ifb.sbo.api.domain.area_interesse;

public record DadosDetalhamentoAreaInteresse (
        Long id,
        String nome) {

    public DadosDetalhamentoAreaInteresse(AreaInteresse areaInteresse) {
        this(areaInteresse.getId(), areaInteresse.getNome());
    }
}
