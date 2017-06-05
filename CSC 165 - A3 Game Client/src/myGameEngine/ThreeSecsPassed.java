package myGameEngine;

import sage.ai.behaviortrees.BTCondition;

public class ThreeSecsPassed extends BTCondition {
	private NPCcontroller npcc;
	private NPC npc;
	float lastUpdateTime;
	
	public ThreeSecsPassed(NPCcontroller c, NPC n, boolean toNegate) {
		super(toNegate);
		npcc = c;
		npc = n;
		lastUpdateTime = System.nanoTime();
	}
	
	protected boolean check() {
		float elapsedMilliSecs = (System.nanoTime() - lastUpdateTime) / (1000000.0f);
		if(elapsedMilliSecs >= 3000.0f) {
			lastUpdateTime = System.nanoTime();
			return true;
		} else {
			return false;
		}
	}
}
