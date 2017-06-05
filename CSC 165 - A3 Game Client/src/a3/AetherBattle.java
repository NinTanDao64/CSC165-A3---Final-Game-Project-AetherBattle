package a3;

import myGameEngine.AetherBattleClient;
import myGameEngine.Avatar;
import myGameEngine.Bomb;
import myGameEngine.Camera3Pcontroller;
import myGameEngine.Explosion;
import myGameEngine.HUDNumber;
import myGameEngine.MyDisplaySystem;
import myGameEngine.MyRotationController;
import myGameEngine.MyScaleController;
import myGameEngine.MyTranslationController;
import myGameEngine.NPCcontroller;
import myGameEngine.NodeMoveBackwardAction;
import myGameEngine.NodeMoveForwardAction;
import myGameEngine.NodeMoveLeftAction;
import myGameEngine.NodeMoveRightAction;
import myGameEngine.NodeTurnLeftAction;
import myGameEngine.NodeTurnRightAction;
import myGameEngine.QuitGameAction;
import myGameEngine.SceneManager;
import net.java.games.input.Component.Identifier.Axis;
import net.java.games.input.Controller;
import net.java.games.input.Event;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.audio.AudioManagerFactory;
import sage.audio.AudioResource;
import sage.audio.AudioResourceType;
import sage.audio.IAudioManager;
import sage.audio.Sound;
import sage.audio.SoundType;
import sage.camera.ICamera;
import sage.camera.JOGLCamera;
import sage.display.IDisplaySystem;
import sage.event.EventManager;
import sage.event.IEventManager;
import sage.input.IInputManager;
import sage.input.InputManager;
import sage.input.ThirdPersonCameraController;
import sage.input.action.AbstractInputAction;
import sage.input.action.IAction;
import sage.networking.IGameConnection.ProtocolType;
import sage.physics.IPhysicsEngine;
import sage.physics.IPhysicsObject;
import sage.physics.PhysicsEngineFactory;
import sage.renderer.IRenderer;
import sage.scene.Group;
import sage.scene.HUDImage;
import sage.scene.HUDString;
import sage.scene.SceneNode;
import sage.scene.TriMesh;
import sage.scene.bounding.BoundingVolume;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.shape.Cube;
import sage.scene.shape.Line;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.shape.Sphere;
import sage.texture.Texture;
import sage.texture.TextureManager;

public class AetherBattle extends BaseGame {
	private boolean developmentDone = false;
	
	private NPCcontroller npcControl;
	
	private int score = 0, score2 = 0;
	private float restoreLootCounter = 0;
	private float time = 0;
	private HUDString scoreString1, scoreString2;
	private HUDString timeString;
	private String directory = "." + File.separator;
	private String dirAudio = "audio" + File.separator;
	private String dirHud = "images" + File.separator + "hud" + File.separator;
	
	//Environment objects (floor, walls, ect)
	private Group environmentGroup = new Group("Environment Group");
	private Group boundaryGroup = new Group("Boundary Group");
	private Group solarSystemGroup;
	private Texture groundTexture;
	public double xBound = 400f;
	public double yBound = 300f;
	
	private ScriptEngine jsEngine;
	
	//Used for referencing ~center of gameworld
	private float origin = 65f;
	
	//Arrays for adding/removing plants in gameworld
	private int veggieIdx;
	private static ArrayList<Bomb> bombList = new ArrayList<Bomb>();
	private static ArrayList<Bomb> removeBombList = new ArrayList<Bomb>();
	private static ArrayList<Explosion> explosionList = new ArrayList<Explosion>();
	private static ArrayList<Explosion> removeExplosionList = new ArrayList<Explosion>();
	private static ArrayList<SceneNode> removeModelList = new ArrayList<SceneNode>();
	private static ArrayList<SceneNode> removeLootList = new ArrayList<SceneNode>();
	private static ArrayList<SceneNode> restoreLootList = new ArrayList<SceneNode>();
	private static ArrayList<HUDImage> HUDList = new ArrayList<HUDImage>();
	
	private Group lootBoxes;
	
	private Avatar player1;
	private TriMesh testBomb;
	private TriMesh loot1, loot2, loot3, loot4, loot5, loot6, loot7, loot8, loot9, loot10, loot11, loot12, loot13; //Center boxes
	private TriMesh loot14, loot15, loot16, loot17, loot18; //Red-Yellow Intersection
	private TriMesh loot19, loot20, loot21, loot22, loot23; //Green-Blue Intersection
	private TriMesh loot24, loot25, loot26, loot27, loot28; //Yellow-Green Intersection
	private TriMesh loot29, loot30, loot31, loot32, loot33; //Red-Blue Intersection
	private TriMesh loot34, loot35, loot36; //Red Corner
	private TriMesh loot37, loot38, loot39; //Yellow Corner
	private TriMesh loot40, loot41, loot42; //Green Corner
	private TriMesh loot43, loot44, loot45; //Blue Corner
	
	private TriMesh redBlock, yellowBlock, greenBlock, blueBlock;
	
	private Bomb playerBomb, ghostBomb;
	private Explosion bombExplosion;
	private Vector3D bombLoc;
	private double bombXLoc, bombZLoc;
	
	public Vector<Avatar> ghostPlayers;
	public int ghostCount = 0;
	
	private HUDNumber scoreHUD;
	private HUDNumber bombHUD;
	private HUDImage p1Bombs;
	private HUDImage p1Score;
	private HUDImage spree3_image, spree4_image, spree5_image, spree6_image, spree7_image, spree8_image, spree9_image, spree10_image;
	private boolean spreeFlag = false;
	private int spreeCountdown = 0;
	private int spreeLimit = 3500;
	
	private AetherBattleClient gameClient;
	private String serverAddr;
	private int serverPort;
	private ProtocolType pType;
	private boolean isConnected;
	
	private IDisplaySystem display;
	private IRenderer renderer;
	private ICamera camera1, camera2;
	public Camera3Pcontroller cam1Controller, cam2Controller;
	private IInputManager im;
	private IEventManager eventMgr;
	private SceneManager sceneManager;
	private Cursor crossHairCursor;
	
	//Audio
	IAudioManager audioMgr;
	AudioResource bgMusicResource, collectResource, tossResource, explosionResource, hitResource, deathResource;
	AudioResource spree3_resource, spree4_resource, spree5_resource, spree6_resource, spree7_resource, spree8_resource, spree9_resource , spree10_resource;
	private Sound bgMusicSound;

	private String gpName;
	private String kbName;
	private int numCrashes = 0;
	
	//Physics variables
	private IPhysicsEngine physicsEngine;
	private IPhysicsObject ballP, groundPlaneP, ballPhysicsObj, ghostBallPhysicsObj;
	
	private boolean running = true;
	
	private Matrix3D mat;
	private CollisionDispatcher colDispatcher;
	private BroadphaseInterface broadPhaseHandler;
	private ConstraintSolver solver;
	private CollisionConfiguration colConfig;
	private RigidBody physicsGround;
	private RigidBody physicsBall;
	private int maxProxies = 1024;
	private Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
	private Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
	private DynamicsWorld physicsWorld;
	
	private Random rand = new Random();
		
	 protected void shutdown()
	 { 
		 display.close() ;
		 
		 //Cleanup GameClient
		 if(gameClient != null) {
			 try {
				 gameClient.getOutputHandler().sendByeMessage();
				 gameClient.shutdown();
			 } catch (IOException e) {
				 e.printStackTrace();
			 }
		 }
		 
		 //Cleanup Audio
		 bgMusicSound.release(audioMgr);
		 player1.releaseSounds();
		 
		 bgMusicResource.unload();
		 collectResource.unload();
		 tossResource.unload();
		 explosionResource.unload();
		 hitResource.unload();
		 deathResource.unload();
		 
		 spree3_resource.unload();
		 spree4_resource.unload();
		 spree5_resource.unload();
		 spree6_resource.unload();
		 spree7_resource.unload();
		 spree8_resource.unload();
		 spree9_resource.unload();
		 spree10_resource.unload();
		 
		 audioMgr.shutdown();
	 }
	 
