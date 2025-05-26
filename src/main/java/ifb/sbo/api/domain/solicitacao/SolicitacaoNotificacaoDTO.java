package ifb.sbo.api.domain.solicitacao;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitacaoNotificacaoDTO(
        Long id,
        String motivo) {

    public static SolicitacaoNotificacaoDTO from(Solicitacao solicitacao) {
        return new SolicitacaoNotificacaoDTO(solicitacao.getId(), solicitacao.getMotivo());
    }
}
