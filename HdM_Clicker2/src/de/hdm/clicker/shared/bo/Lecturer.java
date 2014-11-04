package de.hdm.clicker.shared.bo;

/**
 * Realisierung einer Lecturer.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Lehrenden 
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */

public class Lecturer extends BusinessObject {

	private String user;
	
	private String password;
	
	private String firstName;
	
	private String name;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
