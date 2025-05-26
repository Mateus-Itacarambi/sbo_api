package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;
    private String mensagem;
    private boolean lida = false;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    private String tipo;
    @ManyToOne
    @JoinColumn(name = "id_solicitante")
    private Usuario solicitante;
    @ManyToOne
    @JoinColumn(name = "id_destinatario")
    private Usuario destinatario;
    @ManyToOne
    @JoinColumn(name = "id_solicitacao")
    private Solicitacao solicitacao;
}

