package de.hdm.clicker.shared.bo;

import java.util.Vector;

/**
 * Realisierung einer Belegungsklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation einer realen Belegung 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Belegung extends BusinessObject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Die zu haltende Lehrveranstaltung
	 */
	private Lehrveranstaltung lehrveranstaltung;
	
	/**
	 * Der in Anspruch genommene Raum
	 */
	private Raum raum;
	
	/**
	 * Den belegten Zeitslot
	 */
	private Zeitslot zeitslot;
	
	/**
	 * Die unterrichtenden Dozenten
	 */
	private Vector<Dozent> dozenten;
	
	/**
	 * Die eingeplanten Semesterverbände als Rezipienten
	 */
	private Vector<Semesterverband> semesterverbaende;

	/**
	 * Ausgeben der Lehrveranstaltung
	 */
	public Lehrveranstaltung getLehrveranstaltung() {
		return lehrveranstaltung;
	}

	/**
	 * Setzen der Lehrveranstaltung
	 */
	public void setLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {
		this.lehrveranstaltung = lehrveranstaltung;
	}

	/**
	 * Ausgeben des Raums
	 */
	public Raum getRaum() {
		return raum;
	}

	/**
	 * Setzen des Raums
	 */
	public void setRaum(Raum raum) {
		this.raum = raum;
	}

	/**
	 * Ausgeben des Zeitslots
	 */
	public Zeitslot getZeitslot() {
		return zeitslot;
	}

	/**
	 * Setzen des Zeitslots
	 */
	public void setZeitslot(Zeitslot zeitslot) {
		this.zeitslot = zeitslot;
	}

	/**
	 * Ausgeben der Dozenten
	 */
	public Vector<Dozent> getDozenten() {
		return dozenten;
	}

	/**
	 * Setzen der Dozenten
	 */
	public void setDozenten(Vector<Dozent> dozenten) {
		this.dozenten = dozenten;
	}

	/**
	 * Ausgeben der Semesterverbände
	 */
	public Vector<Semesterverband> getSemesterverbaende() {
		return semesterverbaende;
	}

	/**
	 * Setzen der Semesterverbände
	 */
	public void setSemesterverbaende(Vector<Semesterverband> semesterverbaende) {
		this.semesterverbaende = semesterverbaende;
	}	

}
