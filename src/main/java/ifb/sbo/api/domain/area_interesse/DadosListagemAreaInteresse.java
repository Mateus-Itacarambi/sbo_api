package ifb.sbo.api.domain.area_interesse;

public record DadosListagemAreaInteresse(
        Long id,
        String nome) {

    public DadosListagemAreaInteresse(AreaInteresse areaInteresse) {
        this(areaInteresse.getId(), areaInteresse.getNome());
    }
}
