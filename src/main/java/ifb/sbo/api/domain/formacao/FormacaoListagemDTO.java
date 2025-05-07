package ifb.sbo.api.domain.formacao;

public record FormacaoListagemDTO(
        Long id,
        String curso,
        String instituicao,
        String titulo,
        Long anoInicio,
        Long anoFim) {

    public FormacaoListagemDTO (Formacao formacao){
        this(formacao.getId(), formacao.getCurso(), formacao.getInstituicao(), formacao.getTitulo(), formacao.getAnoInicio(), formacao.getAnoFim());
    }
}
