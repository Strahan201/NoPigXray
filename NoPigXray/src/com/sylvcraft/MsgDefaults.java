package com.sylvcraft;

public class MsgDefaults {
	public String getDefault(String code) {
		switch (code.toLowerCase()) {
		case "player-only":
			return "This only works in-game!"; 
		case "access-denied":
			return "&cAccess denied!"; 
		default:
			return code;
		}
	}
}
