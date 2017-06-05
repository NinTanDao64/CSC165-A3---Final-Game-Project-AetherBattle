package myGameEngine;

import java.awt.Color;

import a3.AetherBattle;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;
import sage.scene.SceneNode;
import sage.scene.shape.Sphere;

public class Explosion extends Sphere {
	private Avatar sourcePlayer;
	
	private double scaleRate = 0.04;
	private double maxTime;
	private double totalTime = 0;
	private double growth = 1.2;
	
	public Explosion(AetherBattle game, Avatar player) {
		super(1, 10, 10, Color.WHITE);
		super.scale(2f, 2f, 2f);
		sourcePlayer = player;
		
		int spree = sourcePlayer.getSpree();
		if(spree < 3) {
			setColor(Color.WHITE);
		} else if (spree >= 3 && spree <6) {
			setColor(Color.ORANGE);
		} else if (spree >= 6 && spree < 9) {
			setColor(Color.MAGENTA);
		} else if (spree == 9) {
			setColor(Color.PINK);
		} else if (spree >= 10) {
			setColor(Color.GREEN);
		}
		
		/*
		//Could not get ghost avatars to update explosion radius with enemy player's current spree
		//Used to cap the max bomb radius at 10 player kill streak
		if (getSpree >= 10) {
			getSpree = 10;
		}
		
		maxTime = 750 + (150 * getSpree); //Max explosion radius increased by 100 per kill in the current spree
		*/
		maxTime = 1150;
		
		if(sourcePlayer != game.getLocalPlayer()) {
			setColor(Color.RED);
		}
	}
	
	public Avatar getSourcePlayer() {
		return sourcePlayer;
	}
	
	public boolean isExpired() {
		return totalTime >= maxTime;
	}
	
	public void update(double time) {	
		/*if(totalTime >= maxTime) {
			growth = -growth*5;
		} else if (totalTime < 0) {
			growth = -1000;
		}*/
		
		totalTime += growth * (time);
		
		double scaleAmount = scaleRate * totalTime;
		
		//Increase explosion radius every update frame
		Matrix3D newScale = new Matrix3D();
		newScale.scale(scaleAmount, scaleAmount, scaleAmount);
		setLocalScale(newScale);
		
		//Rotate the explosion radius every update frame
		Matrix3D newRot = new Matrix3D(0.25 * time, new Vector3D(0,1,0));
		Matrix3D curRot = getLocalRotation();
		curRot.concatenate(newRot);
		setLocalRotation(curRot);
	}
}
