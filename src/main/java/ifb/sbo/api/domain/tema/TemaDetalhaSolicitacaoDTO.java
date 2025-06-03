package ifb.sbo.api.domain.tema;

import ifb.sbo.api.domain.estudante.EstudanteDetalhaDTO;
import ifb.sbo.api.domain.usuario.UsuarioSimplesDTO;

import java.util.List;

public record TemaDetalhaSolicitacaoDTO(
        Long id,
        String titulo,
        String descricao,
        String palavrasChave,
        List<UsuarioSimplesDTO> estudantes) {}
