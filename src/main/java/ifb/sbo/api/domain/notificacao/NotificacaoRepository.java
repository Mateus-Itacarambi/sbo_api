package ifb.sbo.api.domain.notificacao;

import ifb.sbo.api.domain.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao>findByDestinatarioAndLidaFalseOrderByDataCriacaoDesc(Usuario destinatario);
}
