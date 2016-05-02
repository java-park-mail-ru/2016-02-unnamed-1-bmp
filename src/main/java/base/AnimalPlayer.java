package base;

import java.util.Random;

public enum AnimalPlayer {
    CAMEL,
    CHUPACABRA,
    GIRAFFE,
    MONKEY,
    GRIZZLY,
    CHAMELEON,
    ELEPHANT,
    HYENA,
    FROG,
    SHEEP,
    TURTLE,
    IGUANA,
    LEMUR,
    HIPPO,
    COYOTE,
    WOLF,
    PANDA,
    PYTHON;

    private static final int SIZE = AnimalPlayer.values().length;
    private static final Random RANDOM = new Random();

    public static String randomAnimal() {
        final String name = "Anonymous " + AnimalPlayer.values()[RANDOM.nextInt(SIZE)].toString().toLowerCase();
        return name;
    }
}