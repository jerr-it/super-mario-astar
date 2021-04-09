package forwardmodel.bin.core;

public class MarioForwardModelBin {

    private MarioBinData data;

    public MarioForwardModelBin(MarioBinData data) {
        this.data = data;
    }

    public void advance(boolean[] actions) {
        // TODO WorldBin.update(data)
    }

    public MarioForwardModelBin clone() {
        return new MarioForwardModelBin(this.data.clone());
    }

    // pooling test
    /*
    public void returnArrays() {
        data.returnArrays();
    }
    */
}
