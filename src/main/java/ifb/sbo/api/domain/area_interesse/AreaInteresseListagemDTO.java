package ifb.sbo.api.domain.area_interesse;


public record AreaInteresseListagemDTO(
        Long id,
        String nome) {

    public AreaInteresseListagemDTO(AreaInteresse areaInteresse) {
        this(areaInteresse.getId(), areaInteresse.getNome());
    }
}
