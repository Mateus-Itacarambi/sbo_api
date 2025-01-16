package ifb.sbo.api.domain.formacao;

public record FormacaoListagemDTO(
        Long id,
        String curso,
        String modalidade,
        String faculdade,
        String titulo,
        Long anoInicio,
        Long anoFim) {

    public FormacaoListagemDTO (Formacao formacao){
        this(formacao.getId(), formacao.getCurso(), formacao.getModalidade(), formacao.getFaculdade(), formacao.getTitulo(), formacao.getAnoInicio(), formacao.getAnoFim());
    }
}
