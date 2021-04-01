package forwardmodelslim.sprites;

import engine.sprites.BulletBill;
import forwardmodelslim.core.MarioSpriteSlim;
import forwardmodelslim.core.MarioUpdateContext;
import forwardmodelslim.level.SpriteTypeSlim;

public class BulletBillSlim extends MarioSpriteSlim {
    private static final SpriteTypeSlim type = SpriteTypeSlim.BULLET_BILL;
    static final int height = 12;

    private int facing;

    private BulletBillSlim() { }

    public BulletBillSlim(BulletBill originalBulletBill) {
        this.x = originalBulletBill.x;
        this.y = originalBulletBill.y;
        this.alive = originalBulletBill.alive;
        this.facing = originalBulletBill.facing;
    }

    public BulletBillSlim(float x, float y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BulletBillSlim that = (BulletBillSlim) o;
        boolean equal =  facing == that.facing &&
                Float.compare(x, that.x) == 0 &&
                Float.compare(y, that.y) == 0 &&
                alive == that.alive;
        if (equal) {
            System.out.println("    BULLET BILL EQUAL");
            return true;
        }
        else {
            System.out.println("    BULLET BILL NOT EQUAL");
            return false;
        }
    }

    public MarioSpriteSlim clone() {
        BulletBillSlim clone = new BulletBillSlim();
        clone.x = this.x;
        clone.y = this.y;
        clone.alive = this.alive;
        clone.facing = this.facing;
        return clone;
    }

    @Override
    public SpriteTypeSlim getType() {
        return type;
    }

    @Override
    public void update(MarioUpdateContext updateContext) {
        if (!alive) return;
        x += facing * 4f;
    }

    @Override
    public void collideCheck(MarioUpdateContext updateContext) {
        if (!alive) return;

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -16 && xMarioD < 16) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
                if (updateContext.world.mario.ya > 0 && yMarioD <= 0 && (!updateContext.world.mario.onGround || !updateContext.world.mario.wasOnGround)) {
                    updateContext.world.mario.stomp(this, updateContext);
                    updateContext.world.removeSprite(this, updateContext);
                } else {
                    updateContext.world.mario.getHurt(updateContext);
                }
            }
        }
    }

    @Override
    public boolean fireballCollideCheck(FireballSlim fireball, MarioUpdateContext updateContext) {
        if (!alive) return false;

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16)
            return yD > -height && yD < FireballSlim.height;
        else
            return false;
    }

    @Override
    public boolean shellCollideCheck(ShellSlim shell, MarioUpdateContext updateContext) {
        if (!alive) return false;

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < ShellSlim.height) {
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }
}

