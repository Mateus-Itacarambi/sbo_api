package ifb.sbo.api.controller;


import ifb.sbo.api.domain.formacao.*;
import ifb.sbo.api.domain.professor.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
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

//    @PostMapping("/importar-professores")
//    @Transactional
//    public ResponseEntity<?> importarProfessores(@RequestParam("file") MultipartFile file) {
//        try {
//            List<Professor> professoresImportados = professorService.importarProfessores(file);
//            return ResponseEntity.ok(professoresImportados);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao importar professores: " + e.getMessage());
//        }
//    }

    @PostMapping("/importar-relatorio-csv")
    public ResponseEntity<Resource> importarProfessoresRelatorioCsv(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayResource csvRelatorio = professorService.importarProfessoresComRelatorioCsv(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_importacao.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvRelatorio.contentLength())
                    .body(csvRelatorio);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
