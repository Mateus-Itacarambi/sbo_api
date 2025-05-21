package ifb.sbo.api.domain.tema;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FiltroTema {
    private String titulo;
    private List<String> palavrasChave;
    private List<String> professor;
}
