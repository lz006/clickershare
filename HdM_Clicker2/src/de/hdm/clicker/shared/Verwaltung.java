package de.hdm.clicker.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.hdm.clicker.shared.bo.Belegung;
import de.hdm.clicker.shared.bo.Category;
import de.hdm.clicker.shared.bo.Dozent;
import de.hdm.clicker.shared.bo.Lecturer;
import de.hdm.clicker.shared.bo.Lehrveranstaltung;
import de.hdm.clicker.shared.bo.Raum;
import de.hdm.clicker.shared.bo.Semesterverband;
import de.hdm.clicker.shared.bo.Studiengang;
import de.hdm.clicker.shared.bo.Zeitslot;


/**
 * <p>
 * Synchrone Schnittstelle für eine RPC-fähige Klasse zur Verwaltung von Vorlesungen.
 * </p>
 * <p>
 * <code>@RemoteServiceRelativePath("verwaltung")</code> ist bei der
 * Adressierung des aus der zugehörigen Impl-Klasse entstehenden
 * Servlet-Kompilats behilflich. Es gibt im Wesentlichen einen Teil der URL des
 * Servlets an.
 * </p>
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1
 */

@RemoteServiceRelativePath("verwaltung")
public interface Verwaltung extends RemoteService {
		
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Admin
	 * ***********************************************************************************************
	 */
	
	public Vector<Lecturer> auslesenAlleLecturer() throws RuntimeException;
	
	public Lecturer aendernLecturer(Lecturer lecturer) throws RuntimeException;
	
	public Vector<Lecturer> auslesenLecturer(Lecturer lecturer) throws RuntimeException;
	
	public void loeschenLecturer(Lecturer lecturer) throws RuntimeException;
	
	public Lecturer anlegenLecturer(String user, String password, String firstName, String name) throws RuntimeException;
	
	public boolean adminAuthenticate(String password) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Admin
	 * ***********************************************************************************************
	 */
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Lecturer
	 * ***********************************************************************************************
	 */
	
	public boolean lecturerAuthenticate(String user, String password) throws RuntimeException;
	
	public Lecturer getSignedLecturer() throws RuntimeException;
	
	public Category aendernCategory(Category category) throws RuntimeException;
	
	public Vector<Category> auslesenCategory(Category category) throws RuntimeException;
	
	public Vector<Category> auslesenAlleCategories() throws RuntimeException;
	
	public void loeschenCategory(Category category) throws RuntimeException;
	
	public Category anlegenCategory(String description) throws RuntimeException;
	
