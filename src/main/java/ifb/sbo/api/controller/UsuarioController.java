package ifb.sbo.api.controller;

import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.estudante.EstudanteRepository;
import ifb.sbo.api.domain.estudante.EstudanteService;
import ifb.sbo.api.domain.professor.Professor;
import ifb.sbo.api.domain.professor.ProfessorRepository;
import ifb.sbo.api.domain.professor.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {
    @Autowired
    private EstudanteRepository estudanteRepository;

    @Autowired
    private EstudanteService estudanteService;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/{identificador}")
    public ResponseEntity<?> getUsuarioByIdentificador(@PathVariable String identificador) {
        Optional<Estudante> estudanteOpt = estudanteRepository.findByMatriculaAndAtivoTrue(identificador);
        if (estudanteOpt.isPresent()) {
            return ResponseEntity.ok(estudanteService.detalharEstudante(estudanteOpt.get().getId()));
        }

        Optional<Professor> professorOpt = professorRepository.findByIdLattesAndAtivoTrueAndCadastroCompletoTrue(identificador);
        if (professorOpt.isPresent()) {
            return ResponseEntity.ok(professorService.detalharProfessor(professorOpt.get().getId()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
    }

}
