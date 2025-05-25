package ifb.sbo.api.domain.usuario;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.professor.Professor;

public record UsuarioSimplesDTO(
        Long id,
        String nome,
        String role,
        String slug
) {
    public static UsuarioSimplesDTO from(Usuario usuario) {
        if (usuario instanceof Estudante estudante) {
            return new UsuarioSimplesDTO(estudante.getId(), estudante.getNome(), estudante.getRole().toString(), estudante.getMatricula());
        } else if (usuario instanceof Professor professor) {
            return new UsuarioSimplesDTO(professor.getId(), professor.getNome(), professor.getRole().toString(), professor.getIdLattes());
        } else {
            return null;
        }
    }
}

