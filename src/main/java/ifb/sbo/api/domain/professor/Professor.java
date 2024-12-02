package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.usuario.Usuario;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

@Entity
public class Professor extends Usuario {
    private String areaInteresse;
    private String formacaoPrincipal;
    private String titulacao;
    @Enumerated(value = EnumType.STRING)
    private Disponibilidade disponibilidade;

    public Professor() {}

    public Professor(String nome, LocalDate dataNascimento, String genero, String email, String senha, String areaInteresse, String formacaoPrincipal, String titulacao, Disponibilidade disponibilidade) {
        super(nome, dataNascimento, genero, email, senha);
        this.areaInteresse = areaInteresse;
        this.formacaoPrincipal = formacaoPrincipal;
        this.titulacao = titulacao;
        this.disponibilidade = disponibilidade;
    }
}
