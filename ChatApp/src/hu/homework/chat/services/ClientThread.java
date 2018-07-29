package hu.homework.chat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import hu.homework.chat.client.ChatClientMainForm;

public class ClientThread implements Runnable {
	private ChatClientMainForm mainForm = null;
	private Socket socket = null;

	public ClientThread(String host, int port, ChatClientMainForm mainForm) {
		this.mainForm = mainForm;

		try {
			socket = new Socket(host, port);
		} catch (Exception ex) {
			System.err.println(ex);
		}
		new Thread(this).start();
	}

	@Override
	public void run() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String message = br.readLine();
				mainForm.appendMessage(message);
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
			mainForm.setSelectedItem(null);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

}
