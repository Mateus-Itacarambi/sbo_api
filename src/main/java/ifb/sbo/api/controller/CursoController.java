package ifb.sbo.api.controller;

import ifb.sbo.api.domain.curso.*;
import ifb.sbo.api.infra.exception.ConflitoException;
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
@RequestMapping("cursos")
public class CursoController {
    @Autowired
    private CursoRepository repository;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid CursoCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        if (repository.countByNomeAndAtivoTrue(dados.nome()) != 0) {
                throw new ConflitoException("Esse curso já existe!");
            }

        var curso = new Curso(dados);
        repository.save(curso);

        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(curso.getId()).toUri();

        return ResponseEntity.created(uri).body(new CursoListagemDTO(curso));
    }

    @GetMapping
    public ResponseEntity<Page<CursoListagemDTO>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(CursoListagemDTO::new);
        return ResponseEntity.ok(page);
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
        curso.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var curso = repository.getReferenceById(id);
        return ResponseEntity.ok(new CursoListagemDTO(curso));
    }

}
