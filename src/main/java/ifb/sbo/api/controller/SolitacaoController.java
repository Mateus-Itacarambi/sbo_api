package ifb.sbo.api.controller;

import ifb.sbo.api.domain.solicitacao.SolicitacaoListagemDTO;
import ifb.sbo.api.domain.solicitacao.SolitacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/solicitacoes")
public class SolitacaoController {
    @Autowired
    private SolitacaoService solicitacaoService;

    @GetMapping
    public ResponseEntity<Page<SolicitacaoListagemDTO>> listar (Pageable paginacao) {
        Page<SolicitacaoListagemDTO> solicitacoes = solicitacaoService.listarSolicitacoesPaginados(paginacao);
        return ResponseEntity.ok(solicitacoes);
    }

    @PostMapping("/solicitarOrientacao/{estudanteId}/{professorId}")
    public ResponseEntity solicitarOrientacao(@PathVariable Long estudanteId, @PathVariable Long professorId, UriComponentsBuilder uriBuilder) {
        var solicitacao = solicitacaoService.solicitarOrientacao(estudanteId, professorId);

        var uri = uriBuilder.path("/solicitacoes/{id}").buildAndExpand(solicitacao.id()).toUri();

        return ResponseEntity.created(uri).body(solicitacao);
    }

    @GetMapping("{solicitacaoId}")
    public ResponseEntity <SolicitacaoListagemDTO> detalharSolicitacao (@PathVariable Long solicitacaoId) {
        return ResponseEntity.ok(solicitacaoService.detalharSolicitacao(solicitacaoId));
    }
}