	 protected void initSystem()
	 { 	
		 //call a local method to create a DisplaySystem object
		 display = new MyDisplaySystem(1920, 1080, 32, 60, false, "sage.renderer.jogl.JOGLRenderer", "Aether Battle");
		 ((MyDisplaySystem) display).waitForInitialization();
		 setDisplaySystem(display);
		 
		 //create an Input Manager
		 im = new InputManager();
		 setInputManager(im);
		 
		 //create an (empty) gameworld
		 ArrayList<SceneNode> gameWorld = new ArrayList<SceneNode>();
		 setGameWorld(gameWorld);
	 }

	
	protected void initGame() {
		if(!runAsSinglePlayer()) {
			initGameClient();
		}
		renderer = getDisplaySystem().getRenderer();
		eventMgr = EventManager.getInstance();
		im = getInputManager();
		sceneManager = new SceneManager(directory);
		initAudio();
		buildWallFromScript();
		initGameObjects();
		createPlayers();
		initInput();
		addGameWorldObject(sceneManager.initTerrain(display));
		
		crossHairCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		renderer.getCanvas().setCursor(crossHairCursor);
		
		//physics stuff
		initPhysicsInput();
		createGraphicsScene();
		initPhysicsSystem();
		//createSagePhysicsWorld();  //This was used to test ball dropping from sample code
		
		setEarParameters();
		
		if (gameClient != null) {
			gameClient.getOutputHandler().sendRotateMsg(player1.getModel().getLocalRotation());
		}
		
		super.update((float) 0.0);
	}
	
	private void initAudio() {
		audioMgr = AudioManagerFactory.createAudioManager("sage.audio.joal.JOALAudioManager");
		
		if(!audioMgr.initialize()) {
			System.out.println("Failed to initialize Audio Manager.");
			return;
		}
		
		bgMusicResource = audioMgr.createAudioResource(directory + dirAudio + "bgMusic.wav", AudioResourceType.AUDIO_SAMPLE);
		collectResource = audioMgr.createAudioResource(directory + dirAudio + "collect.wav", AudioResourceType.AUDIO_SAMPLE);
		tossResource = audioMgr.createAudioResource(directory + dirAudio + "toss.wav", AudioResourceType.AUDIO_SAMPLE);
		explosionResource = audioMgr.createAudioResource(directory + dirAudio + "explosion.wav", AudioResourceType.AUDIO_SAMPLE);
		hitResource = audioMgr.createAudioResource(directory + dirAudio + "hit.wav", AudioResourceType.AUDIO_SAMPLE);
		deathResource = audioMgr.createAudioResource(directory + dirAudio + "death.wav", AudioResourceType.AUDIO_SAMPLE);
		//--------------------------------------------------------------------------------------------------------------------------
		spree3_resource = audioMgr.createAudioResource(directory + dirAudio + "KILLING_SPREE.wav", AudioResourceType.AUDIO_SAMPLE);
		spree4_resource = audioMgr.createAudioResource(directory + dirAudio + "DOMINATING.wav", AudioResourceType.AUDIO_SAMPLE);
		spree5_resource = audioMgr.createAudioResource(directory + dirAudio + "MEGA_KILL.wav", AudioResourceType.AUDIO_SAMPLE);
		spree6_resource = audioMgr.createAudioResource(directory + dirAudio + "UNSTOPPABLE.wav", AudioResourceType.AUDIO_SAMPLE);
		spree7_resource = audioMgr.createAudioResource(directory + dirAudio + "WICKED_SICK.wav", AudioResourceType.AUDIO_SAMPLE);
		spree8_resource = audioMgr.createAudioResource(directory + dirAudio + "MONSTER_KILL.wav", AudioResourceType.AUDIO_SAMPLE);
		spree9_resource = audioMgr.createAudioResource(directory + dirAudio + "GODLIKE.wav", AudioResourceType.AUDIO_SAMPLE);
		spree10_resource = audioMgr.createAudioResource(directory + dirAudio + "HOLY_SHIT.wav", AudioResourceType.AUDIO_SAMPLE);
		
		bgMusicSound = new Sound(bgMusicResource, SoundType.SOUND_MUSIC, 25, true);
		bgMusicSound.initialize(audioMgr);
		bgMusicSound.play();
	}
	
	public AudioResource getCollectResource() {
		return collectResource;
	}
	
	public AudioResource getTossResource() {
		return tossResource;
	}
	
	public AudioResource getExplosionResource() {
		return explosionResource;
	}
	
	public AudioResource getHitResource() {
		return hitResource;
	}
	
	public AudioResource getDeathResource() {
		return deathResource;
	}
	
	public AudioResource getSpree3Resource() {
		return spree3_resource;
	}
	
	public AudioResource getSpree4Resource() {
		return spree4_resource;
	}
	
	public AudioResource getSpree5Resource() {
		return spree5_resource;
	}
	
	public AudioResource getSpree6Resource() {
		return spree6_resource;
	}
	
	public AudioResource getSpree7Resource() {
		return spree7_resource;
	}
	
	public AudioResource getSpree8Resource() {
		return spree8_resource;
	}
	
	public AudioResource getSpree9Resource() {
		return spree9_resource;
	}
	
	public AudioResource getSpree10Resource() {
		return spree10_resource;
	}
	
	public void setEarParameters() {
		Matrix3D avDir = (Matrix3D) (player1.getModel().getWorldRotation().clone());
		float camAz = cam1Controller.getCameraAzimuth();
		avDir.rotateY(180.0f - camAz);
		Vector3D camDir = new Vector3D(0,0,1);
		camDir = camDir.mult(avDir);
		
		Vector3D locVec = player1.getLocation();
		audioMgr.getEar().setLocation(new Point3D(locVec.getX(), locVec.getY(), locVec.getZ()));
		audioMgr.getEar().setOrientation(camDir, new Vector3D(0,1,0));
	}
	
	public IAudioManager getAudioManager() {
		return audioMgr;
	}
	
