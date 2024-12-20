package ifb.sbo.api.domain.area_interesse;


public record AreaInteresseDTO (
    Long id,
    String nome) {

    public AreaInteresseDTO (AreaInteresse areaInteresse) {
        this(areaInteresse.getId(), areaInteresse.getNome());
    }
}
