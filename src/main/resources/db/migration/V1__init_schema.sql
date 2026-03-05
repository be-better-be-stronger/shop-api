-- -----------------------------------------------------
-- Table shop_db.categories
-- -----------------------------------------------------
CREATE TABLE categories (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table shop_db.products
-- -----------------------------------------------------
CREATE TABLE products (
  id INT NOT NULL AUTO_INCREMENT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  image_url VARCHAR(500),
  name VARCHAR(100) NOT NULL,
  description TEXT NULL,
  stock INT NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  category_id INT NOT NULL,
  version INT NOT NULL DEFAULT 0,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  INDEX fk_products_categories1_idx (category_id ASC),
  INDEX idx_products_is_active (is_active),
  INDEX idx_products_cat_active (category_id, is_active),
  INDEX idx_products_active_price (is_active, price),
  INDEX idx_products_active_name (is_active, name),
  CONSTRAINT chk_products_stock
    CHECK (stock >= 0),
  CONSTRAINT fk_products_categories1
    FOREIGN KEY (category_id)
    REFERENCES categories (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table shop_db.users
-- -----------------------------------------------------
CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  email VARCHAR(120) NOT NULL,
  password VARCHAR(120) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  disabled_at DATETIME NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

CREATE INDEX idx_users_status ON users(status);

-- -----------------------------------------------------
-- Table shop_db.user_profiles
-- -----------------------------------------------------
CREATE TABLE user_profiles (
  user_id INT NOT NULL,
  avatar_url VARCHAR(500),
  full_name VARCHAR(120) NOT NULL,
  phone VARCHAR(30),
  address VARCHAR(255),
  PRIMARY KEY (user_id),
  CONSTRAINT fk_profile_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;



-- -----------------------------------------------------
-- Table shop_db.carts
-- -----------------------------------------------------
-- 1) carts (1 user <-> 1 cart active)
CREATE TABLE carts (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_carts_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table shop_db.cart_items
-- -----------------------------------------------------
-- 2) cart_items (join entity: cart + product + qty + unit_price)
CREATE TABLE cart_items (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  cart_id    INT NOT NULL,
  product_id INT NOT NULL,
  qty        INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_cart_items_cart (cart_id),
  INDEX idx_cart_items_product (product_id),
  CONSTRAINT chk_cart_items_qty
    CHECK (qty > 0),
  CONSTRAINT uq_cart_items_cart_product
    UNIQUE (cart_id, product_id),
  CONSTRAINT fk_cart_items_cart
    FOREIGN KEY (cart_id) REFERENCES carts(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_cart_items_product
    FOREIGN KEY (product_id) REFERENCES products(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
) ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table shop_db.orders
-- -----------------------------------------------------
CREATE TABLE orders (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  cancelled_at DATETIME NULL,
  PRIMARY KEY (id),
  INDEX fk_orders_users_idx (user_id ASC),
  INDEX idx_orders_user_created (user_id, created_at),
  CONSTRAINT fk_orders_users
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

-- -----------------------------------------------------
-- Table shop_db.order_items
-- -----------------------------------------------------
CREATE TABLE order_items (
  id INT NOT NULL AUTO_INCREMENT,
  quantity INT NOT NULL,
  price DECIMAL(12,2) NOT NULL,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_order_items_orders1_idx (order_id ASC),
  INDEX fk_order_items_products1_idx (product_id ASC),
  CONSTRAINT uq_order_items_order_product 
    UNIQUE (order_id, product_id),
  CONSTRAINT chk_order_items_quantity
    CHECK (quantity > 0),
  CONSTRAINT fk_order_items_orders1
    FOREIGN KEY (order_id)
    REFERENCES orders (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_order_items_products1
    FOREIGN KEY (product_id)
    REFERENCES products (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
  
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_0900_ai_ci;

