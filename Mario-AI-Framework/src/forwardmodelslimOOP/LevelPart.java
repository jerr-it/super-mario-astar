package forwardmodelslimOOP;

import engine.helper.SpriteType;

// TODO: can this be byte, not int?
public enum LevelPart {
    // sprites - multiplied by -1 to avoid collisions with tiles
    // only sprites that are a part of the level layout
    GOOMBA(-2),
    GOOMBA_WINGED(-3),
    RED_KOOPA(-4),
    RED_KOOPA_WINGED(-5),
    GREEN_KOOPA(-6),
    GREEN_KOOPA_WINGED(-7),
    SPIKY(-8),
    SPIKY_WINGED(-9),
    ENEMY_FLOWER(-11), // stored as PIPE_TOP_LEFT

    // tiles
    EMPTY(0),
    GROUND_BLOCK(1),
    PYRAMID_BLOCK(2),
    BULLET_BILL_CANNON(3),
    BULLET_BILL_BASE(4),
    BULLET_BILL_COLUMN(5),
    NORMAL_BRICK_BLOCK(6),
    COIN_BRICK_BLOCK(7),
    POWER_UP_QUESTION_BLOCK(8),
    COIN_QUESTION_BLOCK(11),
    USED(14),
    COIN(15),
    PIPE_TOP_LEFT(18), // ENEMY_FLOWER is here
    PIPE_TOP_RIGHT(19),
    PIPE_BODY_LEFT(20),
    PIPE_BODY_RIGHT(21),
    JUMP_THROUGH_BLOCK_ALONE(43),
    JUMP_THROUGH_BLOCK_LEFT(44),
    JUMP_THROUGH_BLOCK_RIGHT(45),
    JUMP_THROUGH_BLOCK_CENTER(46),
    JUMP_THROUGH_BLOCK_BACKGROUND(47),
    INVISIBLE_HEALTH_UP_BLOCK(48),
    INVISIBLE_COIN_BLOCK(49),
    POWER_UP_BRICK_BLOCK(50),
    HEALTH_UP_BRICK_BLOCK(51),
    PIPE_SINGLE_TOP(52),
    PIPE_SINGLE_BODY(53);


    private int value;

    LevelPart(int value) {
        this.value = value;
    }

    static LevelPart getLevelPart(int value, boolean levelTile) {
        if (!levelTile)
            value *= -1;
        for (LevelPart levelPart : LevelPart.values()) {
            if (levelPart.value == value)
                return levelPart;
        }
        throw new IllegalArgumentException();
    }

    static int getLevelBlock(LevelPart levelPart) {
        if (levelPart.value < 0)
            return 0;
        else
            return levelPart.value;
    }

    static SpriteType getLevelSprite(LevelPart levelPart) {
        if (levelPart == PIPE_TOP_LEFT)
            return SpriteType.getSpriteType(-ENEMY_FLOWER.value);

        int value = levelPart.value;
        if (value >= 0)
            return SpriteType.NONE;
        else {
            value *= -1;
            return SpriteType.getSpriteType(value);
        }
    }
}
