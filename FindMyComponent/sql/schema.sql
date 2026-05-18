CREATE DATABASE IF NOT EXISTS findmycomponent;
USE findmycomponent;

DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS user_sessions;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS order_items;
-- =============================================================
-- DATABASE SCHEMA
-- =============================================================

-- -------------------------------------------------------------
-- TABLE: roles
-- PURPOSE: Stores user role definitions for access control
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
                                     id          INT          PRIMARY KEY AUTO_INCREMENT       COMMENT 'Primary key, auto-incremented unique identifier',
                                     name        VARCHAR(20)  UNIQUE NOT NULL                  COMMENT 'Role name: ADMIN, BUYER, SELLER',
    description VARCHAR(100)                                  COMMENT 'Human-readable explanation of the role purpose',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP        COMMENT 'Timestamp when the role record was created'
    ) COMMENT = 'Stores user role definitions for access control';


-- -------------------------------------------------------------
-- TABLE: users
-- PURPOSE: Stores registered user accounts and their assigned roles
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
                                     id                  INT          PRIMARY KEY AUTO_INCREMENT       COMMENT 'Primary key, auto-incremented unique identifier',
                                     role_id             INT          NOT NULL DEFAULT 3               COMMENT 'Foreign key referencing roles.id; default 3 = BUYER',
                                     username            VARCHAR(50)  UNIQUE NOT NULL                  COMMENT 'Unique login username',
    email               VARCHAR(100) UNIQUE NOT NULL                  COMMENT 'Unique email address used for login and notifications',
    password_hash       VARCHAR(255) NOT NULL                         COMMENT 'Hashed password (bcrypt)',
    first_name          VARCHAR(50)                                   COMMENT 'User first name, nullable',
    last_name           VARCHAR(50)                                   COMMENT 'User last name, nullable',
    phone               VARCHAR(20)                                   COMMENT 'User contact number, nullable',
    is_active           BOOLEAN      DEFAULT TRUE                     COMMENT 'Account status: TRUE = active, FALSE = inactive',
    is_email_verified   BOOLEAN      DEFAULT FALSE                    COMMENT 'Email verification status: TRUE = verified',
    last_login          TIMESTAMP    NULL                             COMMENT 'Timestamp of the users last login, nullable',
    created_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP        COMMENT 'Timestamp when the user record was created',
    updated_at          TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP                   COMMENT 'Timestamp when the user record was last updated',

    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
    ) COMMENT = 'Stores registered user accounts and their assigned roles';


-- -------------------------------------------------------------
-- TABLE: user_sessions
-- PURPOSE: Tracks active user sessions and their expiry
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_sessions (
                                             id              INT          PRIMARY KEY AUTO_INCREMENT       COMMENT 'Primary key, auto-incremented unique identifier',
                                             user_id         INT          NOT NULL                         COMMENT 'Foreign key referencing users.id',
                                             session_token   VARCHAR(255) UNIQUE NOT NULL                  COMMENT 'Unique session token issued on login',
    expires_at      TIMESTAMP    NOT NULL                         COMMENT 'Timestamp when the session expires',
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP        COMMENT 'Timestamp when the session was created',

    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token   (session_token),
    INDEX idx_expires (expires_at)
    ) COMMENT = 'Tracks active user sessions and their expiry';


-- -------------------------------------------------------------
-- TABLE: categories
-- PURPOSE: Stores product category definitions
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
                                          id          INT          PRIMARY KEY AUTO_INCREMENT       COMMENT 'Primary key, auto-incremented unique identifier',
                                          name        VARCHAR(100) UNIQUE NOT NULL                  COMMENT 'Unique category name',
    description TEXT                                          COMMENT 'Detailed description of the category, nullable',
    is_active   BOOLEAN      DEFAULT TRUE                     COMMENT 'Category status: TRUE = active, FALSE = inactive',
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP        COMMENT 'Timestamp when the category was created',
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP                   COMMENT 'Timestamp when the category was last updated'
    ) COMMENT = 'Stores product category definitions';


-- -------------------------------------------------------------
-- TABLE: products
-- PURPOSE: Stores product listings created by sellers
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
                                        id              INT             PRIMARY KEY AUTO_INCREMENT       COMMENT 'Primary key, auto-incremented unique identifier',
                                        seller_id       INT             NOT NULL                         COMMENT 'Foreign key referencing users.id (seller)',
                                        category_id     INT             NOT NULL                         COMMENT 'Foreign key referencing categories.id',
                                        name            VARCHAR(200)    NOT NULL                         COMMENT 'Product name',
    brand           VARCHAR(100)                                     COMMENT 'Product brand, nullable',
    description     TEXT                                             COMMENT 'Detailed product description, nullable',
    price           DECIMAL(10, 2)  NOT NULL                         COMMENT 'Product price with 2 decimal precision',
    stock_quantity  INT             DEFAULT 0                        COMMENT 'Available stock count; 0 means out of stock',
    main_image_url  VARCHAR(500)                                     COMMENT 'URL to the main product image, nullable',
    is_active       BOOLEAN         DEFAULT TRUE                     COMMENT 'Product visibility: TRUE = listed, FALSE = unlisted',
    created_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP        COMMENT 'Timestamp when the product was created',
    updated_at      TIMESTAMP       DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP                      COMMENT 'Timestamp when the product was last updated',

    CONSTRAINT fk_products_seller   FOREIGN KEY (seller_id)   REFERENCES users(id)       ON DELETE RESTRICT,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)  ON DELETE RESTRICT
    ) COMMENT = 'Stores product listings created by sellers';



