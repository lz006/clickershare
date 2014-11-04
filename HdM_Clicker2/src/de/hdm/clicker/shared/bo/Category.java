package de.hdm.clicker.shared.bo;

/**
 * Realisierung einer Category.
 * Ein Objekt dieser Klasse ist die Repräsentation einer realen Kategorie 
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */
public class Category extends BusinessObject {
	
	private String description;
	
	private int lecturerID;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLecturerID() {
		return lecturerID;
	}

	public void setLecturerID(int lecturerID) {
		this.lecturerID = lecturerID;
	}
	
	
	
}
