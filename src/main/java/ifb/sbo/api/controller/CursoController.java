package ifb.sbo.api.controller;

import ifb.sbo.api.domain.area_interesse.AreaInteresseListagemDTO;
import ifb.sbo.api.domain.curso.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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

    @GetMapping
    public ResponseEntity<Page<CursoListagemDTO>> listar(@PageableDefault(sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(CursoListagemDTO::new);
        return ResponseEntity.ok(page);
    }

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
        return ResponseEntity.ok(new CursoListagemDTO(curso));
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
        return ResponseEntity.ok(new CursoListagemDTO(curso));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoListagemDTO> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.detalhar(id));
    }
}
