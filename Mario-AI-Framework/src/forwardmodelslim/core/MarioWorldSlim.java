package forwardmodelslim.core;

//TODO: there is a bullet bill spawner - needs attention

import engine.core.MarioSprite;
import engine.core.MarioWorld;
import engine.helper.GameStatus;
import engine.helper.SpriteType;
import engine.sprites.*;
import forwardmodelslim.level.LevelPart;
import forwardmodelslim.level.MarioLevelSlim;
import forwardmodelslim.level.TileFeaturesSlim;
import forwardmodelslim.sprites.*;

import java.util.ArrayList;

public class MarioWorldSlim {
    private GameStatus gameStatus;
    public int pauseTimer;
    private int currentTimer;
    public float cameraX;
    public float cameraY;
    public MarioSlim mario;
    public MarioLevelSlim level;
    private int currentTick;
    public int coins, lives;

    private ArrayList<MarioSpriteSlim> sprites;

    MarioWorldSlim(MarioWorld originalWorld, int levelCutoutTileWidth) {
        this.gameStatus = originalWorld.gameStatus;
        this.pauseTimer = originalWorld.pauseTimer;
        this.currentTimer = originalWorld.currentTimer;
        this.cameraX = originalWorld.cameraX;
        this.cameraY = originalWorld.cameraY;
        this.currentTick = originalWorld.currentTick;
        this.coins = originalWorld.coins;
        this.lives = originalWorld.lives;

        sprites = new ArrayList<>();

        for (MarioSprite originalSprite : originalWorld.sprites) {
            if (originalSprite instanceof BulletBill)
                setupSprite(new BulletBillSlim((BulletBill) originalSprite));
            else if (originalSprite instanceof FlowerEnemy)
                setupSprite(new FlowerEnemySlim((FlowerEnemy) originalSprite));
            else if (originalSprite instanceof Enemy)
                setupSprite(new EnemySlim((Enemy) originalSprite));
            else if (originalSprite instanceof Fireball)
                setupSprite(new FireballSlim((Fireball) originalSprite));
            else if (originalSprite instanceof FireFlower)
                setupSprite(new FireFlowerSlim((FireFlower) originalSprite));
            else if (originalSprite instanceof LifeMushroom)
                setupSprite(new LifeMushroomSlim((LifeMushroom) originalSprite));
            else if (originalSprite instanceof Mario) {
                mario = new MarioSlim((Mario) originalSprite);
                setupSprite(mario);
            }
            else if (originalSprite instanceof Mushroom)
                setupSprite(new MushroomSlim((Mushroom) originalSprite));
            else if (originalSprite instanceof Shell)
                setupSprite(new ShellSlim((Shell) originalSprite));
            else
                throw new IllegalArgumentException();
        }

        // minimum width because world.update method might look this far
        // TODO: is this large enough?
        if (levelCutoutTileWidth < 19)
            levelCutoutTileWidth = 19;

        assert mario != null;
        this.level = new MarioLevelSlim(originalWorld.level, levelCutoutTileWidth, (int) mario.x / 16);
    }

    private void setupSprite(MarioSpriteSlim sprite) {
        sprite.alive = true;
        this.sprites.add(sprite);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarioWorldSlim that = (MarioWorldSlim) o;
        boolean worldProperties = pauseTimer == that.pauseTimer &&
                currentTimer == that.currentTimer &&
                Float.compare(that.cameraX, cameraX) == 0 &&
                Float.compare(that.cameraY, cameraY) == 0 &&
                currentTick == that.currentTick &&
                coins == that.coins &&
                lives == that.lives &&
                gameStatus == that.gameStatus;
        if (worldProperties)
            System.out.println("WORLD PROPERTIES EQUAL");
        else
            System.out.println("WORLD PROPERTIES NOT EQUAL");

        return worldProperties & level.equals(that.level) &
                areSpritesEqual(this.sprites, that.sprites);
    }

