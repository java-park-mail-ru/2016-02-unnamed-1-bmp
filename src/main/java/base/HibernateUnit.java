package base;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.hibernate.Session;


@FunctionalInterface
public interface HibernateUnit<T> {
    @Nullable
    T operate(@NotNull Session session);
}