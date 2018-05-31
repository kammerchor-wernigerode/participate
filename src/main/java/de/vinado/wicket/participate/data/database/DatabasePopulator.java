package de.vinado.wicket.participate.data.database;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Component
@DependsOn("liquibase")
public class DatabasePopulator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePopulator.class);

    private final DataSourceProperties dataSourceProperties;

    private final ApplicationProperties applicationProperties;

    @Autowired
    public DatabasePopulator(final DataSourceProperties dataSourceProperties,
                             final ApplicationProperties applicationProperties) {
        this.dataSourceProperties = dataSourceProperties;
        this.applicationProperties = applicationProperties;
    }

    private Database sourceDatabase() {
        final ApplicationProperties.Database database = applicationProperties.getDatabase();

        if (null != database) {
            return new Database(database.getRemoteUrl(), database.getRemoteUsername(), database.getRemotePassword());
        }

        return new Database("", "", "");
    }

    private Database destinationDatabase() {
        return new Database(
            dataSourceProperties.getUrl(),
            dataSourceProperties.getUsername(),
            dataSourceProperties.getPassword()
        );
    }

    public void run() {
        final List<String> queries = new ArrayList<>();
        try {
            final Connection srcConnection = sourceDatabase().openConnection();
            final Connection destConnection = destinationDatabase().openConnection();

            try {
                final List<String> tableNames = new ArrayList<>(Arrays.asList("c_list_of_value", "communication",
                    "addresses", "attribute", "persons", "users", "user_rec_tokens", "members", "groups", "roles",
                    "events", "m_person_role", "m_role_permission", "m_member_event", "m_member_group", "m_group_event",
                    "m_address_person", "m_address_event", "m_attribute_person", "m_communication_person"));
                final List<String> delQueries = new ArrayList<>();
                int count = 0;

                destConnection.createStatement().executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
                destConnection.commit();

                for (String tableName : tableNames) {
                    delQueries.add("DELETE FROM " + tableName);
                    queries.add("SELECT * FROM " + tableName);
                    count++;
                }

                for (String sql : delQueries) {
                    destConnection.createStatement().executeUpdate(sql);
                }

                LOGGER.info("Found {} tables.", count);

                for (String sql : queries) {
                    try {
                        final Statement srcStatement = srcConnection.createStatement();
                        LOGGER.info("Source Select SQL: " + sql + ";");
                        final ResultSet srcResultSet = srcStatement.executeQuery(sql);
                        final ResultSetMetaData srcResSetMetaData = srcResultSet.getMetaData();

                        destConnection.setAutoCommit(false);
                        final Statement destStatement = destConnection.createStatement();

                        final String tableName = getTableName(sql);
                        final String destSelectSql = "SELECT * FROM " + tableName + " WHERE 1=2;";
                        LOGGER.info("Destination Select SQL: " + destSelectSql);

                        final ResultSet destResultSet = destStatement.executeQuery(destSelectSql);
                        final ResultSetMetaData destResSetMetaData = destResultSet.getMetaData();

                        if (destResSetMetaData.getColumnCount() < srcResSetMetaData.getColumnCount()) {
                            throw new IllegalArgumentException(sql + " - number of columns do not match");
                        }

                        final Map<String, Integer> destColumnNames = getColumnNames(destResSetMetaData);
                        final List<String> columnNames = new ArrayList<>();
                        final List<String> columnParams = new ArrayList<>();

                        for (int i = 1; i <= srcResSetMetaData.getColumnCount(); i++) {
                            final String srcColumnName = srcResSetMetaData.getColumnName(i);
                            if (null == destColumnNames.get(srcColumnName)) {
                                throw new IllegalArgumentException("Could not find " + srcColumnName + " in destinationDatabase.");
                            }
                            columnNames.add(srcColumnName);
                            columnParams.add("?");
                        }

                        final String columns = columnNames.toString().replace("[", "(").replace("]", ")");
                        final String params = columnParams.toString().replace("[", "(").replace("]", ")");

                        final String destInsertSql = "INSERT INTO " + tableName + " " + columns + " VALUES " + params + ";";

                        LOGGER.info("Destination Insert SQL: " + destInsertSql);
                        final PreparedStatement destInsertStatement = destConnection.prepareStatement(destInsertSql);

                        while (srcResultSet.next()) {
                            for (int i = 1; i <= srcResSetMetaData.getColumnCount(); i++) {
                                destInsertStatement.setObject(i, srcResultSet.getObject(i));
                            }

                            try {
                                destInsertStatement.executeUpdate();
                            } catch (SQLException e) {
                                LOGGER.warn("Skipping row where " + srcResSetMetaData.getColumnName(1) + " = "
                                    + srcResultSet.getObject(1) + " because of " + e.getMessage());
                            }
                        }

                        srcResultSet.close();
                        srcStatement.close();
                        destResultSet.close();
                        destStatement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");
                        destStatement.close();
                        destInsertStatement.close();
                    } catch (SQLException e) {
                        LOGGER.warn("Skipping executing SQL: " + sql + " because of: ", e.getMessage());
                    }
                }

                destConnection.commit();
                LOGGER.info("Destination DB is committed successfully");
            } catch (SQLException e) {
                rollback(destConnection);
                LOGGER.error("Major issues because of: " + e.getMessage());
            } finally {
                LOGGER.info("Closing connections");
                destinationDatabase().closeConnection(destConnection);
                sourceDatabase().closeConnection(srcConnection);
                LOGGER.info("Closed connections successfully");
            }
        } catch (SQLException e) {
            LOGGER.error("Unable to open connection.", e);
        }
    }

    private static String getTableName(final String sql) {
        final String tableName;
        int startIndex = sql.indexOf("FROM ");
        int endIndex = sql.indexOf(" WHERE");

        if (startIndex == -1) {
            startIndex = sql.indexOf("from ");
        }

        if (startIndex == -1) {
            return null;
        } else {
            startIndex = startIndex + 5;
        }

        if (endIndex == -1) {
            endIndex = sql.indexOf(" where");
        }

        if (endIndex != -1) {
            tableName = sql.substring(startIndex, endIndex);
        } else {
            tableName = sql.substring(startIndex);
        }

        return tableName.trim();
    }

    private static void rollback(final Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            //Do Nothing
        }
    }

    private static Map<String, Integer> getColumnNames(final ResultSetMetaData metaData) throws SQLException {
        final HashMap<String, Integer> columnNames = new HashMap<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.put(metaData.getColumnName(i), i);
        }
        return columnNames;
    }
}
