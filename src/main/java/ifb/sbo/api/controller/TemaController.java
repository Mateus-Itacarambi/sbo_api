package ifb.sbo.api.controller;

import ifb.sbo.api.domain.estudante.EstudanteListagemDTO;
import ifb.sbo.api.domain.professor.ProfessorListagemDTO;
import ifb.sbo.api.domain.tema.*;
import ifb.sbo.api.domain.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/temas")
public class TemaController {
    @Autowired
    private TemaService temaService;

    @PostMapping("/professor/{professorId}")
    public ResponseEntity<ProfessorListagemDTO> criarTemaProfessor(@PathVariable Long professorId, @RequestBody @Valid TemaCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var professor = temaService.criarTemaProfessor(professorId, dados);

        var uri = uriBuilder.path("/professores/{id}").buildAndExpand(professorId).toUri();
        return ResponseEntity.created(uri).body(professor);
    }

    @PostMapping("/estudante/{estudanteId}")
    public ResponseEntity<?> criarTemaEstudante(@PathVariable Long estudanteId, @RequestBody @Valid TemaCadastroDTO dados, UriComponentsBuilder uriBuilder) {
        var estudante = temaService.criarTemaEstudante(estudanteId, dados);
        var uri = uriBuilder.path("/estudantes/{id}").buildAndExpand(estudanteId).toUri();
        return ResponseEntity.created(uri).body(estudante);
    }

    @GetMapping
    public Page<TemaBuscaDTO> listarTemas(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) List<String> palavrasChave,
            @RequestParam(required = false) List<String> professor,
            Pageable pageable,
            @AuthenticationPrincipal Usuario usuario
    ) {
        FiltroTema filtro = new FiltroTema(titulo, palavrasChave, professor);
        return temaService.listarTemasComFiltros(filtro, pageable, usuario);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<Page<TemaListagemDTO>> listarTemasProfessor(@PathVariable Long professorId, Pageable paginacao) {
        Page<TemaListagemDTO> temas = temaService.listarTemasPaginadosPorProfessor(professorId, paginacao);
        return ResponseEntity.ok(temas);
    }

    @GetMapping("/{temaId}")
    public ResponseEntity<TemaListagemDTO> detalharTema(@PathVariable Long temaId) {
        return ResponseEntity.ok(temaService.detalharTema(temaId));
    }

    @PutMapping("/{temaId}/atualizar/{usuarioId}")
    public ResponseEntity<TemaListagemDTO> atualizarTema(@PathVariable Long temaId, @PathVariable Long usuarioId, @RequestBody @Valid TemaAtualizaDTO dados) {
        var tema = temaService.atualizarTema(temaId, usuarioId, dados);
        return ResponseEntity.ok(tema);
    }

    @DeleteMapping("/{temaId}/deletar/{usuarioId}")
    public ResponseEntity deletarTema(@PathVariable Long temaId, @PathVariable Long usuarioId) {
        temaService.deletarTema(temaId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{temaId}/adicionarEstudante/{usuarioId}")
    public ResponseEntity adicionarEstudanteAoTema(@PathVariable Long temaId, @PathVariable Long usuarioId, @RequestBody EstudanteListagemDTO dados, UriComponentsBuilder uriBuilder) {
        temaService.adicionarEstudanteAoTema(temaId, usuarioId, dados.matricula());

        var uri = uriBuilder.path("/temas/{id}").buildAndExpand(temaId).toUri();

        return ResponseEntity.created(uri).body(temaService.detalharTema(temaId));
    }

    @DeleteMapping("/{temaId}/removerEstudante/{usuarioId}")
    public ResponseEntity removerEstudanteDoTema(@PathVariable Long temaId, @PathVariable Long usuarioId, @RequestBody EstudanteListagemDTO dados) {
        temaService.removerEstudanteDoTema(temaId, usuarioId, dados.matricula());
        return ResponseEntity.noContent().build();
    }
}
