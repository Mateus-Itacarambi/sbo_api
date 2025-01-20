package ifb.sbo.api.domain.solicitacao;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.tema.Tema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private StatusSolicitacao  status;
    @Column(name = "data_solicitacao", nullable = false)
    private LocalDate  dataSolicitacao;
    @Column(name = "data_conclusao_orientacao")
    private LocalDate dataConclusaoOrientacao;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tema", nullable = false)
    private Tema tema;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_estudante", nullable = false)
    private Estudante estudante;
}
