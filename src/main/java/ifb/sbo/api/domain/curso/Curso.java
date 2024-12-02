package ifb.sbo.api.domain.curso;

import ifb.sbo.api.domain.estudante.Estudante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Table(name = "curso")
@Entity(name = "Curso")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Long id;
    private String nome;
    private String sigla;
    private String descricao;
    private Boolean ativo;
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Estudante> estudantes = new ArrayList<>();

    public Curso(DadosCadastroCurso dados) {
        this.nome = dados.nome();
        this.sigla = dados.sigla();
        this.descricao = dados.descricao();
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizaCurso dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.sigla() != null) {
            this.sigla = dados.sigla();
        }
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
    }

    public void excluir() {
        this.ativo = false;
    }
}
