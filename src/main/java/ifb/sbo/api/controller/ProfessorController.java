package ifb.sbo.api.controller;


import ifb.sbo.api.domain.area_interesse.AreaInteresseListagemDTO;
import ifb.sbo.api.domain.formacao.FormacaoAtualizaDTO;
import ifb.sbo.api.domain.formacao.FormacaoCadastroDTO;
import ifb.sbo.api.domain.formacao.FormacaoListagemDTO;
import ifb.sbo.api.domain.formacao.FormacaoRepository;
import ifb.sbo.api.domain.professor.*;
import ifb.sbo.api.domain.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("professores")
public class ProfessorController {
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private FormacaoRepository formacaoRepository;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid ProfessorCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var professor = professorService.cadastrar(dados);
        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professor.id()).toUri();

        return ResponseEntity.created(uri).body(professor);
    }

    @GetMapping
    public Page<ProfessorBuscaDTO> listarProfessores(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) List<String> curso,
            @RequestParam(required = false) List<String> disponibilidade,
            @RequestParam(required = false) List<String> areaInteresse,
            Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
            ) {
        FiltroProfessor filtro = new FiltroProfessor(nome, curso, disponibilidade, areaInteresse);
        return professorService.listarProfessoresComFiltros(filtro, pageable, usuario);
    }

    @GetMapping("/lista")
    public List<ProfessorListaDTO> listarAtivas() {
        return professorRepository.findByAtivoTrueAndCadastroCompletoTrue().stream()
                .map(professor -> new ProfessorListaDTO(professor.getId(), professor.getNome()))
                .sorted(Comparator.comparing(ProfessorListaDTO::nome))
                .toList();
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid ProfessorAtualizaDTO dados) {
        var professor = professorRepository.getReferenceById(dados.id());
        professor.atualizarInformacoes(dados);
        return ResponseEntity.ok(professorService.detalharProfessor(professor.getId()));
    }

    @PutMapping("/atualizar-cadastro")
    public ResponseEntity<?> atualizarCadastro(@RequestBody ProfessorAtualizaCadastroDTO dados) {
        var professor = professorService.atualizarCadastro(dados);
        return ResponseEntity.ok(professor);
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

//    @PostMapping("/{professorId}/adicionarAreaInteresse/{areaInteresseId}")
//    public ResponseEntity<ProfessorListagemDTO> adicionarAreaDeInteresse(@PathVariable Long professorId, @PathVariable Long areaInteresseId, UriComponentsBuilder uriBuilder) {
//        professorService.adicionarAreaInteresse(professorId, areaInteresseId);
//
//        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
//        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
//    }

    @PostMapping("/{professorId}/adicionarAreasInteresse")
    public ResponseEntity<ProfessorListagemDTO> adicionarAreasInteresse(
            @PathVariable Long professorId,
            @RequestBody List<Long> idsAreas
    ) {
        professorService.adicionarAreasInteresse(professorId, idsAreas);
        return ResponseEntity.ok(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerAreaInteresse/{areaInteresseId}")
    public ResponseEntity removerAreaInteresse(@PathVariable Long professorId, @PathVariable Long areaInteresseId) {
        professorService.removerAreaInteresse(professorId, areaInteresseId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{professorId}/adicionarCursos")
    public ResponseEntity<ProfessorListagemDTO> adicionarCursos(
            @PathVariable Long professorId,
            @RequestBody List<Long> idsCursos
    ) {
        professorService.adicionarCursos(professorId, idsCursos);
        return ResponseEntity.ok(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerCurso/{cursoId}")
    public ResponseEntity removerCurso(@PathVariable Long professorId, @PathVariable Long cursoId) {
        professorService.removerCurso(professorId, cursoId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{professorId}/adicionarFormacoes")
    public ResponseEntity<ProfessorListagemDTO> adicionarFormacao(@PathVariable Long professorId, @RequestBody @Valid FormacaoCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        professorService.adicionarFormacao(professorId, dados);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professorService.detalharProfessor(professorId));
    }

    @DeleteMapping("/{professorId}/removerFormacoes/{formacaoId}")
    public ResponseEntity removerFormacao(@PathVariable Long professorId, @PathVariable Long formacaoId) {
        professorService.removerFormacao(professorId, formacaoId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{professorId}/atualizarFormacoes/{formacaoId}")
    @Transactional
    public ResponseEntity<FormacaoListagemDTO> atualizarFormacao(@PathVariable Long professorId, @PathVariable Long formacaoId,  @RequestBody @Valid FormacaoAtualizaDTO dados) {
        return ResponseEntity.ok(professorService.atualizarFormacao(professorId, formacaoId, dados));
    }

    @PostMapping("/importar-relatorio-csv")
    public ResponseEntity<Resource> importarProfessoresRelatorioCsv(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayResource csvRelatorio = professorService.importarProfessoresComRelatorioCsv(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_importacao_professores.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvRelatorio.contentLength())
                    .body(csvRelatorio);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
