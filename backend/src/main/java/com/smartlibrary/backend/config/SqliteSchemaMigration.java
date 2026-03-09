package com.smartlibrary.backend.config;

import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SqliteSchemaMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public SqliteSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<String> userColumns = jdbcTemplate.query(
                "PRAGMA table_info(users)",
                (rs, rowNum) -> rs.getString("name"));

        if (!userColumns.contains("email")) {
            executeWithRetry("ALTER TABLE users ADD COLUMN email TEXT");
        }
        if (!userColumns.contains("phone")) {
            executeWithRetry("ALTER TABLE users ADD COLUMN phone TEXT");
        }

        List<String> orderColumns = jdbcTemplate.query(
                "PRAGMA table_info(orders)",
                (rs, rowNum) -> rs.getString("name"));
        if (!orderColumns.contains("rental_start_date")) {
            executeWithRetry("ALTER TABLE orders ADD COLUMN rental_start_date TEXT");
        }
        if (!orderColumns.contains("due_date")) {
            executeWithRetry("ALTER TABLE orders ADD COLUMN due_date TEXT");
        }

        executeWithRetry("CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users(email)");
        executeWithRetry("CREATE UNIQUE INDEX IF NOT EXISTS ux_users_phone ON users(phone)");
        executeWithRetry("CREATE UNIQUE INDEX IF NOT EXISTS ux_orders_barcode ON orders(barcode)");
        executeWithRetry("""
                CREATE TABLE IF NOT EXISTS password_reset_tokens (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER NOT NULL,
                  token TEXT UNIQUE NOT NULL,
                  expires_at TEXT NOT NULL,
                  used_at TEXT,
                  created_at TEXT NOT NULL,
                  FOREIGN KEY(user_id) REFERENCES users(id)
                )
                """);
        executeWithRetry("""
                CREATE TABLE IF NOT EXISTS assistant_feedback (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  question TEXT NOT NULL,
                  answer TEXT NOT NULL,
                  helpful INTEGER NOT NULL,
                  comment TEXT,
                  created_at TEXT NOT NULL,
                  FOREIGN KEY(user_id) REFERENCES users(id)
                )
                """);
        executeWithRetry("""
                CREATE TABLE IF NOT EXISTS reading_rooms (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT UNIQUE NOT NULL,
                  description TEXT,
                  capacity INTEGER DEFAULT 6
                )
                """);
        executeWithRetry("""
                CREATE TABLE IF NOT EXISTS appointments (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  user_id INTEGER,
                  room_id INTEGER NOT NULL,
                  visitor_name TEXT NOT NULL,
                  visitor_email TEXT NOT NULL,
                  purpose TEXT NOT NULL,
                  notes TEXT,
                  start_time TEXT NOT NULL,
                  end_time TEXT NOT NULL,
                  created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                  FOREIGN KEY(user_id) REFERENCES users(id),
                  FOREIGN KEY(room_id) REFERENCES reading_rooms(id)
                )
                """);
        executeWithRetry("""
                CREATE TABLE IF NOT EXISTS contact_messages (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  name TEXT NOT NULL,
                  email TEXT NOT NULL,
                  subject TEXT NOT NULL,
                  message TEXT NOT NULL,
                  created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )
                """);
        seedBooksIfEmpty();
        seedRoomsIfEmpty();
    }

    private void executeWithRetry(String sql) {
        int maxAttempts = 10;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                jdbcTemplate.execute(sql);
                return;
            } catch (DataAccessException ex) {
                String message = ex.getMessage() == null ? "" : ex.getMessage();
                boolean isBusy = message.contains("SQLITE_BUSY");
                if (!isBusy || attempt == maxAttempts) {
                    throw ex;
                }
                try {
                    Thread.sleep(200L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }
    }

    private void seedBooksIfEmpty() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM books", Integer.class);
        if (count != null && count > 0) {
            return;
        }

        executeWithRetry("""
                INSERT INTO books (title, author, isbn, price, stock, description) VALUES
                ('Clean Code', 'Robert C. Martin', '9780132350884', 24.99, 15, 'A handbook of agile software craftsmanship and coding best practices.'),
                ('The Pragmatic Programmer', 'Andrew Hunt, David Thomas', '9780135957059', 29.99, 12, 'Practical software engineering habits for modern developers.'),
                ('Atomic Habits', 'James Clear', '9780735211292', 18.50, 20, 'A practical framework for building good habits and breaking bad ones.'),
                ('Deep Work', 'Cal Newport', '9781455586691', 17.25, 10, 'Rules for focused success in a distracted world.'),
                ('1984', 'George Orwell', '9780451524935', 11.90, 25, 'Classic dystopian novel about surveillance and freedom.'),
                ('To Kill a Mockingbird', 'Harper Lee', '9780061120084', 12.75, 18, 'A timeless novel of justice, empathy, and courage.'),
                ('The Alchemist', 'Paulo Coelho', '9780061122415', 10.40, 22, 'Inspirational story about purpose and personal legend.'),
                ('Sapiens', 'Yuval Noah Harari', '9780062316097', 21.30, 14, 'A brief history of humankind from evolution to modern society.')
                """);
    }

    private void seedRoomsIfEmpty() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reading_rooms", Integer.class);
        if (count != null && count > 0) {
            return;
        }

        executeWithRetry("""
                INSERT INTO reading_rooms (name, description, capacity) VALUES
                ('Aurora Loft', 'Natural light and greenery for reflective reading.', 8),
                ('Harbor Suite', 'Cozy nook with acoustic walls and plush chairs.', 6),
                ('Skyline Terrace', 'Panoramic windows with city views and standing desks.', 10)
                """);
    }
}
