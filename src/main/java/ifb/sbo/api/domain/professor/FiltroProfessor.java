package ifb.sbo.api.domain.professor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FiltroProfessor {
    private String nome;
    private List<String> curso;
    private List<String> disponibilidade;
    private List<String> areaInteresse;
}

