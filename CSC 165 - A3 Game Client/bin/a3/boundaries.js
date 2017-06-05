var JavaPackages = new JavaImporter(
		 Packages.sage.scene.Group,
		 Packages.sage.scene.shape.Cube,
		 Packages.sage.scene.shape.Rectangle,
		 Packages.graphicslib3D.Vector3D,
		 Packages.graphicslib3D.Point3D);
with (JavaPackages){
		var boundaryGroup = new Group();
		var origin = 65;

		// Adds the game boundaries to the world.
		var wall1 = new Rectangle("North wall", 400, 60);
		wall1.translate(0, 30, 150);
			
		// Adds the game boundaries to the world.
		var wall2 = new Rectangle("South wall", 400, 60);
		wall2.translate(0, 30, -150);
				
		// Adds the game boundaries to the world.
		var wall3 = new Rectangle("East wall", 300, 60);
		wall3.rotate(90, new Vector3D(0, 1, 0));
		wall3.translate(-200, 30, 0);
			
		// Adds the game boundaries to the world.
		var wall4 = new Rectangle("West wall", 300, 60);
		wall4.rotate(90, new Vector3D(0, 1, 0));
		wall4.translate(200, 30, 0);

		boundaryGroup.addChild(wall1);
		boundaryGroup.addChild(wall2);
		boundaryGroup.addChild(wall3);
		boundaryGroup.addChild(wall4);			
}
