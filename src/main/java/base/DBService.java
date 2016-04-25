package base;

import com.sun.istack.internal.NotNull;
import dbservice.DatabaseException;

public interface DBService {
    <T>T doReturningWork(@NotNull HibernateUnit<T> work)
            throws DatabaseException;

    void doWork(@NotNull HibernateUnitVoid work)
            throws DatabaseException;

    void shutdown();
}
