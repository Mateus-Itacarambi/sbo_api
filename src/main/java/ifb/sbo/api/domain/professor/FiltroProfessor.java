package ifb.sbo.api.domain.professor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FiltroProfessor {
    private String nome;
    private String curso;
    private String disponibilidade;
    private List<String> areaInteresse;
}

