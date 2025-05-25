package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.domain.curso.CursoService;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.domain.usuario.TipoUsuario;
import ifb.sbo.api.domain.usuario.UsuarioService;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.util.Objects;


@Service
public class EstudanteService {
    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CursoService cursoService;

    public EstudanteListagemDTO cadastrar(EstudanteCadastroDTO dados) {
        if (!dados.isMaiorDeIdade()) {
            throw new ConflitoException("É necessário ter pelo menos 18 anos!");
        }

        buscarCurso(dados.idCurso());
        usuarioService.buscarEmail(dados.email());
        buscarMatricula(dados.matricula());

        var curso = cursoRepository.getReferenceById(dados.idCurso());

        var estudante = new Estudante(dados, curso);
//        var estudante = new Estudante(dados);
        estudante.setRole(TipoUsuario.ESTUDANTE);
        estudanteRepository.save(estudante);

        return mapearParaDTO(estudante);
    }

    public EstudanteListagemDTO atualizar(EstudanteAtualizaDTO dados) {
        var estudante = estudanteRepository.getReferenceById(dados.id());
        if (!dados.isMaiorDeIdade()) {
            throw new ConflitoException("É necessário ter pelo menos 18 anos!");
        }

//        if (!Objects.equals(estudante.getEmail(), dados.email())) {
//            usuarioService.buscarEmail(dados.email());
//        }

        if (!Objects.equals(estudante.getMatricula(), dados.matricula())) {
            buscarMatricula(dados.matricula());
        }

        var curso = cursoService.buscarCurso(dados.curso());
        estudante.atualizarInformacoes(dados, curso);

        return mapearParaDTO(estudante);
    }

    public Page<EstudanteListagemDTO> listarEstudantesPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return estudanteRepository.findAllByAtivoTrue(paginacao)
                .map(this::mapearParaDTO);
    }

    public EstudanteListagemDTO detalharEstudante(Long estudanteId) {
        var estudante = buscarEstudante(estudanteId);
        return mapearParaDTO(estudante);
    }

    public EstudanteResumoDTO resumoEstudante(Long estudanteId) {
        var estudante = buscarEstudante(estudanteId);
        return mapearParaResumoDTO(estudante);
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado!"));
    }

    private void buscarMatricula(String matricula) {
        if (estudanteRepository.countByMatricula(matricula) != 0) {
            throw new ConflitoException("Matricula já cadastrado no sistema!");
        }
    }

    public Estudante buscarEstudante(Long estudanteId) {
        return estudanteRepository.findByIdAndAtivoTrue(estudanteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado!"));
    }

    public Estudante buscarEstudanteMatricula(String matricula) {
        return estudanteRepository.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado!"));
    }

    public void verificarTemaEstudante(Long estudanteId) {
        var estudante = buscarEstudante(estudanteId);
        if (estudante.getTema() != null) {
            throw new ConflitoException("Este estudante já está associado a um tema.");
        }
    }

    public void estudanteTemTema(Estudante estudante) {
        if (estudante.getTema() == null) {
            throw new ConflitoException("Este estudante precisa ser associado a um tema.");
        }
    }

    public EstudanteResumoDTO mapearParaResumoDTO(Estudante estudante) {
        return new EstudanteResumoDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getRole().toString(),
                estudante.getAtivo(),
                estudante.getCadastroCompleto(),
                estudante.getMatricula()
        );
    }

    public EstudanteListagemDTO mapearParaDTO(Estudante estudante) {
        return new EstudanteListagemDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getDataNascimento(),
                estudante.getGenero(),
                estudante.getEmail(),
                estudante.getRole().toString(),
                estudante.getAtivo(),
                estudante.getCadastroCompleto(),
                estudante.getMatricula(),
                estudante.getSemestre() != null ? estudante.getSemestre() : null,
                estudante.getCurso() != null ? new CursoDetalhaDTO(
                        estudante.getCurso().getId(),
                        estudante.getCurso().getNome(),
                        estudante.getCurso().getSlug()
                ) : null,
                estudante.getTema() != null ? new TemaDetalhaDTO(
                        estudante.getTema().getId(),
                        estudante.getTema().getTitulo(),
                        estudante.getTema().getDescricao(),
                        estudante.getTema().getPalavrasChave(),
                        String.valueOf(estudante.getTema().getStatus()),
                        estudante.getTema().getEstudantes().stream()
                                .map(estudante1 -> new EstudanteDetalhaDTO(
                                        estudante1.getId(),
                                        estudante1.getNome()))
                                .toList(),
                        estudante.getTema().getProfessor() != null ? new ProfessorDetalhaDTO(
                                estudante.getTema().getProfessor().getId(),
                                estudante.getTema().getProfessor().getNome()
                        ) : null) : null
        );
    }

}
