package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import ifb.sbo.api.domain.area_interesse.AreaInteresseRepository;
import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private AreaInteresseRepository areaInteresseRepository;
    @Autowired
    private CursoRepository cursoRepository;

    @Transactional
    public void adicionarAreaInteresse(Long professorId, Long areaInteresseId) {
        Professor professor = buscarProfessor(professorId);
        AreaInteresse areaInteresse = buscarAreaInteresse(areaInteresseId);

        if (professor.getAreasInteresse().contains(areaInteresse)) {
            throw new ConflitoException("Esta área de interesse já foi adicionada ao professor.");
        }

        professor.getAreasInteresse().add(areaInteresse);
    }

    @Transactional
    public void removerAreaInteresse(Long professorId, Long areaInteresseId) {
        Professor professor = buscarProfessor(professorId);
        AreaInteresse areaInteresse = buscarAreaInteresse(areaInteresseId);

        if (!professor.getAreasInteresse().contains(areaInteresse)) {
            throw new ConflitoException("Esta área de interesse já foi removida");
        }

        professor.getAreasInteresse().remove(areaInteresse);
    }

    @Transactional
    public void adicionarCurso(Long professorId, Long cursoId) {
        Professor professor = buscarProfessor(professorId);
        Curso curso = buscarCurso(cursoId);

        if (professor.getCursos().contains(curso)) {
            throw new ConflitoException("Este curso já foi adicionado ao professor.");
        }

        professor.getCursos().add(curso);
    }

    @Transactional
    public void removerCurso(Long professorId, Long cursoId) {
        Professor professor = buscarProfessor(professorId);
        Curso curso = buscarCurso(cursoId);

        if (!professor.getCursos().contains(curso)) {
            throw new ConflitoException("Este curso já foi adicionado ao professor.");
        }

        professor.getCursos().remove(curso);
    }

    public Page<ProfessorListagemDTO> listarProfessoresPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return professorRepository.findAllByAtivoTrue(paginacao)
                .map(this::mapearParaDTO);
    }

    public ProfessorListagemDTO detalharProfessor(Long professorId) {
        Professor professor = buscarProfessor(professorId);
        return mapearParaDTO(professor);
    }

    private Professor buscarProfessor(Long professorId) {
        return professorRepository.findByIdAndAtivoTrue(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
    }

    private AreaInteresse buscarAreaInteresse(Long areaInteresseId) {
        return areaInteresseRepository.findById(areaInteresseId)
                .orElseThrow(() -> new EntityNotFoundException("Área de interesse não encontrada."));
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado."));
    }

    private ProfessorListagemDTO mapearParaDTO(Professor professor) {
        return new ProfessorListagemDTO(
                professor.getId(),
                professor.getNome(),
                professor.getDataNascimento(),
                professor.getGenero(),
                professor.getEmail(),
                professor.getIdLattes(),
                String.valueOf(professor.getDisponibilidade()),
                professor.getCursosString(),
                professor.getAreasInteresseString()
        );
    }
}


