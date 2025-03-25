DROP DATABASE IF EXISTS eventos;
CREATE DATABASE eventos CHARACTER SET utf8mb4;
USE eventos;


CREATE TABLE categoria(
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(50),
descripcion TEXT
);

CREATE TABLE evento(
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(50),
descripcion TEXT,
lugar VARCHAR(50),
fecha_inicio DATE,
fecha_fin DATE,
 id_categoria INT UNSIGNED,
 FOREIGN KEY (id_categoria) REFERENCES categoria(id)

);
CREATE TABLE persona (
id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(50),
apellido1 VARCHAR (50),
apellido2 VARCHAR(50)

);

CREATE TABLE participa (
  id_evento INT UNSIGNED,
  id_persona INT UNSIGNED,
  fecha DATE,
  PRIMARY KEY (id_evento, id_persona),
  FOREIGN KEY (id_evento) REFERENCES evento(id),
  FOREIGN KEY (id_persona) REFERENCES persona(id)
);





CREATE TABLE participante(
id_persona INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
email VARCHAR(50),
FOREIGN KEY (id_persona) REFERENCES persona(id)
);

CREATE TABLE artista (
id_persona INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
fotografia VARCHAR(100),
obra_destacada VARCHAR(100),
FOREIGN KEY (id_persona) REFERENCES persona(id)
);

INSERT INTO categoria (nombre, descripcion) VALUES
('Música', 'Eventos relacionados con conciertos y festivales de música'),
('Arte', 'Exposiciones y eventos artísticos'),
('Deportes', 'Eventos deportivos y competiciones');

INSERT INTO evento (nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria) VALUES
('Concierto de Rock', 'Un concierto de rock con bandas locales', 'Auditorio Nacional', '2023-11-01', '2023-11-01', 1),
('Exposición de Pintura', 'Exposición de obras de artistas locales', 'Galería de Arte', '2023-12-01', '2023-12-15', 2),
('Maratón Anual', 'Competencia de maratón en la ciudad', 'Parque Central', '2023-10-15', '2023-10-15', 3);

INSERT INTO persona (nombre, apellido1, apellido2) VALUES
('Juan', 'Pérez', 'Gómez'),
('María', 'López', 'Hernández'),
('Carlos', 'Martínez', 'Díaz');

INSERT INTO participante (id_persona, email) VALUES
(1, 'juan.perez@example.com'),
(2, 'maria.lopez@example.com'),
(3, 'carlos.martinez@example.com');

INSERT INTO artista (id_persona, fotografia, obra_destacada) VALUES
(2, 'maria_foto.jpg', 'Paisaje al óleo'),
(3, 'carlos_foto.jpg', 'Escultura moderna');

INSERT INTO participa (id_evento, id_persona, fecha) VALUES
(1, 1, '2023-11-01'), -- Juan participa en el Concierto de Rock
(2, 2, '2023-12-01'), -- María participa en la Exposición de Pintura
(3, 3, '2023-10-15'); -- Carlos participa en el Maratón Anual


SELECT * FROM persona INNER JOIN participa ON persona.id=participa.id_persona;