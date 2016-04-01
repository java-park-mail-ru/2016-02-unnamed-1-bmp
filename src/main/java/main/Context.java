package main;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Context {

    private static final Logger LOGGER = LogManager.getLogger(Context.class);

    private Map<Class<?>, Object> context = new HashMap<>();

    public void add(Class<?> clazz, Object object) {
        if(context.containsKey(clazz)) {
            LOGGER.fatal("Trying to add existing class to context");
        } else {
            context.put(clazz, object);
            LOGGER.info("Putting object of '{}' class", clazz.getName());
        }
    }

    public Object get(Class<?> clazz) {
        return context.get(clazz);
    }
}