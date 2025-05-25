package ifb.sbo.api.domain.curso;

import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.professor.ProfessorCursoDTO;

import java.util.List;


public record CursoListagemDTO(
        Long id,
        String nome,
        String sigla,
        String descricao,
        Integer semestres,
        String slug,
        String cargaHoraria,
        String duracaoMax,
        String modalidade,
        List<ProfessorCursoDTO> professores) {}

