package de.gccc.test.database;

import java.sql.SQLException;

public class SavepointException extends RuntimeException {

    public SavepointException(SQLException innerException) {
        super(innerException);
    }

}
