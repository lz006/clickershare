package de.hdm.clicker.shared;

import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import de.hdm.clicker.shared.bo.Dozent;
import de.hdm.clicker.shared.bo.Raum;
import de.hdm.clicker.shared.bo.Semesterverband;
import de.hdm.clicker.shared.bo.Studiengang;
import de.hdm.clicker.shared.report.Dozentenplan;
import de.hdm.clicker.shared.report.Raumplan;
import de.hdm.clicker.shared.report.Studentenplan;

/**
 * <p>
 * Synchrone Schnittstelle für eine RPC-fähige Klasse zur Erstellung von
 * Reports. Diese Schnittstelle benutzt die gleiche Realisierungsgrundlage wie
 * das Paar { @link Verwaltung} und { @link VerwaltungImpl}. Zu
 * technischen Erläuterung etwa bzgl. GWT RPC bzw. { @link RemoteServiceServlet}
 * siehe { @link Verwaltung} und { @link VerwaltungImpl}.
 * </p>
 * <p>
 * Ein ReportGenerator bietet die Möglichkeit, eine Menge von Berichten
 * (Reports) zu erstellen, die Menge von Daten bzgl. bestimmter Sachverhalte des
 * Systems zweckspezifisch darstellen.
 * </p>
 * <p>
 * Die Klasse bietet eine Reihe von <code>create...</code>-Methoden, mit deren
 * Hilfe die Reports erstellt werden können. Jede dieser Methoden besitzt eine
 * dem Anwendungsfall entsprechende Parameterliste. Diese Parameter benötigt der
 * der Generator, um den Report erstellen zu können.
 * </p>
 * 
 * @author 	Thies, Moser, Sonntag, Zanella
 * @version	1
 */
@RemoteServiceRelativePath("report")
public interface Report extends RemoteService {

	/**
	 * Methode um einen Studentenplan anhand eines Semesterverbandes zu erstellen
	 * 
	 * @param	sv - Semesterverband-Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Studentenplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	Studentenplan createStudentenplan(Semesterverband sv) throws RuntimeException;
	
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
	Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException;
	
	/**
	 * Methode um alle Semesterverbände anhand eines Studiengang-Objekts mittels Methode aus 
	 * der Klasse VerwaltungImpl { @see VerwaltungImpl} dem Client zur Verfügung zu stellen
	 */
	Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException;
	
	/**
	 * Methode um einen Dozentenplan anhand eines Dozenten zu erstellen
	 * 
	 * @param	dozent -Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Dozentenplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	Dozentenplan createDozentenplan(Dozent dozent) throws RuntimeException;
	
	/**
	 * Methode um alle Dozenten mittels Methode aus der Klasse 
	 * VerwaltungImpl @see VerwaltungImpl dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Dozent> auslesenAlleDozenten() throws RuntimeException;
	
	/**
	 * Methode um einen Raumplan anhand eines Raumes zu erstellen
	 * 
	 * @param	raum - Raum-Objekt, aufgrund dessen der Report/Plan erstellt 
	 * 			werden sollen
	 * @return	Raumplan-Objekt welches die "Ergebnis-HTML-Tabelle" enhtält
	 */
	Raumplan createRaumplan(Raum raum) throws RuntimeException;
	
	/**
	 * Methode um alle Räume mittels Methode aus der Klasse 
	 * VerwaltungImpl { @see VerwaltungImpl} dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Räumen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Raum> auslesenAlleRaeume() throws RuntimeException;
	
}
