package ifb.sbo.api.domain.solicitacao;
import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.estudante.EstudanteService;
import ifb.sbo.api.domain.professor.ProfessorDetalhaDTO;
import ifb.sbo.api.domain.professor.ProfessorService;
import ifb.sbo.api.domain.tema.TemaDetalhaSolicitacaoDTO;
import ifb.sbo.api.domain.tema.TemaListagemDTO;
import ifb.sbo.api.domain.tema.TemaService;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SolitacaoService {
    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    private final EstudanteService estudanteService;

    private final ProfessorService professorService;

    private final TemaService temaService;

    public SolitacaoService(EstudanteService estudanteService, ProfessorService professorService, TemaService temaService) {
        this.estudanteService = estudanteService;
        this.professorService = professorService;
        this.temaService = temaService;
    }

    public SolicitacaoListagemDTO solicitarOrientacao(Long estudanteId, Long professorId) {
        var estudante = estudanteService.buscarEstudante(estudanteId);
        estudanteService.estudanteTemTema(estudante);

        var tema = estudante.getTema();

        existeSolicitacaoTema(tema.getId());

        var professor = professorService.buscarProfessor(professorId);

        var solicitacao = new Solicitacao();
        solicitacao.setStatus(StatusSolicitacao.PENDENTE);
        solicitacao.setDataSolicitacao(LocalDate.now());
        solicitacao.setTema(tema);
        solicitacao.setEstudante(estudante);
        solicitacao.setProfessor(professor);

        solicitacaoRepository.save(solicitacao);

        return detalharSolicitacao(solicitacao.getId());
    }

    public Page<SolicitacaoListagemDTO> listarSolicitacoesPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return solicitacaoRepository.findAll(paginacao)
                .map(this::mapearParaDTO);
    }

    public SolicitacaoListagemDTO detalharSolicitacao(Long solicitacaoId) {
        var solicitacao = buscarSolicitacao(solicitacaoId);
        return mapearParaDTO(solicitacao);
    }

    public Solicitacao buscarSolicitacao(Long solicitacaoId) {
        return solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação não encontrada!"));
    }

    public void existeSolicitacaoTema(Long temaId) {
        if (solicitacaoRepository.countByIdTema(temaId) != 0) {
            throw new ConflitoException("Já existe uma solicitação para esse tema!");
        }
    }

    private SolicitacaoListagemDTO mapearParaDTO(Solicitacao solicitacao) {
        List<EstudanteDetalhaDTO> estudantesDTO = solicitacao.getTema().getEstudantes()
                .stream()
                .map(estudante -> new EstudanteDetalhaDTO(
                        estudante.getId(),
                        estudante.getNome())).toList();

        return new SolicitacaoListagemDTO(
                solicitacao.getId(),
                solicitacao.getStatus(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataConclusaoOrientacao(),
                new TemaDetalhaSolicitacaoDTO(
                        solicitacao.getTema().getId(),
                        solicitacao.getTema().getTitulo(),
                        solicitacao.getTema().getDescricao(),
                        solicitacao.getTema().getPalavrasChave(),
                        solicitacao.getTema().getAreaConhecimento(),
                        estudantesDTO
                ),
                new ProfessorDetalhaDTO(
                        solicitacao.getProfessor().getId(),
                        solicitacao.getProfessor().getNome()
                )
        );
    }
}
