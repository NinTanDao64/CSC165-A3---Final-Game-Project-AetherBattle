package a3;

import Server.AetherBattleServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import sage.networking.IGameConnection.ProtocolType;

public class Starter {
	public static void main(String args[]) {
		int port;
		
		try {
			System.out.println("Current Address: " + InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		//Obtain port from user
		Scanner console = new Scanner(System.in);
		System.out.print("Enter a port number to host your game on: ");
		port = console.nextInt();
		
		console.close();
		
		try {
			new AetherBattleServer(port, ProtocolType.TCP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
