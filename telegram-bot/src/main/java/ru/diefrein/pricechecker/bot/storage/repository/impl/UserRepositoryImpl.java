package ru.diefrein.pricechecker.bot.storage.repository.impl;

import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    private static final String INSERT_USER_STATEMENT = """
            INSERT INTO checker_bot.users (checker_user_id, telegram_id)
            VALUES (?, ?)
            """;
    private static final String SELECT_USER_BY_TELEGRAM_ID_STATEMENT = """
            SELECT * FROM checker_bot.users WHERE telegram_id = ?
            """;
    private static final String SELECT_USER_BY_CHECKER_ID_STATEMENT = """
            SELECT * FROM checker_bot.users WHERE checker_user_id = ?
            """;
    private static final String SELECT_USERS = """
            SELECT * FROM checker_bot.users
            """;

    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User create(long telegramId, UUID checkerUserId) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt =
                         conn.prepareStatement(INSERT_USER_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setObject(1, checkerUserId);
                stmt.setLong(2, telegramId);
                stmt.executeUpdate();

                return new User(telegramId, checkerUserId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findByTelegramId(long telegramId) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_TELEGRAM_ID_STATEMENT)) {
                stmt.setObject(1, telegramId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_USERS)) {
                ResultSet rs = stmt.executeQuery();

                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(map(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findByCheckerUserId(UUID userId) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_CHECKER_ID_STATEMENT)) {
                stmt.setObject(1, userId);
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("telegram_id"),
                UUID.fromString(rs.getString("checker_user_id"))
        );
    }
}
