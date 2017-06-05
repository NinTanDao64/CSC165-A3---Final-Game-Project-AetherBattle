package myGameEngine;

import java.util.Random;

import a3.AetherBattle;
import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.scene.TriMesh;
import sage.scene.shape.Cube;

public class NPC extends Cube {
	private Random rand;
	private AetherBattle gameInstance;
	
	public NPC(Point3D position, int degrees, AetherBattle game) {
		rand = new Random();
		gameInstance = game;
		this.setPosition(position);
		this.rotate(degrees, new Vector3D(0, 1, 0)); //Prevent NPC from flying directly at player when game starts
		this.scale(9.0f, 3.0f, 3.0f);
		this.translate(0, 2, 0);
	}
	
	public void updateLocation() {
		Matrix3D rot = this.getLocalRotation();
		Vector3D dir = new Vector3D(0,0,1);
		dir = dir.mult(rot);
		dir.scale((double)(0.125));
		this.translate((float)dir.getX(),(float)dir.getY(),(float)dir.getZ());
	}
	
	public Vector3D getLocation() {
		return this.getLocalTranslation().getCol(3);
	}
	
	public Point3D getPosition() {
		return new Point3D(this.getLocalTranslation().getCol(3));
	}
	
	public void setPosition(Point3D position) {
		Matrix3D trans = new Matrix3D();
		trans.translate(position.getX(), position.getY(), position.getZ());
		this.setLocalTranslation(trans);
	}
	
	//Rotate the NPC by a value between -90 and 90 degrees
	public void turn() {
		int degrees = rand.nextInt(180) - 90;
		this.rotate(degrees, new Vector3D(0,1,0));
	}
	
	public void turnAround() {
		this.rotate(180, new Vector3D(0,1,0));
	}
	
	public void respawn() {
		Point3D locP1 = new Point3D(getLocation());
		int newX = gameInstance.rng(190);
		int newZ = gameInstance.rng(140);
		
		//Prevent player from spawning inside one of the 4 colored blocks
		while(gameInstance.collidesWithRed(newX, newZ) || gameInstance.collidesWithYellow(newX, newZ) || gameInstance.collidesWithGreen(newX, newZ) || gameInstance.collidesWithBlue(newX, newZ)) {
			newX = gameInstance.rng(190);
			newZ = gameInstance.rng(140);
		}
		Vector3D newLoc = new Vector3D(newX, locP1.getY(), newZ);
		this.translate((float) (newLoc.getX() - locP1.getX()), 0, (float) (newLoc.getZ() - locP1.getZ()));
		
		this.rotate(rand.nextFloat()*360, new Vector3D(0,1,0));
	}
}