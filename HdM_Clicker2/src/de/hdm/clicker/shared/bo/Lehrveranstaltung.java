package de.hdm.clicker.shared.bo;

import java.util.Vector;

/**
 * Realisierung einer Lehrveranstaltungsklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation einer realen Lehrveranstaltung 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Lehrveranstaltung extends BusinessObject {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Umfang in SWS der Lehrveranstaltung
	 */
	private int umfang;
	
	/**
	 * Bezeichnung der Lehrveranstaltung
	 */
	private String bezeichnung;
	
	/**
	 * Studiensemester der Lehrveranstaltung
	 */
	private int studiensemester;
	
	/**
	 * Studiengänge in denen die Lehrveranstaltung gehalten wird
	 */
	private Vector<Studiengang> studiengaenge = null;
	
	/**
	 * Dozenten die diese Lehrveranstaltung dauerhaft unterrichten
	 */
	private Vector<Dozent> dozenten = null;
	
	/**
	 * Belegungen deren Inhalt diese Lehrveranstaltung ist
	 */
	private Vector<Belegung> belegungen = null;
	
	
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
	 * Ausgeben des Umfangs
	 */
	public int getUmfang() {
		return umfang;
	}

	/**
	 * Setzen des Umfangs
	 */
	public void setUmfang(int umfang) {
		this.umfang = umfang;
	}

	/**
	 * Ausgeben der Bezeichnung
	 */
	public String getBezeichnung() {
		return bezeichnung;
	}

	/**
	 * Setzen der Bezeichnung
	 */
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	/**
	 * Ausgeben des Studiensemesters
	 */
	public int getStudiensemester() {
		return studiensemester;
	}

	/**
	 * Setzen des Studiensemesters
	 */
	public void setStudiensemester(int studiensemester) {
		this.studiensemester = studiensemester;
	}

	/**
	 * Ausgeben der Studiengänge
	 */
	public Vector<Studiengang> getStudiengaenge() {
		return studiengaenge;
	}

	/**
	 * Setzen der Studiengänge
	 */
	public void setStudiengaenge(Vector<Studiengang> studiengaenge) {
		this.studiengaenge = studiengaenge;
	}

	/**
	 * Asugeben der Dozenten
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
	
}
