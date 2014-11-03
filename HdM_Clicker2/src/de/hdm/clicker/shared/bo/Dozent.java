package de.hdm.clicker.shared.bo;

import java.util.Vector;

/**
 * Realisierung einer Dozentenklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Dozenten 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Dozent extends BusinessObject {
		
	private static final long serialVersionUID = 1L;
	
	/**
	 * Vorname des Dozenten
	 */
	private String vorname;
	
	/**
	 * Personalnummer des Dozenten
	 */
	private int personalnummer;
	
	/**
	 * Nachname des Dozenten
	 */
	private String nachname;
	
	/**
	 * Die Belegungen in denen der Dozent Lehrveranstaltungen hält
	 */
	private Vector<Belegung> belegungen;
	
	/**
	 * Lehrveranstaltungen die der Kompetenz des Dozenten entsprechen
	 */
	private Vector<Lehrveranstaltung> lehrveranstaltungen;
	
	/**
	 * Titel des Dozenten
	 */
	private String titel = "Prof.";

	/**
	 * Ausgeben des Titels
	 */
	public String getTitel() {
		return titel;
	}

	/**
	 * Ausgeben der Personalnummer
	 */
	public int getPersonalnummer() {
		return personalnummer;
	}

	/**
	 * Setzen der Personalnummer
	 */
	public void setPersonalnummer(int personalnummer) {
		this.personalnummer = personalnummer;
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

	/**
	 * Ausgeben der Lehrveranstaltungen
	 */
	public Vector<Lehrveranstaltung> getLehrveranstaltungen() {
		return lehrveranstaltungen;
	}

	/**
	 * Setzen der Lehrveranstaltungen
	 */
	public void setLehrveranstaltungen(Vector<Lehrveranstaltung> lehrveranstaltungen) {
		this.lehrveranstaltungen = lehrveranstaltungen;
	}	
	
	/**
	 * Ausgeben des Vornames
	 */
	public String getVorname() {
		return vorname;
	}
	
	/**
	 * Setzen des Vornames
	 */
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
	/**
	 * Ausgeben des Nachnamens
	 */
	public String getNachname() {
		return nachname;
	}
	
	/**
	 * Setzen des Nachnamens
	 */
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	
}