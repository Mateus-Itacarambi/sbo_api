CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    data_nascimento DATE NOT NULL,
    genero VARCHAR(45) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    data_cadastro DATETIME NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ativo BOOLEAN
    );

CREATE TABLE IF NOT EXISTS curso (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    sigla VARCHAR(200) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    semestres INT NOT NULL,
    ativo BOOLEAN
    );

CREATE TABLE IF NOT EXISTS professor (
    id_lattes VARCHAR(50) NOT NULL,
    disponibilidade VARCHAR(50) NOT NULL,
    id_usuario INT PRIMARY KEY NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
    );

CREATE TABLE IF NOT EXISTS tema (
    id_tema INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(200) NOT NULL,
    palavras_chave VARCHAR(200) NOT NULL,
    status_tema VARCHAR(45) NOT NULL,
    data_cadastro DATETIME NOT NULL,
    data_atualizacao DATETIME NOT NULL,
    id_professor INT,
    FOREIGN KEY (id_professor) REFERENCES professor (id_usuario)
    );

CREATE TABLE IF NOT EXISTS estudante (
    semestre INT NOT NULL,
    matricula VARCHAR(45) NOT NULL,
    id_usuario INT PRIMARY KEY NOT NULL,
    id_curso INT NOT NULL,
    id_tema INT,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    FOREIGN KEY (id_curso) REFERENCES curso (id_curso),
    FOREIGN KEY (id_tema) REFERENCES tema (id_tema) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS formacao (
    id_formacao INT AUTO_INCREMENT PRIMARY KEY,
    curso VARCHAR(255) NOT NULL,
    modalidade VARCHAR(50) NOT NULL,
    faculdade VARCHAR(255) NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    ano_inicio INT NOT NULL,
    ano_fim INT,
    id_professor INT NOT NULL,
    FOREIGN KEY (id_professor) REFERENCES professor (id_usuario)
    );

CREATE TABLE IF NOT EXISTS area_interesse (
    id_area_interesse INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    ativo BOOLEAN
    );

CREATE TABLE IF NOT EXISTS area_interesse_professor (
    id_area_interesse_professor INT AUTO_INCREMENT PRIMARY KEY,
    id_area_interesse INT NOT NULL,
    id_professor INT NOT NULL,
    FOREIGN KEY (id_area_interesse) REFERENCES area_interesse (id_area_interesse),
    FOREIGN KEY (id_professor) REFERENCES professor (id_usuario)
    );

CREATE TABLE IF NOT EXISTS curso_professor (
    id_curso_professor INT AUTO_INCREMENT PRIMARY KEY,
    id_curso INT NOT NULL,
    id_professor INT NOT NULL,
    FOREIGN KEY (id_curso) REFERENCES curso (id_curso),
    FOREIGN KEY (id_professor) REFERENCES professor (id_usuario)
    );