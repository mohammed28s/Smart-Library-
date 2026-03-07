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
        List<String> columns = jdbcTemplate.query(
                "PRAGMA table_info(users)",
                (rs, rowNum) -> rs.getString("name"));

        if (!columns.contains("email")) {
            executeWithRetry("ALTER TABLE users ADD COLUMN email TEXT");
        }
        if (!columns.contains("phone")) {
            executeWithRetry("ALTER TABLE users ADD COLUMN phone TEXT");
        }

        executeWithRetry("CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users(email)");
        executeWithRetry("CREATE UNIQUE INDEX IF NOT EXISTS ux_users_phone ON users(phone)");
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
}
