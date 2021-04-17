package forwardmodel.slim.core;

import java.awt.image.VolatileImage;
import java.awt.*;
import java.awt.event.KeyAdapter;

import javax.swing.JFrame;

import agents.human.Agent;
import engine.core.*;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import forwardmodel.common.Converter;

public class MarioGameSlim {

    public static final long maxTime = 40;
    public static final long graceTime = 10;
    public static final int width = 256;
    public static final int height = 256;
    public static final boolean verbose = false;
    public boolean pause = false;

    //visualization
    private MarioRender render = null;
    private MarioAgent agent = null;

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    private void setAgent(MarioAgent agent) {
        this.agent = agent;
        if (agent instanceof KeyAdapter) {
            this.render.addKeyListener((KeyAdapter) this.agent);
        }
    }

    public void playGame(String level, int timer, int marioState) {
        runGame(new Agent(), level, timer, marioState, true, 30, 2);
    }

    public void runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals) {
        runGame(agent, level, timer, marioState, visuals, visuals ? 30 : 0, 2);
    }

    public void runGame(MarioAgent agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {
        if (visuals) {
            JFrame window = new JFrame("Mario AI Framework");
            this.render = new MarioRender(scale);
            window.setContentPane(this.render);
            window.pack();
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.render.init();
            window.setVisible(true);
        }
        this.setAgent(agent);
        gameLoop(level, timer, marioState, visuals, fps);
    }

    private void gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
        MarioWorld world = new MarioWorld(null);
        world.visuals = visual;
        world.initializeLevel(level, 1000 * timer);
        if (visual) {
            world.initializeVisuals(this.render.getGraphicsConfiguration());
        }
        world.mario.isLarge = marioState > 0;
        world.mario.isFire = marioState > 1;
        world.update(new boolean[MarioActions.numberOfActions()]);
        long currentTime = System.currentTimeMillis();

        // add slim model to test it
        int levelCutoutTileWidth = 0;
        MarioForwardModel originalModel = new MarioForwardModel(world.clone());
        MarioForwardModelSlim slimModel = Converter.originalToSlim(originalModel, levelCutoutTileWidth);

        //initialize graphics
        VolatileImage renderTarget = null;
        Graphics backBuffer = null;
        Graphics currentBuffer = null;
        if (visual) {
            renderTarget = this.render.createVolatileImage(MarioGameSlim.width, MarioGameSlim.height);
            backBuffer = this.render.getGraphics();
            currentBuffer = renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
        }

        MarioTimer agentTimer = new MarioTimer(MarioGameSlim.maxTime);
        this.agent.initialize(new MarioForwardModel(world.clone()), agentTimer);

        while (world.gameStatus == GameStatus.RUNNING) {
            if (!this.pause) {

                //get actions
                agentTimer = new MarioTimer(MarioGameSlim.maxTime);
                boolean[] actions = this.agent.getActions(new MarioForwardModel(world.clone()), agentTimer);

                // create a copy to prevent actions from changing during updates
                boolean[] actionsCopy = new boolean[5];
                for (int i = 0; i < 5; i++) {
                    actionsCopy[i] = actions[i];
                }

                if (MarioGameSlim.verbose) {
                    if (agentTimer.getRemainingTime() < 0 && Math.abs(agentTimer.getRemainingTime()) > MarioGameSlim.graceTime) {
                        System.out.println("The Agent is slowing down the game by: "
                                + Math.abs(agentTimer.getRemainingTime()) + " msec.");
                    }
                }

                // update world
                world.update(actionsCopy);

                // clone slim model and advance it
                MarioForwardModelSlim slimClone = slimModel.clone();
                slimClone.advance(actionsCopy);

                // advance slim model
                slimModel.advance(actionsCopy);

                // create control slim model
                originalModel = new MarioForwardModel(world.clone());
                MarioForwardModelSlim controlSlimModel = Converter.originalToSlim(originalModel, levelCutoutTileWidth);

                // test slim model
                if (!slimModel.equals(controlSlimModel)) {
                    System.out.println("SLIM MODEL NOT EQUAL");
                    throw new RuntimeException("SLIM MODEL NOT EQUAL");
                }

                // test slim model clone
                if (!slimClone.equals(controlSlimModel)) {
                    System.out.println("SLIM MODEL CLONE NOT EQUAL");
                    throw new RuntimeException("SLIM MODEL CLONE NOT EQUAL");
                }
            }

            //render world
            if (visual) {
                this.render.renderWorld(world, renderTarget, backBuffer, currentBuffer);
            }

            //check if delay needed
            if (this.getDelay(fps) > 0) {
                try {
                    currentTime += this.getDelay(fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