    private boolean areSpritesEqual(ArrayList<MarioSpriteSlim> sprites1, ArrayList<MarioSpriteSlim> sprites2) {
        for (MarioSpriteSlim sprite1 : sprites1) {
            boolean found = false;
            for (MarioSpriteSlim sprite2 : sprites2) {
                if (sprite1.getType() == sprite2.getType() &&
                        Float.compare(sprite1.x, sprite2.x) == 0 &&
                        Float.compare(sprite1.y, sprite2.y) == 0) {
                    if (sprite1.equals(sprite2)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                System.out.println("SPRITES NOT EQUAL");
                return false;
            }
        }
        System.out.println("SPRITES EQUAL");
        return true;
    }

    public MarioWorldSlim clone() {
        /*MarioWorld world = new MarioWorld(this.killEvents);
        world.visuals = false;
        world.cameraX = this.cameraX;
        world.cameraY = this.cameraY;
        world.fireballsOnScreen = this.fireballsOnScreen;
        world.gameStatus = this.gameStatus;
        world.pauseTimer = this.pauseTimer;
        world.currentTimer = this.currentTimer;
        world.currentTick = this.currentTick;
        world.level = this.level.clone();
        for (MarioSprite sprite : this.sprites) {
            MarioSprite cloneSprite = sprite.clone();
            cloneSprite.world = world;
            if (cloneSprite.type == SpriteType.MARIO) {
                world.mario = (Mario) cloneSprite;
            }
            world.sprites.add(cloneSprite);
        }
        if (world.mario == null) {
            world.mario = (Mario) this.mario.clone();
        }
        //stats
        world.coins = this.coins;
        world.lives = this.lives;
        return world;*/
        return null;
    }

    public ArrayList<MarioSpriteSlim> getEnemies() {
        ArrayList<MarioSpriteSlim> enemies = new ArrayList<>();
        for (MarioSpriteSlim sprite : sprites) {
                if (this.isEnemy(sprite)) {
                    enemies.add(sprite);
                }
        }
        return enemies;
    }

    public void addSprite(MarioSpriteSlim sprite, MarioUpdateContext updateContext) {
        updateContext.addedSprites.add(sprite);
        sprite.alive = true;
        }

    public void removeSprite(MarioSpriteSlim sprite, MarioUpdateContext updateContext) {
        updateContext.removedSprites.add(sprite);
        sprite.alive = false;
    }

    public void win() {
        this.gameStatus = GameStatus.WIN;
    }

    public void lose() {
        this.gameStatus = GameStatus.LOSE;
        this.mario.alive = false;
    }

    private void timeout() {
        this.gameStatus = GameStatus.TIME_OUT;
        this.mario.alive = false;
    }

    private boolean isEnemy(MarioSpriteSlim sprite) {
        return sprite instanceof EnemySlim || sprite instanceof FlowerEnemySlim || sprite instanceof BulletBillSlim;
    }

    public void update(boolean[] actions) {
        if (this.gameStatus != GameStatus.RUNNING) {
            return;
        }
        if (this.pauseTimer > 0) {
            this.pauseTimer -= 1;
            return;
        }

        if (this.currentTimer > 0) {
            this.currentTimer -= 30;
            if (this.currentTimer <= 0) {
                this.currentTimer = 0;
                this.timeout();
                return;
            }
        }

        MarioUpdateContext updateContext = MarioUpdateContext.get();
        updateContext.world = this;

        // workaround the nonexistence of MarioGame here
        int marioGameWidth = 256;
        int marioGameHeight = 256;

        this.currentTick += 1;
        this.cameraX = this.mario.x - marioGameWidth / 2;
        if (this.cameraX + marioGameWidth > this.level.width) {
            this.cameraX = this.level.width - marioGameWidth;
        }
        if (this.cameraX < 0) {
            this.cameraX = 0;
        }
        this.cameraY = this.mario.y - marioGameHeight / 2;
        if (this.cameraY + marioGameHeight > this.level.height) {
            this.cameraY = this.level.height - marioGameHeight;
        }
        if (this.cameraY < 0) {
            this.cameraY = 0;
        }

        updateContext.fireballsOnScreen = 0;
        for (MarioSpriteSlim sprite : sprites) {
            if (sprite.x < cameraX - 64 || sprite.x > cameraX + marioGameWidth + 64 || sprite.y > this.level.height + 32) {
                if (sprite.getType() == SpriteType.MARIO) {
                    this.lose();
                }
                this.removeSprite(sprite, updateContext);
                continue;
            }
            if (sprite.getType() == SpriteType.FIREBALL) {
                updateContext.fireballsOnScreen += 1;
            }
        }

        this.level.update((int) mario.x / 16);

        for (int x = (int) cameraX / 16 - 1; x <= (int) (cameraX + marioGameWidth) / 16 + 1; x++) {
            for (int y = (int) cameraY / 16 - 1; y <= (int) (cameraY + marioGameHeight) / 16 + 1; y++) {
                int dir = 0;
                if (x * 16 + 8 > mario.x + 16)
                    dir = -1;
                if (x * 16 + 8 < mario.x - 16)
                    dir = 1;

                SpriteType spriteType = level.getSpriteType(x, y);
                if (spriteType != SpriteType.NONE) {
                    MarioSpriteSlim newSprite = this.spawnEnemy(spriteType, x, y, dir);
                    this.addSprite(newSprite, updateContext);
                    level.setBlock(x, y, 0); // remove sprite when it is spawned
                }

                if (dir != 0) {
                    if (this.level.getBlock(x, y) == LevelPart.BULLET_BILL_CANNON) {
                        if (this.currentTick % 100 == 0) {
                            addSprite(new BulletBillSlim(x * 16 + 8 + dir * 8, y * 16 + 15, dir), updateContext);
                        }
                    }
                }
            }
        }

        updateContext.actions = actions;

        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.update(updateContext);
        }
        for (MarioSpriteSlim sprite : sprites) {
            if (!sprite.alive) {
                continue;
            }
            sprite.collideCheck(updateContext);
        }

        for (ShellSlim shell : updateContext.shellsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != shell && shell.alive && sprite.alive) {
                    if (sprite.shellCollideCheck(shell, updateContext)) {
                        this.removeSprite(sprite, updateContext);
                    }
                }
            }
        }
        updateContext.shellsToCheck.clear();

