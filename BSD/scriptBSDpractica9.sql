CREATE TABLE Persona (
    id INT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido1 VARCHAR(100) NOT NULL,
    apellido2 VARCHAR(100)
);

CREATE TABLE Participante (
    id INT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (id) REFERENCES Persona(id)
);

CREATE TABLE Artista (
    id INT PRIMARY KEY,
    fotografia VARCHAR(255),
    obra_destacada VARCHAR(255),
    FOREIGN KEY (id) REFERENCES Persona(id)
);

CREATE TABLE Evento (
    id INT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    lugar VARCHAR(255),
    fecha_inicio DATE,
    fecha_fin DATE
);

CREATE TABLE Categoria (
    id INT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT
);

CREATE TABLE Participa (
    id_persona INT,
    id_evento INT,
    fecha DATE,
    PRIMARY KEY (id_persona, id_evento),
    FOREIGN KEY (id_persona) REFERENCES Persona(id),
    FOREIGN KEY (id_evento) REFERENCES Evento(id)
);

CREATE TABLE Pertenece (
    id_evento INT,
    id_categoria INT,
    PRIMARY KEY (id_evento, id_categoria),
    FOREIGN KEY (id_evento) REFERENCES Evento(id),
    FOREIGN KEY (id_categoria) REFERENCES Categoria(id)
);


