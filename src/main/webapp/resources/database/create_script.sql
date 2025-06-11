-- Tabla para usuarios del sistema (login)
CREATE TABLE usuarios (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  admin BOOLEAN NOT NULL DEFAULT 0
);

-- Insertar usuario de prueba
INSERT INTO usuarios (nombre, email, password, admin) 
VALUES ('Admin', 'admin@mail.com', 'admin', 1);

-- Tabla para food trucks
CREATE TABLE foodtrucks (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  ubicacion TEXT,
  lat REAL,
  lng REAL,
  horario_apertura TEXT,
  horario_cierre TEXT,
  imagen TEXT
);

-- Tabla para menús de cada food truck
CREATE TABLE menus (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  foodtruck_id INTEGER NOT NULL,
  nombre TEXT NOT NULL,
  descripcion TEXT,
  precio REAL NOT NULL,
  imagen TEXT,
  FOREIGN KEY (foodtruck_id) REFERENCES foodtrucks(id) ON DELETE CASCADE
);

-- Tabla para reservas (pedidos)
CREATE TABLE reservas (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id INTEGER NOT NULL,
  foodtruck_id INTEGER NOT NULL,
  fecha TEXT NOT NULL,
  hora TEXT NOT NULL,
  total REAL NOT NULL,
  estado TEXT DEFAULT 'pendiente',
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (foodtruck_id) REFERENCES foodtrucks(id) ON DELETE CASCADE
);

-- Tabla detalle de pedidos (items de cada reserva)
CREATE TABLE reserva_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  reserva_id INTEGER NOT NULL,
  menu_id INTEGER NOT NULL,
  cantidad INTEGER NOT NULL,
  precio_unitario REAL NOT NULL,
  FOREIGN KEY (reserva_id) REFERENCES reservas(id) ON DELETE CASCADE,
  FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

-- Tabla para reviews de food trucks por usuarios
CREATE TABLE reviews (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id INTEGER NOT NULL,
  foodtruck_id INTEGER NOT NULL,
  rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
  comentario TEXT,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (foodtruck_id) REFERENCES foodtrucks(id) ON DELETE CASCADE
);

-- Tabla para favoritos (usuarios que marcan food trucks)
CREATE TABLE favoritos (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id INTEGER NOT NULL,
  foodtruck_id INTEGER NOT NULL,
  fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (usuario_id, foodtruck_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (foodtruck_id) REFERENCES foodtrucks(id) ON DELETE CASCADE
);