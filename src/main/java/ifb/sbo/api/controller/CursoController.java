package ifb.sbo.api.controller;

import ifb.sbo.api.domain.area_interesse.AreaInteresseListagemDTO;
import ifb.sbo.api.domain.curso.*;
import ifb.sbo.api.infra.service.SlugUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("cursos")
@CrossOrigin("http://localhost:3000/")
public class CursoController {
    @Autowired
    private CursoRepository repository;

    @Autowired
    private CursoService cursoService;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid CursoCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var curso = cursoService.cadastrar(dados);
        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.id()).toUri();

        return ResponseEntity.created(uri).body(curso);
    }

//    @GetMapping
//    public ResponseEntity<Page<CursoListagemDTO>> listar(@PageableDefault(sort = {"nome"}) Pageable paginacao) {
//        var page = repository.findAllByAtivoTrue(paginacao).map(CursoListagemDTO::new);
//        return ResponseEntity.ok(page);
//    }

    @GetMapping("/lista")
    public List<CursoListagemProfessorDTO> listarAtivas() {
        return repository.findByAtivoTrue().stream()
                .map(curso -> new CursoListagemProfessorDTO(curso.getId(), curso.getNome(), curso.getSemestres()))
                .sorted(Comparator.comparing(CursoListagemProfessorDTO::nome))
                .toList();
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid CursoAtualizaDTO dados) {
        var curso = repository.getReferenceById(dados.id());
        curso.atualizarInformacoes(dados);
        return ResponseEntity.ok(cursoService.mapearParaDTO(curso));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id) {
        var curso = repository.getReferenceById(id);
        curso.desativar();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ativar/{id}")
    @Transactional
    public ResponseEntity ativar(@PathVariable Long id) {
        var curso = repository.getReferenceById(id);
        curso.ativar();
        return ResponseEntity.ok(cursoService.mapearParaDTO(curso));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<CursoListagemDTO> detalhar(@PathVariable Long id) {
//        return ResponseEntity.ok(cursoService.detalhar(id));
//    }

    @GetMapping("/{slug}")
    public ResponseEntity<CursoListagemDTO> buscarPorSlug(@PathVariable String slug) {
        List<Curso> cursos = repository.findAll();

        Curso curso = cursos.stream()
                .filter(c -> SlugUtils.toSlug(c.getNome()).equals(slug))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(cursoService.mapearParaDTO(curso));
    }

    @PostMapping("/importar-relatorio-csv")
    public ResponseEntity<Resource> importarCursosCsv(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayResource csvRelatorio = cursoService.importarCursos(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_importacao_cursos.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvRelatorio.contentLength())
                    .body(csvRelatorio);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
