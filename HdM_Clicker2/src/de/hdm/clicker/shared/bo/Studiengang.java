package de.hdm.clicker.shared.bo;

import java.util.Vector;

/**
 * Realisierung einer Studiengangsklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Studiengangs 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Studiengang extends BusinessObject{


	private static final long serialVersionUID = 1L;
	
	/**
	 * Semesterverbände die diesem Studiengang zugeordnet sind
	 */
	private Vector<Semesterverband> semesterverbaende = null;
	
	/**
	 * Lehrveranstaltungen die diesem Studiengang zugeordnet sind
	 */
	private Vector<Lehrveranstaltung> lehrveranstaltungen = null;
	
	/**
	 * Bezeichnung des Studiengangs
	 */
	private String bezeichnung;
	
	/**
	 * Kürzel des Studiengangs
	 */
	private String kuerzel;	
	
	/**
	 * Ausgeben des Kürzels
	 */
	public String getKuerzel() {
		return kuerzel;
	}
	
	/**
	 * Setzen des Kürzels
	 */
	public void setKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
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
	 * Ausgeben der Bezeichnung
	 */
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	/**
	 * Setzen der Lehrveranstaltungen
	 */
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

}
