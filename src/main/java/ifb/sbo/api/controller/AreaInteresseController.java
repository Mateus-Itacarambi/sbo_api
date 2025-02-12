package ifb.sbo.api.controller;

import ifb.sbo.api.domain.area_interesse.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
}
