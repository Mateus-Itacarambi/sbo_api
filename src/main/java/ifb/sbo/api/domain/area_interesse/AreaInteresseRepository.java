package ifb.sbo.api.domain.area_interesse;

import ifb.sbo.api.domain.curso.Curso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Long> {
    Page<AreaInteresse> findAllByAtivoTrue(Pageable paginacao);

    boolean existsByNome(String nome);

    Optional<AreaInteresse> findByIdAndAtivoTrue(Long areaInteresseId);
}
