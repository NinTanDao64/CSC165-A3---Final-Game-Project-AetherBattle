package myGameEngine;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

import a3.AetherBattle;
import sage.networking.client.GameConnectionClient;

public class AetherBattleClient extends GameConnectionClient {
    private UUID uuid;
    private ClientInputHandler input;
    private ClientOutputHandler output;
    
    public AetherBattleClient(InetAddress remAddr, int remPort, ProtocolType pType, AetherBattle game) throws IOException {
        super(remAddr, remPort, pType);
        
        this.uuid = UUID.randomUUID();
        this.output = new ClientOutputHandler(uuid, this);
        this.input = new ClientInputHandler(game, output);
    }
    
    public UUID getUUID() {
    	return uuid;
    }
    
    @Override
    protected void processPacket(Object o) {
        String msg = (String)o;
        
        if (msg == null)
        	return;
        
        String msgTokens[] = msg.split(",");
        
        if (msgTokens.length > 0) {
            switch(msgTokens[0]) {
                case "join":
                    input.processJoin(msgTokens[1]);
                    break;
                    
                case "create":
                	input.processCreate(UUID.fromString(msgTokens[1]),
                				  Float.parseFloat(msgTokens[2]), 
                				  Float.parseFloat(msgTokens[3]), 
                				  Float.parseFloat(msgTokens[4]));
                	break;
                	
                case "bye":
                	input.processBye(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "dsfr":
                	input.processDsfr(UUID.fromString(msgTokens[1]), 
                    		    Float.parseFloat(msgTokens[2]), 
                    		    Float.parseFloat(msgTokens[3]), 
                    		    Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "wsds":
                	input.processWsds(UUID.fromString(msgTokens[1]));
                    break;
                    
                case "move":
                	input.processMove(UUID.fromString(msgTokens[1]), 
                    			Float.parseFloat(msgTokens[2]), 
                    			Float.parseFloat(msgTokens[3]), 
                    			Float.parseFloat(msgTokens[4]));
                    break;
                    
                case "rot":
                	input.processRotate(UUID.fromString(msgTokens[1]), 
                				  Float.parseFloat(msgTokens[2]), 
                				  Float.parseFloat(msgTokens[3]), 
                				  Float.parseFloat(msgTokens[4]), 
                				  Float.parseFloat(msgTokens[5]));
                	break;
                	
                case "bomb":
                	input.processBomb(UUID.fromString(msgTokens[1]));
                	break;
                
                case "spree":
                	input.processSpree(UUID.fromString(msgTokens[1]),
                			      Integer.parseInt(msgTokens[2]));
                	break;
                case "hit":
                	input.processHit(UUID.fromString(msgTokens[1]),
             			   		  UUID.fromString(msgTokens[2]));
                	break;
            }
        }
    }
    
    public ClientOutputHandler getOutputHandler() {
    	return output;
    }
}
