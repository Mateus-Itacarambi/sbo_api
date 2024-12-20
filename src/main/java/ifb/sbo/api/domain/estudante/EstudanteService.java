package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstudanteService {
    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public EstudanteListagemDTO cadastrar(EstudanteCadastroDTO dados) {
        buscarCurso(dados.idCurso());
        buscarEmail(dados.email());
        buscarMatricula(dados.matricula());

        var curso = cursoRepository.getReferenceById(dados.idCurso());

        var cadastro = new Estudante(dados, curso);
        estudanteRepository.save(cadastro);

        return new EstudanteListagemDTO(cadastro);
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado."));
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
}
