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
-- DATOS DE PRUEBA
-- -----------------------------------------------------------------------

-- Inserción de categorías
-- Inserción de categorías
INSERT INTO categoria (id, nombre, descripcion) VALUES
(1, 'Exposición', 'Exhibición de obras artísticas'),
(2, 'Concierto', 'Eventos musicales en vivo'),
(3, 'Cine', 'Proyecciones y festivales de cine'),
(4, 'Tecnología', 'Eventos relacionados con avances tecnológicos y conferencias');

-- Inserción de eventos
INSERT INTO evento (id, nombre, descripcion, lugar, fecha_inicio, fecha_fin, id_categoria) VALUES
(1, 'Exposición de Arte Moderno', 'Muestra de arte contemporáneo', 'Galería Central', '2023-10-15', '2023-11-15', 1),
(2, 'Concierto de Verano', 'Festival musical al aire libre', 'Parque Municipal', '2023-08-20', '2023-08-22', 2),
(3, 'Festival de Cine', 'Proyección de películas independientes', 'Cinepolis Centro', '2023-12-05', '2023-12-10', 3),
(4, 'Conferencia de Tecnología', 'Avances en inteligencia artificial', 'Centro de Convenciones', '2023-11-20', '2023-11-21', 4);

-- Inserción de personas
INSERT INTO persona (id, nombre, apellido1, apellido2) VALUES
(1, 'Ana', 'García', 'Ramírez'),
(2, 'Pedro', 'Sánchez', 'Morales'),
(3, 'Lucía', 'Fernández', 'Torres'),
(4, 'Javier', 'Hernández', 'López'),
(5, 'Sofía', 'Martín', 'González'),
(6, 'Carlos', 'Rodríguez', 'Pérez'),
(7, 'María', 'López', 'Gómez');

-- Inserción de participantes
INSERT INTO participante (id_persona, email) VALUES
(1, 'ana.garcia@example.com'),
(2, 'pedro.sanchez@example.com'),
(5, 'sofia.martin@example.com'),
(7, 'maria.lopez@example.com');

-- Inserción de artistas
INSERT INTO artista (id_persona, fotografia, obra_destacada) VALUES
(3, 'lucia_foto.jpg', 'Retrato al óleo'),
(4, 'javier_foto.jpg', 'Instalación interactiva'),
(6, 'carlos_foto.jpg', 'Escultura en metal');

-- Inserción de participaciones
INSERT INTO participa (id_evento, id_persona, fecha) VALUES
(1, 1, '2023-10-15'), -- Ana participa en la Exposición
(1, 3, '2023-10-16'), -- Lucía participa en la Exposición
(2, 4, '2023-08-20'), -- Javier participa en el Concierto
(2, 6, '2023-08-21'), -- Carlos participa en el Concierto
(3, 1, '2023-12-05'), -- Ana participa en el Festival de Cine
(3, 2, '2023-12-05'), -- Pedro participa en el Festival de Cine
(3, 3, '2023-12-06'), -- Lucía participa en el Festival de Cine
(4, 4, '2023-11-20'), -- Javier participa en la Conferencia de Tecnología
(4, 5, '2023-11-20'), -- Sofía participa en la Conferencia de Tecnología
(4, 7, '2023-11-21'); -- María participa en la Conferencia de Tecnología
-- -----------------------------------------------------------------------
-- CONSULTAS DE VERIFICACIÓN
-- -----------------------------------------------------------------------

-- Consulta JOIN para mostrar participantes con sus eventos
SELECT 
    persona.id,
    persona.nombre,
    persona.apellido1,
    persona.apellido2,
    participante.email,
    participa.fecha,
    evento.nombre AS evento_nombre,
    evento.descripcion AS evento_descripcion,
    evento.lugar,
    evento.fecha_inicio,
    evento.fecha_fin 
FROM participa 
INNER JOIN evento ON participa.id_evento = evento.id 
INNER JOIN persona ON participa.id_persona = persona.id 
INNER JOIN participante ON participante.id_persona = persona.id;

-- Consulta JOIN para mostrar artistas con sus eventos
SELECT 
    persona.id,
    persona.nombre,
    persona.apellido1,
    persona.apellido2,
    artista.fotografia,
    artista.obra_destacada,
    participa.fecha,
    evento.nombre AS evento_nombre 
FROM participa 
INNER JOIN evento ON participa.id_evento = evento.id 
INNER JOIN persona ON participa.id_persona = persona.id 
INNER JOIN artista ON artista.id_persona = persona.id;

-- Mostrar todos los participantes
SELECT 
    persona.*,
    participante.* 
FROM persona  
INNER JOIN participante ON participante.id_persona = persona.id;

-- Mostrar todos los artistas
SELECT 
    persona.*,
    artista.* 
FROM persona  
INNER JOIN artista ON artista.id_persona = persona.id;