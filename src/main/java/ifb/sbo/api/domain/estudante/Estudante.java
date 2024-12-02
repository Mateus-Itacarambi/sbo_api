package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.curso.Curso;
import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Table(name = "estudante")
@Entity(name = "Estudante")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Estudante extends Usuario {
    private String matricula;
    private Integer semestre;
    private Boolean ativo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;

    public Estudante(DadosCadastroEstudante dados, Curso curso) {
        super(dados.nome(), dados.dataNascimento(), dados.genero(), dados.email(), dados.senha());
        this.matricula = dados.matricula();
        this.semestre = dados.semestre();
        this.curso = curso;
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizaEstudante dados) {
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

    public void excluir() {
        this.ativo = false;
    }
}
