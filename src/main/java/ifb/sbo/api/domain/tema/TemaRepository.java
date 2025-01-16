package ifb.sbo.api.domain.tema;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TemaRepository extends JpaRepository<Tema, Long> {
    @Query("SELECT t FROM Tema t WHERE t.status = 'Disponível'")
    Page<Tema> findAllByStatusDisponivel(Pageable paginacao);

    int countByTitulo(String titulo);

    Optional<Tema> findByIdAndAtivoTrue(Long temaId);

    Optional<Tema> findByTituloAndAtivoTrue(String titulo);

    int countByTituloAndAtivoTrue(String titulo);

    @Query("SELECT t FROM Tema t JOIN FETCH t.estudantes WHERE t.id = :id")
    Optional<Tema> findByIdWithEstudantes(@Param("id") Long id);

    @Override
    @Query("SELECT t FROM Tema t WHERE t.id = :id")
    Optional<Tema> findById(@Param("id") Long id);

}
