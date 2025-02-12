package ifb.sbo.api.domain.tema;


public record TemaDetalhaDTO(
        String titulo,
        String descricao,
        String palavrasChave,
        String areaConchecimento,
        String statusTema) {}
