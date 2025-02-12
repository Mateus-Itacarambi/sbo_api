package ifb.sbo.api.domain.area_interesse;


import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AreaInteresseService {
    @Autowired
    private AreaInteresseRepository areaInteresseRepository;

    public AreaInteresseListagemDTO cadastrar(AreaInteresseCadastroDTO dados) {
        buscarAreaInteresseNome(dados.nome());

        var areaInteresse = new AreaInteresse(dados);
        areaInteresseRepository.save(areaInteresse);

        return mapearParaDTO(areaInteresse);
    }

    public AreaInteresseListagemDTO detalhar(Long areaInteresseId) {
        var areaInteresse = buscarAreaInteresse(areaInteresseId);
        return mapearParaDTO(areaInteresse);
    }

    public AreaInteresse buscarAreaInteresse(Long areaInteresseId) {
        return areaInteresseRepository.findByIdAndAtivoTrue(areaInteresseId)
                .orElseThrow(() -> new EntityNotFoundException("Área de interesse não encontrada!"));
    }

    public void buscarAreaInteresseNome(String nome) {
        if (areaInteresseRepository.countByNomeAndAtivoTrue(nome) != 0) {
            throw new ConflitoException("Esta área de interesse já existe!");
        }
    }

    private AreaInteresseListagemDTO mapearParaDTO(AreaInteresse areaInteresse) {
        return new AreaInteresseListagemDTO(areaInteresse);
    }
}
