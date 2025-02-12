package ifb.sbo.api.domain.solicitacao;

import lombok.Getter;

@Getter
public enum StatusSolicitacao {
    PENDENTE("Pendente"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada"),
    CANCELADA("Cancelada"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusSolicitacao(String descricao) {
        this.descricao = descricao;
    }
}
