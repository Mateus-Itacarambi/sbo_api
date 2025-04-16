package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "tema")
@Entity(name = "Tema")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Tema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tema")
    private Long id;
    private String titulo;
    private String descricao;
    private String palavrasChave;
    @Column(name = "status_tema")
    @Enumerated(EnumType.STRING)
    private StatusTema status;
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;
    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;
    @OneToMany(mappedBy = "tema", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Estudante> estudantes = new ArrayList<>();
    @OneToMany(mappedBy = "tema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Tema(TemaCadastroDTO dados) {
        this.titulo = dados.titulo();
        this.descricao = dados.descricao();
        this.palavrasChave = dados.palavrasChave();
        this.dataCadastro = LocalDate.now();
        this.dataAtualizacao = LocalDate.now();
        this.status = StatusTema.DISPONIVEL;
    }

    public void atualizarTema(TemaAtualizaDTO dados) {
        if (dados.titulo() != null) {
            this.titulo = dados.titulo();
        }

        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }

        if (dados.palavrasChave() != null) {
            this.palavrasChave = dados.palavrasChave();
        }
    }
}
