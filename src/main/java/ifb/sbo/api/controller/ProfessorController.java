package ifb.sbo.api.controller;


import ifb.sbo.api.domain.professor.*;
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
@RequestMapping("professores")
public class ProfessorController {
    @Autowired
    private ProfessorRepository repository;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroProfessor dados, UriComponentsBuilder uriBuilder) {
        var professor = new Professor(dados);
        repository.save(professor);

        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(professor.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoProfessor(professor));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemProfessor>> listar(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemProfessor::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizaProfessor dados) {
        var professor = repository.getReferenceById(dados.id());
        professor.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoProfessor(professor));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var professor = repository.getReferenceById(id);
        professor.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var professor = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoProfessor(professor));
    }
}
