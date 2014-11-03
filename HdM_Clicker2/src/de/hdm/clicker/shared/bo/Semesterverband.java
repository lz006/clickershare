package de.hdm.clicker.shared.bo;

import java.util.Vector;

/**
 * Realisierung einer Semesterverbandsklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Semesterverbandes 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Semesterverband extends BusinessObject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Studiengang dem dieser Semesterverband zugeordnet ist
	 */
	private Studiengang studiengang;
	
	/**
	 * Semester des Studienbeginns
	 */
	private String jahrgang;
	
	/**
	 * Anzahl der Studenten, welche sich zu diesem Semesterverband zählen
	 */
	private int anzahlStudenten;
	
	/**
	 * Belegungen für die dieser Semesterverband eingeteilt ist
	 */
	private Vector<Belegung> belegungen = null;

	/**
	 * Ausgeben des Studiengangs
	 */
	public Studiengang getStudiengang() {
		return studiengang;
	}

	/**
	 * Setzen des Studiengangs
	 */
	public void setStudiengang(Studiengang studiengang) {
		this.studiengang = studiengang;
	}


	/**
	 * Ausgeben des Jahrgangs 
	 */
	public String getJahrgang() {
		return jahrgang;
	}

	/**
	 * Setzen des Jahrgangs
	 */
	public void setJahrgang(String jahrgang) {
		this.jahrgang = jahrgang;
	}

	/**
	 * Ausgeben der Studentenanzahl
	 */
	public int getAnzahlStudenten() {
		return anzahlStudenten;
	}

	/**
	 * Setzen der Studentenanzahl
	 */
	public void setAnzahlStudenten(int anzahlStudenten) {
		this.anzahlStudenten = anzahlStudenten;
	}

	/**
	 * Ausgeben der Belegungen
	 */
	public Vector<Belegung> getBelegungen() {
		return belegungen;
	}

	/**
	 * Setzen der Belegungen
	 */
	public void setBelegungen(Vector<Belegung> belegungen) {
		this.belegungen = belegungen;
	}
	
}
