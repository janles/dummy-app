package com.example.neuegruppeerstellen;

public class NameETA {
	
	private int hours;
	private int minutes;
	private String name;
	
	
	public NameETA(int minutes, String name) {
		this.minutes = minutes;
		this.name = name;
	}
	
	
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
