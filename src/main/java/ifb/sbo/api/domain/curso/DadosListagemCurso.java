package ifb.sbo.api.domain.curso;

public record DadosListagemCurso(
        Long id,
        String nome,
        String sigla,
        String descricao) {

    public DadosListagemCurso(Curso curso) {
        this(curso.getId(), curso.getNome(), curso.getSigla(), curso.getDescricao());
    }
}

