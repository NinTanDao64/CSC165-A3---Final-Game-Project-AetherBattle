package myGameEngine;

import sage.ai.behaviortrees.BTAction;
import sage.ai.behaviortrees.BTStatus;

public class NPCTurn extends BTAction {
	private NPC npc;
	
	public NPCTurn(NPC n) {
		npc = n;
	}
	
	protected BTStatus update(float elapsedTime) {
		npc.turn();
		return BTStatus.BH_SUCCESS;
	}
}
