package ifb.sbo.api.domain.area_interesse;

import ifb.sbo.api.domain.professor.Professor;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "area_interesse")
@Entity(name = "AreaInteresse")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class AreaInteresse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_area_interesse")
    private Long id;
    private String nome;
    private Boolean ativo;
    @ManyToMany(mappedBy = "areasInteresse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Professor> professores = new ArrayList<>();

    public AreaInteresse(AreaInteresseCadastroDTO dados) {
        this.nome = dados.nome();
        this.ativo = true;
    }

    public void atualizarInformacoes(AreaInteresseAtualizaDTO dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
    }

    public void desativar() {
        this.ativo = false;
    }

    public void ativar() {
        this.ativo = true;
    }
}
