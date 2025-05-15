package ifb.sbo.api.domain.area_interesse;

import ifb.sbo.api.domain.professor.ProfessorService;
import ifb.sbo.api.infra.exception.ConflitoException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

    @Transactional
    public ByteArrayResource importarAreasInteresse(MultipartFile file) throws IOException {
        StringBuilder csvContent = new StringBuilder();

        csvContent.append("Linha,Mensagem\n");

        int linhaAtual = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    linhaAtual++;
                    continue;
                }

                String[] campos = linha.split(",");
                if (campos.length < 1 || campos[0].trim().isEmpty()) {
                    csvContent.append(linhaAtual)
                            .append(",formato inválido (nome ausente)\n");
                    linhaAtual++;
                    continue;
                }

                String nome = campos[0].trim();
                try {
                    if (areaInteresseRepository.existsByNome(nome)) {
                        csvContent.append(linhaAtual)
                                .append(",área '")
                                .append(nome)
                                .append("' já existe\n");
                    } else {
                        AreaInteresse area = new AreaInteresse();
                        area.setNome(nome);
                        area.setAtivo(true);
                        areaInteresseRepository.save(area);
                        csvContent.append(linhaAtual)
                                .append(",importado com sucesso\n");
                    }
                } catch (Exception e) {
                    csvContent.append(linhaAtual)
                            .append(",erro inesperado - ")
                            .append(e.getMessage().replaceAll("[\\r\\n]", " "))
                            .append("\n");
                }
                linhaAtual++;
            }
        }

        byte[] reportBytes = csvContent.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayResource(reportBytes);
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
