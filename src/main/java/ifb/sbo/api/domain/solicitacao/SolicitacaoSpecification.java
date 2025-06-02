package ifb.sbo.api.domain.solicitacao;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SolicitacaoSpecification {
    public static Specification<Solicitacao> comFiltros(FiltroSolicitacao filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getStatus() != null && !filtro.getStatus().isEmpty()) {
                List<StatusSolicitacao> statusList = filtro.getStatus().stream()
                        .map(String::toUpperCase)
                        .map(s -> {
                            try { return StatusSolicitacao.valueOf(s); } catch (Exception e) { return null; }
                        })
                        .filter(Objects::nonNull)
                        .toList();
                predicates.add(root.get("status").in(statusList));
            }

            if (filtro.getTipo() != null && !filtro.getTipo().isEmpty()) {
                List<TipoSolicitacao> tipoList = filtro.getTipo().stream()
                        .map(String::toUpperCase)
                        .map(s -> {
                            try { return TipoSolicitacao.valueOf(s); } catch (Exception e) { return null; }
                        })
                        .filter(Objects::nonNull)
                        .toList();
                predicates.add(root.get("tipo").in(tipoList));
            }

            if (filtro.getTituloTema() != null && !filtro.getTituloTema().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("tema").get("titulo")), "%" + filtro.getTituloTema().toLowerCase() + "%"));
            }

            if (filtro.getNomeProfessor() != null && !filtro.getNomeProfessor().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("professor").get("nome")), "%" + filtro.getNomeProfessor().toLowerCase() + "%"));
            }

            if (filtro.getNomeEstudante() != null && !filtro.getNomeEstudante().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("estudante").get("nome")), "%" + filtro.getNomeEstudante().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

