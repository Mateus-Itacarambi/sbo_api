package ifb.sbo.api.controller;

import ifb.sbo.api.domain.estudante.EstudanteListagemDTO;
import ifb.sbo.api.domain.professor.ProfessorListagemDTO;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/temas")
public class TemaController {
    @Autowired
    private TemaService temaService;

    @Autowired
    private TemaRepository temaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/professor/{professorId}")
    public ResponseEntity<ProfessorListagemDTO> criarTemaProfessor(@PathVariable Long professorId, @RequestBody TemaCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var professor = temaService.criarTemaProfessor(professorId, dados);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professor);
    }

    @PostMapping("/estudante/{estudanteId}")
    public ResponseEntity<EstudanteListagemDTO> criarTemaEstudante(@PathVariable Long estudanteId, @RequestBody TemaCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var estudante = temaService.criarTemaEstudante(estudanteId, dados);

        var uri = uriBuilder.path("/estudantes/{id}").buildAndExpand(estudanteId).toUri();
        return ResponseEntity.created(uri).body(estudante);
    }

    @GetMapping
    public ResponseEntity<Page<TemaListagemDTO>> listarTemas(Pageable paginacao) {
        Page<TemaListagemDTO> temas = temaService.listarTemasPaginados(paginacao);
        return ResponseEntity.ok(temas);
    }

    @PutMapping("/{temaId}/atualizar/{usuarioId}")
    public ResponseEntity<TemaListagemDTO> atualizarFormacao(@PathVariable Long temaId, @PathVariable Long usuarioId, @RequestBody TemaAtualizaDTO dados) {
        var tema = temaService.atualizarTema(temaId, usuarioId, dados);
        return ResponseEntity.ok(tema);
    }

    @DeleteMapping("/{temaId}/deletar/{usuarioId}")
    public ResponseEntity deletarTema(@PathVariable Long temaId, @PathVariable Long usuarioId) {
        temaService.deletarTema(temaId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    // Associar Estudante ao Tema
//    @PostMapping("/{temaId}/estudantes/{estudanteId}")
//    public ResponseEntity<Tema> adicionarEstudanteAoTema(
//            @PathVariable Long temaId,
//            @PathVariable Long estudanteId) {
//        Tema tema = temaService.adicionarEstudanteAoTema(temaId, estudanteId);
//        return ResponseEntity.ok(tema);
//    }
}
