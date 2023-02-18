package mff;

import engine.core.MarioGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HumanPlaytesting {
    public static void main(String[] args) {
        MarioGame game = new MarioGame();
        String level = LevelLoader.getLevel("./levels/test/lvl-1.txt");
        game.playGame(level, 10000);
    }
}
