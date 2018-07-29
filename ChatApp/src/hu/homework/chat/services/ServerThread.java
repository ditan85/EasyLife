package hu.homework.chat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ServerThread implements Runnable {
	private Socket socket = null;
	private List<ServerThread> serverThreadList = null;

	public ServerThread(Socket socket, List<ServerThread> serverThreadList) {
		this.socket = socket;
		this.serverThreadList = serverThreadList;
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String message = br.readLine();
				for (ServerThread serverThread : serverThreadList) {
					serverThread.writeMessage(message);
				}

			}

		} catch (IOException e) {
			System.err.println(e);
		}
	}

	public void writeMessage(String message) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write((message + "\r\n").getBytes());
			outputStream.flush();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
