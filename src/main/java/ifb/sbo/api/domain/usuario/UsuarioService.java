package ifb.sbo.api.domain.usuario;


import ifb.sbo.api.domain.estudante.*;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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

    public void verificarUsuarioSolicitacao(Long usuarioId, Solicitacao solicitacao) {
        var usuario = buscarUsuario(usuarioId);

        if (usuario instanceof Professor professor) {
            if (solicitacao.getProfessor() == null || !solicitacao.getProfessor().getId().equals(professor.getId())) {
                throw new ConflitoException("Permissão negada: Você não está associado a esta solicitação.");
            }
        } else if (usuario instanceof Estudante estudante) {
            if (solicitacao.getTema().getEstudantes() == null || solicitacao.getTema().getEstudantes().stream().noneMatch(e -> e.getId().equals(estudante.getId()))) {
                throw new ConflitoException("Permissão negada: Você não está associado a esta solicitação.");
            }
        } else {
            throw new EntityNotFoundException("Tipo de usuário inválido.");
        }
    }

    public void permissaoRejeitar(Long usuarioId) {
        var usuario = buscarUsuario(usuarioId);

        if (usuario instanceof Estudante) {
            throw new ConflitoException("Você não tem permissão para rejeitar essa solicitação!");
        }
    }

    public void permissaoAprovar(Long usuarioId) {
        var usuario = buscarUsuario(usuarioId);

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
