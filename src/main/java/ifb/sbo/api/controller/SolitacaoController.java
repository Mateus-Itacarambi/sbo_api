package ifb.sbo.api.controller;

import ifb.sbo.api.domain.solicitacao.FiltroSolicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoListagemDTO;
import ifb.sbo.api.domain.solicitacao.SolicitacaoMotivoDTO;
import ifb.sbo.api.domain.solicitacao.SolitacaoService;
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
@RequestMapping("/solicitacoes")
public class SolitacaoController {
    @Autowired
    private SolitacaoService solicitacaoService;

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

    @PostMapping("/solicitarOrientacao/{professorId}")
    public ResponseEntity solicitarOrientacao(@AuthenticationPrincipal Usuario usuario, @PathVariable Long professorId, UriComponentsBuilder uriBuilder) {
        var solicitacao = solicitacaoService.solicitarOrientacao(usuario, professorId);

        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(solicitacao.id()).toUri();

        return ResponseEntity.created(uri).body(solicitacao);
    }

    @PostMapping("/solicitarTema/{temaId}")
    public ResponseEntity solicitarTema(@AuthenticationPrincipal Usuario usuario, @PathVariable Long temaId, UriComponentsBuilder uriBuilder) {
        var solicitacao = solicitacaoService.solicitarTema(usuario.getId(), temaId);

        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(solicitacao.id()).toUri();

        return ResponseEntity.created(uri).body(solicitacao);
    }

    @PutMapping("/cancelar/{solicitacaoId}")
    public ResponseEntity<?> cancelarSolicitacao(@PathVariable Long solicitacaoId, @AuthenticationPrincipal Usuario usuario, @RequestBody @Valid SolicitacaoMotivoDTO dados) {
        var solicitacao = solicitacaoService.cancelarSolicitacao(solicitacaoId, usuario, dados);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/rejeitar/{solicitacaoId}")
    public ResponseEntity<SolicitacaoListagemDTO> rejeitarSolicitacao(@PathVariable Long solicitacaoId, @AuthenticationPrincipal Usuario usuario, @RequestBody @Valid SolicitacaoMotivoDTO dados) {
        var solicitacao = solicitacaoService.rejeitarSolicitacao(solicitacaoId, usuario, dados);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/aprovar/{solicitacaoId}")
    public ResponseEntity<SolicitacaoListagemDTO> aprovarSolicitacao(@PathVariable Long solicitacaoId, @AuthenticationPrincipal Usuario usuario) {
        var solicitacao = solicitacaoService.aprovarSolicitacao(solicitacaoId, usuario);
        return ResponseEntity.ok(solicitacao);
    }

    @PutMapping("/concluir/{solicitacaoId}")
    public ResponseEntity<SolicitacaoListagemDTO> concluirSolicitacao(@PathVariable Long solicitacaoId, @AuthenticationPrincipal Usuario usuario) {
        var solicitacao = solicitacaoService.concluirSolicitacao(solicitacaoId, usuario);
        return ResponseEntity.ok(solicitacao);
    }

    @GetMapping("{solicitacaoId}")
    public ResponseEntity <SolicitacaoListagemDTO> detalharSolicitacao (@PathVariable Long solicitacaoId) {
        return ResponseEntity.ok(solicitacaoService.detalharSolicitacao(solicitacaoId));
    }
}