	/**
	 * Methode um alle Semesterverbände mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Semesterverbänden
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Semesterverband> auslesenAlleSemesterverbaende() throws RuntimeException;
	
	/**
	 * Methode um alle Semesterverbände anhand eines Studiengang-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	sg - Studiengang-Objekt aufgrund dessen die Semesterverbände ausgelesen werden sollen
	 * @return	Vector mit Semesterverbänden
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException;
	
	/**
	 * Methode um einen Semesterverband erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	sv - Semesterverband-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Semesterverband
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Semesterverband> auslesenSemesterverband(Semesterverband sv) throws RuntimeException;
	
	/**
	 * Methode um alle Dozenten mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Dozent> auslesenAlleDozenten() throws RuntimeException;
	
	/**
	 * Methode um alle Dozenten anhand eines Lehrveranstaltung-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	lv - Lehrveranstaltung-Objekt aufgrund dessen die Dozenten ausgelesen werden sollen
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Dozent> auslesenDozentenNachLV(Lehrveranstaltung lv) throws RuntimeException;
	
	/**
	 * Methode um alle verfügbaren Dozenten anhand Zeitslot-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	lv - Zeitslot-Objekt aufgrund dessen die Dozenten ausgelesen werden sollen
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Dozent> auslesenDozentenNachZeitslot(Zeitslot lv) throws RuntimeException;
	
	/**
	 * Methode um einen Dozent erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	dozent Dozent-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Dozent> auslesenDozent(Dozent dozent) throws RuntimeException;
	
	/**
	 * Methode um alle Zeitslots mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Zeitslots
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Zeitslot> auslesenAlleZeitslots() throws RuntimeException;
	
	/**
	 * Methode um alle Lehrveranstaltungen mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Lehrveranstaltungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Lehrveranstaltung> auslesenAlleLehrveranstaltungen() throws RuntimeException;
	
	/**
	 * Methode um eine Lehrveranstaltung erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	lv - Lehrveranstaltung-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Lehrveranstaltung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Lehrveranstaltung> auslesenLehrveranstaltung(Lehrveranstaltung lv) throws RuntimeException;
	
	/**
	 * Methode um alle Lehrveranstaltungen anhand eines Semesterverband-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	sv - Semesterverband-Objekt und 
	 * 			sg - Studiengang-Objekt aufgrund dessen die Lehrveranstaltungen 
	 * 			ausgelesen werden sollen
	 * @return	Vector mit Lehrveranstaltungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSV(Semesterverband sv, Studiengang sg) throws RuntimeException;
	
	/**
	 * Methode um alle Lehrveranstaltungen anhand eines Studiengang-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	sg - Studiengang-Objekt aufgrund dessen die Lehrveranstaltungen ausgelesen werden sollen
	 * @return	Vector mit Lehrveranstaltungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSG(Studiengang sg) throws RuntimeException;
	
	/**
	 * Methode um alle Belegungen mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Belegungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Belegung> auslesenAlleBelegungen() throws RuntimeException;
	
	/**
	 * Methode um eine Belegung erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	belegung - Belegung-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Belegung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Belegung> auslesenBelegung(Belegung belegung) throws RuntimeException;
	
	/**
	 * Methode um alle Belegungen anhand eines Semesterverband-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	semesterverband - Semesterverband-Objekt aufgrund dessen die Belegungen ausgelesen werden sollen
	 * @return	Vector mit Belegungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Belegung> auslesenBelegungenNachSV(Semesterverband semesterverband) throws RuntimeException;
	
	/**
	 * Methode um alle Studiengänge mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Studiengängen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Studiengang> auslesenAlleStudiengaenge() throws RuntimeException;
	
	/**
	 * Methode um einen Studiengang erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	studiengang - Studiengang-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Studiengang
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Studiengang> auslesenStudiengang(Studiengang studiengang) throws RuntimeException;
	
	/**
	 * Methode um alle Studiengänge mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * (Diese Studiengang-Objekte enthalten keine Referenzen zu Semesterverband- und Lehrveranstaltungs-
	 * Objekten. Diese Methode dient nur dazu um in gewissen Situationen einen Performancevorteil zu
	 * erzielen)
	 * 
	 * @return	Vector mit Studiengängen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException;
	
	/**
	 * Methode um alle Räume mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Räumen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	Vector<Raum> auslesenAlleRaeume() throws RuntimeException;
	
	/**
	 * Methode um einen Raum erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	raum - Raum-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Belegung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Raum> auslesenRaum(Raum raum) throws RuntimeException;
	
	/**
	 * Methode um alle verfügbaren Räume anhand Zeitslot-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	zeitslot - Zeitslot-Objekt aufgrund dessen die Räume ausgelesen werden sollen
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	Vector<Raum> auslesenVerfuegbareRaeumeZuZeitslotuSV(Zeitslot zeitslot, Vector<Semesterverband> sv) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um dem Client die geforderten BusinessObjects zu übermitteln
	 * ***********************************************************************************************
	 */
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Methoden um die Löschung der vom Client übermittelten BusinessObjects 
	 * 				durchzuführen
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um einen bestimmten Semesterverband zu löschen
	 * 
	 * @param	semesterverband - Semesterverband-Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	void loeschenSemesterverband(Semesterverband semesterverband) throws RuntimeException;
	
	/**
	 * Methode um einen bestimmten Dozenten zu löschen
	 * 
	 * @param	dozent - Dozent-Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	void loeschenDozent(Dozent dozent) throws RuntimeException;
	
	/**
	 * Methode um einen bestimmte Lehrveranstaltung zu löschen
	 * 
	 * @param	lehrveranstaltung - Lehrveranstaltung-Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException;
	
	/**
	 * Methode um eine Belegung für !einen bestimmten Semesterverband zu löschen
	 * 
	 * @param	belegung - Belegung-Objekt und das 
	 * 			smesterverband - Semesterverband-Objekt, zudem die Referenz gelöschten werden soll bzw.
	 * 			bei nur einem referenzierten Semesterverband wird die übwergebene Belegung gelöscht
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	void loeschenBelegungen(Belegung belegung, Semesterverband semesterverband) throws RuntimeException;
	
	/**
	 * Methode um einen bestimmten Studiengang zu löschen
	 * 
	 * @param	studiengang - Studiengang-Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	void loeschenStudiengang(Studiengang studiengang) throws RuntimeException;
	
	/**
	 * Methode um einen bestimmten Raum zu löschen
	 * 
	 * @param	raum - Raum-Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	void loeschenRaum(Raum raum) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um die Löschung der vom Client übermittelten BusinessObjects 
	 * 				durchzuführen
	 * ***********************************************************************************************
	 */
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Methoden um die vom Client gewünschten Änderungen an den BusinessObjects
	 * 				zu bearbeiten
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um einen Semesterverband abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	semesterverband - Semesterverband-Objekt welches geändert sollen
	 * @return	Semesterverband-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */	
	Semesterverband aendernSemesterverband(Semesterverband semesterverband) throws RuntimeException;
	
