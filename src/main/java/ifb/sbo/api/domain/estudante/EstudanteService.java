package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoRepository;
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


@Service
public class EstudanteService {
    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public EstudanteListagemDTO cadastrar(EstudanteCadastroDTO dados) {
        if (!dados.isMaiorDeIdade()) {
            throw new ConflitoException("É necessário ter pelo menos 18 anos para se cadastrar!");
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

    public Page<EstudanteListagemDTO> listarEstudantesPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return estudanteRepository.findAllByAtivoTrue(paginacao)
                .map(this::mapearParaDTO);
    }

    public EstudanteListagemDTO detalharEstudante(Long estudanteId) {
        var estudante = buscarEstudante(estudanteId);
        return mapearParaDTO(estudante);
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

    public EstudanteListagemDTO mapearParaDTO(Estudante estudante) {
        return new EstudanteListagemDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getDataNascimento(),
                estudante.getGenero(),
                estudante.getEmail(),
                estudante.getRole().toString(),
                estudante.getMatricula(),
                estudante.getSemestre() != null ? estudante.getSemestre() : null,
                estudante.getCurso() != null ? new CursoDetalhaDTO(
                        estudante.getCurso().getId(),
                        estudante.getCurso().getNome()
                ) : null,
                estudante.getTema() != null ? new TemaDetalhaDTO(
                        estudante.getTema().getId(),
                        estudante.getTema().getTitulo(),
                        estudante.getTema().getDescricao(),
                        estudante.getTema().getPalavrasChave(),
                        estudante.getTema().getAreaConhecimento(),
                        estudante.getTema().getStatus().getDescricao()) : null
        );
    }

}
