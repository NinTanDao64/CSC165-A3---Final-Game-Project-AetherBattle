package myGameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import net.java.games.input.Event;
import sage.input.action.AbstractInputAction;
import sage.scene.SceneNode;

public class NodeTurnLeftAction extends AbstractInputAction {
	private SceneNode avatar;
	private AetherBattleClient client;
	private double rotationRate = 1;
	private float speed = 0.01f;
	private Vector3D rotationAxis;
	
	public NodeTurnLeftAction(SceneNode n, AetherBattleClient client) {
		avatar = n;
		this.client = client;
	}
	
	public void performAction(float time, Event e) {
		rotationAxis = new Vector3D(0, 1, 0);
		double rotAmount = rotationRate/10 * time;
		Matrix3D newRot = new Matrix3D(rotAmount, rotationAxis);
		Matrix3D curRot = avatar.getLocalRotation();
		curRot.concatenate(newRot);
		avatar.setLocalRotation(curRot);
		
		if (client != null) {
			client.getOutputHandler().sendRotateMsg(avatar.getLocalRotation());
		}
	}
}