package ifb.sbo.api.domain.estudante;


import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaDTO;

import java.time.LocalDate;


public record EstudanteResumoDTO(
        Long id,
        String nome,
        String role,
        Boolean ativo,
        Boolean cadastroCompleto,
        String matricula) {
}
