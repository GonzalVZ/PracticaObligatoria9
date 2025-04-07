DROP DATABASE IF EXISTS EventoDB;
CREATE DATABASE EventoDB CHARACTER SET utf8mb4;
USE EventoDB;

-- Crear tabla de categorías
CREATE TABLE categoria(
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50),
  descripcion TEXT
);

-- Crear tabla de eventos
CREATE TABLE evento(
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50),
  descripcion TEXT,
  lugar VARCHAR(50),
  fecha_inicio VARCHAR(50),
  fecha_fin VARCHAR(50),
  id_categoria INT UNSIGNED,
  FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE CASCADE
);

-- Crear tabla de personas
CREATE TABLE persona (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50),
  apellido1 VARCHAR(50),
  apellido2 VARCHAR(50)
);

-- Crear tabla de participaciones
CREATE TABLE participa (
  id_evento INT UNSIGNED,
  id_persona INT UNSIGNED,
  fecha DATE,
  PRIMARY KEY (id_evento, id_persona),
  FOREIGN KEY (id_evento) REFERENCES evento(id) ON DELETE CASCADE,
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- Crear tabla de participantes (referencia a la tabla persona)
CREATE TABLE participante(
  id_persona INT UNSIGNED PRIMARY KEY,
  email VARCHAR(50),
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- Crear tabla de artistas (referencia a la tabla persona)
CREATE TABLE artista (
  id_persona INT UNSIGNED PRIMARY KEY,
  fotografia VARCHAR(100),
  obra_destacada VARCHAR(100),
  FOREIGN KEY (id_persona) REFERENCES persona(id) ON DELETE CASCADE
);

-- Nuevas categorías
INSERT INTO categoria (nombre, descripcion) VALUES
('Cine', 'Proyecciones y festivales de cine'),
('Tecnología', 'Eventos relacionados con avances tecnológicos y conferencias');

-- Nuevos eventos
INSERT INTO evento (nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria) VALUES
('Festival de Cine', 'Proyección de películas independientes', 'Cinepolis Centro', '2023-12-05', '2023-12-10', 1),  -- id_categoria 1 para 'Cine'
('Conferencia de Tecnología', 'Avances en inteligencia artificial', 'Centro de Convenciones', '2023-11-20', '2023-11-21', 2); -- id_categoria 2 para 'Tecnología'

-- Nuevas personas
INSERT INTO persona (nombre, apellido1, apellido2) VALUES
('Ana', 'García', 'Ramírez'),
('Pedro', 'Sánchez', 'Morales'),
('Lucía', 'Fernández', 'Torres'),
('Javier', 'Hernández', 'López'),
('Sofía', 'Martín', 'González');

-- Nuevos participantes
INSERT INTO participante (id_persona, email) VALUES
(1, 'ana.garcia@example.com'),
(2, 'pedro.sanchez@example.com'),
(3, 'lucia.fernandez@example.com'),
(4, 'javier.hernandez@example.com'),
(5, 'sofia.martin@example.com');

-- Nuevos artistas
INSERT INTO artista (id_persona, fotografia, obra_destacada) VALUES
(3, 'lucia_foto.jpg', 'Retrato al óleo'),
(4, 'javier_foto.jpg', 'Instalación interactiva');

-- Nuevas participaciones en eventos (varios participantes en un mismo evento)
INSERT INTO participa (id_evento, id_persona, fecha) VALUES
(1, 1, '2023-11-01'), -- Ana participa en el Festival de Cine
(1, 2, '2023-11-01'), -- Pedro participa en el Festival de Cine
(1, 3, '2023-11-01'), -- Lucía participa en el Festival de Cine
(2, 4, '2023-12-01'), -- Javier participa en la Conferencia de Tecnología
(2, 5, '2023-12-01'), -- Sofía participa en la Conferencia de Tecnología



SELECT participa.*,evento.*,persona.* FROM participa INNER JOIN evento ON  participa.id_evento=evento.id INNER JOIN persona ON participa.id_persona=persona.id;
