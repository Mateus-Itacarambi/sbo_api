package ifb.sbo.api.domain.curso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface CursoRepository extends JpaRepository<Curso, Long> {
    Page<Curso> findAllByAtivoTrue(Pageable paginacao);

    @Query("select c.ativo FROM Curso c where c.id = :idCurso")
    boolean cursoEstaAtivo(Long idCurso);

    Optional<Curso> findByIdAndAtivoTrue(Long cursoId);
}
