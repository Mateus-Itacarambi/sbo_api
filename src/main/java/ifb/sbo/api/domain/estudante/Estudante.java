package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.tema.Tema;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.curso.Curso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


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
    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;
    @ManyToOne
    @JoinColumn(name = "id_tema")
    private Tema tema;
    @OneToMany(mappedBy = "estudante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Estudante(EstudanteCadastroDTO dados, Curso curso) {
        super(dados.nome(), dados.dataNascimento(), dados.genero(), dados.email(), dados.senha());
        this.matricula = dados.matricula();
        this.semestre = dados.semestre();
        this.curso = curso;
    }

    public void atualizarInformacoes(EstudanteAtualizaDTO dados, Curso curso) {
        if (dados.nome() != null) {
            super.nome = dados.nome();
        }

        if (dados.dataNascimento() != null) {
            super.dataNascimento = dados.dataNascimento();
        }

        if (dados.genero() != null) {
            super.genero = dados.genero();
        }

//        if (dados.email() != null) {
//            super.email = dados.email();
//        }
//
//        if (dados.senha() != null) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            super.senha = passwordEncoder.encode(dados.senha());
//        }

        if (dados.matricula() != null) {
            this.matricula = dados.matricula();
        }

        if (dados.curso() != null) {
            this.curso = curso;
        }

        if (dados.semestre() != null) {
            this.semestre = dados.semestre();
        }
    }

    public void excluir() {
        super.ativo = false;
    }
}
