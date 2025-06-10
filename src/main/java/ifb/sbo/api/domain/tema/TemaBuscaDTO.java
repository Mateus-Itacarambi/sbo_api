package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;

import java.util.List;

public record TemaBuscaDTO(
        Long id,
        String titulo,
        String descricao,
        String palavrasChave,
        String status,
        ProfessorDetalhaDTO professor,
        Boolean solicitacaoPendente,
        Long idSolicitacao) {}
