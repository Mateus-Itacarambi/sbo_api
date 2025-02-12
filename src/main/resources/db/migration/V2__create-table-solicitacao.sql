CREATE TABLE solicitacao (
    id_solicitacao INT AUTO_INCREMENT PRIMARY KEY,
    status_solicitacao VARCHAR(20) NOT NULL,
    data_solicitacao DATE NOT NULL,
    data_conclusao_orientacao DATE DEFAULT NULL,
    id_tema INT NOT NULL,
    id_professor INT NOT NULL,
    id_estudante INT,
    motivo VARCHAR(255),
    FOREIGN KEY (id_tema) REFERENCES tema (id_tema),
    FOREIGN KEY (id_professor) REFERENCES professor (id_usuario),
    FOREIGN KEY (id_estudante) REFERENCES estudante (id_usuario)
);