	/**
	 * Methode um einen Dozent abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	dozent - Dozent-Objekt welches geändert werden sollen
	 * @return	Dozent-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Dozent aendernDozent(Dozent dozent) throws RuntimeException;
	
	/**
	 * Methode um eine Lehrveranstaltung abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	lehrveranstaltung - Lehrveranstaltung-Objekt welches geändert werden sollen
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Lehrveranstaltung aendernLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException;
	
	/**
	 * Methode um einee Belegung abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	belegung - Belegung-Objekt welches geändert werden sollen
	 * @return	Belegung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Belegung aendernBelegung(Belegung belegung) throws RuntimeException;
	
	/**
	 * Methode um einen Studiengang abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	studiengang - Studiengang-Objekt welches geändert werden sollen
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Studiengang aendernStudiengang(Studiengang studiengang) throws RuntimeException;
	
	/**
	 * Methode um einen Raum abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	raum - Raum-Objekt welches geändert werden sollen
	 * @return	Raum-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Raum aendernRaum(Raum raum) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um die vom Client gewünschten Änderungen an den BusinessObjects
	 * 				zu bearbeiten
	 * ***********************************************************************************************
	 */
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Methoden um die vom Client gewünschten neuen BusinessObjects zur erstellen
	 * 				und zu speichern
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um einen neuen Semesterverband mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	anzahlStudenten - Anzahl der Studenten des neuen Semesterverbands
	 * 			jahrgang - Jahrgang des neuen Semesterverbands
	 * 			studiengang - Studiengang-Objekt dem der neue Semesterverband zugeordnet sein
	 * @return	Semesterverband-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Semesterverband anlegenSemesterverband (String anzahlStudenten, String jahrgang, Studiengang studiengang) throws RuntimeException;
	
	/**
	 * Methode um einen neuen Dozenten mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	vorname - Vorname des Dozenten
	 * 			nachname - Nachname des Dozenten
	 * 			personalnummer - Personalnummer des Dozenten
	 * 			lehrveranstaltungen - Lehrveranstaltungs-Objekte die zum Komptenzbereich des Dozenten zählen
	 * @return	Dozent-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Dozent anlegenDozent(String vorname, String nachname, String personalnummer, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException;
	
	/**
	 * Methode um einen neue Lehrveranstaltung mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	umfang - Umfang also die SWS der neuen Lehrveranstaltung
	 * 			bezeichnung - Bezeichnung der neuen Lehrveranstaltung
	 * 			studiensemester - Studiensemester der neuen Lehrveranstaltung
	 * 			studiengaenge - Studiengang-Objekte zu denen siche die neue Lehrveranstaltung zählen soll
	 * 			dozenten - Dozent-Objekte die die neue Lehrveranstaltung voraussichltich halten werden
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, Vector<Dozent> dozenten) throws RuntimeException;
	
	/**
	 * Methode um einen neue Lehrveranstaltung mittels Mapper-Objekt in der DB zu speichern
	 * (Überladen der Methode "anlegenLehrveranstaltung(...)")
	 * 
	 * @param	umfang - Umfang also die SWS der neuen Lehrveranstaltung
	 * 			beziechnung - Bezeichnung der neuen Lehrveranstaltung
	 * 			studiensemester - Studiensemester der neuen Lerhveranstaltung
	 * 			studiengaenge - Studiengang-Objekte zu denen siche die neue Lehrveranstaltung zählen soll
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge) throws RuntimeException;
	
	/**
	 * Methode um einen neue Belegung mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	lehrveranstaltung - Lehrveranstaltung-Objekt welche Inhalt des Vorlesungstermin sein soll
	 * 			raum - Raum-Objekt in dem die Vorlesung stattfindet
	 * 			zeitslot - Zeitsot-Objekt, welches den Zeitpunkt und die Zeitspanne der Vorlesung angibt
	 * 			semesterverbaende - Semesterverband-Objekte, welche die Rezipienten der Belegung darstellen
	 * 			dozenten - Dozent-Objekte, welche diese Vorlesung referieren sollen
	 * @return	Belegung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Belegung anlegenBelegung(Lehrveranstaltung lehrveranstaltung, Raum raum, Zeitslot zeitslot, Vector<Dozent> dozenten, Vector<Semesterverband> semesterverbaende) throws RuntimeException;
	
	/**
	 * Methode um einen neuen Studiengang mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	bezeichnung - Bezeichnung des neuen Studiengangs
	 * 			kuerzel - Kürzel des neuen Studiengangs
	 * 			lehrveranstaltungen - Lehrveranstaltung-Objekte welche dem neuen Studiengang zugeordnet sein sollen
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Studiengang anlegenStudiengang(String bezeichnung, String kuerzel, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException;
	
	/**
	 * Methode um einen neuen Studiengang mittels Mapper-Objekt in der DB zu speichern
	 * (Überladen der Methode "anlegenStudiengang(...)")
	 * 
	 * @param	bezeichnung - Bezeichnung des neuen Studiengangs
	 * 			kuerzel - Kürzel des neuen Studiengangs
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Studiengang anlegenStudiengang(String bezeichnung, String kuerzel) throws RuntimeException;
	
	/**
	 * Methode um einen neuen Raum mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	bezeichnung - Bezeichnung des neuen Raumes
	 * 			kapazitaet - Kapazität des neuen Raumes
	 * @return	Raum-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	Raum anlegenRaum(String bezeichnung, String kapazitaet) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um die vom Client gewünschten neuen BusinessObjects zur erstellen
	 * 				und zu speichern
	 * ***********************************************************************************************
	 */
	
	void closeConnection() throws RuntimeException;
}
