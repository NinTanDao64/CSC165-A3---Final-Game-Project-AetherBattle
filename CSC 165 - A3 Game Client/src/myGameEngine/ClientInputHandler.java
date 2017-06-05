package myGameEngine;

import java.util.HashMap;
import java.util.UUID;

import a3.AetherBattle;
import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class ClientInputHandler {
	private AetherBattle game;
	private ClientOutputHandler output;
    private HashMap<UUID, Avatar> ghostAvatars;
	
	public ClientInputHandler(AetherBattle game, ClientOutputHandler output) {
		this.game = game;
		this.output = output;
        this.ghostAvatars = new HashMap<UUID, Avatar>();
	}
	
	protected void processJoin(String msg) {
    	System.out.println("Processing join: " + msg);
    	
        if ("success".equals(msg)) {
            game.setIsConnected(true);
            output.sendCreateMessage(game.getPlayerLocation());
            output.sendWantsDetailsMsg();
        } else {
        	game.setIsConnected(false);
        }
    }
    
    protected void processCreate(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(ghostID, x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
    }
    
    protected void processBye(UUID ghostID) {
    	System.out.println("Processing bye: " + ghostID);
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) 
    		game.removeGhostFromGame(ghost);
    }
    
    protected void processDsfr(UUID ghostID, float x, float y, float z) {
    	if (!ghostAvatars.containsKey(ghostID)) {
	    	System.out.println("Processing create: " + ghostID + "\t" + x + "," + y + "," + z);
	    	Avatar ghost = game.addGhostToGame(ghostID, x, y, z);
	    	ghostAvatars.put(ghostID, ghost);
    	}
	}
    
    protected void processMove(UUID ghostID, float x, float y, float z) {
    	//System.out.println("Processing move: " + ghostID + "\t" + x + "," + y + "," + z); Commented due to verbosity
    	
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D translate = new Matrix3D();
    		translate.translate(x, y, z);
    		ghost.getModel().setLocalTranslation(translate);
    	}
    }
    
    protected void processRotate(UUID ghostID, float col0x, float col0z, float col2x, float col2z) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if (ghost != null) {
    		Matrix3D rotate = new Matrix3D();
    		rotate.setCol(0, new Vector3D(col0x, 0, col0z));
    		rotate.setCol(2, new Vector3D(col2x, 0, col2z));
    		ghost.getModel().setLocalRotation(rotate);
    	}
    }
    
    protected void processBomb(UUID ghostID) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if(ghost!= null) {
    		game.ghostTossBomb(ghost);
    	}
    }
    
    protected void processSpree(UUID ghostID, int spree) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if(ghost!= null) {
    		ghost.setSpree(spree);
    	}
    }
    
    protected void processHit(UUID shooterID, UUID ghostID) {
    	Avatar ghost = ghostAvatars.get(ghostID);
    	
    	if(ghost != null) {
    		ghost.respawn();
    	}
    }
    
    protected void processWsds(UUID ghostID) {
    	System.out.println("Processing wants details: " + ghostID);
    	output.sendDetailsForMessage(ghostID, game.getPlayerLocation());
    }
    
}