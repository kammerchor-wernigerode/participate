package de.vinado.wicket.participate.data.database;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Database implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private String jdbcUrl;

    private String jdbcDriverClassName;

    private String username;

    private String passwd;

    private Connection connection;

    public Database(final String jdbcUrl, final String username, final String password) {
        this("com.mysql.jdbc.Driver", jdbcUrl, username, password);
    }

    public Database(final String jdbcDriverClassName, final String jdbcUrl, final String username, final String passwd) {
        this.jdbcDriverClassName = jdbcDriverClassName;
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.passwd = passwd;
    }

    public Connection openConnection() throws SQLException {
        validate();
        try {
            Class.forName(jdbcDriverClassName);
        } catch (ClassNotFoundException e) {
            LOGGER.error(jdbcDriverClassName + " is not in classpath", e);
        }
        connection = DriverManager.getConnection(jdbcUrl, username, passwd);
        connection.setAutoCommit(false);

        return connection;
    }

    public void closeConnection() {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public void closeConnection(final Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    private void validate() {
        if (Strings.isEmpty(jdbcDriverClassName)) {
            LOGGER.error("jdbcDriverClassName cannot be null", new IllegalArgumentException());
        }

        if (Strings.isEmpty(jdbcUrl)) {
            LOGGER.error("jdbcUrl cannot be null", new IllegalArgumentException());
        }

        if (Strings.isEmpty(username)) {
            LOGGER.error("username cannot be null", new IllegalArgumentException());
        }

        if (Strings.isEmpty(passwd)) {
            LOGGER.error("password cannot be null", new IllegalArgumentException());
        }
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcDriverClassName() {
        return jdbcDriverClassName;
    }

    public void setJdbcDriverClassName(final String jdbcDriverClassName) {
        this.jdbcDriverClassName = jdbcDriverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(final String passwd) {
        this.passwd = passwd;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("jdbcUrl", jdbcUrl)
                .append("jdbcDriverClassName", jdbcDriverClassName)
                .append("username", username)
                .toString();
    }
}