-- -------------------------------------------------------------
-- TABLE: cart_items
-- PURPOSE: Stores products added to a users shopping cart
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
                                          id          INT       PRIMARY KEY AUTO_INCREMENT                  COMMENT 'Primary key, auto-incremented unique identifier',
                                          user_id     INT       NOT NULL                                    COMMENT 'Foreign key referencing users.id',
                                          product_id  INT       NOT NULL                                    COMMENT 'Foreign key referencing products.id',
                                          quantity    INT       NOT NULL                                    COMMENT 'Number of units added to cart; must be greater than 0',
                                          added_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP                   COMMENT 'Timestamp when the item was added to the cart',
                                          updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                                          ON UPDATE CURRENT_TIMESTAMP                           COMMENT 'Timestamp when the cart item was last updated',

                                          CONSTRAINT chk_quantity    CHECK (quantity > 0),
    CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    UNIQUE KEY uq_user_product (user_id, product_id)
    ) COMMENT = 'Stores products added to a users shopping cart';

-- =====================================================
-- ORDERS TABLE
-- Stores all orders placed by buyers
-- Admin handles accept/reject/dispatch
-- =====================================================
CREATE TABLE IF NOT EXISTS orders (
                                      id              INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key, auto-incremented unique identifier',
                                      order_number    VARCHAR(20) UNIQUE NOT NULL COMMENT 'Auto-incremented order number (e.g., ORD-1001)',
    user_id         INT NOT NULL COMMENT 'Foreign key referencing users.id (buyer who placed order)',
    total_amount    DECIMAL(10, 2) NOT NULL COMMENT 'Total order amount',
    order_status    ENUM('pending', 'accepted', 'rejected', 'dispatched', 'delivered', 'cancelled') DEFAULT 'pending' COMMENT 'Order status: pending=waiting admin, accepted=admin approved, rejected=admin denied, dispatched=shipped, delivered=buyer confirmed, cancelled=buyer cancelled',
    payment_status  ENUM('pending', 'paid', 'failed') DEFAULT 'pending' COMMENT 'Payment status (COD only): pending=not paid, paid=payment completed, failed=payment error',
    payment_method  VARCHAR(50) DEFAULT 'COD' COMMENT 'Payment method (default: Cash on Delivery)',
    shipping_address TEXT COMMENT 'Delivery address for the order',
    notes           TEXT COMMENT 'Additional notes from buyer',
    accepted_at     TIMESTAMP NULL COMMENT 'When admin accepted the order',
    rejected_at     TIMESTAMP NULL COMMENT 'When admin rejected the order',
    dispatched_at   TIMESTAMP NULL COMMENT 'When admin dispatched the order',
    delivered_at    TIMESTAMP NULL COMMENT 'When buyer confirmed delivery',
    cancelled_at    TIMESTAMP NULL COMMENT 'When buyer cancelled order',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When order was created',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT
    ) COMMENT = 'Stores all orders placed by buyers. Admin manages order status.';

-- =====================================================
-- ORDER ITEMS TABLE
-- Stores individual products within an order
-- Each item belongs to one seller (for seller sales tracking)
-- =====================================================
CREATE TABLE IF NOT EXISTS order_items (
                                           id              INT PRIMARY KEY AUTO_INCREMENT COMMENT 'Primary key, auto-incremented unique identifier',
                                           order_id        INT NOT NULL COMMENT 'Foreign key referencing orders.id',
                                           product_id      INT NOT NULL COMMENT 'Foreign key referencing products.id',
                                           seller_id       INT NOT NULL COMMENT 'Foreign key referencing users.id (seller who owns this product)',
                                           product_name    VARCHAR(200) NOT NULL COMMENT 'Snapshot of product name at order time (preserved even if product name changes later)',
    product_price   DECIMAL(10, 2) NOT NULL COMMENT 'Snapshot of product price at order time (preserves price paid by buyer)',
    quantity        INT NOT NULL COMMENT 'Quantity ordered',
    subtotal        DECIMAL(10, 2) NOT NULL COMMENT 'product_price * quantity',

    CONSTRAINT fk_orderitems_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_orderitems_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
    CONSTRAINT fk_orderitems_seller FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE RESTRICT
    ) COMMENT = 'Stores individual products within an order. Each item belongs to a seller for sales tracking. Price and name are snapshots at order time.';