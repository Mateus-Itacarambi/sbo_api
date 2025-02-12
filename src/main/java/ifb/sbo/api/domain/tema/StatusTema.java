package ifb.sbo.api.domain.tema;

import lombok.Getter;

@Getter
public enum StatusTema {
    RESERVADO("Reservado"),
    EM_ANDAMENTO("Em andamento"),
    DISPONIVEL("Disponível"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusTema(String descricao) {
        this.descricao = descricao;
    }
}
