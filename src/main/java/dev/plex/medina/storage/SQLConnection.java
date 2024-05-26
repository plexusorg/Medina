package dev.plex.medina.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.plex.medina.MedinaBase;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Getter
public class SQLConnection implements MedinaBase
{
    private HikariDataSource dataSource;

    public SQLConnection()
    {
        String host = plugin.config.getString("database.hostname");
        int port = plugin.config.getInt("database.port");
        String username = plugin.config.getString("database.username");
        String password = plugin.config.getString("database.password");
        String database = plugin.config.getString("database.name");

        HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource();
        dataSource.setMaxLifetime(15000);
        dataSource.setIdleTimeout(15000 * 2);
        dataSource.setConnectionTimeout(15000 * 4);
        dataSource.setMinimumIdle(2);
        dataSource.setMaximumPoolSize(10);
        try
        {
            Class.forName("org.mariadb.jdbc.Driver");
            dataSource.setJdbcUrl("jdbc:mariadb://" + host + ":" + port + "/" + database);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
        catch (ClassNotFoundException throwables)
        {
            throwables.printStackTrace();
        }

        try (Connection con = getCon())
        {
            con.prepareStatement("CREATE TABLE IF NOT EXISTS `reports` (" +
                    "`reportId` INT NOT NULL AUTOINCREMENT, " +
                    "`reporterUUID` VARCHAR(46) NOT NULL, " +
                    "`reporterName` VARCHAR(18), " +
                    "`reportedUUID` VARCHAR(46) NOT NULL, " +
                    "`reportedName` VARCHAR(18), " +
                    "`timestamp` BIGINT, " +
                    "`reason` VARCHAR(2000), " +
                    "`deleted` BOOLEAN, " +
                    "PRIMARY KEY (`reportId`));").execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    private boolean tableExistsSQL(String tableName) throws SQLException
    {
        try (Connection connection = getCon())
        {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT count(*) "
                    + "FROM information_schema.tables "
                    + "WHERE table_name = ?"
                    + "LIMIT 1;");
            preparedStatement.setString(1, tableName);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) != 0;
        }
        catch (SQLException ignored)
        {
            return false;
        }
    }

    public Connection getCon()
    {
        if (this.dataSource == null)
        {
            return null;
        }
        try
        {
            return dataSource.getConnection();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
