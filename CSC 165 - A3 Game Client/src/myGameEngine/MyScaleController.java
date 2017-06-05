package myGameEngine;

import graphicslib3D.Matrix3D;
import sage.scene.Controller;
import sage.scene.SceneNode;

public class MyScaleController extends Controller {
	private double scaleRate = 0.002;
	private double cycleTime = 2000.0;
	private double totalTime = 0;
	private double growth = 1.0;
	
	public void setCycleTime(double c) {
		cycleTime = c;
	}
	
	public void update(double time) {
		if(totalTime > cycleTime) {
			growth = -growth;
		} else if (totalTime < 0) {
			growth = -growth;
		}
		
		totalTime += growth * (time);
		
		double scaleAmount = scaleRate * totalTime;
		
		Matrix3D newScale = new Matrix3D();
		newScale.scale(scaleAmount, scaleAmount, scaleAmount);
		
		for(SceneNode node: controlledNodes) {
			/*Matrix3D curScale = node.getLocalScale();
			curScale.concatenate(newScale);
			node.setLocalScale(curScale);*/
			node.setLocalScale(newScale);
		}
	}
}
