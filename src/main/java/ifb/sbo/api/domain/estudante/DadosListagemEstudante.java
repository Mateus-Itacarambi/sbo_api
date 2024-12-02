package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;

import java.time.LocalDate;

public record DadosListagemEstudante (
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String matricula,
        Integer semestre,
        Long idCurso) {

    public DadosListagemEstudante (Estudante estudante){
        this(estudante.getId(), estudante.getNome(), estudante.getDataNascimento(), estudante.getGenero(), estudante.getEmail(), estudante.getMatricula(), estudante.getSemestre(), estudante.getCurso().getId());
    }
}
