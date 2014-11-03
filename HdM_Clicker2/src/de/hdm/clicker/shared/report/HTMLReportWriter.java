package de.hdm.clicker.shared.report;

/**
 * Ein <code>ReportWriter</code>, der Reports mittels HTML formatiert. Das im
 * Zielformat vorliegende Ergebnis wird in der Variable <code>result</code>
 * abgelegt.
 * 
 * @author 	Thies, Moser, Sonntag, Zanella
 * @version	1
 */
public class HTMLReportWriter {
	
	/**
	 * Ergebnis-String mit der HTML-Tabelle
	 */
	StringBuffer result = new StringBuffer();
	
	/**
	 * Methode welche den Inhalt einer zweidimensionale Liste/Container von einer 
	 * SimpleReport-Instanz in eine HTML-konforme Tabelle umwandelt
	 * 
	 * @param plan - SimpleReport-Instanz
	 */
	public String getHTMLString(SimpleReport plan) {
		result.append("<table style=\"background-color: black; width:1500px;height: 60px;rules: all; text-align: center; font-family: bold\"> ");
		for (int i = 0; i < 8; i++) {
			if (i == 4) {
				for (int j = 0; j < 8; j++) {
					if(j == 0) {
						result.append("<td style=\"background-color: silver; width:400px;height: 60px;rules: all; text-align: center; font-family: bold\"> ");
						result.append("<b style=\"background-color: silver\">13:15 - 14:15</b>");
						result.append("</td>");
					}
					else {
						result.append("<td style=\"background-color: silver; width:400px;height: 60px;rules: all; text-align: center; font-family: bold\"> ");
						result.append("<b>Pause</b>");
						result.append("</td>");
					}
				}
			}
			
			result.append("<tr style=\"background-color: white; width:400px;height: 60px;rules: all; text-align: center; font-family: bold\"> ");
			for (int j = 0; j < 8; j++) {
				result.append("<td style=\"background-color: white; width:400px;height: 60px;rules: all; text-align: center; font-family: bold\"> ");
				result.append(plan.getPlan().elementAt(j).elementAt(i));
				result.append("</td>");
			}
			result.append("</tr>");
		}
		result.append("</table>");
		return result.toString();
		
		
	
	}

}
