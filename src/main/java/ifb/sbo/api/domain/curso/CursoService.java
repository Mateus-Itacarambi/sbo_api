package ifb.sbo.api.domain.curso;

import ifb.sbo.api.domain.professor.ProfessorBuscaDTO;
import ifb.sbo.api.domain.professor.ProfessorCursoDTO;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void buscarCursoSigla(String sigla) {
        if (cursoRepository.countBySiglaAndAtivoTrue(sigla) != 0) {
            throw new ConflitoException("Esta sigla já existe!");
        }
    }

    public CursoListagemDTO mapearParaDTO(Curso curso) {
        List<ProfessorCursoDTO> professoresDTO = curso.getProfessores()
                .stream()
                .map(p -> new ProfessorCursoDTO(
                        p.getId(),
                        p.getNome(),
                        p.getEmail(),
                        String.valueOf(p.getDisponibilidade()))).toList();

        return new CursoListagemDTO(
                curso.getId(),
                curso.getNome(),
                curso.getSigla(),
                curso.getDescricao(),
                curso.getSemestres(),
                curso.getSlug(),
                curso.getProfessores() != null ? professoresDTO : null
        );
    }
}
