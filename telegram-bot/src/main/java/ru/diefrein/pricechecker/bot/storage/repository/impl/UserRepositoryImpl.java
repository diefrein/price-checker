package ru.diefrein.pricechecker.bot.storage.repository.impl;

import ru.diefrein.pricechecker.bot.bot.state.UserState;
import ru.diefrein.pricechecker.bot.storage.entity.User;
import ru.diefrein.pricechecker.bot.storage.exception.EntityNotFoundException;
import ru.diefrein.pricechecker.bot.storage.repository.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository {

    private static final String INSERT_USER_STATEMENT = """
            INSERT INTO checker_bot.users (checker_user_id, telegram_id, state)
            VALUES (?, ?, ?)
            """;
    private static final String SELECT_USER_BY_TELEGRAM_ID_STATEMENT = """
            SELECT * FROM checker_bot.users WHERE telegram_id = ?
            """;
    private static final String SELECT_USER_BY_CHECKER_ID_STATEMENT = """
            SELECT * FROM checker_bot.users WHERE checker_user_id = ?
            """;
    private static final String UPDATE_USER_STATE_BY_TELEGRAM_ID_STATEMENT = """
            UPDATE checker_bot.users SET state = ? WHERE telegram_id = ?
            """;

    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(long telegramId, UUID checkerUserId) {
        UserState initialState = UserState.INITIAL;
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_STATEMENT)) {
                stmt.setObject(1, checkerUserId);
                stmt.setLong(2, telegramId);
                stmt.setString(3, initialState.name());
                stmt.executeUpdate();
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
                if (!rs.next()) {
                    throw new EntityNotFoundException("User with chatId=%s doesn't exist".formatted(telegramId));
                }
                return map(rs);
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
                if (!rs.next()) {
                    throw new EntityNotFoundException("User with checker_user_id=%s doesn't exist".formatted(userId));
                }
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStateByTelegramId(long telegramId, UserState state) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_STATE_BY_TELEGRAM_ID_STATEMENT)) {
                stmt.setString(1, state.name());
                stmt.setLong(2, telegramId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("telegram_id"),
                UUID.fromString(rs.getString("checker_user_id")),
                UserState.valueOf(rs.getString("state"))
        );
    }
}
