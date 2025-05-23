package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.formacao.Formacao;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.tema.Tema;
import ifb.sbo.api.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Table(name = "professor")
@Entity(name = "Professor")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Professor extends Usuario {
    private String idLattes;
    @Enumerated(value = EnumType.STRING)
    private Disponibilidade disponibilidade;
    @UniqueElements
    @ManyToMany
    @JoinTable(
            name = "curso_professor",
            joinColumns = @JoinColumn(name = "id_professor"),
            inverseJoinColumns = @JoinColumn(name = "id_curso")
    )
    private List<Curso> cursos = new ArrayList<>();
    @UniqueElements
    @ManyToMany
    @JoinTable(
            name = "area_interesse_professor",
            joinColumns = @JoinColumn(name = "id_professor"),
            inverseJoinColumns = @JoinColumn(name = "id_area_interesse")
    )
    private List<AreaInteresse> areasInteresse = new ArrayList<>();
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Formacao> formacoes = new ArrayList<>();
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL)
    private List<Tema> temas = new ArrayList<>();
    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Solicitacao> solicitacoes = new ArrayList<>();

    public Professor(ProfessorCadastroDTO dados) {
        super(dados.nome(), dados.dataNascimento(), dados.genero(), dados.email(), dados.senha());
        this.idLattes = dados.idLattes();
        this.disponibilidade = Disponibilidade.DISPONIVEL;
    }

    public void atualizarInformacoes(ProfessorAtualizaDTO dados) {
        if (dados.nome() != null) {
            super.nome = dados.nome();
        }

        if (dados.dataNascimento() != null) {
            super.dataNascimento = dados.dataNascimento();
        }

        if (dados.genero() != null) {
            super.genero = dados.genero();
        }

//        if (dados.email() != null) {
//            super.email = dados.email();
//        }
//
//        if (dados.senha() != null) {
//            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//            super.senha = passwordEncoder.encode(dados.senha());
//        }

        if (dados.idLattes() != null) {
            this.idLattes = dados.idLattes();
        }
    }

    public List<String> getAreasInteresseString() {
        return getAreasInteresse()
                .stream()
                .map(AreaInteresse::getNome)
                .toList();
    }

    public List<String> getCursosString() {
        return getCursos()
                .stream()
                .map(Curso::getNome)
                .toList();
    }

    public List<String> getFormacoesString() {
        return getFormacoes()
                .stream()
                .map(Formacao::getCurso)
                .toList();
    }

    public void desativar() {
        super.ativo = false;
    }
}
