package ifb.sbo.api.domain.professor;

import ifb.sbo.api.domain.area_interesse.AreaInteresse;
import ifb.sbo.api.domain.area_interesse.AreaInteresseDetalhaDTO;
import ifb.sbo.api.domain.area_interesse.AreaInteresseRepository;
import ifb.sbo.api.domain.curso.Curso;
import ifb.sbo.api.domain.curso.CursoDetalhaDTO;
import ifb.sbo.api.domain.curso.CursoRepository;
import ifb.sbo.api.domain.estudante.Estudante;
import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.formacao.*;
import ifb.sbo.api.domain.solicitacao.Solicitacao;
import ifb.sbo.api.domain.solicitacao.SolicitacaoRepository;
import ifb.sbo.api.domain.solicitacao.StatusSolicitacao;
import ifb.sbo.api.domain.solicitacao.TipoSolicitacao;
import ifb.sbo.api.domain.tema.TemaDetalhaDTO;
import ifb.sbo.api.domain.usuario.TipoUsuario;
import ifb.sbo.api.domain.usuario.Usuario;
import ifb.sbo.api.domain.usuario.UsuarioRepository;
import ifb.sbo.api.domain.usuario.UsuarioService;
import ifb.sbo.api.infra.exception.ConflitoException;
import ifb.sbo.api.infra.service.AuthService;
import ifb.sbo.api.infra.service.EmailService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private AreaInteresseRepository areaInteresseRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private FormacaoRepository formacaoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    public ProfessorListagemDTO cadastrar(ProfessorCadastroDTO dados) {
        usuarioService.buscarEmail(dados.email());
        buscarLattes(dados.idLattes());

        var professor = new Professor(dados);
        professor.setRole(TipoUsuario.PROFESSOR);
        professorRepository.save(professor);

        return mapearParaDTO(professor);
    }

//    @Transactional
//    public void adicionarAreaInteresse(Long professorId, Long areaInteresseId) {
//        Professor professor = buscarProfessor(professorId);
//        AreaInteresse areaInteresse = buscarAreaInteresse(areaInteresseId);
//
//        if (professor.getAreasInteresse().contains(areaInteresse)) {
//            throw new ConflitoException("Esta área de interesse já foi adicionada ao professor.");
//        }
//
//        professor.getAreasInteresse().add(areaInteresse);
//    }

    @Transactional
    public void adicionarAreasInteresse(Long professorId, List<Long> idsAreas) {
        Professor professor = buscarProfessor(professorId);

        List<AreaInteresse> areas = areaInteresseRepository.findByIdIn(idsAreas);

        professor.setAreasInteresse(areas);
        professorRepository.save(professor);
    }

    @Transactional
    public void removerAreaInteresse(Long professorId, Long areaInteresseId) {
        Professor professor = buscarProfessor(professorId);
        AreaInteresse areaInteresse = buscarAreaInteresse(areaInteresseId);

        if (!professor.getAreasInteresse().contains(areaInteresse)) {
            throw new ConflitoException("Esta área de interesse já foi removida ao professor.");
        }

        professor.getAreasInteresse().remove(areaInteresse);
    }

