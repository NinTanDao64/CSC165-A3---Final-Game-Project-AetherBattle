package myGameEngine;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;

import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;
import sage.app.BaseGame;
import sage.display.IDisplaySystem;
import sage.model.loader.OBJLoader;
import sage.model.loader.OBJMaterial;
import sage.model.loader.ogreXML.OgreXMLParser;
import sage.scene.Group;
import sage.scene.Model3DTriMesh;
import sage.scene.SceneNode;
import sage.scene.SceneNode.CULL_MODE;
import sage.scene.SkyBox;
import sage.scene.TriMesh;
import sage.scene.SkyBox.Face;
import sage.scene.shape.Cube;
import sage.scene.shape.Pyramid;
import sage.scene.shape.Rectangle;
import sage.scene.shape.Sphere;
import sage.scene.state.RenderState;
import sage.scene.state.TextureState;
import sage.terrain.AbstractHeightMap;
import sage.terrain.HillHeightMap;
import sage.terrain.TerrainBlock;
import sage.texture.Texture;
import sage.texture.Texture.WrapMode;
import sage.texture.TextureManager;

public class SceneManager {
	private String directory;
	private String dirEnv = "images" + File.separator + "environment" + File.separator;
	private String dirModel = "images" + File.separator + "models" + File.separator;
	
	//private String directory;
	//private String dirEnv = "src/images/environment/";
	
	//Texture objects
	private Texture skyBoxTop;
	private Texture skyBoxBack;
	private Texture skyBoxEast;
	private Texture skyBoxWest;
	private Texture skyBoxFront;
	private Texture skyBoxBot;
	private Texture groundTexture;
	private Texture wallTexture;
	
	private Texture grassTexture;
	private Texture stoneTexture;
	private Texture floorTexture;
	
	private Texture redTexture;
	private Texture yellowTexture;
	private Texture greenTexture;
	private Texture blueTexture;
	
	private Texture avatarTexture;
	private Texture bombTexture;
	private Texture diamondTexture;
	
	//Gameworld SceneNodes
	private SkyBox skyBox;
	private Rectangle floor;
	private TerrainBlock hillTerrain;
	
	private OBJLoader loader = new OBJLoader();
	
	public SceneManager(String dir) {
		directory = dir;
		setupTextures();
	}
	
	private void setupTextures() {
		String skyFront = directory + dirEnv + "front.jpg";
		String skyEast =  directory + dirEnv + "right.jpg";
		String skyWest = directory + dirEnv + "left.jpg";
		String skyBot = directory + dirEnv + "down.jpg";
		String skyTop = directory + dirEnv + "up.jpg";
		String skyBack = directory + dirEnv + "back.jpg";
		String ground = directory + dirEnv + "floor.jpg";
		String floor = directory + dirEnv + "floor.jpg";
		String grass = directory + dirEnv + "grass.jpg";
		String stone = directory + dirEnv + "stone.png";
		String wall = directory + dirEnv + "wall.jpg";	
		String red = directory + dirEnv + "red_texture.png";	
		String yellow = directory + dirEnv + "yellow_texture.png";
		String green = directory + dirEnv + "green_texture.png";
		String blue = directory + dirEnv + "blue_texture.png";
		String avatar = directory + dirModel + "CharacterTexture2.png";
		String bomb = directory + dirModel + "Bomb Texture.png";
		String diamond = directory + dirModel + "DiamondTexture.png";
		
		skyBoxTop = TextureManager.loadTexture2D(skyTop);
		skyBoxBack = TextureManager.loadTexture2D(skyBack);
		skyBoxEast = TextureManager.loadTexture2D(skyEast);
		skyBoxWest = TextureManager.loadTexture2D(skyWest);
		skyBoxFront = TextureManager.loadTexture2D(skyFront);
		skyBoxBot = TextureManager.loadTexture2D(skyBot);
		groundTexture = TextureManager.loadTexture2D(ground);
		floorTexture = TextureManager.loadTexture2D(floor);
		stoneTexture = TextureManager.loadTexture2D(stone);
		stoneTexture.setWrapMode(WrapMode.Repeat);
		wallTexture = TextureManager.loadTexture2D(wall);
		avatarTexture = TextureManager.loadTexture2D(avatar);
		bombTexture = TextureManager.loadTexture2D(bomb);
		diamondTexture = TextureManager.loadTexture2D(diamond);
		redTexture = TextureManager.loadTexture2D(red);
		yellowTexture = TextureManager.loadTexture2D(yellow);
		greenTexture = TextureManager.loadTexture2D(green);
		blueTexture = TextureManager.loadTexture2D(blue);
		//grassTexture = TextureManager.loadTexture2D(grass);
		//grassTexture.setWrapMode(WrapMode.Repeat);
	}
	
