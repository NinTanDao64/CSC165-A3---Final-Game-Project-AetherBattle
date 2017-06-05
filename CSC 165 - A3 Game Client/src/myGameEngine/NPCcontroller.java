package myGameEngine;

import a3.AetherBattle;
import graphicslib3D.Point3D;
import sage.ai.behaviortrees.BTCompositeType;
import sage.ai.behaviortrees.BTSequence;
import sage.ai.behaviortrees.BehaviorTree;
import sage.scene.TriMesh;

public class NPCcontroller {	
	private BehaviorTree bt = new BehaviorTree(BTCompositeType.SELECTOR);
	private AetherBattle gameInstance;
	private NPC npc;
	private NPC npc2;
	
	public NPCcontroller(AetherBattle game) {
		gameInstance = game;
	}
	
	public void startNPCcontrol() {
		setupNPC();
		setupBehaviorTree();
		//npcLoop();
	}
	
	public void npcLoop(float elapsedTime) {
		npc.updateLocation();
		//npc2.updateLocation();
		bt.update(elapsedTime);
	}
	
	public void setupBehaviorTree() {
		bt.insertAtRoot(new BTSequence(10));
		bt.insertAtRoot(new BTSequence(20));
		bt.insert(10,  new ThreeSecsPassed(this, npc, false));
		bt.insert(10, new NPCTurn(npc));
		bt.insert(20,  new BeyondWall(this, npc, gameInstance, false));
		bt.insert(20, new NPCTurnAround(npc));
	}
	
	public void setupNPC() {
		npc = new NPC(new Point3D(0,0,0), 90, gameInstance);
		//npc2 = new NPC(new Point3D(0,0,0), -45);
		
	}
	
	public NPC getNPC() {
		return npc;
	}
	
	public NPC getNPC2() {
		return npc2;
	}
}
