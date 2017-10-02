package l2;

import java.io.*;
import java.util.Scanner;

public class formatted_msg implements Serializable {
	public enum CTRL {
		NORMAL, TERMINATE, LOOPBACK, BROADCAST, SETUP, GET_ALL_CLIENTS
	};
	String from;
	String dest;
	String msg;
	CTRL msg_ctrl;

	public formatted_msg(String from, String msg) {
		this.from = from;
		dest = null;
		this.msg = msg;
		msg_ctrl = CTRL.NORMAL;
	}

	public void set_terminate() {
		msg_ctrl = CTRL.TERMINATE;
	}

	public void set_loopback() {
		msg_ctrl = CTRL.LOOPBACK;
	}

	public void set_ctrl(CTRL ctrl) {
		msg_ctrl = ctrl;
	}

	public CTRL get_ctrl() {
		return msg_ctrl;
	}
	public String get_from(){
		return from;
	}
	public String get_dest() {
		return dest;
	}

	public void set_dest(String s) {
		this.dest = s;
	}

	public void set_msg(String s) {
		this.msg = s;
	}

	public String toString() {
		String str = "New message from " + from +":" + msg;
		switch (msg_ctrl) {
		case NORMAL:
			str += " NORMAL";
			break;
		case TERMINATE:
			str += " TERMINATE";
			break;
		case LOOPBACK:
			str += " LOOPBACK";
			break;
		case BROADCAST:
			str += " BROADCAST";
			break;
		case SETUP:
			str += " SETUP";
			break;
		case GET_ALL_CLIENTS:
			str += " GET_ALL_CLIENTS";
			break;
		}
		return str;
	}

	// Ask the user for the message type, destination, and content
	public void init() {
		Scanner s = new Scanner(System.in);
		System.out.println("Set Msg Type: ");
	/*	while (correct_msg_type ==false){
			
			String type = s.nextLine();
			System.out.println(type);
			if (type == "NORMAL"||type == "TERMINATE"||type == "LOOPBACK"||type == "BROADCAST"||type == "SETUP"||type == "GET_ALL_CLIENTS"){
				correct_msg_type = true; //i feel as though that if statement can and should be written better. good for now though.
			}
			else {
				System.out.println("Possible msg types NORMAL, TERMINATE, LOOPBACK, BROADCAST, SETUP, GET_ALL_CLIENTS");
				}
		}*/
		String type = s.nextLine();
		switch (type) {
		case "NORMAL":
			msg_ctrl = CTRL.NORMAL;
			System.out.println("Destination: ");
			dest = s.nextLine();
			System.out.println("Content:");
			msg = s.nextLine();
			break;
		case "TERMINATE":
			msg_ctrl = CTRL.TERMINATE;
			break;
		case "LOOPBACK":
			msg_ctrl = CTRL.LOOPBACK;
			System.out.println("Content:");
			msg = s.nextLine();
			break;
		case "BROADCAST":
			msg_ctrl = CTRL.BROADCAST;
			System.out.println("Content");
			msg = s.nextLine();
			break;
		case "SETUP":
			msg_ctrl = CTRL.SETUP;
			break;
		case "GET_ALL_CLIENTS":
			msg_ctrl = CTRL.GET_ALL_CLIENTS;
			break;
		}
	}
}
