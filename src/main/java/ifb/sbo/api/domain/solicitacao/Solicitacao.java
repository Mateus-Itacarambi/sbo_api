package ifb.sbo.api.domain.solicitacao;


import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.notificacao.Notificacao;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.tema.Tema;
import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "solicitacao")
@Entity(name = "Solicitacao")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Solicitacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "status_solicitacao")
    private StatusSolicitacao  status;
    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipo;
    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
    @Column(name = "data_conclusao_orientacao")
    private LocalDate dataConclusaoOrientacao;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    private Tema tema;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estudante", nullable = false)
    private Estudante estudante;
    private String motivo;
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notificacao> notificacoes;

    public Solicitacao(Tema tema, Professor professor, Estudante estudante) {
        Clock clock = Clock.systemDefaultZone();
        this.status = StatusSolicitacao.PENDENTE;
        this.dataSolicitacao = LocalDateTime.now(clock);
        this.dataAtualizacao = LocalDateTime.now(clock);
        this.tema = tema;
        this.professor = professor;
        this.estudante = estudante;
    }
}
