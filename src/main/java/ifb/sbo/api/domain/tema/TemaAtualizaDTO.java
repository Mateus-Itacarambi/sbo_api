package ifb.sbo.api.domain.tema;

public record TemaAtualizaDTO(
        String titulo,
        String descricao,
        String palavrasChave,
        String areaConhecimento) {}
