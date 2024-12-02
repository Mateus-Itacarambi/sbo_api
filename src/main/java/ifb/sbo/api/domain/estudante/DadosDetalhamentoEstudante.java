package ifb.sbo.api.domain.estudante;


import java.time.LocalDate;

public record DadosDetalhamentoEstudante (
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha,
        String matricula,
        Integer semestre,
        Long idCurso) {

    public DadosDetalhamentoEstudante (Estudante estudante){
        this(estudante.getId(), estudante.getNome(), estudante.getDataNascimento(), estudante.getGenero(), estudante.getEmail(), estudante.getSenha(), estudante.getMatricula(), estudante.getSemestre(), estudante.getCurso().getId());
    }
}
