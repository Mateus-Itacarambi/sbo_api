package ifb.sbo.api.domain.estudante;


import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaDTO;

import java.time.LocalDate;


public record EstudanteListagemTemaDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String matricula,
        Integer semestre,
        CursoDetalhaDTO curso) {
}
