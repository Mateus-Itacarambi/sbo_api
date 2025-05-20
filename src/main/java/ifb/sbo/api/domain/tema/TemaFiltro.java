package ifb.sbo.api.domain.tema;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TemaFiltro {
    private String titulo;
    private List<String> professor;
}
