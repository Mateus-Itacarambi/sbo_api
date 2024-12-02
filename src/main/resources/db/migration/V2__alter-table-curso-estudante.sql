ALTER TABLE curso ADD ativo tinyint NOT NULL;
ALTER TABLE estudante ADD ativo tinyint NOT NULL;
UPDATE curso set ativo = 1;
UPDATE estudante set ativo = 1;