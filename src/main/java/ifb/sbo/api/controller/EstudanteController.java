package ifb.sbo.api.controller;

import ifb.sbo.api.domain.estudante.*;
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
@RequestMapping("estudantes")
public class EstudanteController {
    @Autowired
    private EstudanteRepository repository;

    @Autowired
    private EstudanteService estudanteService;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid EstudanteCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var estudante = estudanteService.cadastrar(dados);
        var uri = uriBuilder.path("/estudantes/{id}").buildAndExpand(estudante.id()).toUri();
        return ResponseEntity.created(uri).body(estudante);
    }

    @GetMapping
    public ResponseEntity<Page<EstudanteListagemDTO>> listar(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(EstudanteListagemDTO::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid EstudanteAtualizaDTO dados) {
        var estudante = repository.getReferenceById(dados.id());
        estudante.atualizarInformacoes(dados);
        return ResponseEntity.ok(new EstudanteListagemDTO(estudante));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var estudante = repository.getReferenceById(id);
        estudante.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var estudante = repository.getReferenceById(id);
        return ResponseEntity.ok(new EstudanteListagemDTO(estudante));
    }
}
