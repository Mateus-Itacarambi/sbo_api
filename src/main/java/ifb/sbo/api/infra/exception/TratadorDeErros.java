package ifb.sbo.api.infra.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErros {
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity TratarErro404() {
//        return ResponseEntity.notFound().build();
//    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Usuário ou senha inválidos. Verifique suas credenciais.");
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity TratarErro400(MethodArgumentNotValidException ex) {
//        var erros = ex.getFieldErrors();
//        return ResponseEntity.badRequest().body(erros.stream().map(DadosErroValidacao::new).toList());
//    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<String> TratarErro409(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    private record DadosErroValidacao (String campo, String mensagem) {
        public DadosErroValidacao (FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFound(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Usuário não encontrado!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> TratarErro400(MethodArgumentNotValidException ex) {
        for (FieldError erro : ex.getFieldErrors()) {
            switch (erro.getField()) {
                case "nome":
                    return ResponseEntity.badRequest().body("O campo 'nome' é obrigatório.");
                case "dataNascimento":
                    return ResponseEntity.badRequest().body("O campo 'data de nascimento' é obrigatório.");
                case "genero":
                    return ResponseEntity.badRequest().body("O campo 'gênero' é obrigatório.");
                case "idCurso":
                    return ResponseEntity.badRequest().body("O campo 'curso' é obrigatório.");
                case "semestre":
                    return ResponseEntity.badRequest().body("O campo 'semestre' é obrigatório.");
                case "matricula":
                    return ResponseEntity.badRequest().body("O campo 'matrícula' é obrigatório.");
                case "email":
                    return ResponseEntity.badRequest().body("O campo 'e-mail' é obrigatório.");
                case "senha":
                    return ResponseEntity.badRequest().body("O campo 'senha' é obrigatório.");
            }
        }
        return ResponseEntity.badRequest().body("Erro de validação no formulário.");
    }
}
