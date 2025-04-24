package ifb.sbo.api.domain.usuario;

import ifb.sbo.api.domain.estudante.Estudante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Usuario findByEmailAndAtivoTrue(String email);

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmailUsuario(String email);

    int countByEmail(String email);
}
