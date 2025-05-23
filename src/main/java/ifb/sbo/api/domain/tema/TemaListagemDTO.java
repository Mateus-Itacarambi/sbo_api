package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.estudante.EstudanteListagemTemaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;

import java.util.List;

public record TemaListagemDTO(
        Long id,
        String titulo,
        String descricao,
        String palavrasChave,
        String status,
        ProfessorDetalhaDTO professor,
        List<EstudanteDetalhaDTO> estudante) {}
