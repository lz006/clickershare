package de.hdm.clicker.server.report;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import de.hdm.clicker.server.VerwaltungImpl;
import de.hdm.clicker.shared.bo.*;
import de.hdm.clicker.shared.report.*;
import de.hdm.clicker.shared.Report;

/**
 * Implementierung des <code>Report</code>-Interface. Die technische
 * Realisierung bzgl. <code>RemoteServiceServlet</code> bzw. GWT RPC erfolgt
 * analog zu {@link ReportImpl}. Für Details zu GWT RPC siehe dort.
 * 
 * Diese Klasse Aggregiert sämtliche Belegungs-Informationen rund ein 
 * BusinessObjekt und verpackt diese mittelbar in eine menschenlesbare
 * Form (HTML-Tabelle)
 *  
 * @see Report
 * @see com.hdm.stundenplantool2.shared.ReportAsync
 * @see RemoteServiceServlet
 * @author Thies, Moser, Sonntag, Zanella
 * @version 1
 */

@SuppressWarnings("serial")
public class ReportImpl extends RemoteServiceServlet implements Report {

	/**
	   * Instanziierung der VerwaltungsImpl um sich derer "Mapper-Referenzen"
	   * bedienen zu können
	   */
	VerwaltungImpl verwaltung = new VerwaltungImpl();

