package base;

import com.sun.istack.internal.NotNull;
import dbservice.DatabaseException;

public interface DBService {
    public <T>T doReturningWork(@NotNull HibernateUnit<T> work)
            throws DatabaseException;

    public void doWork(@NotNull HibernateUnitVoid work)
            throws DatabaseException;

    public void shutdown();
}
