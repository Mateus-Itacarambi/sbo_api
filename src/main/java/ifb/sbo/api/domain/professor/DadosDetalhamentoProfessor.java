package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.estudante.Estudante;

import java.time.LocalDate;

public record DadosDetalhamentoProfessor(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha,
        String idLattes,
        String disponibilidade
        ) {

    public DadosDetalhamentoProfessor (Professor professor){
        this(professor.getId(), professor.getNome(), professor.getDataNascimento(), professor.getGenero(), professor.getEmail(), professor.getSenha(), professor.getIdLattes(), String.valueOf(professor.getDisponibilidade()));
    }
}
