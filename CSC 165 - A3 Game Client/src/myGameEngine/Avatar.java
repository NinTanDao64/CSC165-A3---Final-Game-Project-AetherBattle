package myGameEngine;

import java.awt.Color;
import java.io.File;
import java.util.Random;
import java.util.UUID;

import a3.AetherBattle;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.shape.Pyramid;

public class Avatar {
	private Random rand;
	private UUID uuid;
	private SceneNode model;
	private TriMesh triMesh;
	private AetherBattle gameInstance;
	private float health;
	private boolean hasChanged  = true;
	
	private Sound toss, explosion, hit, collect, death;
	private Sound spree3, spree4, spree5, spree6, spree7, spree8, spree9, spree10;
	
	private int bombCount = 0;
	private int score = 0;
	private int spree = 0;
	
	public Avatar(String name, TriMesh triMesh, AetherBattle game, UUID id) {
		rand = new Random();
		this.triMesh = triMesh;
		this.health = 100.0f;
		
		if(id == null) {
			uuid = UUID.randomUUID();
		} else {
			uuid = id;
		}
		
		this.gameInstance = game;
		
		setupSound();
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public float getHealth() {
		return health;
	}
	
	public void setHealth (float health) {
		this.health = health;
		
		if(this.health > 100.0f) {
			this.health = 100.0f;
		}
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public TriMesh getModel() {
		return triMesh;
	}
	
	public Vector3D getLocation() {
		return triMesh.getLocalTranslation().getCol(3);
	}
	
	public boolean isHasChanged() {
		return hasChanged;
	}
	
	public void setHasChanged(boolean hasChanged) {
		this.hasChanged = hasChanged;
	}
	
	public int getScore() {
		return score;
	}
	
	public void incrementScore() {
		score++;
		if(score>999) {
			score = 999;
		}
		
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public void decrementScore() {
		score--;
		if(score < 0) {
			score = 0;
		}
		
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public void setScore(int totalScore) {
		this.score = totalScore;
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public int getSpree() {
		return spree;
	}
	
	public void setSpree(int x) {
		this.spree = x;
	}
	
	public void incrementSpree() {
		spree++;
	}
	
	//Loot Boxes are worth more if player is on a killing spree
	public void incrementBombCount() {
		if(spree < 3) {
			bombCount++;
		} else if (spree >= 3 && spree <6) {
			bombCount += 2;
		} else if (spree >= 6 && spree < 9) {
			bombCount += 3;
		} else if (spree == 9) {
			bombCount += 4;
		} else if (spree >= 10) {
			bombCount += 5;
		}
		
		if(bombCount > 999) {
			bombCount = 999;
		}
		
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public void decrementBombCount() {
		bombCount--;
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public void setBombCount(int x) {
		bombCount = x;
		if(bombCount > 999) {
			bombCount = 999;
		}
		
		if(this.hasChanged != true) {
			this.hasChanged = true;
		}
	}
	
	public int getBombCount() {
		return bombCount;
	}
	
	public void respawn() {
		if(this == gameInstance.getLocalPlayer()) {
			//Play 'death' sound effect
			playDeath();
			
			Point3D locP1 = new Point3D(getLocation());
			int newX = gameInstance.rng(190);
			int newZ = gameInstance.rng(140);
			
			//Prevent player from spawning inside one of the 4 colored blocks
			while(gameInstance.collidesWithRed(newX, newZ) || gameInstance.collidesWithYellow(newX, newZ) || gameInstance.collidesWithGreen(newX, newZ) || gameInstance.collidesWithBlue(newX, newZ)) {
				newX = gameInstance.rng(190);
				newZ = gameInstance.rng(140);
			}
			Vector3D newLoc = new Vector3D(newX, locP1.getY(), newZ);
			triMesh.translate((float) (newLoc.getX() - locP1.getX()), 0, (float) (newLoc.getZ() - locP1.getZ()));
			
			triMesh.rotate(rand.nextFloat()*360, new Vector3D(0,1,0));
		
			if(gameInstance.getClient() != null) {
				gameInstance.getClient().getOutputHandler().sendMoveMsg(getLocation());
				gameInstance.getClient().getOutputHandler().sendRotateMsg(triMesh.getLocalRotation());
			}
		} else {
			//Play 'hit' sound effect
			playHit();
			
			//Increase player score + killing spree count
			gameInstance.getLocalPlayer().incrementScore();
			gameInstance.getLocalPlayer().incrementSpree();
			
			//If player is on a killing spree, play appropriate sound effect and trigger a popup HUD image
			if(gameInstance.getLocalPlayer().getSpree() == 3) {
				//KILLING SPREE
				playSpree3();
				gameInstance.addSpree3HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 4) {
				//DOMINATING
				playSpree4();
				gameInstance.addSpree4HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 5) {
				//MEGA KILL
				playSpree5();
				gameInstance.addSpree5HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 6) {
				//UNSTOPPABLE
				playSpree6();
				gameInstance.addSpree6HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 7) {
				//WICKED SICK
				playSpree7();
				gameInstance.addSpree7HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 8) {
				//M-M-M-MONSTER KILL
				playSpree8();
				gameInstance.addSpree8HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() == 9) {
				//GODLIKE
				playSpree9();
				gameInstance.addSpree9HUD();
			} else if (gameInstance.getLocalPlayer().getSpree() >= 10) {
				//HOLY SHIT
				playSpree10();
				gameInstance.addSpree10HUD();
			}
		}
	}
	
	private void setupSound() {
		collect = new Sound(gameInstance.getCollectResource(), SoundType.SOUND_EFFECT, 25, false);
		toss = new Sound(gameInstance.getTossResource(), SoundType.SOUND_EFFECT, 25, false);
		explosion = new Sound(gameInstance.getExplosionResource(), SoundType.SOUND_EFFECT, 7, false);
		hit = new Sound(gameInstance.getHitResource(), SoundType.SOUND_EFFECT, 40, false);
		death = new Sound(gameInstance.getDeathResource(), SoundType.SOUND_EFFECT, 25, false);
		spree3 = new Sound(gameInstance.getSpree3Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree4 = new Sound(gameInstance.getSpree4Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree5 = new Sound(gameInstance.getSpree5Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree6 = new Sound(gameInstance.getSpree6Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree7 = new Sound(gameInstance.getSpree7Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree8 = new Sound(gameInstance.getSpree8Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree9 = new Sound(gameInstance.getSpree9Resource(), SoundType.SOUND_EFFECT, 50, false);
		spree10 = new Sound(gameInstance.getSpree10Resource(), SoundType.SOUND_EFFECT, 50, false);
		
		collect.initialize(gameInstance.getAudioManager());
		toss.initialize(gameInstance.getAudioManager());
		explosion.initialize(gameInstance.getAudioManager());
		hit.initialize(gameInstance.getAudioManager());
		death.initialize(gameInstance.getAudioManager());
		spree3.initialize(gameInstance.getAudioManager());
		spree4.initialize(gameInstance.getAudioManager());
		spree5.initialize(gameInstance.getAudioManager());
		spree6.initialize(gameInstance.getAudioManager());
		spree7.initialize(gameInstance.getAudioManager());
		spree8.initialize(gameInstance.getAudioManager());
		spree9.initialize(gameInstance.getAudioManager());
		spree10.initialize(gameInstance.getAudioManager());
		
		collect.setMaxDistance(300.0f);
		collect.setMinDistance(3.0f);
		collect.setRollOff(5.0f);
		
		toss.setMaxDistance(400.0f);
		toss.setMinDistance(3.0f);
		toss.setRollOff(5.0f);
		
		explosion.setMaxDistance(400.0f);
		explosion.setMinDistance(3.0f);
		explosion.setRollOff(5.0f);
		
		hit.setMaxDistance(400.0f);
		hit.setMinDistance(3.0f);
		hit.setRollOff(5.0f);
		
		death.setMaxDistance(2000.0f);
		death.setMinDistance(1.0f);
		death.setRollOff(1.0f);
		
		spree3.setMaxDistance(2000.0f);
		spree3.setMinDistance(1.0f);
		spree3.setRollOff(1.0f);
		
		spree4.setMaxDistance(2000.0f);
		spree4.setMinDistance(1.0f);
		spree4.setRollOff(1.0f);
		
		spree5.setMaxDistance(2000.0f);
		spree5.setMinDistance(1.0f);
		spree5.setRollOff(1.0f);
		
		spree6.setMaxDistance(2000.0f);
		spree6.setMinDistance(1.0f);
		spree6.setRollOff(1.0f);
		
		spree7.setMaxDistance(2000.0f);
		spree7.setMinDistance(1.0f);
		spree7.setRollOff(1.0f);
		
		spree8.setMaxDistance(2000.0f);
		spree8.setMinDistance(1.0f);
		spree8.setRollOff(1.0f);
		
		spree9.setMaxDistance(2000.0f);
		spree9.setMinDistance(1.0f);
		spree9.setRollOff(1.0f);
		
		spree10.setMaxDistance(2000.0f);
		spree10.setMinDistance(1.0f);
		spree10.setRollOff(1.0f);
	}
	
	public void releaseSounds() {
		collect.release(gameInstance.getAudioManager());
		toss.release(gameInstance.getAudioManager());
		explosion.release(gameInstance.getAudioManager());
		hit.release(gameInstance.getAudioManager());
		death.release(gameInstance.getAudioManager());
		
		spree3.release(gameInstance.getAudioManager());
		spree4.release(gameInstance.getAudioManager());
		spree5.release(gameInstance.getAudioManager());
		spree6.release(gameInstance.getAudioManager());
		spree7.release(gameInstance.getAudioManager());
		spree8.release(gameInstance.getAudioManager());
		spree9.release(gameInstance.getAudioManager());
		spree10.release(gameInstance.getAudioManager());
	}
	
	public void updateSoundLocation(Sound sound) {
		Point3D newLoc = new Point3D(getLocation().getX(), getLocation().getY(), getLocation().getZ());
		sound.setLocation(newLoc);
	}
	
	public void playCollect() {
		updateSoundLocation(collect);
		collect.play();
	}
	
	public void playToss() {
		updateSoundLocation(toss);
		toss.play();
	}
	
	public void playExplosion() {
		updateSoundLocation(explosion);
		explosion.play();
	}
	
	public void playHit() {
		updateSoundLocation(hit);
		hit.play();
	}
	
	public void playDeath() {
		updateSoundLocation(death);
		death.play();
	}
	
	public void playSpree3() {
		updateSoundLocation(spree3);
		spree3.play();
	}
	
	public void playSpree4() {
		updateSoundLocation(spree3);
		spree4.play();
	}
	
	public void playSpree5() {
		updateSoundLocation(spree3);
		spree5.play();
	}
	
	public void playSpree6() {
		updateSoundLocation(spree3);
		spree6.play();
	}
	
	public void playSpree7() {
		updateSoundLocation(spree3);
		spree7.play();
	}
	
	public void playSpree8() {
		updateSoundLocation(spree3);
		spree8.play();
	}
	
	public void playSpree9() {
		updateSoundLocation(spree3);
		spree9.play();
	}
	
	public void playSpree10() {
		updateSoundLocation(spree3);
		spree10.play();
	}
	
	public void setExplosionLocation(Point3D loc) {
		explosion.setLocation(loc);
	}
}
