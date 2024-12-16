package ifb.sbo.api.domain.professor;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DadosAtualizaProfessor(
        @NotNull
        Long id,
        String nome,
        LocalDate dataNascimento,
        String genero,
        String email,
        String senha,
        String idLattes) {}
