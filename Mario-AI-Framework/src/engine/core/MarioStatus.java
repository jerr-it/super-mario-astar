package engine.core;

import engine.helper.GameStatus;

public class MarioStatus {
    public int status;
    public int x;
    public int y;

    public MarioStatus(GameStatus status, int x, int y) {
        switch (status) {
            case RUNNING -> this.status = 0;
            case WIN -> this.status = 1;
            case LOSE -> this.status = 2;
            case TIME_OUT -> this.status = 3;
        }
        this.x = x;
        this.y = y;
    }

    public int getStatus() {
        return this.status;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
