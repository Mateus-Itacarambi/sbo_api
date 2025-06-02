package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import ifb.sbo.api.domain.curso.Curso;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfessorSpecification {

    public static Specification<Professor> comFiltros(FiltroProfessor filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("ativo")));
            predicates.add(cb.isTrue(root.get("cadastroCompleto")));

            if (filtro.getNome() != null && !filtro.getNome().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + filtro.getNome().toLowerCase() + "%"));
            }

            if (filtro.getCurso() != null && !filtro.getCurso().isEmpty()) {
                Join<Professor, Curso> join = root.join("cursos", JoinType.INNER);
                predicates.add(join.get("nome").in(filtro.getCurso()));
            }

            if (filtro.getDisponibilidade() != null && !filtro.getDisponibilidade().isEmpty()) {
                List<Disponibilidade> disponiveisValidos = filtro.getDisponibilidade().stream()
                        .map(String::toUpperCase)
                        .map(val -> {
                            try {
                                return Disponibilidade.valueOf(val);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();

                if (!disponiveisValidos.isEmpty()) {
                    predicates.add(root.get("disponibilidade").in(disponiveisValidos));
                }
            }

            if (filtro.getAreaInteresse() != null && !filtro.getAreaInteresse().isEmpty()) {
                Join<Professor, AreaInteresse> join = root.join("areasInteresse", JoinType.INNER);
                predicates.add(join.get("nome").in(filtro.getAreaInteresse()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
