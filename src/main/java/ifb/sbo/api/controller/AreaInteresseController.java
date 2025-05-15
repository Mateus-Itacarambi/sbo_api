package ifb.sbo.api.controller;

import ifb.sbo.api.domain.area_interesse.*;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("areasInteresse")
public class AreaInteresseController {
    @Autowired
    private AreaInteresseRepository repository;

    @Autowired
    private AreaInteresseService areaInteresseService;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid AreaInteresseCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var areaInteresse = areaInteresseService.cadastrar(dados);
        var uri = uriBuilder.path("/areasInteresse/{id}").buildAndExpand(areaInteresse.id()).toUri();

        return ResponseEntity.created(uri).body(areaInteresse);
    }

    @GetMapping
    public ResponseEntity<Page<AreaInteresseListagemDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(AreaInteresseListagemDTO::new);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/lista")
    public List<AreaInteresseListagemDTO> listarAtivas() {
        return repository.findByAtivoTrue().stream()
                .map(area -> new AreaInteresseListagemDTO(area.getId(), area.getNome()))
                .sorted(Comparator.comparing(AreaInteresseListagemDTO::nome))
                .toList();
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid AreaInteresseAtualizaDTO dados) {
        var areaInteresse = repository.getReferenceById(dados.id());
        areaInteresse.atualizarInformacoes(dados);
        return ResponseEntity.ok(new AreaInteresseListagemDTO(areaInteresse));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity desativar(@PathVariable Long id) {
        var areaInteresse = repository.getReferenceById(id);
        areaInteresse.desativar();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ativar/{id}")
    @Transactional
    public ResponseEntity ativar(@PathVariable Long id) {
        var areaInteresse = repository.getReferenceById(id);
        areaInteresse.ativar();
        return ResponseEntity.ok(new AreaInteresseListagemDTO(areaInteresse));
    }

    @GetMapping("{id}")
    public ResponseEntity <AreaInteresseListagemDTO> detalhar (@PathVariable Long id) {
        return ResponseEntity.ok(areaInteresseService.detalhar(id));
    }

    @PostMapping("/importar-relatorio-csv")
    public ResponseEntity<Resource> importarAreaInteresseRelatorioCsv(@RequestParam("file") MultipartFile file) {
        try {
            ByteArrayResource csvRelatorio = areaInteresseService.importarAreasInteresse(file);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio_importacao_area_interesse.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvRelatorio.contentLength())
                    .body(csvRelatorio);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
