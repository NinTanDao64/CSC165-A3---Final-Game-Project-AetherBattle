package myGameEngine;

import a3.AetherBattle;
import sage.ai.behaviortrees.BTCondition;

public class BeyondWall extends BTCondition {
	private AetherBattle gameInstance;
	private NPCcontroller npcc;
	private NPC npc;
	
	public BeyondWall(NPCcontroller c, NPC n, AetherBattle game, boolean toNegate) {
		super(toNegate);
		gameInstance = game;
		npcc = c;
		npc = n;
	}
	
	protected boolean check() {
		double npcX = npc.getLocation().getX();
		double npcZ = npc.getLocation().getZ();
		if(npcX >= 195 || npcX <=-195 || npcZ >= 145 || npcZ <= -145 || gameInstance.collidesWithRed(npcX, npcZ) || gameInstance.collidesWithYellow(npcX, npcZ)
				|| gameInstance.collidesWithGreen(npcX, npcZ) || gameInstance.collidesWithBlue(npcX, npcZ)) {
			return true;
		} else {
			return false;
		}
	}
}
