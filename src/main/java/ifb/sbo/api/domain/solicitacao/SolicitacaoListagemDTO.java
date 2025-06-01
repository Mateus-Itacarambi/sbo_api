package ifb.sbo.api.domain.solicitacao;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitacaoListagemDTO(
        Long id,
        StatusSolicitacao status,
        LocalDateTime dataSolicitacao,
        LocalDate dataConclusaoOrientacao,
        TemaDetalhaSolicitacaoDTO tema,
        ProfessorDetalhaDTO professor,
        EstudanteDetalhaDTO estudante,
        String motivo,
        TipoSolicitacao tipo) {}
