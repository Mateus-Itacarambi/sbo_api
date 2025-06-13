package ifb.sbo.api.domain.usuario;


import ifb.sbo.api.domain.estudante.*;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioListagemDTO cadastrar(UsuarioCadastroDTO dados) {
        buscarEmail(dados.email());

        Usuario admin = new Usuario(
                dados.nome(),
                dados.dataNascimento(),
                dados.genero(),
                dados.email(),
                dados.senha()
        );
        admin.setRole(TipoUsuario.ADMINISTRADOR);
        usuarioRepository.save(admin);

        return mapearParaDTO(admin);
    }

    public void redefinirSenha(String token, String novaSenha) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Usuario usuario = usuarioRepository.findByTokenRecuperacao(token)
                .orElseThrow(() -> new ConflitoException("Token inválido"));

        if (usuario.getDataExpiracaoToken().isBefore(LocalDateTime.now())) {
            throw new ConflitoException("Token expirado");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTokenRecuperacao(null);
        usuario.setDataExpiracaoToken(null);
        usuarioRepository.save(usuario);
    }

    public void alterarSenha(Usuario usuario, String senhaAtual, String novaSenha) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new ConflitoException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public UsuarioListagemDTO detalharUsuario(Long usuarioId) {
        Usuario usuario = buscarUsuario(usuarioId);
        return mapearParaDTO(usuario);
    }

    public Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    public void buscarEmail(String email) {
        if (usuarioRepository.countByEmail(email) != 0) {
            throw new ConflitoException("Email já cadastrado no sistema!");
        }
    }

    public void verificarUsuarioTema(Long usuarioId, Tema tema) {
        var usuario = buscarUsuario(usuarioId);

        if (usuario instanceof Professor professor) {
            if (tema.getProfessor() == null || !tema.getProfessor().getId().equals(professor.getId())) {
                throw new ConflitoException("Permissão negada: Você não é o criador deste tema.");
            }
        } else if (usuario instanceof Estudante estudante) {
            if (tema.getEstudantes() == null || tema.getEstudantes().stream().noneMatch(e -> e.getId().equals(estudante.getId()))) {
                throw new ConflitoException("Permissão negada: Você não é o criador deste tema.");
            }
        } else {
            throw new EntityNotFoundException("Tipo de usuário inválido.");
        }
    }

    public void verificarUsuarioSolicitacao(Usuario usuario, Solicitacao solicitacao) {
        if (usuario instanceof Professor professor) {
            if (solicitacao.getProfessor() == null || !solicitacao.getProfessor().getId().equals(professor.getId())) {
                throw new ConflitoException("Você não está associado a esta solicitação.");
            }
        } else if (usuario instanceof Estudante estudante) {
            if (solicitacao.getEstudante() == null || !solicitacao.getEstudante().getId().equals(estudante.getId())) {
                throw new ConflitoException("Somente o dono da solicitação pode cancelar!");
            }
        } else {
            throw new EntityNotFoundException("Tipo de usuário inválido.");
        }
    }

    public void permissaoRejeitar(Usuario usuario) {
        if (usuario instanceof Estudante) {
            throw new ConflitoException("Você não tem permissão para rejeitar essa solicitação!");
        }
    }

    public void permissaoAprovar(Usuario usuario) {
        if (usuario instanceof Estudante) {
            throw new ConflitoException("Você não tem permissão para aprovar essa solicitação!");
        }
    }

    private UsuarioListagemDTO mapearParaDTO(Usuario usuario) {
        return new UsuarioListagemDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getDataNascimento(),
                usuario.getGenero(),
                usuario.getEmail(),
                usuario.getRole().toString(),
                usuario.getAtivo(),
                usuario.getCadastroCompleto()
        );
    }
}
