package de.gccc.test.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.UUID;

public interface ProxyImplHelper {

    default public void releaseSafe(Savepoint savepoint, Connection connection) throws SQLException  {
        try {
            connection.releaseSavepoint(savepoint);
        } catch (SQLException ignored) {
            System.out.println("Release Savepoint (" + savepoint.getSavepointName() + ") failed with: " + ignored.getMessage());
            throw new SavepointException(ignored);
        }
    }

    default public void rollbackSafe(Savepoint savepoint, Connection connection) throws SQLException {
        try {
            connection.rollback(savepoint);
        } catch (SQLException ignored) {
            System.out.println("Rollback Savepoint (" + savepoint.getSavepointName() + ") failed with: " + ignored.getMessage());
            throw new SavepointException(ignored);
        }
    }

    static public String uuid() {
        return UUID.randomUUID().toString().replace("-", "_").toUpperCase().substring(0, 10);
    }

}
