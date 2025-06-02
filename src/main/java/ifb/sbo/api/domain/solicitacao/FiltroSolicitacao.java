package ifb.sbo.api.domain.solicitacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;


@Getter
@AllArgsConstructor
public class FiltroSolicitacao {
    private List<String> status;
    private List<String> tipo;
    private String tituloTema;
    private String nomeProfessor;
    private String nomeEstudante;
}
