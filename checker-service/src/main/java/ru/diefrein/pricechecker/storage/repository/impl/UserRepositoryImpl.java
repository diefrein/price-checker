package ru.diefrein.pricechecker.storage.repository.impl;

import ru.diefrein.pricechecker.storage.entity.User;
import ru.diefrein.pricechecker.storage.pool.ConnectionPool;
import ru.diefrein.pricechecker.storage.repository.UserRepository;

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
            INSERT INTO checker.users (name, is_active)
            VALUES (?, ?)
            """;
    private static final String SELECT_USER_BY_ID_STATEMENT = """
            SELECT * FROM checker.users WHERE id = ?
            """;
    private static final String SELECT_USERS = """
            SELECT * FROM checker.users
            """;
    private static final String SELECT_ACTIVE_USERS_PAGE_STATEMENT = """
            SELECT * FROM checker.users WHERE is_active = true limit ? offset ?
            """;
    private static final String DELETE_USER_BY_ID_STATEMENT = """
            DELETE FROM checker.users WHERE id = ?
            """;
    private static final String UPDATE_IS_ACTIVE_BY_ID_STATEMENT = """
            UPDATE checker.users SET is_active = ? WHERE id = ?
            """;

    private final DataSource dataSource;

    public UserRepositoryImpl(ConnectionPool connectionPool) {
        this.dataSource = connectionPool.getDataSource();
    }

    @Override
    public User create(String name, boolean isActive) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt =
                         conn.prepareStatement(INSERT_USER_STATEMENT, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setBoolean(2, isActive);
                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        UUID id = (UUID) generatedKeys.getObject(1);
                        return new User(id, name, isActive);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User findById(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID_STATEMENT)) {
                stmt.setObject(1, id);
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
    public void remove(UUID id) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_USER_BY_ID_STATEMENT)) {
                stmt.setObject(1, id);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(UUID id, boolean isActive) {
        try (Connection conn = dataSource.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_IS_ACTIVE_BY_ID_STATEMENT)) {
                stmt.setBoolean(1, isActive);
                stmt.setObject(2, id);
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
                UUID.fromString(rs.getString("id")),
                rs.getString("name"),
                rs.getBoolean("is_active")
        );
    }
}
