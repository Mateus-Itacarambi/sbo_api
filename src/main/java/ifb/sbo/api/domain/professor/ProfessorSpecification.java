package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

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
                predicates.add(cb.equal(root.join("cursos").get("nome"), filtro.getCurso()));
            }

            if (filtro.getDisponibilidade() != null && !filtro.getDisponibilidade().isEmpty()) {
                predicates.add(cb.equal(root.get("disponibilidade"), filtro.getDisponibilidade()));
            }

            if (filtro.getAreaInteresse() != null && !filtro.getAreaInteresse().isEmpty()) {
                Join<Professor, AreaInteresse> join = root.join("areasInteresse", JoinType.INNER);
                predicates.add(join.get("nome").in(filtro.getAreaInteresse()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
