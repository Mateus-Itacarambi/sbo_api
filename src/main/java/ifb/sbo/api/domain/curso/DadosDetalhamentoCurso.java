package ifb.sbo.api.domain.curso;

public record DadosDetalhamentoCurso(
        Long id,
        String nome,
        String sigla,
        String descricao) {

    public DadosDetalhamentoCurso (Curso curso){
        this(curso.getId(), curso.getNome(), curso.getSigla(), curso.getDescricao());
    }
}
