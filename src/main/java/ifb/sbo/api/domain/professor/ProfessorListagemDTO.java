package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresseDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.formacao.FormacaoDetalhaDTO;
import ifb.sbo.api.domain.tema.TemaDetalhaDTO;

import java.time.LocalDate;
import java.util.List;

public record ProfessorListagemDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String idLattes,
        String role,
        Boolean ativo,
        Boolean cadastroCompleto,
        String disponibilidade,
        List<CursoDetalhaDTO> cursos,
        List<AreaInteresseDetalhaDTO> areasDeInteresse,
        List<FormacaoDetalhaDTO> formacoes,
        List<TemaDetalhaDTO> temas) {
}