	/**
	 * Methode um einen Studentenplan anhand eines Semesterverbandes zu erstellen
	 * 
	 * @param	sv - Semesterverband-Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Studentenplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	public Studentenplan createStudentenplan(Semesterverband sv) throws RuntimeException {

		// Conatiner, welche alle Belegungen eines Semesterverbandes enthält
		Vector<Belegung> svBelegungen = verwaltung.belegungMapper.findBySemesterverband(sv);

		if (svBelegungen == null || svBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen zu diesem Semesterverband vorhanden");
		}

		// Instaziierung eines Studentenplan-Objekts
		Studentenplan sPlan = new Studentenplan();
		
		// Setzen des Studienhalbjahrs
		sPlan.setStudienhalbjahr(sv.getJahrgang());	
		
		// Erzeugen einer zweidimensionalen leere Liste/Container, welche eine 7-Tage Woche repräsentiert
		sPlan.init();

		// Setzen der ersten Spalte
		sPlan.getPlan().elementAt(0).set(0, "<b>Wochentag/<br/>Uhrzeit");
		sPlan.getPlan().elementAt(0).set(1, "<b>08:15 - 09:45</b>");
		sPlan.getPlan().elementAt(0).set(2, "<b>10:00 - 11:30</b>");
		sPlan.getPlan().elementAt(0).set(3, "<b>11:45 - 13:15</b>");
		sPlan.getPlan().elementAt(0).set(4, "<b>14:15 - 15:45</b>");
		sPlan.getPlan().elementAt(0).set(5, "<b>16:00 - 17:30</b>");
		sPlan.getPlan().elementAt(0).set(6, "<b>17:45 - 19:15</b>");
		sPlan.getPlan().elementAt(0).set(7, "<b>19:30 - 21:00</b>");

		// Setzen der ersten Reihe
		sPlan.getPlan().elementAt(1).set(0, "<b>Montag</b>");
		sPlan.getPlan().elementAt(2).set(0, "<b>Dienstag</b>");
		sPlan.getPlan().elementAt(3).set(0, "<b>Mittwoch</b>");
		sPlan.getPlan().elementAt(4).set(0, "<b>Donnerstag</b>");
		sPlan.getPlan().elementAt(5).set(0, "<b>Freitag</b>");
		sPlan.getPlan().elementAt(6).set(0, "<b>Samstag</b>");
		sPlan.getPlan().elementAt(7).set(0, "<b>Sonntag</b>");

		// Für jede Belegung...
		for (int i = 0; i < svBelegungen.size(); i++) {

			int rowPointer = 0;
			int columnPointer = 0;

			// ...wird die Position im Wochenplan ermittelt
			if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 7	&& svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {
				columnPointer = 2;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}

			// Erzeugen eines StringBuffer-Objekt, das den Inhalt einer Zelle beherrbergt
			StringBuffer tempSB = new StringBuffer();
			
			// Hinzufügen der LV-Bezeichung
			tempSB.append(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");
			
			// Hinzufügen der Raum-Bezeichnung
			tempSB.append(svBelegungen.elementAt(i).getRaum().getBezeichnung());
			tempSB.append("<br />");

			/*
			 *  Hinzufügen der Vor- und Nachnamen aller Dozenten, die zu "dieser" Belegung
			 *  eingeteilt sind
			 */
			for (int j = 0; j < svBelegungen.elementAt(i).getDozenten().size() - 1; j++) {
				tempSB.append("Prof. ");
				tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(j).getVorname());
				tempSB.append(" ");
				tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(j).getNachname());
				tempSB.append("<br />");
			}

			tempSB.append("Prof. ");
			tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(svBelegungen.elementAt(i).getDozenten().size() - 1).getVorname());
			tempSB.append(" ");
			tempSB.append(svBelegungen.elementAt(i).getDozenten().elementAt(svBelegungen.elementAt(i).getDozenten().size() - 1).getNachname());

			// Setzen des Ergebnisses an die richtige Position
			sPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());

		}

		// Erzeugen eines StringBuffer-Objekts, welches des gesamten <body>-Inhalt beinhaltet
		StringBuffer htmlSV = new StringBuffer();
		
		// Hinzufügen eines Absatzes, welcher den mit dem Report assozierten Semesterverband anzeigt
		htmlSV.append("<p style=\"font-size: 40; font-weight: bold\">");
		htmlSV.append(sv.getStudiengang().getBezeichnung());
		htmlSV.append(" ");
		htmlSV.append(sv.getJahrgang());
		htmlSV.append("</p>");

		// Erzeugen einer HTML-<table> aus der zweidimensionalen Liste/Container, welche eine 7-Tage Woche repräsentiert
		String tempHtml = new HTMLReportWriter().getHTMLString(sPlan);

		// Hinzufügen eines Datums am Ende eine Reports
		SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
		String datum = datumFormat.format(new Date()).toString();
		htmlSV.append(tempHtml);
		
		htmlSV.append("<p style\"font-size:20; font-weight: bold \">");
		htmlSV.append("Stand: ");
		htmlSV.append(datum);
		htmlSV.append("</p>");
		

		// Setzen des <body>-Inhalts in das Studentenplan-Objekt
		sPlan.setHtmlTable(htmlSV.toString());
		
		return sPlan;
	}

	/**
	 * Methode um alle Studiengänge mittels Methode aus der Klasse VerwaltungImpl { @see VerwaltungImpl} 
	 * (Diese Studiengang-Objekte enthalten keine Referenzen zu Semesterverband- und 
	 * Lehrveranstaltungs- Objekten. Diese Methode dient nur dazu um in gewissen Situationen einen 
	 * Performancevorteil zu erzielen)
	 * 
	 * @return	Vector mit Studiengängen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV()	throws RuntimeException {
		return verwaltung.auslesenAlleStudiengaengeOhneSVuLV();
	}

	/**
	 * Methode um alle Semesterverbände anhand eines Studiengang-Objekts mittels Methode aus 
	 * der Klasse VerwaltungImpl { @see VerwaltungImpl} dem Client zur Verfügung zu stellen
	 * 
	 * @param	sg - Studiengang-Objekt aufgrund dessen die Semesterverbände ausgelesen werden sollen
	 * @return	Vector mit Semesterverbänden
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException {
		return verwaltung.auslesenSemesterverbaendeNachStudiengang(sg);
	}

	/**
	 * Methode um einen Dozentenplan anhand eines Dozenten zu erstellen
	 * 
	 * @param	dozent - Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Dozentenplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	public Dozentenplan createDozentenplan(Dozent dozent) throws RuntimeException {

		// Conatiner, welche alle Belegungen eines Dozenten enthält
		Vector<Belegung> dozentBelegungen = verwaltung.belegungMapper.findByDozent(dozent);

		if (dozentBelegungen == null || dozentBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen für diesem Dozent vorhanden");
		}

		// Instaziierung eines Dozentenplan-Objekts
		Dozentenplan dPlan = new Dozentenplan();
		
		// Erzeugen einer zweidimensionalen leere Liste/Container, welche eine 7-Tage Woche repräsentiert
		dPlan.init();

		// Setzen der ersten Spalte
		dPlan.getPlan().elementAt(0).set(0, "<b>Wochentag/<br/>Uhrzeit");
		dPlan.getPlan().elementAt(0).set(1, "<b>08:15 - 09:45</b>");
		dPlan.getPlan().elementAt(0).set(2, "<b>10:00 - 11:30</b>");
		dPlan.getPlan().elementAt(0).set(3, "<b>11:45 - 13:15</b>");
		dPlan.getPlan().elementAt(0).set(4, "<b>14:15 - 15:45</b>");
		dPlan.getPlan().elementAt(0).set(5, "<b>16:00 - 17:30</b>");
		dPlan.getPlan().elementAt(0).set(6, "<b>17:45 - 19:15</b>");
		dPlan.getPlan().elementAt(0).set(7, "<b>19:30 - 21:00</b>");

		// Setzen der ersten Reihe
		dPlan.getPlan().elementAt(1).set(0, "<b>Montag</b>");
		dPlan.getPlan().elementAt(2).set(0, "<b>Dienstag</b>");
		dPlan.getPlan().elementAt(3).set(0, "<b>Mittwoch</b>");
		dPlan.getPlan().elementAt(4).set(0, "<b>Donnerstag</b>");
		dPlan.getPlan().elementAt(5).set(0, "<b>Freitag</b>");
		dPlan.getPlan().elementAt(6).set(0, "<b>Samstag</b>");
		dPlan.getPlan().elementAt(7).set(0, "<b>Sonntag</b>");

		// Für jede Belegung...
		for (int i = 0; i < dozentBelegungen.size(); i++) {

			int rowPointer = 0;
			int columnPointer = 0;

			// ...wird die Position im Wochenplan ermittelt
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 7	&& dozentBelegungen.elementAt(i).getZeitslot().getId() <= 14) {
				columnPointer = 2;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 14 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 21 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 28 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 35 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (dozentBelegungen.elementAt(i).getZeitslot().getId() > 42 && dozentBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = dozentBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}

			// Erzeugen eines StringBuffer-Objekt, das den Inhalt einer Zelle beherrbergt
			StringBuffer tempSB = new StringBuffer();
			
			// Hinzufügen der LV-Bezeichung
			tempSB.append(dozentBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");
			
			// Hinzufügen der Raum-Bezeichnung
			tempSB.append(dozentBelegungen.elementAt(i).getRaum().getBezeichnung());
			tempSB.append("<br />");

			/*
			 *  Hinzufügen des Jahrgangs sowie des Studiengang-Kürzels aller Semesterverbände, 
			 *  die zu "dieser" Belegung eingeteilt sind
			 */
			if (dozentBelegungen.elementAt(i).getSemesterverbaende() != null
					&& dozentBelegungen.elementAt(i).getSemesterverbaende()	.size() > 0) {
				
				for (int j = 0; j < dozentBelegungen.elementAt(i).getSemesterverbaende().size() - 1; j++) {
					tempSB.append(verwaltung.auslesenSemesterverband(dozentBelegungen.elementAt(i).getSemesterverbaende()
							.elementAt(j)).elementAt(0).getStudiengang().getKuerzel());
					tempSB.append(" ");
					tempSB.append(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getJahrgang());
					tempSB.append("<br />");
				}

				tempSB.append(verwaltung.auslesenSemesterverband(dozentBelegungen.elementAt(i).getSemesterverbaende()
						.elementAt(dozentBelegungen.elementAt(i).getSemesterverbaende().size() - 1))
						.elementAt(0).getStudiengang().getKuerzel());
				tempSB.append(" ");
				tempSB.append(dozentBelegungen.elementAt(i).getSemesterverbaende().elementAt(dozentBelegungen.elementAt(i)
						.getSemesterverbaende().size() - 1).getJahrgang());
			}

			// Setzen des Ergebnisses an die richtige Position
			dPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());

		}

		// Erzeugen eines StringBuffer-Objekts, welches des gesamten <body>-Inhalt beinhaltet
		StringBuffer htmld = new StringBuffer();
		
		// Hinzufügen eines Absatzes, welcher den mit dem Report assozierten Dozent anzeigt
		htmld.append("<p style=\"font-size: 26; font-weight: bold\">");
		htmld.append("Lehrveranstaltungsplan für: </br>");
		htmld.append("</p>");

		htmld.append("<p style=\"font-size: 40; font-weight: bold\">");
		htmld.append(dozent.getNachname());
		htmld.append(" ");
		htmld.append(dozent.getVorname());
		htmld.append("</p>");

		// Erzeugen einer HTML-<table> aus der zweidimensionalen Liste/Container, welche eine 7-Tage Woche repräsentiert
		String tempHtml = new HTMLReportWriter().getHTMLString(dPlan);

		// Hinzufügen eines Datums am Ende eine Reports
		SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
		String datum = datumFormat.format(new Date()).toString();
		htmld.append(tempHtml);
		htmld.append("<p style\"font-size:20; font-weight: bold \">");
		htmld.append("Stand: ");
		htmld.append(datum);
		htmld.append("</p>");
		
		// Setzen des <body>-Inhalts in das Dozentenplan-Objekt
		dPlan.setHtmlTable(htmld.toString());

		return dPlan;

	}

	/**
	 * Methode um alle Dozenten mittels Methode aus der Klasse 
	 * VerwaltungImpl { @see VerwaltungImpl} dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Dozent> auslesenAlleDozenten() throws RuntimeException {
		return verwaltung.auslesenAlleDozenten();
	}

	/**
	 * Methode um einen Raumplan anhand eines Raumes zu erstellen
	 * 
	 * @param	raum - Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Raumplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	public Raumplan createRaumplan(Raum raum) throws RuntimeException {

		// Conatiner, welche alle Belegungen, die einen bestimmten Raum betreffen, enthält
		Vector<Belegung> raumBelegungen = verwaltung.belegungMapper.findByRaum(raum);

		if (raumBelegungen == null || raumBelegungen.size() <= 0) {
			throw new RuntimeException("Es sind keine Belegungen zu diesem Raum vorhanden");
		}

		// Instaziierung eines Raumplan-Objekts
		Raumplan rPlan = new Raumplan();
		
		// Erzeugen einer zweidimensionalen leere Liste/Container, welche eine 7-Tage Woche repräsentiert
		rPlan.init();

		// Setzen der ersten Spalte
		rPlan.getPlan().elementAt(0).set(0, "<b>Wochentag/<br/>Uhrzeit");
		rPlan.getPlan().elementAt(0).set(1, "<b>08:15 - 09:45</b>");
		rPlan.getPlan().elementAt(0).set(2, "<b>10:00 - 11:30</b>");
		rPlan.getPlan().elementAt(0).set(3, "<b>11:45 - 13:15</b>");
		rPlan.getPlan().elementAt(0).set(4, "<b>14:15 - 15:45</b>");
		rPlan.getPlan().elementAt(0).set(5, "<b>16:00 - 17:30</b>");
		rPlan.getPlan().elementAt(0).set(6, "<b>17:45 - 19:15</b>");
		rPlan.getPlan().elementAt(0).set(7, "<b>19:30 - 21:00</b>");

		// Setzen der ersten Reihe
		rPlan.getPlan().elementAt(1).set(0, "<b>Montag</b>");
		rPlan.getPlan().elementAt(2).set(0, "<b>Dienstag</b>");
		rPlan.getPlan().elementAt(3).set(0, "<b>Mittwoch</b>");
		rPlan.getPlan().elementAt(4).set(0, "<b>Donnerstag</b>");
		rPlan.getPlan().elementAt(5).set(0, "<b>Freitag</b>");
		rPlan.getPlan().elementAt(6).set(0, "<b>Samstag</b>");
		rPlan.getPlan().elementAt(7).set(0, "<b>Sonntag</b>");

		// Für jede Belegung...
		for (int i = 0; i < raumBelegungen.size(); i++) {

			int rowPointer = 0;
			int columnPointer = 0;

			// ...wird die Position im Wochenplan ermittelt
			if (raumBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				columnPointer = 1;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId();
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 7 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 14) {
				columnPointer = 2;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 14 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				columnPointer = 3;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 21 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				columnPointer = 4;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 28 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				columnPointer = 5;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 35 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				columnPointer = 6;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			if (raumBelegungen.elementAt(i).getZeitslot().getId() > 42 && raumBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				columnPointer = 7;
				rowPointer = raumBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}

			// Erzeugen eines StringBuffer-Objekt, das den Inhalt einer Zelle beherrbergt
			StringBuffer tempSB = new StringBuffer();
			
			// Hinzufügen der LV-Bezeichung
			tempSB.append(raumBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			tempSB.append("<br />");

			/*
			 *  Hinzufügen der Vor- und Nachnamen aller Dozenten, die zu "dieser" Belegung
			 *  eingeteilt sind
			 */
			for (int j = 0; j < raumBelegungen.elementAt(i).getDozenten().size() - 1; j++) {
				tempSB.append("Prof. ");
				tempSB.append(raumBelegungen.elementAt(i).getDozenten().elementAt(j).getVorname());
				tempSB.append(" ");
				tempSB.append(raumBelegungen.elementAt(i).getDozenten().elementAt(j).getNachname());
				tempSB.append("<br />");

			}
			tempSB.append("Prof. ");
			tempSB.append(raumBelegungen.elementAt(i).getDozenten()
					.elementAt(raumBelegungen.elementAt(i).getDozenten().size() - 1).getVorname());
			tempSB.append(" ");
			tempSB.append(raumBelegungen.elementAt(i).getDozenten()
					.elementAt(raumBelegungen.elementAt(i).getDozenten().size() - 1).getNachname());
			tempSB.append("<br />");

			/*
			 *  Hinzufügen des Jahrgangs sowie des Studiengang-Kürzels aller Semesterverbände, 
			 *  die zu "dieser" Belegung eingeteilt sind
			 */
			if (raumBelegungen.elementAt(i).getSemesterverbaende() != null
					&& raumBelegungen.elementAt(i).getSemesterverbaende().size() > 0) {
				for (int j = 0; j < raumBelegungen.elementAt(i).getSemesterverbaende().size() - 1; j++) {
					tempSB.append(verwaltung.auslesenSemesterverband(raumBelegungen.elementAt(i)
						.getSemesterverbaende().elementAt(j)).elementAt(0).getStudiengang().getKuerzel());
					tempSB.append(" ");
					tempSB.append(raumBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getJahrgang());
					tempSB.append("<br />");
				}

				tempSB.append(verwaltung.auslesenSemesterverband(raumBelegungen.elementAt(i).getSemesterverbaende()
					.elementAt(raumBelegungen.elementAt(i).getSemesterverbaende().size() - 1)).elementAt(0).getStudiengang().getKuerzel());
				tempSB.append(" ");
				tempSB.append(raumBelegungen.elementAt(i).getSemesterverbaende()
					.elementAt(raumBelegungen.elementAt(i).getSemesterverbaende().size() - 1).getJahrgang());
			}

			rPlan.getPlan().elementAt(columnPointer).set(rowPointer, tempSB.toString());

		}

		// Erzeugen eines StringBuffer-Objekts, welches des gesamten <body>-Inhalt beinhaltet
		StringBuffer htmlr = new StringBuffer();
		
		// Hinzufügen eines Absatzes, welcher den mit dem Report assozierten Raum anzeigt
		htmlr.append("<p style=\"font-size: 40; font-weight: bold;\">");
		htmlr.append(raum.getBezeichnung());
		htmlr.append("<br/>");
		htmlr.append("Kapazität: ");
		htmlr.append(raum.getKapazitaet());
		htmlr.append("</p>");

		// Erzeugen einer HTML-<table> aus der zweidimensionalen Liste/Container, welche eine 7-Tage Woche repräsentiert
		String tempHtml = new HTMLReportWriter().getHTMLString(rPlan);

		
		
		// Hinzufügen eines Datums am Ende eine Reports
		SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
		String datum = datumFormat.format(new Date()).toString();
		htmlr.append(tempHtml);
		htmlr.append("<p style\"font-size:20; font-weight: bold \">");
		htmlr.append("Stand: ");
		htmlr.append(datum);
		htmlr.append("</p>");

		// Setzen des <body>-Inhalts in das Studentenplan-Objekt
		rPlan.setHtmlTable(htmlr.toString());
		
		return rPlan;

	}

	/**
	 * Methode um alle Räume mittels Methode aus der Klasse 
	 * VerwaltungImpl { @see VerwaltungImpl} dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Räumen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Raum> auslesenAlleRaeume() throws RuntimeException {
		return verwaltung.auslesenAlleRaeume();

	}

}
