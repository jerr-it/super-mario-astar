package engine.core;

public class MarioPosition {
    private final float x;
    private final float y;

    MarioPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }
}
