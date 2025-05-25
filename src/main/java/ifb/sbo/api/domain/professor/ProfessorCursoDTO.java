package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresseDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;

import java.util.List;

public record ProfessorCursoDTO(
        Long id,
        String nome,
        String email,
        String disponibilidade,
        String idLattes) {
}
