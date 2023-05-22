package engine.core;

public class JumpEvent {
    private MarioEvent start;
    private MarioEvent end;

    public JumpEvent(MarioEvent start, MarioEvent end) {
        this.start = start;
        this.end = end;
    }

    public MarioEvent getStart() {
        return this.start;
    }

    public MarioEvent getEnd() {
        return this.end;
    }
}
