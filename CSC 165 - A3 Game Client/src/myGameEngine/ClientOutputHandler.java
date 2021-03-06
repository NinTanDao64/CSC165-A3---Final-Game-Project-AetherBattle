package myGameEngine;

import java.io.IOException;
import java.util.UUID;

import graphicslib3D.Matrix3D;
import graphicslib3D.Vector3D;

public class ClientOutputHandler {
	private UUID id;
	private AetherBattleClient client;
	
	public ClientOutputHandler(UUID id, AetherBattleClient client) {
		this.id = id;
		this.client = client;
	}
	
    public void sendCreateMessage(Vector3D pos) {
        // Format: create,localID,x,y,z
        try {
            String msg = new String("create," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending create message: " + msg);
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendJoinMsg() {
        // Format: join,localID
        try {
            String msg = new String("join," + id.toString());
            
            System.out.println("Sending join message: " + msg);
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendByeMessage() {
    	// Format: bye,localID
        try {
            String msg = new String("bye," + id.toString());
            
            System.out.println("Sending bye message: " + msg);
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendDetailsForMessage(UUID remId, Vector3D pos) {
    	// Format: dsfr,localID,remoteID,x,y,z
        try {
            String msg = new String("dsfr," + id.toString() + "," + remId.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            System.out.println("Sending dsfr message: " + msg);
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendMoveMsg(Vector3D pos) {
    	// Format move,localID,x,y,z
        try {
            String msg = new String("move," + id.toString());
            msg += "," + pos.getX() + "," + pos.getY() + "," + pos.getZ();
            
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendRotateMsg(Matrix3D rot) {
    	Vector3D col0 = rot.getCol(0);
    	Vector3D col2 = rot.getCol(2);

    	// Format rot,localID,rotamt
    	try {
    		String msg = new String("rot," + id.toString());
    		msg += "," + col0.getX() + "," + col0.getZ();
    		msg += "," + col2.getX() + "," + col2.getZ();
    		
    		// System.out.println("Sending rotation message: " + msg);
    		client.sendPacket(msg);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void sendWantsDetailsMsg() {
    	// Format wsds,localID
        try {
            String msg = new String("wsds," + id.toString());
            
            System.out.println("Sending wsds: " + msg);
            client.sendPacket(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendBombMsg() {
    	//Format bomb, localID
    	try {
    		String msg = new String("bomb," + id.toString());
    		
    		client.sendPacket(msg);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void sendSpreeMsg(int spree) {
    	//Format: spree, localID, local player spree
    	try {
    		String msg = new String("spree," + id.toString());
    		msg += "," + spree;
    		
    		client.sendPacket(msg);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void sendHitMsg(UUID shooterID) {
    	//Format: hit, shooterID
    	try {
    		String msg = new String("hit," + shooterID);
    		msg += "," + id;
    		
    		client.sendPacket(msg);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    protected UUID getUUID() {
    	return id;
    }
}
