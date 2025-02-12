package ifb.sbo.api.domain.professor;

import lombok.Getter;

@Getter
public enum Disponibilidade {
    DISPONIVEL("Disponível"),
    INDISPONIVEL("Indisponível");

    private final String descricao;

    Disponibilidade(String descricao) {
        this.descricao = descricao;
    }
}
