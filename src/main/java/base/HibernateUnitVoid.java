package base;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.hibernate.Session;

@FunctionalInterface
public interface HibernateUnitVoid {
    @Nullable
    void operate(@NotNull Session session);
}