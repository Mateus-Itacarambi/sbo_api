CREATE TABLE notificacao (
    id_notificacao INT AUTO_INCREMENT PRIMARY KEY,
    mensagem VARCHAR(255) NOT NULL,
    lida boolean DEFAULT FALSE,
    data_criacao DATETIME NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    id_solicitante INT,
    id_destinatario INT NOT NULL,
    id_solicitacao INT,
    FOREIGN KEY (id_solicitante) REFERENCES usuario (id_usuario),
    FOREIGN KEY (id_destinatario) REFERENCES usuario (id_usuario),
    FOREIGN KEY (id_solicitacao) REFERENCES solicitacao (id_solicitacao)
);