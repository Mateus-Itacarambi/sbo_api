package ifb.sbo.api.domain.solicitacao;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitacaoListagemDTO(
        Long id,
        StatusSolicitacao status,
        LocalDateTime dataSolicitacao,
        LocalDate dataConclusaoOrientacao,
        TemaDetalhaSolicitacaoDTO tema,
        UsuarioSimplesDTO professor,
        UsuarioSimplesDTO estudante,
        String motivo,
        TipoSolicitacao tipo) {}
