package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class EstudanteService {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public EstudanteListagemDTO cadastrar(EstudanteCadastroDTO dados) {
        buscarCurso(dados.idCurso());
        buscarEmail(dados.email());
        buscarMatricula(dados.matricula());

        String senhaHasheada = passwordEncoder.encode(dados.senha());

        var curso = cursoRepository.getReferenceById(dados.idCurso());

        var estudante = new Estudante(dados, curso);
        estudante.setSenha(senhaHasheada);
        estudanteRepository.save(estudante);

        return mapearParaDTO(estudante);
    }

//    @Transactional
//    public void adicionarEstudanteAoTema(Long temaId, String matricula) {
//        var tema = temaService.buscarTema(temaId);
//
//        if (tema.getEstudantes().size() >= 2) {
//            throw new ConflitoException("Este tema já possui o número máximo de estudantes (2).");
//        }
//
//        var estudante = buscarEstudanteMatricula(matricula);
//
//        verificarTemaEstudante(estudante.getId());
//
//        estudante.setTema(tema);
//        tema.getEstudantes().add(estudante);
//
//        estudanteRepository.save(estudante);
//    }

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

    private void buscarEmail(String email) {
        if (estudanteRepository.countByEmail(email) != 0) {
            throw new ConflitoException("Email já cadastrado no sistema!");
        }
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

    private Estudante buscarEstudanteMatricula(String matricula) {
        return estudanteRepository.findByMatriculaAndAtivoTrue(matricula)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado!"));
    }

    public void verificarTemaEstudante(Long estudanteId) {
        var estudante = buscarEstudante(estudanteId);
        if (estudante.getTema() != null) {
            throw new ConflitoException("Este estudante já está associado a um tema.");
        }
    }

    public EstudanteListagemDTO mapearParaDTO(Estudante estudante) {
        return new EstudanteListagemDTO(
                estudante.getId(),
                estudante.getNome(),
                estudante.getDataNascimento(),
                estudante.getGenero(),
                estudante.getEmail(),
                estudante.getMatricula(),
                estudante.getSemestre(),
                new CursoDetalhaDTO(
                        estudante.getCurso().getNome()
                ),
                estudante.getTema() != null ? new TemaDetalhaDTO(
                        estudante.getTema().getTitulo(),
                        estudante.getTema().getDescricao(),
                        estudante.getTema().getPalavrasChave(),
                        estudante.getTema().getAreaConhecimento()) : null
        );
    }

}
