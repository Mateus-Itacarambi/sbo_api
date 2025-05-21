package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.professor.Professor;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TemaSpecification {

    public static Specification<Tema> comFiltros(FiltroTema filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), StatusTema.DISPONIVEL));

            if (filtro.getTitulo() != null && !filtro.getTitulo().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("titulo")), "%" + filtro.getTitulo().toLowerCase() + "%"));
            }

            if (filtro.getPalavrasChave() != null && !filtro.getPalavrasChave().isEmpty()) {
                List<Predicate> palavrasPredicates = new ArrayList<>();
                for (String palavra : filtro.getPalavrasChave()) {
                    palavrasPredicates.add(
                            cb.like(cb.lower(root.get("palavrasChave")), "%" + palavra.toLowerCase() + "%")
                    );
                }
                predicates.add(cb.or(palavrasPredicates.toArray(new Predicate[0])));
            }

            if (filtro.getProfessor() != null && !filtro.getProfessor().isEmpty()) {
                Join<Tema, Professor> join = root.join("professor", JoinType.INNER);
                CriteriaBuilder.In<String> inClause = cb.in(cb.lower(join.get("nome")));
                filtro.getProfessor().forEach(n -> inClause.value(n.toLowerCase()));
                predicates.add(inClause);
            }


            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

