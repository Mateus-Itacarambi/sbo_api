package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.professor.Professor;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemaRepository extends JpaRepository<Tema, Long>, JpaSpecificationExecutor<Tema> {
    @Query("SELECT t FROM Tema t WHERE t.status = 'Disponível'")
    Page<Tema> findAllByStatusDisponivel(Pageable paginacao);

    Page<Tema> findAllByProfessor_Id(Long id, Pageable paginacao);

    int countByTitulo(String titulo);

    @Query("SELECT t FROM Tema t JOIN FETCH t.estudantes WHERE t.id = :id")
    Optional<Tema> findByIdWithEstudantes(@Param("id") Long id);

    @Override
    @Query("SELECT t FROM Tema t WHERE t.id = :id")
    Optional<Tema> findById(@Param("id") Long id);

    Page<Tema> findAll(@Nullable Specification<Tema> spec, Pageable pageable);

    Optional<Tema> findByIdAndStatus(Long temaId, StatusTema statusTema);
}
