package base;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.hibernate.Session;


@FunctionalInterface
public interface HibernateUnit<T> {
    @Nullable
    T operate(@NotNull Session session);
}