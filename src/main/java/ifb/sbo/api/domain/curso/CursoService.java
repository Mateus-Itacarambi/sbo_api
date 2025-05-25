package ifb.sbo.api.domain.curso;

import ifb.sbo.api.domain.professor.ProfessorBuscaDTO;
import ifb.sbo.api.domain.professor.ProfessorCursoDTO;
import ifb.sbo.api.infra.exception.ConflitoException;
import ifb.sbo.api.infra.service.SlugUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CursoService {
    @Autowired
    private CursoRepository cursoRepository;

    public CursoListagemDTO cadastrar(CursoCadastroDTO dados) {
        buscarCursoNome(dados.nome());

        var curso = new Curso(dados);
        cursoRepository.save(curso);

        return mapearParaDTO(curso);
    }

    @Transactional
    public ByteArrayResource importarCursos(MultipartFile file) throws IOException {
        StringBuilder csvContent = new StringBuilder();
        csvContent.append("Linha,Mensagem\n");

        int linhaAtual = 1;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String linha;
            boolean primeiraLinha = true;

            while ((linha = reader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                    linhaAtual++;
                    continue;
                }

                String[] campos = linha.split(",", -1); // -1 para manter campos vazios no final

                if (campos.length < 7) {
                    csvContent.append(linhaAtual).append(",formato inválido (campos insuficientes)\n");
                    linhaAtual++;
                    continue;
                }

                String nome = campos[0].trim();
                String sigla = campos[1].trim();
                String descricao = campos[2].trim();
                String semestresStr = campos[3].trim();
                String cargaHoraria = campos[4].trim();
                String duracaoMax = campos[5].trim();
                String modalidade = campos[6].trim();

                if (nome.isEmpty() || sigla.isEmpty() || descricao.isEmpty() || semestresStr.isEmpty()
                        || cargaHoraria.isEmpty() || duracaoMax.isEmpty() || modalidade.isEmpty()) {
                    csvContent.append(linhaAtual).append(",campos obrigatórios ausentes\n");
                    linhaAtual++;
                    continue;
                }

                try {
                    int semestres = Integer.parseInt(semestresStr);

                    if (cursoRepository.existsByNome(nome)) {
                        csvContent.append(linhaAtual).append(",curso '").append(nome).append("' já existe\n");
                    } else {
                        Curso curso = new Curso();
                        curso.setNome(nome);
                        curso.setSigla(sigla);
                        curso.setDescricao(descricao);
                        curso.setSemestres(semestres);
                        curso.setAtivo(true);
                        curso.setSlug(SlugUtils.toSlug(nome));
                        curso.setCargaHoraria(cargaHoraria);
                        curso.setDuracaoMax(duracaoMax);
                        curso.setModalidade(modalidade);
                        cursoRepository.save(curso);

                        csvContent.append(linhaAtual).append(",importado com sucesso\n");
                    }
                } catch (NumberFormatException e) {
                    csvContent.append(linhaAtual).append(",semestres inválido (não é número)\n");
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


    public CursoListagemDTO detalhar(Long cursoId) {
        var curso = buscarCurso(cursoId);
        return mapearParaDTO(curso);
    }

    public Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrada!"));
    }

    public void buscarCursoNome(String nome) {
        if (cursoRepository.countByNomeAndAtivoTrue(nome) != 0) {
            throw new ConflitoException("Este curso já existe!");
        }
    }

    public void buscarCursoSigla(String sigla) {
        if (cursoRepository.countBySiglaAndAtivoTrue(sigla) != 0) {
            throw new ConflitoException("Esta sigla já existe!");
        }
    }

    public CursoListagemDTO mapearParaDTO(Curso curso) {
        List<ProfessorCursoDTO> professoresDTO = curso.getProfessores()
                .stream()
                .map(p -> new ProfessorCursoDTO(
                        p.getId(),
                        p.getNome(),
                        p.getEmail(),
                        String.valueOf(p.getDisponibilidade()),
                        p.getIdLattes())).toList();

        return new CursoListagemDTO(
                curso.getId(),
                curso.getNome(),
                curso.getSigla(),
                curso.getDescricao(),
                curso.getSemestres(),
                curso.getSlug(),
                curso.getCargaHoraria(),
                curso.getDuracaoMax(),
                curso.getModalidade(),
                curso.getProfessores() != null ? professoresDTO : null
        );
    }
}
