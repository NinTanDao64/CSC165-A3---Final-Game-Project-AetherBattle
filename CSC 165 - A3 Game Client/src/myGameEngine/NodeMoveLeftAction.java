package myGameEngine;

import a3.AetherBattle;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

public class NodeMoveLeftAction extends AbstractInputAction {
	private SceneNode avatar;
	private AetherBattleClient client;
	private AetherBattle gameInstance;
	private float speed = 0.0325f;
	
	public NodeMoveLeftAction(SceneNode n, AetherBattleClient client, AetherBattle game) {
		avatar = n;
		this.client = client;
		this.gameInstance = game;
	}
	
	public void performAction(float time, Event e) {
		Matrix3D curTranslation = (Matrix3D)avatar.getLocalTranslation().clone();
		
		Matrix3D rot = avatar.getLocalRotation();
		Vector3D dir = new Vector3D(-1,0,0);
		dir = dir.mult(rot);
		dir.scale((double)(speed*time));
		avatar.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
		
		//Prevent player from moving beyond walls or going through the 4 colored blocks
		double playerX = avatar.getLocalTranslation().getCol(3).getX();
		double playerZ = avatar.getLocalTranslation().getCol(3).getZ();
		if(playerX >= 199 || playerX <=-199 || playerZ >= 149 || playerZ <= -149 || 
				gameInstance.collidesWithRed(playerX, playerZ) || gameInstance.collidesWithYellow(playerX, playerZ) || gameInstance.collidesWithGreen(playerX, playerZ) || gameInstance.collidesWithBlue(playerX, playerZ)) {
			avatar.setLocalTranslation(curTranslation);
		}
		
		if (client != null) {
			client.getOutputHandler().sendMoveMsg(avatar.getLocalTranslation().getCol(3));
		}
	}
}