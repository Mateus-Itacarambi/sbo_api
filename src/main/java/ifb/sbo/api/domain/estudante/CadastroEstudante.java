package ifb.sbo.api.domain.estudante;

import ifb.sbo.api.domain.curso.CursoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CadastroEstudante {
    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private CursoRepository cursoRepository;

    public DadosDetalhamentoEstudante cadastrar(DadosCadastroEstudante dados) {
        if (!cursoRepository.existsById(dados.idCurso())) {
            throw new ValidationException("Id do curso informado não existe ou não está disponível!");
        }

        if (!cursoRepository.cursoEstaAtivo(dados.idCurso())) {
            throw new ValidationException("Id do curso informado não está disponível!");
        }

        var curso = cursoRepository.getReferenceById(dados.idCurso());

        var cadastro = new Estudante(dados, curso);
        estudanteRepository.save(cadastro);

        return new DadosDetalhamentoEstudante(cadastro);
    }
}
