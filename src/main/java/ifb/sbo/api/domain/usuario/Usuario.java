package ifb.sbo.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Table(name = "usuario")
@Entity(name = "Usuario")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
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

    public Usuario(String nome, LocalDate dataNascimento, String genero, String email, String senha) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.email = email;
        this.senha = senha;
        this.dataCadastro = LocalDate.now();
    }
}
