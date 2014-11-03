package de.hdm.clicker.shared.bo;

/**
 * Realisierung einer Zeitslotklasse.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Zeitslots 
 * 
 * @author thies (implement: Roth, Zimmermann, Klatt, Moser, Sonntag, Zanella)
 * @version 1.0
 */
public class Zeitslot extends BusinessObject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Zeitpunkt an dem dieser Zeitslot endet
	 * (Zählung fortlaufender Minuten innerhalb von 24h)
	 */
	private int endzeit;
	
	/**
	 * Zeitpunkt an dem dieser Zeitslot beginnt
	 * (Zählung fortlaufender Minuten innerhalb von 24h)
	 */
	private int anfangszeit;
	
	/**
	 * Wochentag dem dieser Zeitslot zugeordnet ist
	 */
	private String wochentag;

	/**
	 * Ausgeben des Endzeitpunktes
	 */
	public int getEndzeit() {
		return endzeit;
	}
	
	/**
	 * Setzen des Endzeitpunktes
	 */
	public void setEndzeit(int endzeit) {
		this.endzeit = endzeit;
	}

	/**
	 * Ausgeben des Anfangszeitpunktes
	 */
	public int getAnfangszeit() {
		return anfangszeit;
	}
	
	/**
	 * Setzen des Anfangszeitpunktes
	 */
	public void setAnfangszeit(int anfangszeit) {
		this.anfangszeit = anfangszeit;
	}

	/**
	 * Ausgeben des Wochentags
	 */
	public String getWochentag() {
		return wochentag;
	}
	
	/**
	 * Setzen des Wochentags
	 */
	public void setWochentag(String wochentag) {
		this.wochentag = wochentag;
	}

}
