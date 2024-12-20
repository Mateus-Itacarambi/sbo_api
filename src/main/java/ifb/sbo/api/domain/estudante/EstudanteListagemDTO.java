package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;

import java.time.LocalDate;

public record EstudanteListagemDTO(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String matricula,
        Integer semestre,
        String curso) {

    public EstudanteListagemDTO(Estudante estudante){
        this(estudante.getId(), estudante.getNome(), estudante.getDataNascimento(), estudante.getGenero(), estudante.getEmail(), estudante.getMatricula(), estudante.getSemestre(), estudante.getCurso().getNome());
    }
}
