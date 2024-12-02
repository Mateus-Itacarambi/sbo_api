CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    data_nascimento DATE NOT NULL,
    genero VARCHAR(45) NOT NULL,
    email VARCHAR(200) NOT NULL,
    senha VARCHAR(45) NOT NULL,
    data_cadastro DATETIME NOT NULL
    );

CREATE TABLE IF NOT EXISTS curso (
    id_curso INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    sigla VARCHAR(10) NOT NULL,
    descricao VARCHAR(200) NOT NULL
    );

CREATE TABLE IF NOT EXISTS estudante (
    semestre INT NOT NULL,
    matricula VARCHAR(45) NOT NULL,
    id_usuario INT NOT NULL,
    id_curso INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    FOREIGN KEY (id_curso) REFERENCES curso (id_curso)
    );