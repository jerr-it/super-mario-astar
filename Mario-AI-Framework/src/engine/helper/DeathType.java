package engine.helper;

public enum DeathType {
    FALL(1),
    KILLED(2),
    TIMEOUT(3);

    private int value;

    DeathType(int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
