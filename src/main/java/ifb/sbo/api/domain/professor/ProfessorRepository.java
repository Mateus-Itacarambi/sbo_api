package ifb.sbo.api.domain.professor;

import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long>, JpaSpecificationExecutor<Professor> {
    Page<Professor> findAllByAtivoTrueAndCadastroCompletoTrue(Pageable paginacao);

    int countByIdLattes(String idLattes);

    Optional<Professor> findByIdAndAtivoTrue(Long professorId);

    Optional<Professor> findByIdLattesAndAtivoTrueAndCadastroCompletoTrue(String identificador);

    Page<Professor> findAll(@Nullable Specification<Professor> spec, Pageable pageable);

    List<Professor> findByAtivoTrueAndCadastroCompletoTrue();
}
