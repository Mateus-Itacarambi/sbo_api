package ifb.sbo.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Table(name = "usuario")
@Entity(name = "Usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;
    protected String nome;
    @Column(name = "data_nascimento")
    protected LocalDate dataNascimento;
    protected String genero;
    @Column(unique=true)
    protected String email;
    protected String senha;
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoUsuario role;
    protected Boolean ativo;
    @Column(name = "cadastro_completo")
    protected Boolean cadastroCompleto;
    @Column(name = "token_recuperacao")
    private String tokenRecuperacao;
    @Column(name = "data_expiracao_token")
    private LocalDateTime dataExpiracaoToken;

    public Usuario(String nome, LocalDate dataNascimento, String genero, String email, String senha) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.email = email;
        this.senha = passwordEncoder.encode(senha);
        this.dataCadastro = LocalDate.now();
        this.ativo = true;
        this.cadastroCompleto = true;
    }

    public void atualizarInformacoes(UsuarioAtualizaDTO dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }

        if (dados.dataNascimento() != null) {
            this.dataNascimento = dados.dataNascimento();
        }

        if (dados.genero() != null) {
            this.genero = dados.genero();
        }

        if (dados.email() != null) {
            this.email = dados.email();
        }

        if (dados.senha() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            this.senha = passwordEncoder.encode(dados.senha());
        }
    }

    public void desativar() {
        this.ativo = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

}
