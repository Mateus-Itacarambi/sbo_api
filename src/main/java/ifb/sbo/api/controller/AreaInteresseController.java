package ifb.sbo.api.controller;

import ifb.sbo.api.domain.area_interesse.*;
import ifb.sbo.api.domain.curso.DadosAtualizaCurso;
import ifb.sbo.api.domain.curso.DadosDetalhamentoCurso;
import ifb.sbo.api.domain.curso.DadosListagemCurso;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
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

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroAreaInteresse dados, UriComponentsBuilder uriBuilder) {
        if (repository.existsByNome(dados.nome())) {
            throw new ValidationException("Área de Interesse já cadastrado!");
        }

        var areaInteresse = new AreaInteresse(dados);
        repository.save(areaInteresse);

        var uri = uriBuilder.path("/cursos/{id}").buildAndExpand(areaInteresse.getId()).toUri();

        return ResponseEntity.created(uri).body(new DadosDetalhamentoAreaInteresse(areaInteresse));
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemAreaInteresse>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemAreaInteresse::new);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizaAreaInteresse dados) {
        var areaInteresse = repository.getReferenceById(dados.id());
        areaInteresse.atualizarInformacoes(dados);
        return ResponseEntity.ok(new DadosDetalhamentoAreaInteresse(areaInteresse));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity excluir(@PathVariable Long id) {
        var areaInteresse = repository.getReferenceById(id);
        areaInteresse.excluir();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detalhar(@PathVariable Long id) {
        var areaInteresse = repository.getReferenceById(id);
        return ResponseEntity.ok(new DadosDetalhamentoAreaInteresse(areaInteresse));
    }

    @PostMapping("/professores/{professorId}/tags")
    public ResponseEntity<AreaInteresse> addAreaInteresse(@PathVariable(value = "professorId") Long professorId, @RequestBody AreaInteresse tagRequest) {
        AreaInteresse areaInteresse = professorRepository.findById(professorId).map(professor -> {
            long tagId = tagRequest.getId();

            // tag is existed
            if (tagId != 0L) {
                Tag _tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Not found Tag with id = " + tagId));
                tutorial.addTag(_tag);
                tutorialRepository.save(tutorial);
                return _tag;
            }

            // add and create new Tag
            tutorial.addTag(tagRequest);
            return tagRepository.save(tagRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId));

        return new ResponseEntity<>(tag, HttpStatus.CREATED);
    }
}
