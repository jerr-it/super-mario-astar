package forwardmodelslimOOP;

import engine.helper.SpriteType;
import engine.sprites.Enemy;

public class EnemySlim extends  MarioSpriteSlim {
    public static final float GROUND_INERTIA = 0.89f;
    public static final float AIR_INERTIA = 0.89f;
    private static final int width = 4;

    private SpriteType type;

    private float xa, ya;
    private int facing;

    int height;

    private boolean onGround;
    private boolean avoidCliffs;
    private boolean winged;
    private boolean noFireballDeath;

    EnemySlim(Enemy originalEnemy) {
        this.x = originalEnemy.x;
        this.y = originalEnemy.y;
        this.type = originalEnemy.type;
        this.xa = originalEnemy.xa;
        this.ya = originalEnemy.ya;
        this.facing = originalEnemy.facing;
        this.height = originalEnemy.height;

        Enemy.PrivateEnemyCopyInfo info = originalEnemy.getPrivateCopyInfo();
        this.onGround = info.onGround;
        this.avoidCliffs = info.avoidCliffs;
        this.winged = info.winged;
        this.noFireballDeath = info.noFireballDeath;
    }

    EnemySlim(float x, float y, int dir, SpriteType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.height = 24;
        if (this.type != SpriteType.RED_KOOPA && this.type != SpriteType.GREEN_KOOPA
                && this.type != SpriteType.RED_KOOPA_WINGED && this.type != SpriteType.GREEN_KOOPA_WINGED) {
            this.height = 12;
        }
        this.winged = this.type.getValue() % 2 == 1;
        this.avoidCliffs = this.type == SpriteType.RED_KOOPA || this.type == SpriteType.RED_KOOPA_WINGED;
        this.noFireballDeath = this.type == SpriteType.SPIKY || this.type == SpriteType.SPIKY_WINGED;
        this.onGround = false;
        this.facing = dir;
        if (this.facing == 0) {
            this.facing = 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnemySlim that = (EnemySlim) o;
        return  Float.compare(that.xa, xa) == 0 &&
                Float.compare(that.ya, ya) == 0 &&
                facing == that.facing &&
                height == that.height &&
                onGround == that.onGround &&
                avoidCliffs == that.avoidCliffs &&
                winged == that.winged &&
                noFireballDeath == that.noFireballDeath &&
                type == that.type &&
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
        /*Enemy e = new Enemy(false, this.x, this.y, this.facing, this.type);
        e.xa = this.xa;
        e.ya = this.ya;
        e.initialCode = this.initialCode;
        e.width = this.width;
        e.height = this.height;
        e.onGround = this.onGround;
        e.winged = this.winged;
        e.avoidCliffs = this.avoidCliffs;
        e.noFireballDeath = this.noFireballDeath;
        return e;*/
        return null;
    }

    @Override
    public void collideCheck(MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

        float xMarioD = updateContext.world.mario.x - x;
        float yMarioD = updateContext.world.mario.y - y;
        if (xMarioD > -width * 2 - 4 && xMarioD < width * 2 + 4) {
            if (yMarioD > -height && yMarioD < updateContext.world.mario.height) {
                if (type != SpriteType.SPIKY && type != SpriteType.SPIKY_WINGED && type != SpriteType.ENEMY_FLOWER &&
                        updateContext.world.mario.ya > 0 && yMarioD <= 0 && (!updateContext.world.mario.onGround || !updateContext.world.mario.wasOnGround)) {
                    updateContext.world.mario.stomp(this, updateContext);
                    if (winged) {
                        winged = false;
                        ya = 0;
                    } else {
                        if (type == SpriteType.GREEN_KOOPA || type == SpriteType.GREEN_KOOPA_WINGED) {
                            updateContext.world.addSprite(new ShellSlim(x, y), updateContext);
                        } else if (type == SpriteType.RED_KOOPA || type == SpriteType.RED_KOOPA_WINGED) {
                            updateContext.world.addSprite(new ShellSlim(x, y), updateContext);
                        }
                        updateContext.world.removeSprite(this, updateContext);
                    }
                } else {
                    updateContext.world.mario.getHurt(updateContext);
                }
            }
        }
    }

    @Override
    public void update(MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

        float sideWaysSpeed = 1.75f;

        if (xa > 2) {
            facing = 1;
        }
        if (xa < -2) {
            facing = -1;
        }

        xa = facing * sideWaysSpeed;

        if (!move(xa, 0, updateContext))
            facing = -facing;
        onGround = false;
        move(0, ya, updateContext);

        ya *= winged ? 0.95f : 0.85f;
        if (onGround) {
            xa *= GROUND_INERTIA;
        } else {
            xa *= AIR_INERTIA;
        }

        if (!onGround) {
            if (winged) {
                ya += 0.6f;
            } else {
                ya += 2;
            }
        } else if (winged) {
            ya = -10;
        }
    }

    private boolean move(float xa, float ya, MarioUpdateContext updateContext) {
        while (xa > 8) {
            if (!move(8, 0, updateContext))
                return false;
            xa -= 8;
        }
        while (xa < -8) {
            if (!move(-8, 0, updateContext))
                return false;
            xa += 8;
        }
        while (ya > 8) {
            if (!move(0, 8, updateContext))
                return false;
            ya -= 8;
        }
        while (ya < -8) {
            if (!move(0, -8, updateContext))
                return false;
            ya += 8;
        }

        boolean collide = false;
        if (ya > 0) {
            if (isBlocking(x + xa - width, y + ya, xa, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya, xa, 0, updateContext))
                collide = true;
            else if (isBlocking(x + xa - width, y + ya + 1, xa, ya, updateContext))
                collide = true;
            else if (isBlocking(x + xa + width, y + ya + 1, xa, ya, updateContext))
                collide = true;
        }
        if (ya < 0) {
            if (isBlocking(x + xa, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (collide || isBlocking(x + xa - width, y + ya - height, xa, ya, updateContext))
                collide = true;
            else if (collide || isBlocking(x + xa + width, y + ya - height, xa, ya, updateContext))
                collide = true;
        }
        if (xa > 0) {
            if (isBlocking(x + xa + width, y + ya - height, xa, ya, updateContext))
                collide = true;
            if (isBlocking(x + xa + width, y + ya - height / 2, xa, ya, updateContext))
                collide = true;
            if (isBlocking(x + xa + width, y + ya, xa, ya, updateContext))
                collide = true;

            if (avoidCliffs && onGround
                    && !updateContext.world.level.isBlocking((int) ((x + xa + width) / 16), (int) ((y) / 16 + 1), xa, 1))
                collide = true;
        }
        if (xa < 0) {
            if (isBlocking(x + xa - width, y + ya - height, xa, ya, updateContext))
                collide = true;
            if (isBlocking(x + xa - width, y + ya - height / 2, xa, ya, updateContext))
                collide = true;
            if (isBlocking(x + xa - width, y + ya, xa, ya, updateContext))
                collide = true;

            if (avoidCliffs && onGround
                    && !updateContext.world.level.isBlocking((int) ((x + xa - width) / 16), (int) ((y) / 16 + 1), xa, 1))
                collide = true;
        }

        if (collide) {
            if (xa < 0) {
                x = (int) ((x - width) / 16) * 16 + width;
                this.xa = 0;
            }
            if (xa > 0) {
                x = (int) ((x + width) / 16 + 1) * 16 - width - 1;
                this.xa = 0;
            }
            if (ya < 0) {
                y = (int) ((y - height) / 16) * 16 + height;
                this.ya = 0;
            }
            if (ya > 0) {
                y = (int) (y / 16 + 1) * 16 - 1;
                onGround = true;
            }
            return false;
        } else {
            x += xa;
            y += ya;
            return true;
        }
    }

    private boolean isBlocking(float _x, float _y, float xa, float ya, MarioUpdateContext updateContext) {
        int x = (int) (_x / 16);
        int y = (int) (_y / 16);
        if (x == (int) (this.x / 16) && y == (int) (this.y / 16))
            return false;

        boolean blocking = updateContext.world.level.isBlocking(x, y, xa, ya);

        return blocking;
    }

    @Override
    public boolean shellCollideCheck(ShellSlim shell, MarioUpdateContext updateContext) {
        if (!this.alive) {
            return false;
        }

        float xD = shell.x - x;
        float yD = shell.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < ShellSlim.height) {
                xa = shell.facing * 2;
                ya = -5;
                //this.world.addEvent(EventType.SHELL_KILL, this.type.getValue());
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean fireballCollideCheck(FireballSlim fireball, MarioUpdateContext updateContext) {
        if (!this.alive) {
            return false;
        }

        float xD = fireball.x - x;
        float yD = fireball.y - y;

        if (xD > -16 && xD < 16) {
            if (yD > -height && yD < FireballSlim.height) {
                if (noFireballDeath)
                    return true;

                xa = fireball.facing * 2;
                ya = -5;
                //this.world.addEvent(EventType.FIRE_KILL, this.type.getValue());
                updateContext.world.removeSprite(this, updateContext);
                return true;
            }
        }
        return false;
    }

    @Override
    public void bumpCheck(int xTile, int yTile, MarioUpdateContext updateContext) {
        if (!this.alive) {
            return;
        }

        if (x + width > xTile * 16 && x - width < xTile * 16 + 16 && yTile == (int) ((y - 1) / 16)) {
            xa = -updateContext.world.mario.facing * 2;
            ya = -5;
            updateContext.world.removeSprite(this, updateContext);
        }
    }
}
