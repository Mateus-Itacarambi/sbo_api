package ifb.sbo.api.domain.solicitacao;


import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.tema.Tema;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long>, JpaSpecificationExecutor<Solicitacao> {
    @Query("select count(*) from Solicitacao s where s.tema.id = :id and s.status = :status")
    int countByTemaAndStatus(@Param("id") Long temaId, StatusSolicitacao status);

    @Query("select count(*) from Solicitacao s where s.tema.id = :temaId and s.professor.id = :professorId and s.status = :status")
    int countByIdTemaAndIdProfessor(@Param("temaId") Long temaId, @Param("professorId") Long professorId, StatusSolicitacao status);

    Page<Solicitacao> findAllByProfessor(Pageable paginacao, Professor professor);

    Page<Solicitacao> findAllByEstudante(Pageable paginacao, Estudante estudante);

    @Query("select s from Solicitacao s inner join Tema t on s.tema.id = t.id " +
            "                           inner join Professor p on s.professor.id = p.id" +
            "                           left join Estudante e on t.id = e.tema.id where e.id = :estudanteId")
    Page<Solicitacao> findAllByTemaEstudanteId(Pageable paginacao, @Param("estudanteId") Long estudanteId);

    List<Solicitacao> findAllByTemaAndStatus(Tema tema, StatusSolicitacao status);

    int  countByStatusAndProfessor (StatusSolicitacao status, Professor professor);

@Query("select count (s) from Solicitacao s inner join Tema t on s.tema.id = t.id" +
        "                                   left join Estudante e on t.id = e.tema.id where s.status = :status and e.id = :estudanteId")
    int countByStatusAndEstudante (StatusSolicitacao status, Long estudanteId);

    Optional<Solicitacao> findByTemaId(Long temaId);

    Page<Solicitacao> findAll(@Nullable Specification<Solicitacao> spec, Pageable pageable);

    Optional<Solicitacao> findByEstudanteIdAndProfessorIdAndTipoAndStatus(Long estudanteId, Long id, TipoSolicitacao tipo, StatusSolicitacao statusSolicitacao);

    Optional<Solicitacao> findByEstudanteIdAndTemaIdAndStatus(Long estudanteId, Long id, StatusSolicitacao statusSolicitacao);

    List<Solicitacao> findAllByEstudanteIdAndStatus(Long estudanteId, StatusSolicitacao statusSolicitacao);
}
