package myGameEngine;

import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class NPCTurnAround extends BTAction {
	private NPC npc;
	
	public NPCTurnAround(NPC n) {
		npc = n;
	}
	
	protected BTStatus update(float elapsedTime) {
		npc.turnAround();
		return BTStatus.BH_SUCCESS;
	}
}
