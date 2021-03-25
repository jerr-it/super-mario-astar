package forwardmodelslim.sprites;

import engine.helper.SpriteType;
import engine.sprites.FireFlower;
import forwardmodelslim.core.MarioSpriteSlim;
import forwardmodelslim.core.MarioUpdateContext;

public class FireFlowerSlim extends MarioSpriteSlim {
    private static final int height = 12;
    private static final SpriteType type = SpriteType.FIRE_FLOWER;

    private int life;

    public FireFlowerSlim(FireFlower originalFireFlower) {
        this.x = originalFireFlower.x;
        this.y = originalFireFlower.y;
        this.life = originalFireFlower.getLife();
    }

    public FireFlowerSlim(float x, float y) {
        this.x = x;
        this.y = y;
        this.life = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FireFlowerSlim that = (FireFlowerSlim) o;
        return life == that.life  &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
    }

    @Override
    public SpriteType getType() {
        return type;
    }

    @Override
    public MarioSpriteSlim clone() {
        /*FireFlower f = new FireFlower(false, x, y);
        f.xa = this.xa;
        f.ya = this.ya;
        f.initialCode = this.initialCode;
        f.width = this.width;
        f.height = this.height;
        f.facing = this.facing;
        f.life = this.life;
        return f;*/
        return null;
    }

    @Override
    public void collideCheck(MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
                updateContext.world.mario.getFlower(updateContext);
                updateContext.world.removeSprite(this, updateContext);
            }
        }
    }

    @Override
    public void update(MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

       life++;
        if (life < 9) {
            this.y--;
        }
    }
}
