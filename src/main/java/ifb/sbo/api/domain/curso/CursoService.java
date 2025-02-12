package ifb.sbo.api.domain.curso;

import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    public CursoListagemDTO cadastrar(CursoCadastroDTO dados) {
        buscarCursoNome(dados.nome());

        var curso = new Curso(dados);
        cursoRepository.save(curso);

        return mapearParaDTO(curso);
    }

    public CursoListagemDTO detalhar(Long cursoId) {
        var curso = buscarCurso(cursoId);
        return mapearParaDTO(curso);
    }

    public Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrada!"));
    }

    public void buscarCursoNome(String nome) {
        if (cursoRepository.countByNomeAndAtivoTrue(nome) != 0) {
            throw new ConflitoException("Este curso já existe!");
        }
    }

    private CursoListagemDTO mapearParaDTO(Curso curso) {
        return new CursoListagemDTO(curso);
    }
}
