package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.tema.Tema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    @Query("SELECT e FROM Estudante e LEFT JOIN FETCH e.tema WHERE e.ativo = true")
    Page<Estudante> findAllByAtivoTrue(Pageable paginacao);

    int countByEmail(String email);

    int countByMatricula(String matricula);

    @Query("SELECT e FROM Estudante e LEFT JOIN FETCH e.tema WHERE e.id = :estudanteId AND e.ativo = true")
    Optional<Estudante> findByIdAndAtivoTrue(Long estudanteId);

    Optional<Estudante> findByMatriculaAndAtivoTrue(String matricula);

    List<Estudante> findAllByTema(Tema tema);

    Optional<Estudante> findByEmailAndAtivoTrue(String email);
}
