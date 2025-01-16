package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.EstudanteListagemTemaDTO;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;

import java.util.List;

public record TemaListagemDTO(
        String titulo,
        String descricao,
        String palavrasChave,
        String areaConhecimento,
        String status,
        ProfessorDetalhaDTO professor,
        List<EstudanteListagemTemaDTO> estudante) {}
