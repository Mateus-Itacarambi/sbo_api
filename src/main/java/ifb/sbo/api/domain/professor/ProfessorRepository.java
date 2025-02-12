package ifb.sbo.api.domain.professor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Page<Professor> findAllByAtivoTrue(Pageable paginacao);

    int countByIdLattes(String idLattes);

    Optional<Professor> findByIdAndAtivoTrue(Long professorId);
}
