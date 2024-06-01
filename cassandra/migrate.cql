CREATE KEYSPACE IF NOT EXISTS app WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };

CREATE TABLE IF NOT EXISTS app.users (
    email text,
    senha text,
    PRIMARY KEY (email)
);

CREATE TABLE IF NOT EXISTS app.jti_logout (
    jti text,
    email text,
    PRIMARY KEY ((jti), email)
);

CREATE TABLE IF NOT EXISTS app.ra_por_doc (
    documento text,
    ra text,
    PRIMARY KEY (documento)
);

CREATE TABLE IF NOT EXISTS app.aluno_por_ra (
    ra text,
    nome text,
    documento text,
    status_matricula text,
    status_cursoa text,
    nota_cursoa float,
    status_cursob text,
    nota_cursob float,
    status_cursoc text,
    nota_cursoc float,
    PRIMARY KEY (ra)
);

INSERT INTO app.users (email, senha) VALUES ('admin@exemplo.com', '1348c9a04febcf0e5318200f8c785c37f211296fdc9ffceba52ff48a7c675b93');