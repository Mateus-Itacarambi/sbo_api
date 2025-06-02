package ifb.sbo.api.controller;

import ifb.sbo.api.domain.solicitacao.FiltroSolicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoListagemDTO;
import ifb.sbo.api.domain.solicitacao.SolicitacaoMotivoDTO;
import ifb.sbo.api.domain.solicitacao.SolitacaoService;
import ifb.sbo.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/solicitacoes")
public class SolitacaoController {
    @Autowired
    private SolitacaoService solicitacaoService;

//    @GetMapping
//    public ResponseEntity<Page<SolicitacaoListagemDTO>> listar (Pageable paginacao) {
//        Page<SolicitacaoListagemDTO> solicitacoes = solicitacaoService.listarSolicitacoesPaginados(paginacao);
//        return ResponseEntity.ok(solicitacoes);
//    }

    @GetMapping("/professor")
    public ResponseEntity<Page<SolicitacaoListagemDTO>> listarSolicitacoesProfessor (@AuthenticationPrincipal Usuario usuario, Pageable paginacao) {
        Page<SolicitacaoListagemDTO> solicitacoes = solicitacaoService.listarSolicitacoesPorProfessor(paginacao, usuario);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/estudante")
    public ResponseEntity<Page<SolicitacaoListagemDTO>> listarSolicitacoesEstudante (@AuthenticationPrincipal Usuario usuario, Pageable paginacao) {
        Page<SolicitacaoListagemDTO> solicitacoes = solicitacaoService.listarSolicitacoesPorAluno(paginacao, usuario);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping
    public Page<SolicitacaoListagemDTO> listarSolicitacoes(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) List<String> tipo,
            @RequestParam(required = false) String tituloTema,
            @RequestParam(required = false) String nomeProfessor,
            @RequestParam(required = false) String nomeEstudante,
            Pageable pageable
    ) {
        FiltroSolicitacao filtro = new FiltroSolicitacao(status, tipo, tituloTema, nomeProfessor, nomeEstudante);
        return solicitacaoService.buscarSolicitacoesComFiltros(usuario, filtro, pageable);
    }

    @PostMapping("/solicitarOrientacao/{estudanteId}/{professorId}")
    public ResponseEntity solicitarOrientacao(@PathVariable Long estudanteId, @PathVariable Long professorId, UriComponentsBuilder uriBuilder) {
        var solicitacao = solicitacaoService.solicitarOrientacao(estudanteId, professorId);

        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(solicitacao.id()).toUri();

        return ResponseEntity.created(uri).body(solicitacao);
    }

    @PostMapping("/solicitarTema/{estudanteId}/{temaId}")
    public ResponseEntity solicitarTema(@PathVariable Long estudanteId, @PathVariable Long temaId, UriComponentsBuilder uriBuilder) {
        var solicitacao = solicitacaoService.solicitarTema(estudanteId, temaId);

        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(solicitacao.id()).toUri();

        return ResponseEntity.created(uri).body(solicitacao);
    }

    @DeleteMapping("/cancelar/{temaId}")
    public ResponseEntity cancelarSolicitacao(@PathVariable Long temaId, @AuthenticationPrincipal Usuario usuario, @RequestBody SolicitacaoMotivoDTO dados) {
        solicitacaoService.cancelarSolicitacao(temaId, usuario, dados);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{solicitacaoId}/rejeitar/{usuarioId}")
    public ResponseEntity<SolicitacaoListagemDTO> rejeitarSolicitacao(@PathVariable Long solicitacaoId, @PathVariable Long usuarioId, @RequestBody SolicitacaoMotivoDTO dados) {
        var solicitacao = solicitacaoService.rejeitarSolicitacao(solicitacaoId, usuarioId, dados);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/{solicitacaoId}/aprovar/{usuarioId}")
    public ResponseEntity<SolicitacaoListagemDTO> aprovarSolicitacao(@PathVariable Long solicitacaoId, @PathVariable Long usuarioId) {
        var solicitacao = solicitacaoService.aprovarSolicitacao(solicitacaoId, usuarioId);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/{solicitacaoId}/concluir/{usuarioId}")
    public ResponseEntity<SolicitacaoListagemDTO> concluirSolicitacao(@PathVariable Long solicitacaoId, @PathVariable Long usuarioId) {
        var solicitacao = solicitacaoService.concluirSolicitacao(solicitacaoId, usuarioId);
        return ResponseEntity.ok(solicitacao);
    }

    @GetMapping("{solicitacaoId}")
    public ResponseEntity <SolicitacaoListagemDTO> detalharSolicitacao (@PathVariable Long solicitacaoId) {
        return ResponseEntity.ok(solicitacaoService.detalharSolicitacao(solicitacaoId));
    }
}