//    @Transactional
//    public void adicionarCurso(Long professorId, Long cursoId) {
//        Professor professor = buscarProfessor(professorId);
//        Curso curso = buscarCurso(cursoId);
//
//        if (professor.getCursos().contains(curso)) {
//            throw new ConflitoException("Este curso já foi adicionado ao professor.");
//        }
//
//        professor.getCursos().add(curso);
//    }

    @Transactional
    public void adicionarCursos(Long professorId, List<Long> idsCursos) {
        Professor professor = buscarProfessor(professorId);

        List<Curso> cursos = cursoRepository.findByIdIn(idsCursos);

        professor.setCursos(cursos);
        professorRepository.save(professor);
    }

    @Transactional
    public void removerCurso(Long professorId, Long cursoId) {
        Professor professor = buscarProfessor(professorId);
        Curso curso = buscarCurso(cursoId);

        if (!professor.getCursos().contains(curso)) {
            throw new ConflitoException("Este curso já foi removida ao professor.");
        }

        professor.getCursos().remove(curso);
    }

    @Transactional
    public void adicionarFormacao(Long professorId, FormacaoCadastroDTO dados) {
        Professor professor = buscarProfessor(professorId);
        Formacao formacao = new Formacao(dados);
        formacao.setProfessor(professor);
        formacaoRepository.save(formacao);
    }

    @Transactional
    public FormacaoListagemDTO atualizarFormacao(Long professorId, Long formacaoId, FormacaoAtualizaDTO dados) {
        Professor professor = buscarProfessor(professorId);
        Formacao formacao = buscarFormacao(formacaoId);

        if (!professor.getFormacoes().contains(formacao)) {
            throw new ConflitoException("Você não possui esta formação.");
        }

        formacao.atualizarFormacao(dados);

        return new FormacaoListagemDTO(formacao);
    }

    @Transactional
    public void removerFormacao(Long professorId, Long formacaoId) {
        Professor professor = buscarProfessor(professorId);
        Formacao formacao = buscarFormacao(formacaoId);

        if (!professor.getFormacoes().contains(formacao)) {
            throw new ConflitoException("Esta formação já foi removida ao professor.");
        }

        professor.getFormacoes().remove(formacao);
        formacao.setProfessor(null);

        professorRepository.save(professor);

        formacaoRepository.deleteById(formacaoId);
    }

    @Transactional
    public ByteArrayResource importarProfessoresComRelatorioCsv(MultipartFile file) throws IOException {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        StringBuilder csvContent = new StringBuilder();

        // Cabeçalho do CSV
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
                if (campos.length < 2) {
                    csvContent.append(linhaAtual).append(",formato inválido\n");
                    linhaAtual++;
                    continue;
                }

                String nome = campos[0].trim();
                String email = campos[1].trim();

                try {
                    if (usuarioRepository.existsByEmail(email)) {
                        csvContent.append(linhaAtual).append(",e-mail '").append(email).append("' já existe.\n");
                        linhaAtual++;
                        continue;
                    }

                    String senhaGerada = gerarSenhaAleatoria();
                    Professor professor = new Professor();
                    professor.setNome(nome);
                    professor.setDataNascimento(LocalDate.of(2000, 1, 1));
                    professor.setDataCadastro(LocalDate.now());
                    professor.setGenero("Outro");
                    professor.setIdLattes("000000000000000");
                    professor.setEmail(email);
                    professor.setSenha(passwordEncoder.encode(senhaGerada));
                    professor.setAtivo(true);
                    professor.setDisponibilidade(Disponibilidade.DISPONIVEL);
                    professor.setRole(TipoUsuario.PROFESSOR);
                    professor.setCadastroCompleto(false);

                    professorRepository.save(professor);
                    System.out.println("EMAIL: " + professor.getEmail() + "SENHA: " + senhaGerada);
                    emailService.enviarSenhaPorEmail(email, senhaGerada);

                    csvContent.append(linhaAtual).append(",importado com sucesso\n");

                } catch (Exception e) {
                    csvContent.append(linhaAtual).append(",erro inesperado - ").append(e.getMessage()).append("\n");
                }

                linhaAtual++;
            }
        }

        if (csvContent.length() == 0) {
            csvContent.append("Todos os professores foram importados com sucesso.\n");
        }

        return new ByteArrayResource(csvContent.toString().getBytes(StandardCharsets.UTF_8));
    }

    public ProfessorListagemDTO atualizarCadastro(ProfessorAtualizaCadastroDTO dados) {
        Professor professor = professorRepository.findById(dados.id())
                .orElseThrow(() -> new RuntimeException("Professor não encontrado."));

        professor.setNome(dados.nome());
        professor.setGenero(dados.genero());

        if (!Objects.equals(professor.getEmail(), dados.email())) {
            usuarioService.buscarEmail(dados.email());
        }
        professor.setEmail(dados.email());



        if (!Objects.equals(professor.getIdLattes(), dados.idLattes())) {
            buscarLattes(dados.idLattes());
        }
        professor.setIdLattes(dados.idLattes());

        professor.setDataNascimento(dados.dataNascimento());

        if (dados.senhaAtual() != null && !dados.senhaAtual().isEmpty()) {
            if (!authService.verificarSenha(dados.senhaAtual(), professor.getSenha())) {
                throw new ConflitoException("Senha atual incorreta.");
            }

            if (!dados.senhaNova().equals(dados.senhaConfirmar())) {
                throw new ConflitoException("Nova senha e confirmação não conferem.");
            }

            if (dados.senhaNova().equals(professor.getSenha())) {
                throw new ConflitoException("Senha atual e nova senha são iguais.");
            }

            professor.setSenha(authService.criptografarSenha(dados.senhaNova()));
        }

        professor.setCadastroCompleto(true);

        professorRepository.save(professor);

        return mapearParaDTO(professor);
    }


    public String gerarSenhaAleatoria() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public Page<ProfessorListagemDTO> listarProfessoresPaginados(@PageableDefault(size = 20, sort = {"nome"}) Pageable paginacao) {
        return professorRepository.findAllByAtivoTrueAndCadastroCompletoTrue(paginacao)
                .map(this::mapearParaDTO);
    }

    public Page<ProfessorBuscaDTO> listarProfessoresComFiltros(FiltroProfessor filtro, Pageable pageable, Usuario usuario) {
        Specification<Professor> spec = ProfessorSpecification.comFiltros(filtro);

        Long estudanteId;

        if (usuario instanceof Estudante estudante) {
            estudanteId = estudante.getId();
        } else {
            estudanteId = null;
        }

        return professorRepository.findAll(spec, pageable)
                .map(professor -> {
                    var solicitacao = solicitacaoRepository.findByEstudanteIdAndProfessorIdAndTipoAndStatus(
                            estudanteId, professor.getId(), TipoSolicitacao.ORIENTACAO, StatusSolicitacao.PENDENTE
                    );

                    return mapearParaListaDTO(professor, solicitacao.isPresent(), solicitacao.map(Solicitacao::getId).orElse(null));
                });
    }

    public ProfessorListagemDTO detalharProfessor(Long professorId) {
        Professor professor = buscarProfessor(professorId);
        return mapearParaDTO(professor);
    }

    public ProfessorResumoDTO resumoProfessor(Long professorId) {
        Professor professor = buscarProfessor(professorId);
        return mapearParaResumoDTO(professor);
    }

    public Professor buscarProfessor(Long professorId) {
        return professorRepository.findByIdAndAtivoTrue(professorId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado."));
    }

    private AreaInteresse buscarAreaInteresse(Long areaInteresseId) {
        return areaInteresseRepository.findByIdAndAtivoTrue(areaInteresseId)
                .orElseThrow(() -> new EntityNotFoundException("Área de interesse não encontrada."));
    }

    private Curso buscarCurso(Long cursoId) {
        return cursoRepository.findByIdAndAtivoTrue(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso não encontrado."));
    }

    public Formacao buscarFormacao(Long formacaoId) {
        return formacaoRepository.findById(formacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Formação não encontrada."));
    }

    public void buscarLattes(String idLattes) {
        if (professorRepository.countByIdLattes(idLattes) != 0) {
            throw new ConflitoException("Lattes já cadastrado no sistema!");
        }
    }

    public ProfessorResumoDTO mapearParaResumoDTO(Professor professor) {
        return new ProfessorResumoDTO(
                professor.getId(),
                professor.getNome(),
                professor.getIdLattes(),
                professor.getRole().toString(),
                professor.getAtivo(),
                professor.getCadastroCompleto()
        );
    }

    public ProfessorBuscaDTO mapearParaListaDTO(Professor professor, Boolean solicitacaoPendente, Long idSolicitacao) {
        List<CursoDetalhaDTO> cursosDTO = professor.getCursos()
                .stream()
                .map(curso -> new CursoDetalhaDTO(
                        curso.getId(),
                        curso.getNome(),
                        curso.getSlug())).toList();

        List<AreaInteresseDetalhaDTO> areasInteresseDTO = professor.getAreasInteresse()
                .stream()
                .map(areaInteresse -> new AreaInteresseDetalhaDTO(
                        areaInteresse.getId(),
                        areaInteresse.getNome())).toList();

        return new ProfessorBuscaDTO(
                professor.getId(),
                professor.getNome(),
                professor.getEmail(),
                professor.getIdLattes(),
                String.valueOf(professor.getDisponibilidade()),
                cursosDTO,
                areasInteresseDTO,
                solicitacaoPendente,
                idSolicitacao
        );
    }

    private ProfessorListagemDTO mapearParaDTO(Professor professor) {
        List<CursoDetalhaDTO> cursosDTO = professor.getCursos()
                .stream()
                .map(curso -> new CursoDetalhaDTO(
                        curso.getId(),
                        curso.getNome(),
                        curso.getSlug())).toList();

        List<AreaInteresseDetalhaDTO> areasInteresseDTO = professor.getAreasInteresse()
                .stream()
                .map(areaInteresse -> new AreaInteresseDetalhaDTO(
                        areaInteresse.getId(),
                        areaInteresse.getNome())).toList();

        List<FormacaoDetalhaDTO> formacoesDTO = professor.getFormacoes()
                .stream()
                .map(formacao -> new FormacaoDetalhaDTO(
                        formacao.getId(),
                        formacao.getCurso(),
                        formacao.getInstituicao(),
                        formacao.getTitulo(),
                        formacao.getAnoInicio(),
                        formacao.getAnoFim())).toList();

        List<TemaDetalhaDTO> temasDTO = professor.getTemas()
                .stream()
                .map(tema -> new TemaDetalhaDTO(
                        tema.getId(),
                        tema.getTitulo(),
                        tema.getDescricao(),
                        tema.getPalavrasChave(),
                        String.valueOf(tema.getStatus()),
                        tema.getEstudantes().stream()
                                .map(estudante -> new EstudanteDetalhaDTO(
                                        estudante.getId(),
                                        estudante.getNome()
                                ))
                                .toList(),
                        new ProfessorDetalhaDTO(
                                tema.getProfessor().getId(),
                                tema.getProfessor().getNome()
                        ))).toList();


        return new ProfessorListagemDTO(
                professor.getId(),
                professor.getNome(),
                professor.getDataNascimento(),
                professor.getGenero(),
                professor.getEmail(),
                professor.getIdLattes(),
                professor.getRole().toString(),
                professor.getAtivo(),
                professor.getCadastroCompleto(),
                String.valueOf(professor.getDisponibilidade()),
                cursosDTO,
                areasInteresseDTO,
                formacoesDTO,
                temasDTO
        );
    }
}


