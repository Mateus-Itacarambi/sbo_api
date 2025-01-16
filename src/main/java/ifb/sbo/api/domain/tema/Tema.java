package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.professor.Professor;
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
    private String areaConhecimento;
    private String status;
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;
    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;
    private Boolean ativo;
    @ManyToOne
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;
    @OneToMany(mappedBy = "tema", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Estudante> estudantes = new ArrayList<>();

    public Tema(TemaCadastroDTO dados) {
        this.titulo = dados.titulo();
        this.descricao = dados.descricao();
        this.palavrasChave = dados.palavrasChave();
        this.areaConhecimento = dados.areaConhecimento();
        this.dataCadastro = LocalDate.now();
        this.dataAtualizacao = LocalDate.now();
        this.status = "Disponível";
        this.ativo = true;
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

        if (dados.areaConhecimento() != null) {
            this.areaConhecimento = dados.areaConhecimento();
        }
    }
}
