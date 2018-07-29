package hu.homework.chat.client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import hu.homework.chat.services.ClientThread;
import hu.homework.chat.services.JSONMessageBuilder;


public class ChatClientMainForm extends JFrame
		implements ActionListener, KeyListener, ListSelectionListener, ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1250061283687678809L;
	private static final String AC_SEND = "AC_SEND";
	private static final String AC_CLEAR = "AC_CLEAR";

	private JPanel mainPanel = null;
	private JTextPane chatHistory = null;
	private JTextPane chatText = null;
	private JButton sendButton = null;
	private JButton clearHistory = null;
	private DefaultListModel<String> nameListModel = null;
	private JList<String> nameList = null;
	private JScrollPane nameListPanel = null;

	private String selectedItem = null;
	
	private String myname = null;

	private int gepY = 40;
	private int gepX = 20;

	private ClientThread clientThread = null;
	
	private JSONMessageBuilder jsonMessageBuilder = null;
	
	public ChatClientMainForm() {
		init();
	}
	
	public void init() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, 600, 600);
		this.setResizable(true);
		this.addComponentListener(this);
		this.setVisible(true);
		myname = (String) JOptionPane.showInputDialog("What is your name?");

		if (myname == null || myname.length() < 1) {
			myname = "Anonimus" + new Random().nextInt(999999);
		}

		this.setTitle("Chat APP, " + myname);
		mainPanel = new JPanel();
		GroupLayout layout = new GroupLayout(mainPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		mainPanel.setLayout(layout);

		chatHistory = new JTextPane();
		mainPanel.add(chatHistory);

		chatText = new JTextPane();
		chatText.addKeyListener(this);	
		chatText.requestFocus();
		mainPanel.add(chatText);
		
		nameListModel = new DefaultListModel<String>();
		nameList = new JList<String>(buildNameListModel(myname));
		nameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		nameList.setLayoutOrientation(JList.VERTICAL);
		nameList.setVisibleRowCount(-1);
		nameList.addListSelectionListener(this);

		nameListPanel = new JScrollPane(nameList);

		nameListPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		nameListPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(nameListPanel);
		
		sendButton = new JButton("SEND");
		sendButton.addActionListener(this);
		sendButton.setActionCommand(AC_SEND);
		
		mainPanel.add(sendButton);

		clearHistory = new JButton("CLEAR");
		clearHistory.addActionListener(this);
		clearHistory.setActionCommand(AC_CLEAR);
		
		mainPanel.add(clearHistory);

		add(mainPanel);

		setContent();
		
		setVisible(true);
		validate();
		repaint();
		
		clientThread = new ClientThread("localhost", 10080, this);
		
		jsonMessageBuilder = new JSONMessageBuilder();
		
		clientThread.writeMessage(jsonMessageBuilder.buildMessage("Hello my name is " + myname, myname, selectedItem, true));
	}
	
	private void setContent() {
		mainPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		
		chatHistory.setSize((this.getWidth()/3)*2, ((this.getHeight()/4)*3) - gepY);
		chatHistory.setLocation(5, 5);
		
		chatText.setSize(chatHistory.getWidth(), this.getHeight()/4 - gepY);
		chatText.setLocation(chatHistory.getX(), this.getHeight() - (chatText.getHeight() + gepY) - 5);
		
		nameListPanel.setSize(this.getWidth() - chatHistory.getWidth() - 2*gepX , chatHistory.getHeight());
		nameListPanel.setLocation(chatHistory.getWidth() + gepX, chatHistory.getY());
		
		sendButton.setSize(nameListPanel.getWidth() - gepX, chatText.getHeight()/2);
		sendButton.setLocation(chatText.getX() + chatText.getWidth() + gepX, chatText.getY());
		
		clearHistory.setSize(sendButton.getWidth(), sendButton.getHeight());
		clearHistory.setLocation(sendButton.getX(),	sendButton.getY() + sendButton.getHeight());
		
		validate();
		repaint();
	}

	
	private ListModel<String> buildNameListModel(String name) {

		if (!nameListModel.contains(name)) {
			nameListModel.addElement(name);
		}
		
		validate();
		repaint();
		
		return nameListModel;
	}

	private void clearChatHistory() {
		chatHistory.setText("");
		chatHistory.setCaretPosition(0);
	}

	private void clearChatText() {
		chatText.setText("");
		validate();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			sendMessage();
		}
	}

	private void sendMessage() {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		
		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append(timeStamp).append(" - ").append("ME").append(" - ").append(chatText.getText().replaceAll("\n", "")).append("\n");
		chatHistory.setForeground(Color.BLUE);
		validate();
		repaint();
		appendToPane(chatHistory, messageBuilder.toString(), Color.BLUE);
	
		clientThread.writeMessage(jsonMessageBuilder.buildMessage(chatText.getText().replaceAll("\n", ""), myname, selectedItem, false));
		selectedItem = null;
		clearChatText();		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case AC_SEND: {
			sendMessage();
		}
			break;
		case AC_CLEAR: {
			clearChatHistory();
			clearChatText();
		}
			break;

		default:
			break;
		}

	}

	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource().equals(nameList)) {
			selectedItem = nameList.getSelectedValue().toString();
		}
	}

	public String getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(String selectedItem) {
		this.selectedItem = selectedItem;
	}

	public void appendMessage(String message) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
		StringBuilder messageBuilder = new StringBuilder();
		
		jsonMessageBuilder.parseMessage(message);
		
		String whisper = jsonMessageBuilder.getReceiverString();
		String sender = jsonMessageBuilder.getSenderString();
		
		if(!sender.equals(myname)) {
			if(whisper != null) {
				if(!whisper.equals(myname)) {
					messageBuilder.append(timeStamp).append(" - ").append(sender).append(" - ").append(jsonMessageBuilder.getMessageString()).append("\n");
					appendToPane(chatHistory, messageBuilder.toString(), Color.RED);
				}
			}else {		
				messageBuilder.append(timeStamp).append(" - ").append(sender).append(" - ").append(jsonMessageBuilder.getMessageString()).append("\n");
				appendToPane(chatHistory, messageBuilder.toString(), Color.GREEN);
				if(jsonMessageBuilder.getHelloBoolean()) {
					clientThread.writeMessage(jsonMessageBuilder.buildMessage("Hello " + sender + "!", myname, selectedItem, false));
				}
			}
		}
		
		buildNameListModel(sender);
		validate();
		repaint();
	}
	
	public String getMyname() {
		return myname;
	}

	public void setMyname(String myname) {
		this.myname = myname;
	}
	
	private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		if(mainPanel != null)
			setContent();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