	public TriMesh addAvatar() {
		/*Model3DTriMesh avatarTM = null;
		OgreXMLParser xmlLoader = new OgreXMLParser();
		String baseDir = directory + dirModel;
		
		try {
			Group model = xmlLoader.loadModel(baseDir + "Cube.001.mesh.xml", baseDir
					+ "AetherBattleToon.material", baseDir + "Cube.001.skeleton.xml");
			model.updateGeometricState(0, true);
			Iterator<SceneNode> itr = model.iterator();
			
			avatarTM = (Model3DTriMesh) itr.next();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		avatarTexture.setApplyMode(Texture.ApplyMode.Replace);
		avatarTM.setTexture(avatarTexture);
		
		return avatarTM;*/
		
		TriMesh avatarTM = loader.loadModel(directory + dirModel + "AetherBattleToon2.obj");
		avatarTM.updateLocalBound();
		
		avatarTM.setTexture(avatarTexture);
		avatarTM.setColorBuffer(null);
		return avatarTM;
	}
	
	public TriMesh addBomb() {
		TriMesh bombTM = loader.loadModel(directory + dirModel + "Bomb.obj");
		bombTM.updateLocalBound();
		
		bombTM.setTexture(bombTexture);
		bombTM.setColorBuffer(null);
		return bombTM;
	}
	
	public TriMesh addDiamond() {
		TriMesh diamondTM = loader.loadModel(directory + dirModel + "Diamond.obj");
		diamondTM.updateLocalBound();
		
		diamondTM.setTexture(diamondTexture);
		diamondTM.setColorBuffer(null);
		return diamondTM;
	}
	
	public TriMesh addRedBlock() {
		TriMesh redBlockTM = loader.loadModel(directory + dirModel + "Block.obj");
		redBlockTM.updateLocalBound();
		
		redBlockTM.setTexture(redTexture);
		redBlockTM.setColorBuffer(null);
		return redBlockTM;
	}
	
	public TriMesh addYellowBlock() {
		TriMesh yellowBlockTM = loader.loadModel(directory + dirModel + "Block.obj");
		yellowBlockTM.updateLocalBound();
		
		yellowBlockTM.setTexture(yellowTexture);
		yellowBlockTM.setColorBuffer(null);
		return yellowBlockTM;
	}
	
	public TriMesh addGreenBlock() {
		TriMesh greenBlockTM = loader.loadModel(directory + dirModel + "Block.obj");
		greenBlockTM.updateLocalBound();
		
		greenBlockTM.setTexture(greenTexture);
		greenBlockTM.setColorBuffer(null);
		return greenBlockTM;
	}
	
	public TriMesh addBlueBlock() {
		TriMesh blueBlockTM = loader.loadModel(directory + dirModel + "Block.obj");
		blueBlockTM.updateLocalBound();
		
		blueBlockTM.setTexture(blueTexture);
		blueBlockTM.setColorBuffer(null);
		return blueBlockTM;
	}
		
	public void updateBoundaryGroup(Group wallGrp) {
		Iterator<SceneNode> children = wallGrp.getChildren();
		
		Rectangle s;
		
		while (children.hasNext()) {
			s = (Rectangle) children.next();
			s.setTexture(wallTexture);
			s.setCullMode(CULL_MODE.NEVER);
		}
	}
	
