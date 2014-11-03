package de.hdm.clicker.shared.report;

/**
 * Kind-Klasse von SimpleReport, welcher die gesamte Wochenübersicht 
 * eines Semesterverbandes repräsentiert und zur eindeutigen 
 * Klassifizierung den den Namen "Studentenplan" trägt
 * 
 * @author 	Thies, Moser, Sonntag, Zanella
 * @version	1
 */
public class Studentenplan extends SimpleReport {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Studienhalbjahr eines Semesterverbandes
	 */
	String studienhalbjahr = null;

	/**
	 * Ausgeben des Studienhalbjahrs
	 * 
	 * @return	String (Studienhalbjahr)
	 */
	public String getStudienhalbjahr() {
		return studienhalbjahr;
	}

	/**
	 * Setzen des Studienhalbjahrs
	 * 
	 * @param studienhalbjahr (-String)
	 */
	public void setStudienhalbjahr(String studienhalbjahr) {
		this.studienhalbjahr = studienhalbjahr;
	}

}
