package ifb.sbo.api.domain.area_interesse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Long> {
    Page<AreaInteresse> findAllByAtivoTrue(Pageable paginacao);

    Optional <AreaInteresse> findByIdAndAtivoTrue(Long areaInteresseId);

    int countByNomeAndAtivoTrue(String nome);

    boolean existsByNome(String nome);

    List<AreaInteresse>  findByAtivoTrue();

    List<AreaInteresse> findByIdIn(List<Long> idsAreas);
}
