package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import ifb.sbo.api.domain.area_interesse.AreaInteresseDetalhaDTO;
import ifb.sbo.api.domain.area_interesse.AreaInteresseRepository;
import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.formacao.Formacao;
import ifb.sbo.api.domain.formacao.FormacaoCadastroDTO;
import ifb.sbo.api.domain.formacao.FormacaoDetalhaDTO;
import ifb.sbo.api.domain.formacao.FormacaoRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AreaInteresseRepository areaInteresseRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private FormacaoRepository formacaoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public ProfessorListagemDTO cadastrar(ProfessorCadastroDTO dados) {
        usuarioService.buscarEmail(dados.email());
        buscarLattes(dados.idLattes());

        var professor = new Professor(dados);
        professor.setRole(TipoUsuario.PROFESSOR);
        professorRepository.save(professor);

        return mapearParaDTO(professor);
    }

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
            throw new ConflitoException("Esta área de interesse já foi removida ao professor.");
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
            throw new ConflitoException("Este curso já foi removida ao professor.");
        }

        professor.getCursos().remove(curso);
    }

    @Transactional
    public void adicionarFormacao(Long professorId, FormacaoCadastroDTO dados) {
        Professor professor = buscarProfessor(professorId);
        Formacao formacao = new Formacao(dados);
        formacao.setProfessor(professor);
        formacaoRepository.save(formacao);
    }

    @Transactional
    public void removerFormacao(Long professorId, Long formacaoId) {
        Professor professor = buscarProfessor(professorId);
        Formacao formacao = buscarFormacao(formacaoId);

        if (!professor.getFormacoes().contains(formacao)) {
            throw new ConflitoException("Esta formação já foi removida ao professor.");
        }

        professor.getFormacoes().remove(formacao);
        formacao.setProfessor(null);

        professorRepository.save(professor);

        formacaoRepository.deleteById(formacaoId);
    }

    public Page<ProfessorListagemDTO> listarProfessoresPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return professorRepository.findAllByAtivoTrue(paginacao)
                .map(this::mapearParaDTO);
    }

    public ProfessorListagemDTO detalharProfessor(Long professorId) {
        Professor professor = buscarProfessor(professorId);
        return mapearParaDTO(professor);
    }

    public Professor buscarProfessor(Long professorId) {
        return professorRepository.findByIdAndAtivoTrue(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
    }

    private AreaInteresse buscarAreaInteresse(Long areaInteresseId) {
        return areaInteresseRepository.findByIdAndAtivoTrue(areaInteresseId)
                .orElseThrow(() -> new EntityNotFoundException("Área de interesse não encontrada."));
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado."));
    }

    private Formacao buscarFormacao(Long formacaoId) {
        return formacaoRepository.findById(formacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Formação não encontrada."));
    }

    public void buscarLattes(String idLattes) {
        if (professorRepository.countByIdLattes(idLattes) != 0) {
            throw new ConflitoException("Lattes já cadastrado no sistema!");
        }
    }

    private ProfessorListagemDTO mapearParaDTO(Professor professor) {
        List<CursoDetalhaDTO> cursosDTO = professor.getCursos()
                .stream()
                .map(curso -> new CursoDetalhaDTO(
                        curso.getId(),
                        curso.getNome())).toList();

        List<AreaInteresseDetalhaDTO> areasInteresseDTO = professor.getAreasInteresse()
                .stream()
                .map(areaInteresse -> new AreaInteresseDetalhaDTO(
                        areaInteresse.getNome())).toList();

        List<FormacaoDetalhaDTO> formacoesDTO = professor.getFormacoes()
                .stream()
                .map(formacao -> new FormacaoDetalhaDTO(
                        formacao.getCurso(),
                        formacao.getModalidade(),
                        formacao.getFaculdade(),
                        formacao.getTitulo(),
                        formacao.getAnoInicio(),
                        formacao.getAnoFim())).toList();

        List<TemaDetalhaDTO> temasDTO = professor.getTemas()
                .stream()
                .map(tema -> new TemaDetalhaDTO(
                        tema.getId(),
                        tema.getTitulo(),
                        tema.getDescricao(),
                        tema.getPalavrasChave(),
                        tema.getStatus().getDescricao(),
                        tema.getEstudantes().stream()
                                .map(estudante -> new EstudanteDetalhaDTO(
                                        estudante.getId(),
                                        estudante.getNome()
                                ))
                                .toList(),
                        new ProfessorDetalhaDTO(
                                tema.getProfessor().getId(),
                                tema.getProfessor().getNome()
                        ))).toList();


        return new ProfessorListagemDTO(
                professor.getId(),
                professor.getNome(),
                professor.getDataNascimento(),
                professor.getGenero(),
                professor.getEmail(),
                professor.getIdLattes(),
                String.valueOf(professor.getDisponibilidade()),
                cursosDTO,
                areasInteresseDTO,
                formacoesDTO,
                temasDTO
        );
    }
}


