package mff.agents.astarPlanningDynamic;

import mff.agents.astarHelper.MarioAction;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import java.util.ArrayList;

public class Agent implements  IMarioAgentMFF {
    private ArrayList<boolean[]> actionsList = new ArrayList<>();
    private AStarTree tree;
    private boolean findTempPlan = true;
    private int ticksRemaining;
    private boolean startNewFinishSearch;
    private boolean finalPlanExtracted;
    private boolean winFoundDuringTempSearch;

    @Override
    public void initialize(MarioForwardModelSlim model) {
        AStarTree.exitTileX = model.getWorld().level.exitTileX * 16;
    }

    @Override
    public boolean[] getActions(MarioForwardModelSlim model, MarioTimerSlim timer) {
        if (tree != null && tree.winFound) {
            if (actionsList.size() == 0) {
                if (!finalPlanExtracted && !winFoundDuringTempSearch) {
                    //System.out.println("Getting final plan");
                    finalPlanExtracted = true;
                    actionsList = tree.getPlanToFinish();
                    return actionsList.remove(actionsList.size() - 1);
                }
                return MarioAction.NO_ACTION.value;
            }
            else {
                return actionsList.remove(actionsList.size() - 1);
            }
        }

        if (findTempPlan) {
            findTempPlan = false;
            AStarTree tree = new AStarTree();
            tree.initPlanAhead(model, 3);
            tree.planAhead(timer);
            //System.out.println("Temp plan init and search");
            if (tree.winFound) {
                actionsList = tree.getPlanToFinish();
                winFoundDuringTempSearch = true;
            }
            else {
                actionsList = tree.getTempSafePlan();
            }
            ticksRemaining = actionsList.size() - 1;
            startNewFinishSearch = true;
            assert actionsList.size() != 0;
            return actionsList.remove(actionsList.size() - 1);
        }

        if (ticksRemaining == 1) {
            findTempPlan = true;
            return actionsList.remove(actionsList.size() - 1);
        }

        if (startNewFinishSearch) {
            //System.out.println("New finish search init");
            startNewFinishSearch = false;
            tree = new AStarTree();
            for (int i = 0; i < ticksRemaining; i++) {
                model.advance(actionsList.get(actionsList.size() - (1 + i)));
            }
            tree.initPlanToFinish(model, 2);
        }

        assert tree != null;
        //System.out.println("Searching for finish");
        tree.planToFinish(timer);

        ticksRemaining--;
        return actionsList.remove(actionsList.size() - 1);
    }

    @Override
    public String getAgentName() {
        return "MFF Dynamic Planning AStar Agent";
    }
}
