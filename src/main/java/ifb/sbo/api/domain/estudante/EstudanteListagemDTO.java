package ifb.sbo.api.domain.estudante;


import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaDTO;

import java.time.LocalDate;


public record EstudanteListagemDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String role,
        Boolean ativo,
        Boolean cadastroCompleto,
        String matricula,
        Integer semestre,
        CursoDetalhaDTO curso,
        TemaDetalhaDTO tema) {
}
