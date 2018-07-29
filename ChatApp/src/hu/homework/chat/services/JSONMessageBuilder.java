package hu.homework.chat.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONMessageBuilder {
	
	private JSONObject messageObject = new JSONObject();
	
	private String messageString = null;
	private String senderString = null;
	private String receiverString = null;
	private Boolean helloBoolean = null;
	
	public String getMessageString() {
		return messageString;
	}

	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

	public String getSenderString() {
		return senderString;
	}

	public void setSenderString(String senderString) {
		this.senderString = senderString;
	}

	public String getReceiverString() {
		return receiverString;
	}

	public void setReceiverString(String receiverString) {
		this.receiverString = receiverString;
	}

	
	
	public JSONMessageBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	public String buildMessage(String message, String from, String to, Boolean hello) {
		
		messageObject.put("message", message);
		messageObject.put("from", from);
		if(to != null) {
			messageObject.put("to", to);
		}
		messageObject.put("hello", new Boolean(hello));
		
		return messageObject.toJSONString();
	}
	
	public void parseMessage(String message) {
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(message);
			
			setMessageString((String)jsonObject.get("message"));
			setSenderString((String)jsonObject.get("from"));
			if(jsonObject.containsKey("to")) {
				setReceiverString((String)jsonObject.get("to"));
			}else {
				setReceiverString(null);
			}
			setHelloBoolean((Boolean)jsonObject.get("hello"));
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Boolean getHelloBoolean() {
		return helloBoolean;
	}

	public void setHelloBoolean(Boolean helloBoolean) {
		this.helloBoolean = helloBoolean;
	}
	
}
