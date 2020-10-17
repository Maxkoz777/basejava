package com.urise.webapp.sql;

import com.urise.webapp.exception.ExistStorageException;
import com.urise.webapp.exception.StorageException;
import org.postgresql.util.PSQLException;

import java.sql.SQLException;

public class ExceptionUtil {
    private ExceptionUtil(){
    }

    public static ExistStorageException convertException(SQLException exception){
        if (exception instanceof PSQLException){
            if (exception.getSQLState().equals("23505")){
                return new ExistStorageException(null);
            }
        }
        return (ExistStorageException) new StorageException(exception);
    }
}
