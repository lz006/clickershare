package de.hdm.clicker.shared.report;

import java.io.Serializable;
import java.util.Vector;

/**
 * <p>
 * Ein einfacher Report, welcher das "Grundgerüst" aller Reportvarianten
 * beinhaltet. Konkrete Ausprägungen sind Studentenplan { @see Studentenplan},
 * Dozentenplan { @see Dozentenplan} und Raumplan { @see Raumplan}. Es das Serializable
 * Interface implmentiert, um dessen Serialisierbarkeit zu signalisieren, da Instanzen
 * dieser Klasse an den Client übermittelt werden. 
 * <p>
 * 
 * @author 	Thies, Moser, Sonntag, Zanella
 * @version	1
 */
public class SimpleReport implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 *  Eine zweidimensionalen Liste/Container, welche eine 7-Tage Woche repräsentiert
	 */
	Vector<Vector<String>> plan = new Vector<Vector<String>>();
	
	/**
	 * Ergebnis- HTML-Tabelle
	 */
	String htmlTable = null;

	public SimpleReport() {
		
	}
	
	/**
	 * Methode welche eine zweidimensionale leere Liste/Container erzeugt, die eine 7-Tage Woche
	 * repräsentieren soll
	 */
	public void init() {
		for (int i = 0; i < 8; i++) {
			Vector<String> tempColumn = new Vector<String>();
			for (int j = 0; j < 8; j++) {
				tempColumn.add("");
			}
			plan.add(tempColumn);
		}
	}
	
	/**
	 * Ausgeben der zweidimensionalen leeren Liste/Container
	 * 
	 * @return	Vector<Vector<String>>
	 */
	public Vector<Vector<String>> getPlan() {
		return plan;
	}
	
	/**
	 * Ausgeben der Ergebnis-HTML-Tabelle
	 * 
	 * @return	String mit <table>-Tag
	 */
	public String getHtmlTable() {
		return htmlTable;
	}

	/**
	 * Setzen der Ergebnis-HTML-Tabelle
	 * 
	 * @param	htmlTable - String mit <table>-Tag
	 */
	public void setHtmlTable(String htmlTable) {
		this.htmlTable = htmlTable;
	}
}
