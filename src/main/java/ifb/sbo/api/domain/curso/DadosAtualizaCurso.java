package ifb.sbo.api.domain.curso;


import jakarta.validation.constraints.NotNull;

public record DadosAtualizaCurso(
        @NotNull
        Long id,
        String nome,
        String sigla,
        String descricao) {}