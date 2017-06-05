package myGameEngine;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class MyRotationController extends Controller {
	private double rotationRate = 1;
	
	public void update(double time) {
		//Matrix3D newRot = new Matrix3D((rotationRate / 1000.0) * time, new Vector3D(0,1,0));
		Matrix3D newRot = new Matrix3D(0.25 * time, new Vector3D(0,1,0));
		
		for(SceneNode node: controlledNodes) {
			Matrix3D curRot = node.getLocalRotation();
			curRot.concatenate(newRot);
			node.setLocalRotation(curRot);
		}
	}
}
