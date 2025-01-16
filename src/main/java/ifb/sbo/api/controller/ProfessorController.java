package ifb.sbo.api.controller;


import ifb.sbo.api.domain.estudante.EstudanteListagemDTO;
import ifb.sbo.api.domain.formacao.*;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.tema.TemaCadastroDTO;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("professores")
public class ProfessorController {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private FormacaoRepository formacaoRepository;


    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid ProfessorCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        if(professorRepository.countByEmail(dados.email()) != 0) {
            throw new ConflitoException("Email já cadastrado no sistema!");
        }

        String senhaHasheada = passwordEncoder.encode(dados.senha());

        var professor = new Professor(dados);
        professor.setSenha(senhaHasheada);
        professorRepository.save(professor);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professor.getId()).toUri();

        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professor.getId()));
    }

    @GetMapping
    public ResponseEntity<Page<ProfessorListagemDTO>> listar(Pageable paginacao) {
        Page<ProfessorListagemDTO> professores = professorService.listarProfessoresPaginados(paginacao);
        return ResponseEntity.ok(professores);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid ProfessorAtualizaDTO dados) {
        var professor = professorRepository.getReferenceById(dados.id());
        professor.atualizarInformacoes(dados);
        return ResponseEntity.ok(professorService.detalharProfessor(professor.getId()));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id) {
        var professor = professorRepository.getReferenceById(id);
        professor.desativar();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorListagemDTO> detalhar(@PathVariable Long id) {
        ProfessorListagemDTO professor = professorService.detalharProfessor(id);
        return ResponseEntity.ok(professor);
    }

    @PostMapping("/{professorId}/adicionarAreaInteresse/{areaInteresseId}")
    public ResponseEntity<ProfessorListagemDTO> adicionarAreaDeInteresse(@PathVariable Long professorId, @PathVariable Long areaInteresseId, UriComponentsBuilder uriBuilder) {
        professorService.adicionarAreaInteresse(professorId, areaInteresseId);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerAreaInteresse/{areaInteresseId}")
    public ResponseEntity removerAreaInteresse(@PathVariable Long professorId, @PathVariable Long areaInteresseId) {
        professorService.removerAreaInteresse(professorId, areaInteresseId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{professorId}/adicionarCurso/{cursoId}")
    public ResponseEntity<ProfessorListagemDTO> adicionarCurso(@PathVariable Long professorId, @PathVariable Long cursoId, UriComponentsBuilder uriBuilder) {
        professorService.adicionarCurso(professorId, cursoId);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerCurso/{cursoId}")
    public ResponseEntity removerCurso(@PathVariable Long professorId, @PathVariable Long cursoId) {
        professorService.removerCurso(professorId, cursoId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{professorId}/adicionarFormacoes")
    public ResponseEntity<ProfessorListagemDTO> adicionarFormacao(@PathVariable Long professorId, @RequestBody FormacaoCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        professorService.adicionarFormacao(professorId, dados);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerFormacoes/{formacaoId}")
    public ResponseEntity removerFormacao(@PathVariable Long professorId, @PathVariable Long formacaoId) {
        professorService.removerFormacao(professorId, formacaoId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/atualizarFormacoes/{formacaoId}")
    @Transactional
    public ResponseEntity<FormacaoListagemDTO> atualizarFormacao(@PathVariable Long formacaoId,  @RequestBody FormacaoAtualizaDTO dados) {
        var formacao = formacaoRepository.getReferenceById(formacaoId);
        formacao.atualizarFormacao(dados);

        return ResponseEntity.ok(new FormacaoListagemDTO(formacao));
    }

//    @PostMapping("/{professorId}/cadastrarTema")
//    public ResponseEntity<ProfessorListagemDTO> cadastrarTema(@PathVariable Long professorId, @RequestBody TemaCadastroDTO dados, UriComponentsBuilder uriBuilder) {
//        professorService.cadastrarTema(professorId, dados);
//
//        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
//
//        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
//    }
}
