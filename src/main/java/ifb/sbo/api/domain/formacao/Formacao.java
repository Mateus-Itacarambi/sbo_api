package ifb.sbo.api.domain.formacao;

import ifb.sbo.api.domain.professor.Professor;
import jakarta.persistence.*;
import lombok.*;


@Table(name = "formacao")
@Entity(name = "Formacao")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Formacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_formacao")
    private Long id;
    private String curso;
    private String instituicao;
    @Column(name = "titulo_tcc")
    private String titulo;
    private Long anoInicio;
    private Long anoFim;
    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    public Formacao(FormacaoCadastroDTO dados) {
        this.curso = dados.curso();
        this.instituicao = dados.instituicao();
        this.titulo = dados.titulo();
        this.anoInicio = dados.anoInicio();
        this.anoFim = dados.anoFim();
    }

    public void atualizarFormacao(FormacaoAtualizaDTO dados) {
        if (dados.curso() != null) {
            this.curso = dados.curso();
        }

        if (dados.instituicao() != null) {
            this.instituicao = dados.instituicao();
        }

        if (dados.titulo() != null) {
            this.titulo = dados.titulo();
        }

        if (dados.anoInicio() != null) {
            this.anoInicio = dados.anoInicio();
        }

        if (dados.anoFim() != null) {
            this.anoFim = dados.anoFim();
        }
    }
}
