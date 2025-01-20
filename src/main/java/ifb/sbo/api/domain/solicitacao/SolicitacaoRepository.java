package ifb.sbo.api.domain.solicitacao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {
    @Query("select count(*) from Solicitacao s where s.tema.id = :id")
    int countByIdTema(@Param("id") Long temaId);
}
