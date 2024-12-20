package ifb.sbo.api.domain.professor;

import java.time.LocalDate;
import java.util.List;

public record ProfessorListagemDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String idLattes,
        String disponibilidade,
        List<String> cursos,
        List<String> areasDeInteresse) {

    public ProfessorListagemDTO (Professor professor){
        this(professor.getId(), professor.getNome(), professor.getDataNascimento(), professor.getGenero(), professor.getEmail(), professor.getIdLattes(), String.valueOf(professor.getDisponibilidade()), professor.getCursosString(), professor.getAreasInteresseString());
    }
}
