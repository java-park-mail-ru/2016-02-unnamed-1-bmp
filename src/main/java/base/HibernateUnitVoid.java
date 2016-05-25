package base;

import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface HibernateUnitVoid {
    void operate(@NotNull Session session);
}