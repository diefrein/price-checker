package ru.diefrein.pricechecker.bot.storage.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ru.diefrein.pricechecker.bot.configuration.parameters.DbParameterProvider;

public class ConnectionPool {

    private final HikariDataSource dataSource;

    public ConnectionPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DbParameterProvider.DB_URL);
        config.setUsername(DbParameterProvider.DB_USERNAME);
        config.setPassword(DbParameterProvider.DB_PASSWORD);
        config.setMaximumPoolSize(DbParameterProvider.MAXIMUM_POOL_SIZE);

        this.dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
