package ifb.sbo.api.domain.estudante;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    Page<Estudante> findAllByAtivoTrue(Pageable paginacao);

    int countByEmail(String email);

    int countByMatricula(String matricula);
}