        for (FireballSlim fireball : updateContext.fireballsToCheck) {
            for (MarioSpriteSlim sprite : sprites) {
                if (sprite != fireball && fireball.alive && sprite.alive) {
                    if (sprite.fireballCollideCheck(fireball, updateContext)) {
                        this.removeSprite(fireball, updateContext);
                    }
                }
            }
        }
        updateContext.fireballsToCheck.clear();

        sprites.addAll(0, updateContext.addedSprites);
        sprites.removeAll(updateContext.removedSprites);
        updateContext.addedSprites.clear();
        updateContext.removedSprites.clear();

        updateContext.world = null;
        updateContext.actions = null;
        updateContext.fireballsOnScreen = 0;
        MarioUpdateContext.back(updateContext);
    }

    private MarioSpriteSlim spawnEnemy(SpriteType type, int x, int y, int dir) {
        if (type == SpriteType.ENEMY_FLOWER) {
            // flower enemy constructor needs to call update - which uses world
            MarioUpdateContext updateContext = MarioUpdateContext.get();
            updateContext.world = this;

            FlowerEnemySlim flowerEnemy = new FlowerEnemySlim(x * 16 + 17, y * 16 + 18, updateContext);

            updateContext.world = null;
            MarioUpdateContext.back(updateContext);

            return flowerEnemy;
        }
        else
            return new EnemySlim(x * 16 + 8, y * 16 + 15, dir, type);
    }

    public void bump(int xTile, int yTile, boolean canBreakBricks, MarioUpdateContext updateContext) {
        LevelPart block = this.level.getBlock(xTile, yTile);
        ArrayList<TileFeaturesSlim> features = TileFeaturesSlim.getTileFeatures(block);

        if (features.contains(TileFeaturesSlim.BUMPABLE)) {
            bumpInto(xTile, yTile - 1, updateContext);
            level.setBlock(xTile, yTile, 14);

            if (features.contains(TileFeaturesSlim.SPECIAL)) {
                if (!this.mario.isLarge) {
                    addSprite(new MushroomSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
                } else {
                    addSprite(new FireFlowerSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
                }
            } else if (features.contains(TileFeaturesSlim.LIFE)) {
                addSprite(new LifeMushroomSlim(xTile * 16 + 9, yTile * 16 + 8), updateContext);
            } else {
                mario.collectCoin(updateContext);
            }
        }

        if (features.contains(TileFeaturesSlim.BREAKABLE)) {
            bumpInto(xTile, yTile - 1, updateContext);
            if (canBreakBricks)
                level.setBlock(xTile, yTile, 0);
        }
    }

    private void bumpInto(int xTile, int yTile, MarioUpdateContext updateContext) {
        LevelPart block = level.getBlock(xTile, yTile);
        if (block == LevelPart.COIN) {
            this.mario.collectCoin(updateContext);
            level.setBlock(xTile, yTile, 0);
        }

        for (MarioSpriteSlim sprite : sprites) {
            sprite.bumpCheck(xTile, yTile, updateContext);
        }
    }
}