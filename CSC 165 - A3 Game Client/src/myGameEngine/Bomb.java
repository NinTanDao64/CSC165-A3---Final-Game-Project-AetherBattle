package myGameEngine;

import sage.scene.SceneNode;
import sage.scene.TriMesh;

public class Bomb extends TriMesh {
	private TriMesh bomb;
	private Avatar sourcePlayer;
	
	public Bomb (TriMesh bomb, Avatar player) {
		this.bomb = bomb;
		this.sourcePlayer = player;
	}
	
	public Avatar getSourcePlayer() {
		return sourcePlayer;
	}
	
	public TriMesh getModel() {
		return bomb;
	}
}
