package ifb.sbo.api.domain.professor;

import java.time.LocalDate;

public record DadosListagemProfessor(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String idLattes,
        String disponibilidade) {

    public DadosListagemProfessor (Professor professor){
        this(professor.getId(), professor.getNome(), professor.getDataNascimento(), professor.getGenero(), professor.getEmail(), professor.getIdLattes(), String.valueOf(professor.getDisponibilidade()));
    }
}
