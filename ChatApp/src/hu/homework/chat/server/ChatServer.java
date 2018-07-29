package hu.homework.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import hu.homework.chat.services.ServerThread;

public class ChatServer {

	public static void main(String[] args) {
		List<ServerThread> serverThreadList = new ArrayList<>();
		try {
			ServerSocket serverSocket = new ServerSocket(10080);
			System.out.println("Sever started!");
			while(true) {
				Socket tempSocket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(tempSocket,serverThreadList);
				serverThreadList.add(serverThread);
				System.out.println("Request received from " + serverThread.toString());
				
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
