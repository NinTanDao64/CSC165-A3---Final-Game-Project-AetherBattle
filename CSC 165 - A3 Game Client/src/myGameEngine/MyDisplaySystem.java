package myGameEngine;

import java.awt.Canvas;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.jogamp.newt.Window;

import sage.display.DisplaySettingsDialog;
import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

public class MyDisplaySystem implements IDisplaySystem {
	private JFrame myFrame;
	private GraphicsDevice device;
	private IRenderer myRenderer;
	private int width, height, bitDepth, refreshRate;
	private Canvas rendererCanvas;
	private boolean isCreated;
	private boolean isFullScreen;
	
	private JRadioButton singlePlayer;
	private JRadioButton multiPlayer;
	private JTextField serverIP;
	private JTextField serverPort;
	
	public MyDisplaySystem (int w, int h, int depth, int rate, boolean isFS, String rName, String title) {
		width = w;
		height = h;
		bitDepth = depth;
		refreshRate = rate;
		this.isFullScreen = isFS;
		
		// Get a renderer from the Renderer Factory.
		myRenderer = RendererFactory.createRenderer(rName);
		if (myRenderer == null) {
			throw new RuntimeException("Unable to find renderer '" + rName + "'");
		}
		rendererCanvas = myRenderer.getCanvas();
		myFrame = new JFrame(title);
		myFrame.add(rendererCanvas);
		
		initScreen();
		
		// Save the configured DisplaySystem, show the window and flag that the DisplaySystem was made.
		DisplaySystem.setCurrentDisplaySystem(this);
		myFrame.setVisible(true);
		isCreated = true;
	}
	
	 public void close()
	 	{ if (device != null) {
	 		java.awt.Window window = device.getFullScreenWindow();
	 		if (window != null) { 
	 			((java.awt.Window) window).dispose();
	 		}
	 		device.setFullScreenWindow(null);
	 	} 
	 }
	 
	 public boolean isSinglePlayer() {
		 return singlePlayer.isSelected();
	 }
	 
	 public String getServerIP() {
		 return serverIP.getText();
	 }
	 
	 public int getServerPort() {
		 return Integer.parseInt(serverPort.getText());
	 }
	
		private void initScreen() {
			// Get the default screen device out of the local graphics environment.
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			
			device = environment.getDefaultScreenDevice();
			
			// Show the display dialog and allow the user to specify their display settings.
			DisplaySettingsDialog dialog = getConfiguirationWindow();
			dialog.showIt();
			
			DisplayMode dispMode = dialog.getSelectedDisplayMode();
			
			if (device.isFullScreenSupported() && dialog.isFullScreenModeSelected()) {
				myFrame.setUndecorated(true);
				myFrame.setResizable(false);
				myFrame.setIgnoreRepaint(true);
				
				// Put the device in full screen mode
				device.setFullScreenWindow(myFrame);
				
				// Try to set full screen device DisplayMode
				if (dispMode != null && device.isDisplayChangeSupported()) {
					try {
						device.setDisplayMode(dispMode);
						myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
					} catch (Exception e) {
						System.err.println("Exception while setting device DisplayMode: " + e);
					}
				} else {
					System.err.println("Cannot set display mode");
					
				}
				
			} else {
				// Windowed mode
				myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
				myFrame.setLocationRelativeTo(null);
			}
		}
		
		private DisplaySettingsDialog getConfiguirationWindow() {
			DisplaySettingsDialog dialog = new DisplaySettingsDialog(device);
				
			// Add networking options to the display window
			singlePlayer = new JRadioButton("Single Player");
			multiPlayer = new JRadioButton("Multi Player");
			serverIP = new JTextField("Server IP Address", 20);
			serverPort = new JTextField("Server Port", 20);
			ButtonGroup radioGroup = new ButtonGroup();

			// Radio buttons for single and multi-player
			radioGroup.add(singlePlayer);
			radioGroup.add(multiPlayer);
				
			dialog.setLayout(new FlowLayout());
			dialog.add(singlePlayer);
			dialog.add(multiPlayer);
			dialog.add(serverIP);
			dialog.add(serverPort);
			singlePlayer.setSelected(true);
				
			dialog.setSize(400, 300);
			dialog.setResizable(false);
				
			return dialog;
			}
		
		public IDisplaySystem waitForInitialization() {
			System.out.print("\nWaiting for display creation...");
			int count = 0;
			// wait until display creation completes or a timeout occurs
			while (!this.isCreated()) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					throw new RuntimeException("Display creation interrupted");
				}
				count++;
				System.out.print("+");
				if (count % 80 == 0) {
					System.out.println();
				}
				if (count > 2000) // 20 seconds (approx.)
				{
					throw new RuntimeException("Unable to create display");
				}
			}
			System.out.println();
			System.out.println("Finished creating the display.");
			
			return this;
		}

	@Override
	public void addKeyListener(KeyListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseListener(MouseListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convertPointToScreen(Point arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBitDepth() {
		// TODO Auto-generated method stub
		return this.bitDepth;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return this.height;
	}

	@Override
	public int getRefreshRate() {
		// TODO Auto-generated method stub
		return this.refreshRate;
	}

	@Override
	public IRenderer getRenderer() {
		// TODO Auto-generated method stub
		return this.myRenderer;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return this.width;
	}

	@Override
	public boolean isCreated() {
		// TODO Auto-generated method stub
		return this.isCreated;
	}

	@Override
	public boolean isFullScreen() {
		// TODO Auto-generated method stub
		return this.isFullScreen;
	}

	@Override
	public boolean isShowing() {
		// TODO Auto-generated method stub
		return this.isShowing();
	}

	@Override
	public void setBitDepth(int arg0) {
		// TODO Auto-generated method stub
		this.bitDepth = arg0;
	}

	@Override
	public void setCustomCursor(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setHeight(int arg0) {
		// TODO Auto-generated method stub
		this.height = arg0;
	}

	@Override
	public void setPredefinedCursor(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRefreshRate(int arg0) {
		// TODO Auto-generated method stub
		this.refreshRate = arg0;
	}

	@Override
	public void setTitle(String arg0) {
		// TODO Auto-generated method stub
		myFrame.setTitle(arg0);
	}

	@Override
	public void setWidth(int arg0) {
		// TODO Auto-generated method stub
		this.width = arg0;
	}

}
