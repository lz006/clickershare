package de.hdm.clicker.shared.bo;


/**
 * Realisierung einer Raumklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Raumes 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Raum extends BusinessObject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Kapazität eines Raumes, die Anzahl steht für die
	 * Personenzahl, welche Platz finden
	 */
	private int kapazitaet;
	
	/**
	 * Eindeutige Bezeichung des Raumes
	 */
	private String bezeichnung;
	
	/**
	 * Ausgeben der Kapazität
	 */
	public int getKapazitaet() {
		return kapazitaet;
	}

	/**
	 * Setzen der Kapazität
	 */
	public void setKapazitaet(int kapazitaet) {
		this.kapazitaet = kapazitaet;
	}

	/**
	 * Ausgeben der Bezeichung
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

}