	private void initGameClient() {
		serverAddr = ((MyDisplaySystem) display).getServerIP();
		serverPort = ((MyDisplaySystem) display).getServerPort();
		System.out.println("Launching as multiplayer, connecting to " + serverAddr + ":" + serverPort);
		
		pType = ProtocolType.TCP;
		
		try {
			gameClient = new AetherBattleClient(InetAddress.getByName(serverAddr), serverPort, pType, this);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (gameClient != null) {
			System.out.println("Connected to server, joining . . . ");
			gameClient.getOutputHandler().sendJoinMsg();
		}
	}
	
	public AetherBattleClient getClient() {
		return gameClient;
	}
	
	public void buildWallFromScript() {
		ScriptEngineManager factory = new ScriptEngineManager();
		//String scriptFileName = directory + "src" + File.separator + "a3" + File.separator + "boundaries.js";
		String scriptFileName = "boundaries.js";
		
		List<ScriptEngineFactory> list = factory.getEngineFactories();
		
		System.out.println("Script Engine Factories found: ");
		for(ScriptEngineFactory f : list) {
			System.out.println("Name = " + f.getEngineName() + " language = " + f.getLanguageName() + " extensions = " + f.getExtensions());
		}
		
		jsEngine = factory.getEngineByName("js");
		this.runScript(jsEngine, scriptFileName);
		
		boundaryGroup = (Group) jsEngine.get("boundaryGroup");
	}
	
	private void runScript(ScriptEngine engine, String scriptFileName) {
		try {
			//FileReader fileReader = new FileReader("scriptFileName");
			//engine.eval(fileReader);
			//fileReader.close();
			engine.eval(new InputStreamReader(AetherBattle.class.getResourceAsStream(scriptFileName)));
		//} catch (FileNotFoundException e1) {
			//System.out.println(scriptFileName + " not found " + e1);
		//} catch (IOException e2) {
			//System.out.println("IO problem with " + scriptFileName + e2);;
		} catch (ScriptException e3) {
			System.out.println("ScriptException in " + scriptFileName + e3);
		} catch (NullPointerException e4) {
			System.out.println("Null ptr exception in " + scriptFileName + e4);
		}
	}
	
	private boolean runAsSinglePlayer() {
		return ((MyDisplaySystem) display).isSinglePlayer();
	}
	
	public Avatar addGhostToGame(UUID uuid, float x, float y, float z) {
		//Pyramid newGhost = new Pyramid();
		Avatar ghost = new Avatar("Ghost " + ++ghostCount, sceneManager.addAvatar(), this, uuid);
		ghost.getModel().translate(x, y, z);
		ghost.getModel().rotate(180, new Vector3D(0,1,0));
		ghost.getModel().scale(3.0f, 4f, 3.0f);
		addGameWorldObject(ghost.getModel());
		
		return ghost;
	}
	
	public void removeGhostFromGame (Avatar ghost) {
		removeGameWorldObject(ghost.getModel());
	}
	
	public void setIsConnected(boolean connected) {
		isConnected = connected;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Vector3D getPlayerLocation() {
		return player1.getLocation();
	}
	
	private void moveSkyBox(ICamera cam) {
		Point3D camLoc = cam.getLocation();
		Matrix3D camTranslation = new Matrix3D();
		camTranslation.translate(camLoc.getX(), camLoc.getY(), camLoc.getZ());
		sceneManager.getSkyBox().setLocalTranslation(camTranslation);
	}
	
	public void update(float elapsedTimeMS) {	
		if (gameClient != null) {
			gameClient.processPackets();
		}
		
		restoreLootCounter += elapsedTimeMS; //Used later to restore consumed Loot Boxes
		
		npcControl.npcLoop(elapsedTimeMS); //Update NPC loop
		
		//player1.getModel().updateAnimation(elapsedTimeMS*2); //Update player avatar animation
		
		setEarParameters();
		cam1Controller.update(elapsedTimeMS);
		
		moveSkyBox(camera1);		
		
		physicsEngine.update(20.0f);
		
		//Handles movement of Bomb objects via their associated physicsObjects
		for(Bomb s: bombList) {
			if(s.getModel().getPhysicsObject() != null) {
				mat = new Matrix3D(s.getModel().getPhysicsObject().getTransform());
				s.getModel().getLocalTranslation().setCol(3, mat.getCol(3));
				
				bombXLoc = s.getModel().getLocalTranslation().getCol(3).getX();
				bombZLoc = s.getModel().getLocalTranslation().getCol(3).getZ();
				
				//Trigger an Explosion if Bomb object touches floor, any of the arena boundaries, or any of the 4 colored box obstacles
				if((s.getModel().getLocalTranslation().getCol(3).getY() <= 0) || (bombXLoc >= 199 || bombXLoc <=-199 || bombZLoc >= 149 || bombZLoc <= -149)
						|| (collidesWithRed(bombXLoc, bombZLoc) || collidesWithYellow(bombXLoc, bombZLoc) || collidesWithGreen(bombXLoc, bombZLoc) || collidesWithBlue(bombXLoc, bombZLoc))) {
					/*if(s instanceof Bomb) {
						System.out.println(s.getSourcePlayer().getUUID()); //Used for testing
					}*/
					//player1.setExplosionLocation(new Point3D(s.getModel().getWorldTranslation().getCol(3)));
					//setEarParameters();
					player1.playExplosion(); //Play explosion sound
					
					if(s.getModel().getPhysicsObject() != null) {
						physicsEngine.removeObject(s.getModel().getPhysicsObject().getUID()); //Remove Bomb's associated physicsObject from physics world
					}
					s.getModel().setPhysicsObject(null); //Nullify Bomb's physicsObject entirely, making this Bomb ineligible for next physicsWorld update
					
					removeBombList.add(s); //Queue bomb object for removal
					removeModelList.add(s.getModel()); //Queue bomb's model for removal
					
					//Spawn an Explosion object at the location where Bomb object touched the ground
					bombLoc = s.getModel().getLocalTranslation().getCol(3);
					bombExplosion = new Explosion(this, s.getSourcePlayer());
					bombExplosion.translate((float)bombLoc.getX(), (float)bombLoc.getY(), (float)bombLoc.getZ()); //Spawn explosion where Bomb object just hit the floor
					explosionList.add(bombExplosion); //Add explosion to ArrayList of explosions, to be dealt with for collision later
					addGameWorldObject(bombExplosion);
				}
			}
		}
		
		//Update explosion animations, remove them from game if they get 'big enough'. This max lifespan/radius can be changed in the Explosion class by adjusting maxTime
		//Also checks for collision with player, and sends message to other players across network if local player is hit by a hostile explosion
		for (Explosion e: explosionList) {
			e.update(elapsedTimeMS);
			
			if(e.isExpired()) {
				removeExplosionList.add(e);
			}
			
			BoundingVolume playerVol = player1.getModel().getWorldBound();
			
			if(e.getWorldBound() != null && e.getWorldBound().intersects(playerVol)) {
				Avatar bombOwner = e.getSourcePlayer();
				if(bombOwner != getLocalPlayer()) {
					System.out.println("Killed by: " + bombOwner.getUUID());
					player1.respawn();
					player1.setSpree(0);
					player1.setBombCount(player1.getBombCount() / 2);
					clearSpreeHUD();
					
					if(gameClient != null) {
						gameClient.getOutputHandler().sendHitMsg(bombOwner.getUUID());
						//gameClient.getOutputHandler().sendSpreeMsg(player1.getSpree());
					}
				}
			}
			
			//If Single Player mode is enabled, the NPC is killable, and can trigger killing spree events
			if(runAsSinglePlayer()) {
				if(e.getWorldBound() != null && e.getWorldBound().intersects(npcControl.getNPC().getWorldBound())) {
					npcControl.getNPC().respawn();
					
					//Play 'hit' sound effect
					player1.playHit();
					
					//Increase player score + killing spree count
					player1.incrementScore();
					player1.incrementSpree();
					
					//If player is on a killing spree, play appropriate sound effect and trigger a popup HUD image
					if(player1.getSpree() == 3) {
						//KILLING SPREE
						player1.playSpree3();
						addSpree3HUD();
					} else if (player1.getSpree() == 4) {
						//DOMINATING
						player1.playSpree4();
						addSpree4HUD();
					} else if (player1.getSpree() == 5) {
						//MEGA KILL
						player1.playSpree5();
						addSpree5HUD();
					} else if (player1.getSpree() == 6) {
						//UNSTOPPABLE
						player1.playSpree6();
						addSpree6HUD();
					} else if (player1.getSpree() == 7) {
						//WICKED SICK
						player1.playSpree7();
						addSpree7HUD();
					} else if (player1.getSpree() == 8) {
						//M-M-M-MONSTER KILL
						player1.playSpree8();
						addSpree8HUD();
					} else if (player1.getSpree() == 9) {
						//GODLIKE
						player1.playSpree9();
						addSpree9HUD();
					} else if (player1.getSpree() >= 10) {
						//HOLY SHIT
						player1.playSpree10();
						addSpree10HUD();
					}
				}
			}
		}
		
		//Handle local player collision with Loot Boxes
		for (SceneNode s: lootBoxes) {
			if(s.getWorldBound() != null) {
				if(s.getWorldBound().intersects(player1.getModel().getWorldBound())) {
					player1.playCollect();
					player1.incrementBombCount();
					removeLootList.add(s);
					restoreLootList.add(s);
				}
			}
		}
		
		//If local player touches the Doomba NPC, player is instantly killed. Trigger a respawn
		if(npcControl.getNPC().getWorldBound().intersects(player1.getModel().getWorldBound())) {
			player1.respawn();
			//player1.decrementScore();
			player1.setBombCount(0);
			player1.setSpree(0);
			clearSpreeHUD();
			
			/*if(gameClient != null) {
				gameClient.getOutputHandler().sendSpreeMsg(player1.getSpree());
			}*/
		}		
		
		//Remove loot boxes from the rotate/translation controller groups, temporarily removing them until the next respawn
		if(removeLootList.size() > 0) {
			for(int i = 0; i < removeLootList.size(); i++) {
				lootBoxes.removeChild(removeLootList.get(i));
				removeLootList.remove(removeLootList.get(i));
			}
		}
		
		//Respawn all loot boxes that have been picked up by the player. Do this every 7.5 seconds.
		if(restoreLootCounter >= 7500) {
			restoreLootCounter = 0;
			if(restoreLootList.size() > 0) {
				for(int i = 0; i < restoreLootList.size(); i++) {
					lootBoxes.addChild(restoreLootList.get(i));
					restoreLootList.remove(restoreLootList.get(i));
				}
			}
		}
		
		//Remove the bomb models from the game, lessening the stress on the renderer as a result of updating all physics objects
		if(removeModelList.size() > 0) {
			for(int i = 0; i < removeModelList.size(); i++) {
				/*if(removeModelList.get(i).getPhysicsObject() != null) {
					physicsEngine.removeObject(removeModelList.get(i).getPhysicsObject().getUID()); //Remove Bomb's associated physicsObject from physics world
					removeModelList.get(i).setPhysicsObject(null); //Nullify Bomb's physicsObject entirely, making this Bomb ineligible for next physicsWorld update
				}*/				
				
				removeGameWorldObject(removeModelList.get(0));
				removeModelList.set(0, null);
				removeModelList.remove(removeModelList.get(0));
			}
		}
	
		//Remove Bomb objects in queue to be deleted
		if(removeBombList.size() > 0) {
			for(int i = 0; i < removeBombList.size(); i++) {
				bombList.remove(removeBombList.get(0));
				removeBombList.set(0, null);
				removeBombList.remove(removeBombList.get(0));
			}
		}
		
		//Remove Explosion objects in queue to be deleted
		if(removeExplosionList.size() > 0) {
			for(int i = 0; i < removeExplosionList.size(); i++) {
				removeGameWorldObject(removeExplosionList.get(0));
				explosionList.remove(removeExplosionList.get(0));
				removeExplosionList.set(0, null);
				removeExplosionList.remove(removeExplosionList.get(0));
			}
		}
		
		//Handles collisions between rigid bodies, no need for 'bouncing' bombs in this game -> commented out
		/*if (running) {
			physicsWorld.stepSimulation(1.0f / 60.0f, 8);
			Transform pBallTransform = new Transform();
			physicsBall.getMotionState().getWorldTransform(pBallTransform);
			
			float[] vals = new float[16];
			pBallTransform.getOpenGLMatrix(vals);
			Matrix3D gBallXform = new Matrix3D(vals);
			testBomb.setLocalTranslation(gBallXform);
		}*/
		
		//Used for testing
		//System.out.println("Score: " + player1.getScore() + ", Spree: " + player1.getSpree());
		
		if(player1.isHasChanged()) {
			bombHUD.updateValue((int) player1.getBombCount());
			scoreHUD.updateValue((int) player1.getScore());
			player1.setHasChanged(false);
		}
		
		//Handles killing spree popup text
		//Each popup lasts 3.5s, and only 1 can be visible at a time
		if(spreeFlag) {
			int curSpree = player1.getSpree();
			if(curSpree == 3) {
				if(HUDList.size() > 1) { //Clear out any other spree-related HUDs, if any, to make room for new one
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree3_image);
					HUDList.remove(spree3_image);
					spreeFlag = false;
				}
			} else if (curSpree == 4) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree4_image);
					HUDList.remove(spree4_image);
					spreeFlag = false;
				}
			} else if (curSpree == 5) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree5_image);
					HUDList.remove(spree5_image);
					spreeFlag = false;
				}
			} else if (curSpree == 6) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree6_image);
					HUDList.remove(spree6_image);
					spreeFlag = false;
				}
			} else if (curSpree == 7) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree7_image);
					HUDList.remove(spree7_image);
					spreeFlag = false;
				}
			} else if (curSpree == 8) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree8_image);
					HUDList.remove(spree8_image);
					spreeFlag = false;
				}
			} else if (curSpree == 9) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree9_image);
					HUDList.remove(spree9_image);
					spreeFlag = false;
				}
			} else if (curSpree >= 10) {
				if(HUDList.size() > 1) {
					for(int i = 0; i < HUDList.size(); i++) {
						camera1.removeFromHUD(HUDList.get(0));
						HUDList.remove(HUDList.get(0));
					}
				}
				spreeCountdown += elapsedTimeMS;
				if(spreeCountdown >= spreeLimit) {
					spreeCountdown = 0;
					camera1.removeFromHUD(spree10_image);
					HUDList.remove(spree10_image);
					spreeFlag = false;
				}
			}
			
		}
		
		solarSystemGroup.rotate(0.35f, new Vector3D(0, 1, 0));
		
		//System.out.println("X: " + player1.getLocation().getX() + ", Z: " + player1.getLocation().getZ());
		//System.out.println(player1.getBombCount());
		super.update(elapsedTimeMS);
	}
	
	private void createPlayers() {
		player1 = new Avatar("Player 1", sceneManager.addAvatar(), this, gameClient != null ? gameClient.getUUID() : null);
		player1.getModel().translate(0, 0, 50);
		player1.getModel().rotate(180, new Vector3D(0,1,0));
		player1.getModel().scale(3.0f, 4f, 3.0f);
		//player1.getModel().startAnimation("my_animation");
		addGameWorldObject(player1.getModel());
		
		camera1 = new JOGLCamera(renderer);
		camera1.setPerspectiveFrustum(100, 1, 0.01, 1000);

		createPlayerHUDs();
		
		//Create the Doomba NPC and initiate it's BehaviorTree AI
		npcControl = new NPCcontroller(this);
		npcControl.startNPCcontrol();
		addGameWorldObject(npcControl.getNPC());
	}
	
	public Avatar getLocalPlayer() {
		return player1;
	}
	
	private void createPlayerHUDs() {
		bombHUD = new HUDNumber("Bombs", directory, -0.845f, -0.881f);
		scoreHUD = new HUDNumber("Score", directory, 0.05f, 0.899f);
		
		p1Score = new HUDImage(directory + dirHud + "Kill_HUD.png");
		p1Score.setName("Player 1 Score");
		p1Score.setLocation(0, 0.90);
		p1Score.rotateImage(180);
		p1Score.scale(0.156f, 0.0575f, 0.1f);
		p1Score.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		camera1.addToHUD(p1Score);
		
		p1Bombs = new HUDImage(directory + dirHud + "Bomb_HUD.png");
		p1Bombs.setName("Player 1 Bombs");
		p1Bombs.setLocation(-.9f, -0.88f);
		p1Bombs.rotateImage(180);
		p1Bombs.scale(.1570f, .0750f, .1f);
		p1Bombs.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		camera1.addToHUD(p1Bombs);
		
		addGameWorldObject(scoreHUD.getHUDNumber());
		addGameWorldObject(bombHUD.getHUDNumber());
		
		//---------------------------------------------------------------------------
		//                   Loading Killing Spree Popup HUD Images
		//---------------------------------------------------------------------------
		spree3_image = new HUDImage(directory + dirHud + "KILLING_SPREE.png");
		spree3_image.setName("KILLING SPREE");
		spree3_image.setLocation(0,0.80);
		spree3_image.rotateImage(180);
		spree3_image.scale(0.312f, 0.115f, 0.2f);
		spree3_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree4_image = new HUDImage(directory + dirHud + "DOMINATING.png");
		spree4_image.setName("DOMINATING");
		spree4_image.setLocation(0,0.80);
		spree4_image.rotateImage(180);
		spree4_image.scale(0.312f, 0.115f, 0.2f);
		spree4_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree5_image = new HUDImage(directory + dirHud + "MEGA_KILL.png");
		spree5_image.setName("MEGA KILL");
		spree5_image.setLocation(0,0.80);
		spree5_image.rotateImage(180);
		spree5_image.scale(0.312f, 0.115f, 0.2f);
		spree5_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree6_image = new HUDImage(directory + dirHud + "UNSTOPPABLE.png");
		spree6_image.setName("UNSTOPPABLE");
		spree6_image.setLocation(0,0.80);
		spree6_image.rotateImage(180);
		spree6_image.scale(0.312f, 0.115f, 0.2f);
		spree6_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree7_image = new HUDImage(directory + dirHud + "WICKED_SICK.png");
		spree7_image.setName("WICKED SICK");
		spree7_image.setLocation(0,0.80);
		spree7_image.rotateImage(180);
		spree7_image.scale(0.312f, 0.115f, 0.2f);
		spree7_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree8_image = new HUDImage(directory + dirHud + "MONSTER_KILL.png");
		spree8_image.setName("MONSTER KILL");
		spree8_image.setLocation(0,0.80);
		spree8_image.rotateImage(180);
		spree8_image.scale(0.312f, 0.115f, 0.2f);
		spree8_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree9_image = new HUDImage(directory + dirHud + "GODLIKE.png");
		spree9_image.setName("GODLIKE");
		spree9_image.setLocation(0,0.80);
		spree9_image.rotateImage(180);
		spree9_image.scale(0.312f, 0.115f, 0.2f);
		spree9_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
		
		spree10_image = new HUDImage(directory + dirHud + "HOLY_SHIT.png");
		spree10_image.setName("HOLY SHIT");
		spree10_image.setLocation(0,0.80);
		spree10_image.rotateImage(180);
		spree10_image.scale(0.312f, 0.115f, 0.2f);
		spree10_image.setRenderMode(sage.scene.SceneNode.RENDER_MODE.ORTHO);
	}
	
	public void addSpree3HUD() {
		camera1.addToHUD(spree3_image);
		HUDList.add(spree3_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree4HUD() {
		camera1.addToHUD(spree4_image);
		HUDList.add(spree4_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree5HUD() {
		camera1.addToHUD(spree5_image);
		HUDList.add(spree5_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree6HUD() {
		camera1.addToHUD(spree6_image);
		HUDList.add(spree6_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree7HUD() {
		camera1.addToHUD(spree7_image);
		HUDList.add(spree7_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree8HUD() {
		camera1.addToHUD(spree8_image);
		HUDList.add(spree8_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree9HUD() {
		camera1.addToHUD(spree9_image);
		HUDList.add(spree9_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void addSpree10HUD() {
		camera1.addToHUD(spree10_image);
		HUDList.add(spree10_image);
		spreeCountdown = 0;
		spreeFlag = true;
	}
	
	public void clearSpreeHUD() {
		for(int i = 0; i < HUDList.size(); i++) {
			camera1.removeFromHUD(HUDList.get(0));
			HUDList.remove(0);
		}
		spreeFlag = false;
		spreeCountdown = 0;
	}
	
	private void initInput() {
		kbName = im.getKeyboardName();
		gpName = im.getFirstGamepadName();
		
		cam1Controller = new Camera3Pcontroller(camera1, player1.getModel(), im, gpName, kbName);
		
		//Keyboard controls
		IAction moveForward1 = new NodeMoveForwardAction(player1.getModel(), gameClient, this);
		IAction moveBackward1 = new NodeMoveBackwardAction(player1.getModel(), gameClient, this);
		IAction moveLeft1 = new NodeMoveLeftAction(player1.getModel(), gameClient, this);
		IAction moveRight1 = new NodeMoveRightAction(player1.getModel(), gameClient, this);
		IAction turnLeft1 = new NodeTurnLeftAction(player1.getModel(), gameClient);
		IAction turnRight1 = new NodeTurnRightAction(player1.getModel(), gameClient);
		
		IAction quitGame1 = new QuitGameAction(this);
		
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.W, moveForward1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.S, moveBackward1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.A, moveRight1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.D, moveLeft1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.Q, turnLeft1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.E, turnRight1, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		
		im.associateAction(kbName, net.java.games.input.Component.Identifier.Key.ESCAPE, quitGame1, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		//Gamepad controls
		IAction moveForward2 = new NodeMoveForwardAction(player1.getModel(), gameClient, this);
		IAction moveBackward2 = new NodeMoveBackwardAction(player1.getModel(), gameClient, this);
		IAction moveLeft2 = new NodeMoveLeftAction(player1.getModel(), gameClient, this);
		IAction moveRight2 = new NodeMoveRightAction(player1.getModel(), gameClient, this);
		IAction turnLeft2 = new NodeTurnLeftAction(player1.getModel(), gameClient);
		IAction turnRight2 = new NodeTurnRightAction(player1.getModel(), gameClient);
		
		if(gpName != null) {
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._0, moveBackward2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._3, moveForward2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._1, moveLeft2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._2, moveRight2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._4, turnLeft2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
			im.associateAction(gpName, net.java.games.input.Component.Identifier.Button._5, turnRight2, IInputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
		}
	}
	
	private void initGameObjects() {				
		/*ground = new Rectangle();
		Matrix3D newRot = new Matrix3D(90, new Vector3D(1,0,0));
		Matrix3D curRot = ground.getLocalRotation();
		curRot.concatenate(newRot);
		ground.setLocalRotation(curRot);
		ground.scale(1000f, 1000f, 1000f);
		ground.setColor(Color.lightGray);
		addGameWorldObject(ground);*/
		
		//Add skybox
		addGameWorldObject(sceneManager.addSkyBox(this, origin));
		
		/*boundaryGroup = new Group();
		Rectangle wall1 = new Rectangle("North wall", 400, 60);
		wall1.translate(0, 1, 150);
		
		Rectangle wall2 = new Rectangle("South wall", 400, 60);
		wall2.translate(0, 1, -150);
		
		Rectangle wall3 = new Rectangle("East wall", 300, 60);
		wall3.rotate(90, new Vector3D(0,1,0));
		wall3.translate(-200, 1, 0);
		
		Rectangle wall4 = new Rectangle("West wall", 300, 60);
		wall4.rotate(90,  new Vector3D(0,1,0));
		wall4.translate(200, 1, 0);
		
		boundaryGroup.addChild(wall1);
		boundaryGroup.addChild(wall2);
		boundaryGroup.addChild(wall3);
		boundaryGroup.addChild(wall4);*/
		
		solarSystemGroup = sceneManager.createSolarSystem();
		solarSystemGroup.translate(0f, 70f, 0f);
		solarSystemGroup.scale(.15f, .15f, .15f);
		addGameWorldObject(solarSystemGroup);
		
		addGameWorldObject(boundaryGroup);
		sceneManager.updateBoundaryGroup(boundaryGroup);
		
		//Add ground plane
		sceneManager.addGameFloor(environmentGroup);
		addGameWorldObject(environmentGroup);
		
		//---------------------------------------------------------------------------
		//				Add the 4 colored blocks as obstacles into the gameworld
		//---------------------------------------------------------------------------
		//NOTE TO SELF: DON'T TEXTURE SAGE-NATIVE SCENENODES/SHAPES. IT WILL FUCK UP OTHER OBJECTS' COLORS
		//EXPORT SIMPLE TEXTURED SHAPES FROM BLENDER!!!
		
		redBlock = sceneManager.addRedBlock();
		redBlock.translate(-100, 0, 65);
		redBlock.scale(20, 10, 12);
		redBlock.updateGeometricState(1.0f, true);
		addGameWorldObject(redBlock);
		
		yellowBlock = sceneManager.addYellowBlock();
		yellowBlock.translate(-100, 0, -65);
		yellowBlock.scale(20, 10, 12);
		yellowBlock.updateGeometricState(1.0f, true);
		addGameWorldObject(yellowBlock);
		
		greenBlock = sceneManager.addGreenBlock();
		greenBlock.translate(100, 0, -65);
		greenBlock.scale(20, 10, 12);
		greenBlock.updateGeometricState(1.0f, true);
		addGameWorldObject(greenBlock);
		
		blueBlock = sceneManager.addBlueBlock();
		blueBlock.translate(100, 0, 65);
		blueBlock.scale(20, 10, 12);
		blueBlock.updateGeometricState(1.0f, true);
		addGameWorldObject(blueBlock);
		
		//---------------------------------------------------------------------------
		//				Add Loot Boxes into the gameworld		
		//---------------------------------------------------------------------------
		
		//* * * CENTER LOOT BOXES * * *
		
		loot1 = sceneManager.addDiamond();
		loot1.translate(0, 2, 0);
		loot1.scale(2.0f, 2.0f, 2.0f);
		loot1.updateGeometricState(1.0f, true);
		
		loot2 = sceneManager.addDiamond();
		loot2.translate(-18, 2, 18);
		loot2.scale(2.0f, 2.0f, 2.0f);
		loot2.updateGeometricState(1.0f, true);
		
		loot3 = sceneManager.addDiamond();
		loot3.translate(-18, 2, -18);
		loot3.scale(2.0f, 2.0f, 2.0f);
		loot3.updateGeometricState(1.0f, true);
		
		loot4 = sceneManager.addDiamond();
		loot4.translate(18, 2, 18);
		loot4.scale(2.0f, 2.0f, 2.0f);
		loot4.updateGeometricState(1.0f, true);
		
		loot5 = sceneManager.addDiamond();
		loot5.translate(18, 2, -18);
		loot5.scale(2.0f, 2.0f, 2.0f);
		loot5.updateGeometricState(1.0f, true);
		
		loot6 = sceneManager.addDiamond();
		loot6.translate(-25, 2, 0);
		loot6.scale(2.0f, 2.0f, 2.0f);
		loot6.updateGeometricState(1.0f, true);
		
		loot7 = sceneManager.addDiamond();
		loot7.translate(25, 2, 0);
		loot7.scale(2.0f, 2.0f, 2.0f);
		loot7.updateGeometricState(1.0f, true);
		
		loot8 = sceneManager.addDiamond();
		loot8.translate(0, 2, -25);
		loot8.scale(2.0f, 2.0f, 2.0f);
		loot8.updateGeometricState(1.0f, true);
		
		loot9 = sceneManager.addDiamond();
		loot9.translate(0, 2, 25);
		loot9.scale(2.0f, 2.0f, 2.0f);
		loot9.updateGeometricState(1.0f, true);
		
		loot10 = sceneManager.addDiamond();
		loot10.translate(-12, 2, 0);
		loot10.scale(2.0f, 2.0f, 2.0f);
		loot10.updateGeometricState(1.0f, true);
		
		loot11 = sceneManager.addDiamond();
		loot11.translate(12, 2, 0);
		loot11.scale(2.0f, 2.0f, 2.0f);
		loot11.updateGeometricState(1.0f, true);
		
		loot12 = sceneManager.addDiamond();
		loot12.translate(0, 2, 12);
		loot12.scale(2.0f, 2.0f, 2.0f);
		loot12.updateGeometricState(1.0f, true);
		
		loot13 = sceneManager.addDiamond();
		loot13.translate(0, 2, -12);
		loot13.scale(2.0f, 2.0f, 2.0f);
		loot13.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * RED-YELLOW INTERSECTION BOXES * * *
		
		loot14 = sceneManager.addDiamond();
		loot14.translate(-172, 2, 0);
		loot14.scale(2.0f, 2.0f, 2.0f);
		loot14.updateGeometricState(1.0f, true);
		
		loot15 = sceneManager.addDiamond();
		loot15.translate(-187, 2, 15);
		loot15.scale(2.0f, 2.0f, 2.0f);
		loot15.updateGeometricState(1.0f, true);
		
		loot16 = sceneManager.addDiamond();
		loot16.translate(-187, 2, -15);
		loot16.scale(2.0f, 2.0f, 2.0f);
		loot16.updateGeometricState(1.0f, true);
		
		loot17 = sceneManager.addDiamond();
		loot17.translate(-157, 2, 15);
		loot17.scale(2.0f, 2.0f, 2.0f);
		loot17.updateGeometricState(1.0f, true);
		
		loot18 = sceneManager.addDiamond();
		loot18.translate(-157, 2, -15);
		loot18.scale(2.0f, 2.0f, 2.0f);
		loot18.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * GREEN-BLUE INTERSECTION BOXES * * *
		
		loot19 = sceneManager.addDiamond();
		loot19.translate(172, 2, 0);
		loot19.scale(2.0f, 2.0f, 2.0f);
		loot19.updateGeometricState(1.0f, true);
		
		loot20 = sceneManager.addDiamond();
		loot20.translate(187, 2, 15);
		loot20.scale(2.0f, 2.0f, 2.0f);
		loot20.updateGeometricState(1.0f, true);
		
		loot21 = sceneManager.addDiamond();
		loot21.translate(187, 2, -15);
		loot21.scale(2.0f, 2.0f, 2.0f);
		loot21.updateGeometricState(1.0f, true);
		
		loot22 = sceneManager.addDiamond();
		loot22.translate(157, 2, 15);
		loot22.scale(2.0f, 2.0f, 2.0f);
		loot22.updateGeometricState(1.0f, true);
		
		loot23 = sceneManager.addDiamond();
		loot23.translate(157, 2, -15);
		loot23.scale(2.0f, 2.0f, 2.0f);
		loot23.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * YELLOW-GREEN INTERSECTION BOXES * * *
		
		loot24 = sceneManager.addDiamond();
		loot24.translate(0, 2, -124);
		loot24.scale(2.0f, 2.0f, 2.0f);
		loot24.updateGeometricState(1.0f, true);
		
		loot25 = sceneManager.addDiamond();
		loot25.translate(15, 2, -139);
		loot25.scale(2.0f, 2.0f, 2.0f);
		loot25.updateGeometricState(1.0f, true);
		
		loot26 = sceneManager.addDiamond();
		loot26.translate(15, 2, -109);
		loot26.scale(2.0f, 2.0f, 2.0f);
		loot26.updateGeometricState(1.0f, true);
		
		loot27 = sceneManager.addDiamond();
		loot27.translate(-15, 2, -139);
		loot27.scale(2.0f, 2.0f, 2.0f);
		loot27.updateGeometricState(1.0f, true);
		
		loot28 = sceneManager.addDiamond();
		loot28.translate(-15, 2, -109);
		loot28.scale(2.0f, 2.0f, 2.0f);
		loot28.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * RED-BLUE INTERSECTION BOXES * * *
		
		loot29 = sceneManager.addDiamond();
		loot29.translate(0, 2, 124);
		loot29.scale(2.0f, 2.0f, 2.0f);
		loot29.updateGeometricState(1.0f, true);
		
		loot30 = sceneManager.addDiamond();
		loot30.translate(15, 2, 139);
		loot30.scale(2.0f, 2.0f, 2.0f);
		loot30.updateGeometricState(1.0f, true);
		
		loot31 = sceneManager.addDiamond();
		loot31.translate(15, 2, 109);
		loot31.scale(2.0f, 2.0f, 2.0f);
		loot31.updateGeometricState(1.0f, true);
		
		loot32 = sceneManager.addDiamond();
		loot32.translate(-15, 2, 139);
		loot32.scale(2.0f, 2.0f, 2.0f);
		loot32.updateGeometricState(1.0f, true);
		
		loot33 = sceneManager.addDiamond();
		loot33.translate(-15, 2, 109);
		loot33.scale(2.0f, 2.0f, 2.0f);
		loot33.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * RED CORNER BOXES * * *
		
		loot34 = sceneManager.addDiamond();
		loot34.translate(-172, 2, 124);
		loot34.scale(2.0f, 2.0f, 2.0f);
		loot34.updateGeometricState(1.0f, true);
		
		loot35 = sceneManager.addDiamond();
		loot35.translate(-157, 2, 109);
		loot35.scale(2.0f, 2.0f, 2.0f);
		loot35.updateGeometricState(1.0f, true);
		
		loot36 = sceneManager.addDiamond();
		loot36.translate(-187, 2, 139);
		loot36.scale(2.0f, 2.0f, 2.0f);
		loot36.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * YELLOW CORNER BOXES * * *
		
		loot37 = sceneManager.addDiamond();
		loot37.translate(-172, 2, -124);
		loot37.scale(2.0f, 2.0f, 2.0f);
		loot37.updateGeometricState(1.0f, true);
		
		loot38 = sceneManager.addDiamond();
		loot38.translate(-157, 2, -109);
		loot38.scale(2.0f, 2.0f, 2.0f);
		loot38.updateGeometricState(1.0f, true);
		
		loot39 = sceneManager.addDiamond();
		loot39.translate(-187, 2, -139);
		loot39.scale(2.0f, 2.0f, 2.0f);
		loot39.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * GREEN CORNER BOXES * * *
		
		loot40 = sceneManager.addDiamond();
		loot40.translate(172, 2, -124);
		loot40.scale(2.0f, 2.0f, 2.0f);
		loot40.updateGeometricState(1.0f, true);
		
		loot41 = sceneManager.addDiamond();
		loot41.translate(157, 2, -109);
		loot41.scale(2.0f, 2.0f, 2.0f);
		loot41.updateGeometricState(1.0f, true);
		
		loot42 = sceneManager.addDiamond();
		loot42.translate(187, 2, -139);
		loot42.scale(2.0f, 2.0f, 2.0f);
		loot42.updateGeometricState(1.0f, true);
		
		//----------------------------------------------------
		
		//* * * BLUE CORNER BOXES * * *
		
		loot43 = sceneManager.addDiamond();
		loot43.translate(172, 2, 124);
		loot43.scale(2.0f, 2.0f, 2.0f);
		loot43.updateGeometricState(1.0f, true);
		
		loot44 = sceneManager.addDiamond();
		loot44.translate(157, 2, 109);
		loot44.scale(2.0f, 2.0f, 2.0f);
		loot44.updateGeometricState(1.0f, true);
		
		loot45 = sceneManager.addDiamond();
		loot45.translate(187, 2, 139);
		loot45.scale(2.0f, 2.0f, 2.0f);
		loot45.updateGeometricState(1.0f, true);
		
		//-------------------- Add all Loot Boxes into a Group ---------------------
		//--------------------      then add to gameworld      ---------------------
		
		lootBoxes = new Group();
		
		//CENTER BOXES
		lootBoxes.addChild(loot1);
		lootBoxes.addChild(loot2);
		lootBoxes.addChild(loot3);
		lootBoxes.addChild(loot4);
		lootBoxes.addChild(loot5);
		lootBoxes.addChild(loot6);
		lootBoxes.addChild(loot7);
		lootBoxes.addChild(loot8);
		lootBoxes.addChild(loot9);
		lootBoxes.addChild(loot10);
		lootBoxes.addChild(loot11);
		lootBoxes.addChild(loot12);
		lootBoxes.addChild(loot13);
		
		//RED-YELLOW INTERSECTION BOXES
		lootBoxes.addChild(loot14);
		lootBoxes.addChild(loot15);
		lootBoxes.addChild(loot16);
		lootBoxes.addChild(loot17);
		lootBoxes.addChild(loot18);
		
		//GREEN-BLUE INTERSECTION BOXES
		lootBoxes.addChild(loot19);
		lootBoxes.addChild(loot20);
		lootBoxes.addChild(loot21);
		lootBoxes.addChild(loot22);
		lootBoxes.addChild(loot23);
		
		//YELLOW-GREEN INTERSECTION BOXES
		lootBoxes.addChild(loot24);
		lootBoxes.addChild(loot25);
		lootBoxes.addChild(loot26);
		lootBoxes.addChild(loot27);
		lootBoxes.addChild(loot28);
		
		//RED-BLUE INTERSECTION BOXES
		lootBoxes.addChild(loot29);
		lootBoxes.addChild(loot30);
		lootBoxes.addChild(loot31);
		lootBoxes.addChild(loot32);
		lootBoxes.addChild(loot33);
		
		//RED CORNER BOXES
		lootBoxes.addChild(loot34);
		lootBoxes.addChild(loot35);
		lootBoxes.addChild(loot36);
		
		//YELLOW CORNER BOXES
		lootBoxes.addChild(loot37);
		lootBoxes.addChild(loot38);
		lootBoxes.addChild(loot39);
		
		//GREEN CORNER BOXES
		lootBoxes.addChild(loot40);
		lootBoxes.addChild(loot41);
		lootBoxes.addChild(loot42);
		
		//BLUE CORNER BOXES
		lootBoxes.addChild(loot43);
		lootBoxes.addChild(loot44);
		lootBoxes.addChild(loot45);
		
		addGameWorldObject(lootBoxes);
		
		//---------------------------------------------------------------------------
		//				Setup Rotation/Translation controllers for the Loot Boxes	
		//---------------------------------------------------------------------------
		MyRotationController rotCtr = new MyRotationController();
		rotCtr.addControlledNode(lootBoxes);
		lootBoxes.addController(rotCtr);
		
		MyTranslationController transCtr = new MyTranslationController();
		transCtr.addControlledNode(lootBoxes);
		lootBoxes.addController(transCtr);
		
		//----------- X,Y,Z Axes (for testing) --------------
		
		/*Point3D origin = new Point3D(0,0,0);
		Point3D xEnd = new Point3D(100,0,0);
		Point3D yEnd = new Point3D(0,100,0);
		Point3D zEnd = new Point3D(0,0,100);
		Line xAxis = new Line(origin, xEnd, Color.red, 2);
		Line yAxis = new Line(origin, yEnd, Color.green, 2);
		Line zAxis = new Line(origin, zEnd, Color.blue, 2);
		addGameWorldObject(xAxis);
		addGameWorldObject(yAxis);
		addGameWorldObject(zAxis);*/
	}
	
	public TriMesh getRedBlock() {
		return redBlock;
	}
	
	public boolean collidesWithRed(double x, double z) {
		if(x >= -152 && x <= -48 && z >= 25 && z <= 106) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean collidesWithYellow(double x, double z) {
		if(x >= -152 && x <= -48 && z >= -106 && z <= -25) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean collidesWithGreen(double x, double z) {
		if(x >= 48 && x <= 152 && z >= -106 && z <= -25) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean collidesWithBlue(double x, double z) {
		if(x >= 48 && x <= 152 && z >= 25 && z <= 106) {
			return true;
		} else {
			return false;
		}
	}
	
	protected void render() {
		renderer.setCamera(camera1);
		super.render();
	}
	
	//Random number generator (creates integer values). Used in Avatar class to generate respawn location
	public int rng(int bound) {
		int result = rand.nextInt(bound);
		
		if(rand.nextBoolean()) {
			result = -result;
		}
		
		return result;
	}
	
	private void initPhysicsInput() {
		IAction kbTossBomb = new TossBomb();
		im.associateAction(im.getKeyboardName(), net.java.games.input.Component.Identifier.Key.SPACE, kbTossBomb, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		
		IAction gpTossBomb = new TossBomb();
		if(im.getFirstGamepadName() != null) {
			im.associateAction(im.getFirstGamepadName(), net.java.games.input.Component.Identifier.Button._6, gpTossBomb, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
			im.associateAction(im.getFirstGamepadName(), net.java.games.input.Component.Identifier.Button._7, gpTossBomb, IInputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
		}
	}
	
	protected void initPhysicsSystem() {
		String engine = "sage.physics.JBullet.JBulletPhysicsEngine";
		physicsEngine = PhysicsEngineFactory.createPhysicsEngine(engine);
		physicsEngine.initSystem();
		float[] gravity = {0,-8f,0};
		physicsEngine.setGravity(gravity);
	}
	
	private class TossBomb extends AbstractInputAction {
		public void performAction(float time, Event evt) {
			if(player1.getBombCount() >0) {
				player1.decrementBombCount();
				float bombMass = 3.0f;
			
				//create Bomb object
				playerBomb = new Bomb(sceneManager.addBomb(), player1);
			
				//get local player's location and spawn Bomb's location 4 units above that
				Vector3D playerTranslate = player1.getLocation();
				playerBomb.getModel().translate((float)playerTranslate.getX(), (float)playerTranslate.getY()+4, (float)playerTranslate.getZ());
				playerBomb.getModel().scale(2.0f, 2.0f, 2.0f);
				ballPhysicsObj = physicsEngine.addSphereObject(physicsEngine.nextUID(), bombMass, playerBomb.getModel().getLocalTranslation().getValues(), 1.0f);
				//ballP2.setBounciness(1.0f);
			
				//Obtain the direction the local player is facing, and apply a force vector on the Bomb's associated physicsObject's center to throw it forward and up in an arc.
				//Physics system will calculate trajectory. Note that gravity strength can be adjusted via the initPhysicsSystem()'s gravity variable above
				//**IMPORTANT**: If you change the force vector here, the force vector in the ghostBombToss() method must also use the exact same values to maintain consistency across network
				Matrix3D rot = (Matrix3D)player1.getModel().getWorldRotation().clone();
				Vector3D dir = new Vector3D(0, 2650, 4600);
				dir = dir.mult(rot);
				ballPhysicsObj.applyForce((float)dir.getX(), (float)dir.getY(), (float)dir.getZ(), 0, 0, 0);
				playerBomb.getModel().setPhysicsObject(ballPhysicsObj); //Attach the spherical physics object to the Bomb's model. Update() will use physicsObject's location to update Bomb's location
				addGameWorldObject(playerBomb.getModel());
				playerBomb.getModel().updateGeometricState(1.0f, true);
				bombList.add(playerBomb);
			
				//Play 'toss' sound
				player1.playToss();
			
				if(gameClient != null) {
					gameClient.getOutputHandler().sendBombMsg();
				}
			}
		}
	}
	
	public void ghostTossBomb(Avatar player) {
		float bombMass2 = 3.0f;
		ghostBomb = new Bomb(sceneManager.addBomb(), player);
		
		Vector3D playerTranslate2 = player.getLocation();
		ghostBomb.getModel().translate((float)playerTranslate2.getX(), (float)playerTranslate2.getY()+4, (float)playerTranslate2.getZ());
		ghostBomb.getModel().scale(2.0f, 2.0f, 2.0f);
		ghostBallPhysicsObj = physicsEngine.addSphereObject(physicsEngine.nextUID(), bombMass2, ghostBomb.getModel().getLocalTranslation().getValues(), 1.0f);

		Matrix3D rot2 = (Matrix3D)player.getModel().getWorldRotation().clone();
		Vector3D dir2 = new Vector3D(0, 2650, 4600);
		dir2 = dir2.mult(rot2);
		ghostBallPhysicsObj.applyForce((float)dir2.getX(), (float)dir2.getY(), (float)dir2.getZ(), 0, 0, 0);
		ghostBomb.getModel().setPhysicsObject(ghostBallPhysicsObj);
		addGameWorldObject(ghostBomb.getModel());
		ghostBomb.getModel().updateGeometricState(1.0f, true);
		bombList.add(ghostBomb);
		
		player.playToss();
	}
	
	private void createGraphicsScene() {
		Transform myTransform;
		
		//define the broad-phase collision to be used (Sweep-and-Prune)
		broadPhaseHandler = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);
		
		//set up the narrow-phase collision handler ("dispatcher")
		colConfig = new DefaultCollisionConfiguration();
		colDispatcher = new CollisionDispatcher(colConfig);
		
		//create a constraint solver
		solver = new SequentialImpulseConstraintSolver();
		
		//create a physics world utilizing the above objects
		physicsWorld = new DiscreteDynamicsWorld(colDispatcher, broadPhaseHandler, solver, colConfig);
		physicsWorld.setGravity(new Vector3f(0, -10, 0));
		
		//define physicsGround plane: normal vector = 'up', dist from origin = 1
		CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0,1,0), 1);
		
		//set position and orientation of physicsGround's transform
		myTransform = new Transform();
		myTransform.origin.set(new Vector3f(0,-1,0));
		myTransform.setRotation(new Quat4f(0,0,0,1));
		
		//define construction info for a 'physicsGround' rigid body
		DefaultMotionState groundMotionState = new DefaultMotionState(myTransform);
		RigidBodyConstructionInfo groundCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0));
		groundCI.restitution = 0.8f;
		
		//create the physicsGround rigid body and add it to the physics world
		physicsGround = new RigidBody(groundCI);
		physicsWorld.addRigidBody(physicsGround);
		
		//define a collision shape for a physicsBall
		CollisionShape fallShape = new SphereShape(1);
		
		//define a transform for a position and orientation of ball collision shape
		myTransform = new Transform();
		myTransform.origin.set(new Vector3f(0,20,0));
		myTransform.setRotation(new Quat4f(0,0,0,1));
		
		//define the parameters of the collision shape
		DefaultMotionState fallMotionState = new DefaultMotionState(myTransform);
		float myFallMass = 1;
		Vector3f myFallInertia = new Vector3f(0,0,0);
		fallShape.calculateLocalInertia(myFallMass, myFallInertia);
		
		//define construction info for a 'physicsBall' rigid body
		RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(myFallMass, fallMotionState, fallShape, myFallInertia);
		fallRigidBodyCI.restitution = 0.8f;
		
		//create the physicsBall rigid bodyand add it to the physics world
		physicsBall = new RigidBody(fallRigidBodyCI);
		physicsWorld.addRigidBody(physicsBall);
	}
	
	private void createSagePhysicsWorld() {
		//add ball physics
		float mass = 1.0f;
		ballP = physicsEngine.addSphereObject(physicsEngine.nextUID(), mass, testBomb.getWorldTransform().getValues(), 1.0f);
		ballP.setBounciness(1.0f);
		testBomb.setPhysicsObject(ballP);
		
		//add ground plane physics
		float up[] = {0, 1, 0}; //{0,1,0} is flat
		groundPlaneP = physicsEngine.addStaticPlaneObject(physicsEngine.nextUID(), sceneManager.getFloor().getWorldTransform().getValues(), up, 0.0f);
		groundPlaneP.setBounciness(1.0f);
		sceneManager.getFloor().setPhysicsObject(groundPlaneP);
		
		//should also set damping, friction, ect.
	}
	
	public static void main(String[] args) {
		new AetherBattle().start();
	}
}
