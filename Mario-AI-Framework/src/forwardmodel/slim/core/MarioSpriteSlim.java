package forwardmodel.slim.core;

import forwardmodel.common.SpriteTypeCommon;
import forwardmodel.slim.sprites.FireballSlim;
import forwardmodel.slim.sprites.ShellSlim;

public abstract class MarioSpriteSlim {
    public float x, y;
    public boolean alive;

    public abstract SpriteTypeCommon getType();
    public abstract void update(MarioUpdateContextSlim updateContext);

    public void collideCheck(MarioUpdateContextSlim updateContext) { }
    public void bumpCheck(int xTile, int yTile, MarioUpdateContextSlim updateContext) { }
    public boolean shellCollideCheck(ShellSlim shell, MarioUpdateContextSlim updateContext) { return false; }
    public boolean fireballCollideCheck(FireballSlim fireball, MarioUpdateContextSlim updateContext) { return false; }

    public abstract MarioSpriteSlim clone();
}
