-- -----------------------------------------------------------------------
-- CREACIÓN DE LA BASE DE DATOS
-- -----------------------------------------------------------------------
DROP DATABASE IF EXISTS EventoDB;
CREATE DATABASE EventoDB CHARACTER SET utf8mb4;
USE EventoDB;

-- -----------------------------------------------------------------------
-- CREACIÓN DE TABLAS
-- -----------------------------------------------------------------------

-- Tabla de categorías de eventos
CREATE TABLE categoria (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  descripcion TEXT
);

-- Tabla de eventos
CREATE TABLE evento (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  descripcion TEXT,
  lugar VARCHAR(50),
  fecha_inicio VARCHAR(50),
  fecha_fin VARCHAR(50),
  id_categoria INT UNSIGNED,
  FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE
);

-- Tabla de personas (entidad base)
CREATE TABLE persona (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  apellido1 VARCHAR(50),
  apellido2 VARCHAR(50)
);

-- Tabla de participaciones (relación N:M)
CREATE TABLE participa (
  id_evento INT UNSIGNED,
  id_persona INT UNSIGNED,
  fecha VARCHAR(50),
  PRIMARY KEY (id_evento, id_persona),
  FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE,
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- Tabla de participantes (especialización de persona)
CREATE TABLE participante (
  id_persona INT UNSIGNED PRIMARY KEY, -- No necesita AUTO_INCREMENT
  email VARCHAR(50),
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- Tabla de artistas (especialización de persona)
CREATE TABLE artista (
  id_persona INT UNSIGNED PRIMARY KEY, -- No necesita AUTO_INCREMENT
  fotografia VARCHAR(100),
  obra_destacada VARCHAR(100),
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- -----------------------------------------------------------------------
-- INSERCIONES DE PRUEBA
-- -----------------------------------------------------------------------

-- Insertar datos en la tabla "categoria"
INSERT INTO categoria (nombre, descripcion) VALUES
('Festival de música', 'Evento musical al aire libre con diversos géneros.'),
('Conferencia de tecnología', 'Reunión de expertos en innovación tecnológica.'),
('Exposición de arte', 'Muestra de obras de arte contemporáneo y clásico.');

-- Insertar datos en la tabla "evento"
INSERT INTO evento (nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria) VALUES
('Rock en vivo', 'Concierto de bandas de rock con ambiente festivo.', 'Madrid', '2023-05-20', '2023-05-21', 1),
('Tech Summit 2023', 'Conferencia con líderes del sector tecnológico.', 'Barcelona', '2023-06-15', '2023-06-17', 2),
('Galería Abierta', 'Exposición de pinturas, esculturas y fotografías.', 'Valencia', '2023-07-10', '2023-07-12', 3);

-- Insertar datos en la tabla "persona"
INSERT INTO persona (nombre, apellido1, apellido2) VALUES
('Juan', 'Pérez', 'Gómez'),
('María', 'López', 'Sánchez'),
('Carlos', 'Martínez', 'Ruiz');

-- Insertar datos en la tabla "participante"
INSERT INTO participante (id_persona, email) VALUES
(1, 'juan.perez@example.com'),
(2, 'maria.lopez@example.com'),
(3, 'carlos.martinez@example.com');

-- Insertar datos en la tabla "artista"
INSERT INTO artista (id_persona, fotografia, obra_destacada) VALUES
(1, 'juan_foto.jpg', 'Instalación interactiva "Conexiones"'),
(2, 'maria_foto.jpg', 'Pintura al óleo "Sueño Abstracto"'),
(3, 'carlos_foto.jpg', 'Fotografía creativa "Luz y Sombra"');

-- Insertar datos en la tabla "participa"
-- Se relacionan los eventos con las personas
INSERT INTO participa (id_evento, id_persona, fecha) VALUES
(1, 1, '2023-05-20'),
(2, 2, '2023-06-15'),
(3, 3, '2023-07-10');

-- FIN DEL SCRIPT DE PRUEBA