package main;

import org.junit.Test;

import static org.junit.Assert.*;


public class ContextTest {

    @Test
    public void testAdd() throws Exception {
        final Context context = new Context();
        final String str = "I\'m an example";

        context.add(String.class, str);
        assertEquals(str, context.get(String.class));

        context.add(String.class, "I\'m the second example");
        assertEquals(str, context.get(String.class));
    }

}