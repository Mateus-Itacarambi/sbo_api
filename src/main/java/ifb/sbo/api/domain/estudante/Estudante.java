package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.tema.Tema;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.curso.Curso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "estudante")
@Entity(name = "Estudante")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Estudante extends Usuario {
    private String matricula;
    private Integer semestre;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tema")
    private Tema tema;

    public Estudante(EstudanteCadastroDTO dados, Curso curso) {
        super(dados.nome(), dados.dataNascimento(), dados.genero(), dados.email(), dados.senha());
        this.matricula = dados.matricula();
        this.semestre = dados.semestre();
        this.curso = curso;
    }

    public void atualizarInformacoes(EstudanteAtualizaDTO dados) {
        if (dados.nome() != null) {
            super.nome = dados.nome();
        }

        if (dados.dataNascimento() != null) {
            super.dataNascimento = dados.dataNascimento();
        }

        if (dados.genero() != null) {
            super.genero = dados.genero();
        }

        if (dados.email() != null) {
            super.email = dados.email();
        }

        if (dados.senha() != null) {
            super.senha = dados.senha();
        }

        if (dados.matricula() != null) {
            this.matricula = dados.matricula();
        }

        if (dados.semestre() != null) {
            this.semestre = dados.semestre();
        }
    }

    public Curso getCurso() {
        return curso;
    }

    public void excluir() {
        super.ativo = false;
    }
}