	public SkyBox addSkyBox(BaseGame bg, float origin) {
		skyBox = new SkyBox();
		skyBox.scale(550, 550, 550);
		skyBox.translate(65, 0, 55 + origin);
		skyBox.setTexture(Face.Up, skyBoxTop);
		skyBox.setTexture(Face.Down, skyBoxBot);
		skyBox.setTexture(Face.East, skyBoxEast);
		skyBox.setTexture(Face.West, skyBoxWest);
		skyBox.setTexture(Face.North, skyBoxFront);
		skyBox.setTexture(Face.South, skyBoxBack);
		skyBox.setZBufferStateEnabled(false);
		
		return skyBox;
	}
	
	public void addGameFloor(Group environmentGroup) {
		floor = new Rectangle();
		floor.scale(400, 300, 10);
		floor.rotate(90, new Vector3D(1, 0, 0));
		floor.translate(0, 0, 0);
		floor.setTexture(groundTexture);
		
		environmentGroup.addChild(floor);
	}
	
	public Rectangle getFloor() {
		return floor;
	}
	
	public TerrainBlock initTerrain(IDisplaySystem display) {
		HillHeightMap myHillHeightMap = new HillHeightMap(200, 1750, 45.0f, 50.0f, (byte) 2, 12345);
		myHillHeightMap.setHeightScale(1.1f);
		hillTerrain = createTerBlock(myHillHeightMap);
		
		TextureState terrainState;
		floorTexture.setApplyMode(sage.texture.Texture.ApplyMode.Replace);
		terrainState = (TextureState) display.getRenderer().createRenderState(RenderState.RenderStateType.Texture);
		terrainState.setTexture(floorTexture, 0);
		terrainState.setEnabled(true);
		
		hillTerrain.setRenderState(terrainState);
		return hillTerrain;
	}
	
	private TerrainBlock createTerBlock (AbstractHeightMap  heightMap) {
		float heightScale = 1.19f;
		Vector3D terrainScale = new Vector3D(1, heightScale, 1);
		
		int terrainSize = heightMap.getSize();
		
		float cornerHeight = heightMap.getTrueHeightAtPoint(0, 0) * heightScale;
		Point3D terrainOrigin = new Point3D(2000, -cornerHeight, 720);
		
		String name = "Terrain: " + heightMap.getClass().getSimpleName();
		TerrainBlock tb = new TerrainBlock(name, terrainSize, terrainScale, heightMap.getHeightData(), terrainOrigin);
		
		return tb;
	}
	
	public Group createSolarSystem() {
		Group temp = new Group("Solar System");
		Group temp2 = new Group("Planet System Rotation 1");
		Group temp3 = new Group("Planet System Rotation 2");
		Group temp4 = new Group("Moon");
		
		Sphere sun = new Sphere("Sun", 60, 30, 30, Color.ORANGE);
		sun.translate(20, 0, 0);
		Sphere planet = new Sphere("Planet", 30, 20, 20, Color.CYAN);
		planet.translate(-80, 20, 20);
		planet.scale(.6f, .6f, .6f);
		Sphere moon = new Sphere("Moon", 10, 30, 30, Color.GRAY);
		moon.translate(-110, 40, -5);
		moon.scale(.5f, .5f, .5f);
		
		temp.addChild(sun);
		temp.addChild(temp2);
		temp.addChild(temp3);
		temp3.addChild(planet);
		temp3.addChild(temp4);
		temp4.addChild(moon);
		
		temp.setIsTransformSpaceParent(true);
		temp2.setIsTransformSpaceParent(true);
		temp3.setIsTransformSpaceParent(true);
		temp4.setIsTransformSpaceParent(true);
		sun.setIsTransformSpaceParent(true);
		planet.setIsTransformSpaceParent(true);
		moon.setIsTransformSpaceParent(true);
		
		temp2.scale(2, 2, 2);
		temp3.scale(3, 3, 3);
		temp4.rotate(20, new Vector3D(0, 1, 0));
		
		return temp;
	}
	
	public SkyBox getSkyBox() {
		return skyBox;
	}
}
