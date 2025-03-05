package ifb.sbo.api.domain.curso;

public record CursoListagemDTO(
        Long id,
        String nome,
        String sigla,
        String descricao,
        Integer semestres) {

    public CursoListagemDTO(Curso curso) {
        this(curso.getId(), curso.getNome(), curso.getSigla(), curso.getDescricao(), curso.getSemestres());
    }
}

