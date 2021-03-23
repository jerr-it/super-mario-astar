package forwardmodelslimOOP;

import engine.helper.SpriteType;
import engine.sprites.FlowerEnemy;

public class FlowerEnemySlim extends MarioSpriteSlim {
    private static final SpriteType type = SpriteType.ENEMY_FLOWER;
    static final int width = 2;

    private float yStart;
    private int waitTime;
    private float ya;

    FlowerEnemySlim(FlowerEnemy originalFlowerEnemy) {
        this.x = originalFlowerEnemy.x;
        this.y = originalFlowerEnemy.y;
        this.yStart = originalFlowerEnemy.getyStart();
        this.waitTime = originalFlowerEnemy.getWaitTime();
        this.ya = originalFlowerEnemy.ya;
    }

    // this constructor calls update on itself - needs context with world set
    FlowerEnemySlim(float x, float y, MarioUpdateContext updateContext) {
        this.x = x;
        this.y -= 1;
        this.yStart = this.y;
        this.ya = -1;
        for (int i = 0; i < 4; i++) {
            this.update(updateContext);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlowerEnemySlim that = (FlowerEnemySlim) o;
        return  Float.compare(that.yStart, yStart) == 0 &&
                waitTime == that.waitTime &&
                Float.compare(that.ya, ya) == 0  &&
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
        /*FlowerEnemy sprite = new FlowerEnemy(false, this.x, this.y);
        sprite.xa = this.xa;
        sprite.ya = this.ya;
        sprite.initialCode = this.initialCode;
        sprite.width = this.width;
        sprite.height = this.height;
        sprite.onGround = this.onGround;
        sprite.winged = this.winged;
        sprite.avoidCliffs = this.avoidCliffs;
        sprite.noFireballDeath = this.noFireballDeath;
        sprite.yStart = yStart;
        sprite.waitTime = waitTime;
        return sprite;*/
        return null;
    }

    @Override
    public void update(MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

        if (ya > 0) {
            if (y >= yStart) {
                y = yStart;
                int xd = (int) (Math.abs(updateContext.world.mario.x - x));
                waitTime++;
                if (waitTime > 40 && xd > 24) {
                    waitTime = 0;
                    ya = -1;
                }
            }
        } else if (ya < 0) {
            if (yStart - y > 20) {
                y = yStart - 20;
                waitTime++;
                if (waitTime > 40) {
                    waitTime = 0;
                    ya = 1;
                }
            }
        }
        y += ya;
    }
}
