package de.hdm.clicker.server;


import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;

import de.hdm.clicker.server.db.*;
import de.hdm.clicker.shared.Verwaltung;
import de.hdm.clicker.shared.bo.*;

import com.google.appengine.api.images.Image;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * <p>
 * Implementierungsklasse des Interface <code>Verwaltung</code>. Diese
 * Klasse ist <em>die</em> Klasse, die neben { @link com.hdm.stundenplantool2.server.report.ReportImpl}
 * sämtliche Applikationslogik (oder engl. Business Logic) aggregiert. Sie ist
 * wie eine Spinne, die sämtliche Zusammenhänge in ihrem Netz (in unserem Fall
 * die Daten der Applikation) überblickt und für einen geordneten Ablauf und
 * dauerhafte Konsistenz der Daten und Abläufe sorgt.
 * </p>
 * <p>
 * Die Applikationslogik findet sich in den Methoden dieser Klasse. Jede dieser
 * Methoden kann als <em>Transaction Script</em> bezeichnet werden. Dieser Name
 * lässt schon vermuten, dass hier analog zu Datenbanktransaktion pro
 * Transaktion gleiche mehrere Teilaktionen durchgeführt werden, die das System
 * von einem konsistenten Zustand in einen anderen, auch wieder konsistenten
 * Zustand überführen. Wenn dies zwischenzeitig scheitern sollte, dann ist das
 * jeweilige Transaction Script dafür verwantwortlich, eine Fehlerbehandlung
 * durchzuführen.
 * </p>
 * <p>
 * Diese Klasse steht mit einer Reihe weiterer Datentypen in Verbindung. Dies
 * sind:
 * <ol>
 * <li>{ @link Verwaltung}: Dies ist das <em>lokale</em> - also
 * Server-seitige - Interface, das die im System zur Verfügung gestellten
 * Funktionen deklariert.</li>
 * <li>{ @link VerwaltungAsync}: <code>VerwaltungImpl</code> und
 * <code>Verwaltung</code> bilden nur die Server-seitige Sicht der
 * Applikationslogik ab. Diese basiert vollständig auf synchronen
 * Funktionsaufrufen. Wir müssen jedoch in der Lage sein, Client-seitige
 * asynchrone Aufrufe zu bedienen. Dies bedingt ein weiteres Interface, das in
 * der Regel genauso benannt wird, wie das synchrone Interface, jedoch mit dem
 * zusätzlichen Suffix "Async". Es steht nur mittelbar mit dieser Klasse in
 * Verbindung. Die Erstellung und Pflege der Async Interfaces wird durch das
 * Google Plugin semiautomatisch unterstützt. Weitere Informationen unter
 * { @link VerwaltungAsync}.</li>
 * <li> { @link RemoteServiceServlet}: Jede Server-seitig instantiierbare und
 * Client-seitig über GWT RPC nutzbare Klasse muss die Klasse
 * <code>RemoteServiceServlet</code> implementieren. Sie legt die funktionale
 * Basis für die Anbindung von <code>VerwaltungImpl</code> an die Runtime
 * des GWT RPC-Mechanismus.</li>
 * </ol>
 * </p>
 * <p>
 * <b>Wichtiger Hinweis:</b> Diese Klasse bedient sich sogenannter
 * Mapper-Klassen. Sie gehören der Datenbank-Schicht an und bilden die
 * objektorientierte Sicht der Applikationslogik auf die relationale
 * organisierte Datenbank ab.
 * </p>
 * <p>
 * Beachten Sie, dass sämtliche Methoden, die mittels GWT RPC aufgerufen werden
 * können ein <code>throws RuntimeException</code> in der
 * Methodendeklaration aufweisen. Diese Methoden dürfen also Instanzen von
 * { @link RuntimeException} auswerfen. Mit diesen Exceptions können z.B.
 * Probleme auf der Server-Seite in einfacher Weise auf die Client-Seite
 * transportiert und dort individuell behandelt werden.
 * 
 * @see Verwaltung
 * @see com.hdm.stundenplantool2.shared.VerwaltungAsync
 * @see RemoteServiceServlet
 * @author Thies, Moser, Sonntag, Zanella
 * @version 1
 */

@SuppressWarnings("serial")
public class VerwaltungImpl extends RemoteServiceServlet implements Verwaltung {
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Belegungsobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public BelegungMapper belegungMapper = BelegungMapper.belegungMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Dozentobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public DozentMapper dozentMapper = DozentMapper.dozentMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Lehrveranstaltungsobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public LehrveranstaltungMapper lehrveranstaltungMapper = LehrveranstaltungMapper.lehrveranstaltungMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Raumobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public RaumMapper raumMapper = RaumMapper.raumMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Semesterverbandobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public SemesterverbandMapper semesterverbandMapper = SemesterverbandMapper.semesterverbandMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Studiengangobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public StudiengangMapper studiengangMapper= StudiengangMapper.studiengangMapper();
	
	/**
	   * Referenz auf den bereits instantiierten DatenbankMapper, der Zeitslotobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public ZeitslotMapper zeitslotMapper = ZeitslotMapper.zeitslotMapper();
	
	/**
	   * Referenzen auf den bereits instantiierten DatenbankMappern, der Businessobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public LecturerMapper lecturerMapper = LecturerMapper.lecturerMapper();
	public CategoryMapper categoryMapper = CategoryMapper.categoryMapper();
	public QuestionMapper questionMapper = QuestionMapper.questionMapper();
	
	/**
	 * Flag, welche angibt, ob sich der Admin bereits authentifiziert hat
	 */
	private boolean isAdmin = false;

	/**
	 * Der aktuell angemeldete Lecturer
	 */
	private Lecturer signedInLecturer = null;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Admin
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um alle Lecturer mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Lecturer
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Lecturer> auslesenAlleLecturer() throws RuntimeException {
		if (!isAdmin) {
			throw new RuntimeException("Berechtigung nicht ausreichend");
		}
		
		return lecturerMapper.findAll();
	}
	
	/**
	 * Methode um einen Lecturer erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	lecturer - Lecturer-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Lecturer
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Lecturer> auslesenLecturer(Lecturer lecturer) throws RuntimeException {
		if (isAdmin || signedInLecturer != null) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(lecturer.getId());
			return lecturerMapper.findByKey(vi);
		}
		
		throw new RuntimeException("Berechtigung nicht ausreichend");
	}
	
	/**
	 * Methode um einen Lecturer abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	lecturer - Objekt welches geändert werden sollen
	 * @return	Lecturer-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 */
	public Lecturer aendernLecturer(Lecturer lecturer) throws RuntimeException {
		if (isAdmin || signedInLecturer != null) {
			Lecturer editedLec = this.lecturerMapper.update(lecturer);
			if (signedInLecturer != null) {
				signedInLecturer = editedLec;
			}
			return editedLec;
		}
		
		throw new RuntimeException("Berechtigung nicht ausreichend");		

	}
	
	/**
	 * Methode um einen bestimmten Lecturer zu löschen
	 * 
	 * @param	lecturer -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenLecturer(Lecturer lecturer) throws RuntimeException {
		if (!isAdmin) {
			throw new RuntimeException("Berechtigung nicht ausreichend");
		}
		
		this.lecturerMapper.delete(lecturer);
	}
	
	/**
	 * Methode um einen neuen Lecturer mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	bezeichnung des neuen Lecturers
	 * 			kapazitaet des neuen Raumes
	 * @return	Lecturer-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Lecturer anlegenLecturer(String user, String password, String firstName, String name) throws RuntimeException {
		if (!isAdmin) {
			throw new RuntimeException("Berechtigung nicht ausreichend");
		}
		
		Lecturer newLecturer = new Lecturer();
		newLecturer.setUser(user);
		newLecturer.setPassword(password);
		newLecturer.setFirstName(firstName);
		newLecturer.setName(name);
		
		return this.lecturerMapper.insertIntoDB(newLecturer);
		
	}
	
	/**
	 * Methode um den Admin zu authentifizieren
	 * 
	 * @param	password des Admin
	 * @return	Boolean
	 * @throws	Beim prüfen des Passworts kann es zur Unstimmigkeit kommen
	 */
	public boolean adminAuthenticate(String password) throws RuntimeException {
		
		if (password.equals("pass")) {
			isAdmin = true;
		}
		else {
			throw new RuntimeException("Authentifizierung fehlgeschlagen!");
		}
		
		return isAdmin;
		
	}
	
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
	
	/**
	 * Methode um den Lecturer zu authentifizieren
	 * 
	 * @param	user des Lecturers
	 * 			password des Lecturers
	 * @return	Boolean
	 * @throws	Beim prüfen des Usernamens und des Passworts kann es zur Unstimmigkeit kommen
	 */
	public boolean lecturerAuthenticate(String user, String password) throws RuntimeException {
		signedInLecturer = this.lecturerMapper.findByLogin(user, password);
		
		if (signedInLecturer == null) {
			throw new RuntimeException("User oder Passwort nicht korrekt!");
		}
		return true;
	}
	
	/**
	 * Methode um den eingeloggten Lecturer als Objekt zurückzugeben
	 * 
	 * @return	Boolean
	 * @throws	Beim prüfen des Usernamens und des Passworts kann es zur Unstimmigkeit kommen
	 */
	public Lecturer getSignedLecturer() throws RuntimeException {
		
		if (signedInLecturer != null) {
			return signedInLecturer;
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um eine Category abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	category - Objekt welches geändert werden sollen
	 * @return	Category-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 */
	public Category aendernCategory(Category category) throws RuntimeException {
		if (signedInLecturer != null) {
			return this.categoryMapper.update(category);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um eine Category erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	category - Category-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Category
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Category> auslesenCategory(Category category) throws RuntimeException {
		if (signedInLecturer != null) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(category.getId());
			return categoryMapper.findByKey(vi);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um alle Categories zu einem Lecturer mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Categories
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Category> auslesenAlleCategoriesByLecturer() throws RuntimeException {
		if (signedInLecturer != null) {
			return categoryMapper.findAllByLecturer(this.signedInLecturer);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um eine bestimmte Category zu löschen
	 * 
	 * @param	category -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenCategory(Category category) throws RuntimeException {
		if (signedInLecturer != null) {
			this.categoryMapper.delete(category);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um eine neue Category mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	description der neuen Category
	 * @return	Category-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Category anlegenCategory(String description) throws RuntimeException {
		if (signedInLecturer != null) {
			
			Category newCategory = new Category();
			newCategory.setDescription(description);
			newCategory.setLecturerID(this.signedInLecturer.getId());
			
			return this.categoryMapper.insertIntoDB(newCategory);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um ein Image erneut anhand einer ID dem Client zur Verfügung zu stellen
	 * 
	 * @param	keys - entspricht den IDs der Image
	 * @return	Vector mit Image-Strings
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<String> auslesenImages(Vector<Integer> keys) throws RuntimeException {
		Vector<String> imageStrings = new Vector<String>();
		
		this.lecturerMapper.findAll();
		
		
		return imageStrings;
		
	}
	
	
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Lecturer
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um alle Semesterverbände mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Semesterverbänden
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Semesterverband> auslesenAlleSemesterverbaende() throws RuntimeException {
		return semesterverbandMapper.findAll(false);
	}
	
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
	public Vector<Semesterverband> auslesenSemesterverbaendeNachStudiengang(Studiengang sg) throws RuntimeException {
		return semesterverbandMapper.findByStudiengang(sg, false);
	}
	
	/**
	 * Methode um einen Semesterverband erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	 sv - Semesterverband-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Semesterverband
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Semesterverband> auslesenSemesterverband(Semesterverband sv) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(sv.getId());
		return semesterverbandMapper.findByKey(vi, true);
	}
		
	/**
	 * Methode um alle Dozenten mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Dozent> auslesenAlleDozenten() throws RuntimeException {
		return dozentMapper.findAll(false);
	}
	
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
	public Vector<Dozent> auslesenDozentenNachLV(Lehrveranstaltung lv) throws RuntimeException {
		return dozentMapper.findByLV(lv, false);
	}
	
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
	public Vector<Dozent> auslesenDozentenNachZeitslot(Zeitslot lv) throws RuntimeException {
		
		// Zuerst werden alle Belegungen die an dem gewünschten Zeitslot stattfiniden geladen
		Vector<Belegung> zeitslotBelegungen = belegungMapper.findByZeitslot(lv);
		
		// Dann werden alle erfassten Dozenten geladen
		Vector<Dozent> alleDozenten = dozentMapper.findAll(false);
		
		/* 
		 * Erstellung eines Containers, welcher die Dozenten aufnimmt, welche in den geladenen
		 * Belegungen an keiner Stelle eingeteilt sind
		 */		
		Vector<Dozent> freieDozenten = new Vector<Dozent>();
		
		// Für jeden Dozent wird geschaut...
		for (int i = 0; i < alleDozenten.size(); i++) {
			boolean check1 = true;
			if (zeitslotBelegungen != null) {
				// ...ob dieser in irgend einer der geladenen Belegungen... 
				for (int j = 0; j < zeitslotBelegungen.size(); j++) {
					boolean check2 = true;
					if (zeitslotBelegungen.elementAt(j).getDozenten() != null) {
						// ... alleine oder zusammen mit anderen Dozenten für die Vorlesung vorgesehen ist
						for (int k = 0; k < zeitslotBelegungen.elementAt(j).getDozenten().size(); k++) {
							if(alleDozenten.elementAt(i).getId() == zeitslotBelegungen.elementAt(j).getDozenten().elementAt(k).getId()) {
								check1 = false;
								check2 = false;
								break;
							}
						}
					}
					if (!check2) {
						break;
					}
				}
			}
			if (check1) {
				freieDozenten.add(alleDozenten.elementAt(i));
			}
		}
		
		if (freieDozenten.size() > 0) {
			return freieDozenten;
		}
		else {
			throw new RuntimeException("Es sind leider keine Dozenten zu diesem Zeitslot verfügbar");
		}
	}
	
	/**
	 * Methode um einen Dozent erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	dozent - Dozent-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Dozent> auslesenDozent(Dozent dozent) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(dozent.getId());
		return dozentMapper.findByKey(vi, true);
	}
	
	/**
	 * Methode um alle Zeitslots mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Zeitslots
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Zeitslot> auslesenAlleZeitslots() throws RuntimeException {
		return zeitslotMapper.findAll();
	}
	
	/**
	 * Methode um alle Lehrveranstaltungen mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Lehrveranstaltungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Lehrveranstaltung> auslesenAlleLehrveranstaltungen() throws RuntimeException {
		return lehrveranstaltungMapper.findAll(false);
	}
	
	/**
	 * Methode um eine Lehrveranstaltung erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	lv - Lehrveranstaltung-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Lehrveranstaltung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltung(Lehrveranstaltung lv) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(lv.getId());
		return lehrveranstaltungMapper.findByKey(vi, true);
	}
	
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
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSV(Semesterverband sv, Studiengang sg) throws RuntimeException {
		
		// Laden aller Lehrveranstaltungen, welche für den gemeinten Studiengang vorgesehen sind
		Vector<Lehrveranstaltung> tempLVVector = lehrveranstaltungMapper.findByStudiengang(sg, false);
		
		/* 
		 * Container um die Lehrveranstaltungen aufzunehmen welche zum Studiensemester bzw. Jahrgang des gemeinten
		 * Semesterverbandes passen
		 */
		Vector<Lehrveranstaltung> ergebnisLVVector = new Vector<Lehrveranstaltung>();
		
		// Aussortieren der Lehrveranstaltungen, welche mit Studiensemester des Semesterverbands vereinbar sind		
		for (int i = 0; i < tempLVVector.size(); i++) {
			
			Integer semesterAlt = null;
			Integer semesterNeu = null;
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (sv.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (sv.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(sv.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};
			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {				
			
				int zaehler = 0;
				
				for (int j = semMonat-1; j < calendar.length; j++ ) {
					
					zaehler++;
					
					if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
						break;
					}
					if(calendar[j] == 12) {
						j = -1;
						jahresDiff--;
					}
				}
				
				int studienSem = zaehler / 6;
				int studienSemMonat = zaehler;
				
				if (zaehler != 0) {
					studienSemMonat = zaehler % 6;
				}
				
				if (studienSem == 0) {
					semesterAlt = 1;
					semesterNeu = 0;
				}
				
				else if (studienSemMonat == 0) {
					semesterAlt = studienSem;
					semesterNeu = studienSem + 1;
				}			
						
				else {
					semesterAlt = studienSem + 1;
				}
			}
			else {
				semesterAlt = 1;
			}		
		
			if (semesterNeu == null || semesterNeu == 0) {
				if (tempLVVector.elementAt(i).getStudiensemester() != semesterAlt) {
					continue;
				}
			}
			
			else if (semesterAlt == null || semesterAlt == 0) {
				if (tempLVVector.elementAt(i).getStudiensemester() != semesterNeu) {
					continue;
				}
			}		
		
			else {
				if ((tempLVVector.elementAt(i).getStudiensemester() != semesterAlt) && 
						(tempLVVector.elementAt(i).getStudiensemester() != semesterNeu)) {
					continue;
				}
			}
			ergebnisLVVector.add(tempLVVector.elementAt(i));
		}
		
		if (ergebnisLVVector.size() <= 0) {
			throw new RuntimeException("Für den gewählten Studiengang existiert noch keine passende Lehrveranstaltung.\n"
					+ "In Folge dessen existieren auch keine Belegungen bzw. können auch nicht angelegt werden");
		}
		return ergebnisLVVector;
	} 
	
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
	public Vector<Lehrveranstaltung> auslesenLehrveranstaltungenNachSG(Studiengang sg) throws RuntimeException {
		return lehrveranstaltungMapper.findByStudiengang(sg, false);
	}
	
	/**
	 * Methode um alle Belegungen mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Belegungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Belegung> auslesenAlleBelegungen() throws RuntimeException {
		return belegungMapper.findAll(false);
	}
	
	/**
	 * Methode um eine Belegung erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	belegung -Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Belegung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Belegung> auslesenBelegung(Belegung belegung) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getId());
		return belegungMapper.findByKey(vi, true);
	}
	
	/**
	 * Methode um alle Belegungen anhand eines Semesterverband-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	semesterverband -Objekt aufgrund dessen die Belegungen ausgelesen werden sollen
	 * @return	Vector mit Belegungen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Belegung> auslesenBelegungenNachSV(Semesterverband semesterverband) throws RuntimeException {
		return belegungMapper.findBySemesterverband(semesterverband);
	}
	
	/**
	 * Methode um alle Studiengänge mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Studiengängen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Studiengang> auslesenAlleStudiengaenge() throws RuntimeException {
		return studiengangMapper.findAll(true);
	}
	
	/**
	 * Methode um einen Studiengang erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	studiengang - Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Studiengang
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Studiengang> auslesenStudiengang(Studiengang studiengang) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(studiengang.getId());
		return studiengangMapper.findByKey(vi, true);
	}
	
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
	public Vector<Studiengang> auslesenAlleStudiengaengeOhneSVuLV() throws RuntimeException {
		return studiengangMapper.findAll(false);
	}
	
	/**
	 * Methode um alle Räume mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Räumen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */	
	public Vector<Raum> auslesenAlleRaeume() throws RuntimeException {
		return raumMapper.findAll();
	}
	
	/**
	 * Methode um einen Raum erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	raum -Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Belegung
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Raum> auslesenRaum(Raum raum) throws RuntimeException {
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(raum.getId());
		return raumMapper.findByKey(vi);
	}
	
	/**
	 * Methode um alle verfügbaren Räume anhand Zeitslot-Objekts mittels einem 
	 * Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @param	zeitslot -Objekt aufgrund dessen die Räume ausgelesen werden sollen
	 * @return	Vector mit Dozenten
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Raum> auslesenVerfuegbareRaeumeZuZeitslotuSV(Zeitslot zeitslot, Vector<Semesterverband> sv) throws RuntimeException {
		
		// Zuerst werden alle Belegungen geladen
		Vector<Belegung> tempBelegungVector = belegungMapper.findAll(false);
		
		// Erstellung eines Containers um alle besetzten Räume aufzunehmen
		Vector<Raum> besetzteRaeumeVector = new Vector<Raum>();
		
		// Erstellung eines Containers um alle freien Räume aufzunehmen
		Vector<Raum> freieRaeumeVector = new Vector<Raum>();
		
		// Erstellung eines Containers um alle passenden (bez. Kapazität u Zeitslot) Räume aufzunehmen
		Vector<Raum> passendeRaeumeVector = new Vector<Raum>();
		
		// Variable um die Studentenanzahl festzuhalten	
		int studentenzahl = 0;
		
		// Errechnen der Studentenanzahl
		for (Semesterverband tempSV : sv) {
			studentenzahl = studentenzahl + tempSV.getAnzahlStudenten();
		}
		
		// Auslesen aller besetzten Räume zum gewünschten Zeitslot		
		for (int i = 0; i < tempBelegungVector.size(); i++) {
			if (tempBelegungVector.elementAt(i).getZeitslot().getId() == zeitslot.getId()) {
				besetzteRaeumeVector.add(tempBelegungVector.elementAt(i).getRaum());
			}
		}
		
		/*
		 *  "Besetzte Räume" und "Räume" deren Kapazität nicht ausreichend für die Stundentenanzahl des 
		 *  gewählten Semesterverbands ist, aus allen Räumen herausfiltern		
		 */
		
		Vector<Raum> alleRaeumeVector = raumMapper.findAll();
		if (alleRaeumeVector.size() != besetzteRaeumeVector.size()) {
			for (int i = 0; i < alleRaeumeVector.size(); i++) {
				boolean check = false;
				for (int j = 0; j < besetzteRaeumeVector.size(); j++) {
					if (alleRaeumeVector.elementAt(i).getId() == besetzteRaeumeVector.elementAt(j).getId()) {
						check = true;
						break;
					}
				}
				if (!check) {
					freieRaeumeVector.add(alleRaeumeVector.elementAt(i));
				}
			}
			for (int i = 0; i < freieRaeumeVector.size(); i++) {
				if (freieRaeumeVector.elementAt(i).getKapazitaet() >= studentenzahl) {
					passendeRaeumeVector.add(freieRaeumeVector.elementAt(i));
				}
			}
			if (passendeRaeumeVector.size() > 0) {
				return passendeRaeumeVector;
			}
			else {
				throw new RuntimeException("Kein Raum verfügbar\nBitte wählen Sie einen anderen Zeitslot\n"
						+ "bzw. die Kapazitäten der verfügbaren Räume ist zu gering");
			}
		}
		else {
			throw new RuntimeException("Kein Raum verfügbar\nBitte wählen Sie einen anderen Zeitslot");
		}
	}		
			
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
	 * @param	semesterverband -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenSemesterverband(Semesterverband semesterverband) throws RuntimeException {
		
		// Ein Semesterverband kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist		
		if (semesterverband.getBelegungen() == null || semesterverband.getBelegungen().size() == 0) {
			semesterverbandMapper.delete(semesterverband);
		}
		else {
			StringBuffer eText = new StringBuffer();
			eText.append("Bitte löschen Sie zuerst alle Belegungen von " + semesterverband.getStudiengang().getKuerzel() + " " + semesterverband.getJahrgang()+ "\n");
			eText.append("Folgende Belegungen wurden identifiziert:\n");
			eText.append("\n");
			for(int i = 0; i < semesterverband.getBelegungen().size(); i++) {
				eText.append(semesterverband.getBelegungen().elementAt(i).getZeitslot().getWochentag() + "\t\t");
				eText.append(semesterverband.getBelegungen().elementAt(i).getLehrveranstaltung().getBezeichnung());
				eText.append("\n");
			}			
			throw new RuntimeException(eText.toString());
		}
	}
	
	/**
	 * Methode um einen bestimmten Dozenten zu löschen
	 * 
	 * @param	dozent - Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenDozent(Dozent dozent) throws RuntimeException {
		
		// Ein Dozent kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist		
		if (dozent.getBelegungen() == null || dozent.getBelegungen().size() == 0) {
			dozentMapper.delete(dozent);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von " + dozent.getTitel() + " " + dozent.getVorname() + " " + dozent.getNachname());
		}		
	}
	
	/**
	 * Methode um einen bestimmte Lehrveranstaltung zu löschen
	 * 
	 * @param	lehrveranstaltung -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException {
		
		// Ein Dozent kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist		
		if (lehrveranstaltung.getBelegungen() == null || lehrveranstaltung.getBelegungen().size() == 0) {
			lehrveranstaltungMapper.delete(lehrveranstaltung);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von " + lehrveranstaltung.getBezeichnung());
		}	
	}
	
	/**
	 * Methode um eine Belegung für !einen bestimmten Semesterverband zu löschen
	 * 
	 * @param	belegung -Objekt und das 
	 * 			semesterverband -Objekt, zudem die Referenz gelöschten werden soll bzw.
	 * 			bei nur einem referenzierten Semesterverband wird die übwergebene Belegung gelöscht
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenBelegungen(Belegung belegung, Semesterverband semesterverband) throws RuntimeException {
		
		/*
		 *  Wenn mehrere Semesterverbände die Belegung referenzieren, wird nur die Referenz des aus des 
		 *  vom Client gewählten Semesterverbandes zu der gemeinten Belegung gelöscht
		 */
		if (belegung.getSemesterverbaende().size() > 1) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(belegung.getId());
			Vector<Belegung> vb = this.belegungMapper.findByKey(vi, true);
			for (int i = 0; i < vb.elementAt(0).getSemesterverbaende().size(); i++) {
				if (vb.elementAt(0).getSemesterverbaende().elementAt(i).getId() == semesterverband.getId()) {
					vb.elementAt(0).getSemesterverbaende().removeElementAt(i);
					this.belegungMapper.update(vb.elementAt(0));
					break;
				}
			}
		}
		else {
			belegungMapper.delete(belegung);
		}
	}
	
	/**
	 * Methode um einen bestimmten Studiengang zu löschen
	 * 
	 * @param	studiengang -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenStudiengang(Studiengang studiengang) throws RuntimeException {
		
		// Ein Studiengang kann nur gelöscht werden, wenn er durch keine Lehrveranstaltungen und Semesterverbände mehr referenziert wird		
		if((studiengang.getLehrveranstaltungen() == null || studiengang.getLehrveranstaltungen().size() == 0) && 
				(studiengang.getSemesterverbaende() == null || studiengang.getSemesterverbaende().size() == 0)) {
			studiengangMapper.delete(studiengang);
		}
		else {
			StringBuffer eText = new StringBuffer();
			eText.append("Bitte löschen Sie zuerst alle Referenzen auf " + studiengang.getBezeichnung() + "\n\n");
			if (studiengang.getLehrveranstaltungen() != null) {
				eText.append("Folgende Lehrveranstaltungen sind verknüpft:\n");
				for (int i = 0; i < studiengang.getLehrveranstaltungen().size(); i++) {
					eText.append(studiengang.getLehrveranstaltungen().elementAt(i).getBezeichnung() + "\n");					
				}
				eText.append("\n\n");
			}
			if (studiengang.getSemesterverbaende() != null) {
				eText.append("Folgende Jahrgänge sind verknüpft:\n");
				for (int i = 0; i < studiengang.getSemesterverbaende().size(); i++) {
					eText.append(studiengang.getSemesterverbaende().elementAt(i).getJahrgang() + "\n");					
				}
			}
			throw new RuntimeException(eText.toString());
		}		
	}
	
	/**
	 * Methode um einen bestimmten Raum zu löschen
	 * 
	 * @param	raum -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenRaum(Raum raum) throws RuntimeException {
		
		// Ein Raum kann nur gelöscht werden, wenn er durch keine Belegungen mehr referenziert ist		
		if (this.belegungMapper.findByRaum(raum) == null) {
			raumMapper.delete(raum);
		}
		else {			
			throw new RuntimeException("Bitte löschen Sie zuerst alle Belegungen von Raum " + raum.getBezeichnung());
		}
	}
	
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
	 * @param	semesterverband -Objekt welches geändert sollen
	 * @return	Semesterverband-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Semesterverband aendernSemesterverband(Semesterverband semesterverband) throws RuntimeException {
			
		// Prüfung ob eine Studentenazahl angegeben wurde 
		if(new Integer(semesterverband.getAnzahlStudenten()).toString() == null || new Integer(semesterverband.getAnzahlStudenten()).toString().length() == 0) {
			throw new IllegalArgumentException("Bitte geben Sie die Anzahl der Studenten an");
		}
		
		// Prüfung ob ein Jahrgang angegeben wurde
		if (semesterverband.getJahrgang() == null || semesterverband.getJahrgang().length() == 0) {
			throw new IllegalArgumentException("Bitte geben Sie den Jahrgang an");
		}
		
		// Erzeugung eines Integer-Vectors mit der ID des Semsterverbandes als Element...
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(semesterverband.getId());
		
		// ...um anschließend den ürsprünglichen Semesterverband zu laden
		Vector<Semesterverband> altSemesterverband = semesterverbandMapper.findByKey(vi, true);
		
		boolean check = true;
		
		// Zunächst wird geprüft ob zu dem gemeinten Semesterverband Belegungen bestehen		
		if (altSemesterverband.elementAt(0).getBelegungen() != null && altSemesterverband.elementAt(0).getBelegungen().size() > 0) {
			check = false;
		}
		
		// Prüfung ob der Studiengang geändert wurde		
		if(semesterverband.getStudiengang().getId() != altSemesterverband.elementAt(0).getStudiengang().getId() && !check) {
			throw new RuntimeException("Der Studiengang eines Semesterverbandes kann nur geändert werden wenn keine Belegungen mehr refernziert werden");
		}
		
		// Prüfung ob der Jahrgang geändert wurde		
		if(!semesterverband.getJahrgang().equals(altSemesterverband.elementAt(0).getJahrgang()) && !check) {
			throw new RuntimeException("Der Jahrgang eines Semesterverbandes kann nur geändert werden wenn keine Belegungen mehr refernziert werden");
		}
		
		// Prüfung ob die Anzahl der Studenten korrekt angegeben wurde		
		if (!new Integer(semesterverband.getAnzahlStudenten()).toString().matches("[1-9]|[1-9][0-9]|[1-9][0-9][0-9]")) {
			throw new IllegalArgumentException("Die Anzahl der Studenten kann eine natürliche Zahl von 1 bis 999 sein.\n(Bitte auch keine führende Null angeben)");
		}
		
		// Prüfen ob dieser Jahrgang in dem Studiengang bereits vorhanden ist		
		Vector<Semesterverband> sgNachSV = this.auslesenSemesterverbaendeNachStudiengang(semesterverband.getStudiengang());
		
		for (Semesterverband sv : sgNachSV) {
			if (semesterverband.getJahrgang().equals(sv.getJahrgang()) && semesterverband.getId() != sv.getId()) {
				throw new IllegalArgumentException("Dieser Semesterverband existiert bereits");
			}
		}
		
		// Prüfung ob eine neue Studentenanzahl mit bestehenden Belegungen vereinbar ist		
		if (!check) {
			if (semesterverband.getAnzahlStudenten() != altSemesterverband.elementAt(0).getAnzahlStudenten()) {
				for (Belegung belegung : altSemesterverband.elementAt(0).getBelegungen()) {
					if (belegung.getRaum().getKapazitaet() < semesterverband.getAnzahlStudenten()) {
						throw new IllegalArgumentException("Die Anzahl der Studenten ist zu hoch für bestehende Belegungen");
					}
				}
			}
		}
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde		
		StringBuffer jahrgang = new StringBuffer();
		jahrgang.append(semesterverband.getJahrgang());
		
		if (jahrgang.length() == 0) {
			throw new IllegalArgumentException("Das Feld \"Jahrgang\" darf nicht leer sein"); 
		}
		
		if (!jahrgang.substring(0,2).equals("SS") && !jahrgang.substring(0,2).equals("WS") || !jahrgang.substring(2,4).equals("20")) {
			throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nBeachten Sie auch die Gross-/Kleinschreibung");			
		}
		
		if ((jahrgang.substring(0,2).equals("SS") && jahrgang.length() != 6) || (jahrgang.substring(0,2).equals("WS") && jahrgang.length() != 9) ||
				(jahrgang.substring(0,2).equals("WS") && jahrgang.charAt(6) != new Character('/'))) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
		}
		
		if (jahrgang.substring(0,2).equals("SS")) {
			try {
				Integer.parseInt(jahrgang.substring(4,6));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
		}
		
		if (jahrgang.substring(0,2).equals("WS")) {
			int vJahr;
			int nJahr;
			try {
				vJahr = Integer.parseInt(jahrgang.substring(4,6));
				nJahr = Integer.parseInt(jahrgang.substring(7,9));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
			if (nJahr - vJahr != 1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nNur ein Jahreswechsel erlaubt");
			}
		}
		
		altSemesterverband.elementAt(0).setAnzahlStudenten(semesterverband.getAnzahlStudenten());
		altSemesterverband.elementAt(0).setJahrgang(semesterverband.getJahrgang());
		altSemesterverband.elementAt(0).setStudiengang(semesterverband.getStudiengang());
		
		this.semesterverbandMapper.update(altSemesterverband.elementAt(0));
			
		return semesterverband;
	}
	
	/**
	 * Methode um einen Dozent abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	dozent -Objekt welches geändert werden sollen
	 * @return	Dozent-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Dozent aendernDozent(Dozent dozent) throws RuntimeException {
							
		// Prüfung ob Vor- und Nachname angegeben wurden		
		if ((dozent.getVorname() == null ||dozent.getVorname().length() == 0) || (dozent.getNachname() == null || dozent.getNachname().length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie Vor- und Nachname an");
		}
		
		// Prüfung ob Vor- oder Nachname je nicht länger als 45 Zeichen sind
		if (dozent.getVorname().length() > 45 || dozent.getVorname().length() > 45) {
			throw new IllegalArgumentException("Der Vor- und Nachname darf je nicht mehr als 45 Zeichen enthalten");
		}
			
		// Prüfung ob am Ende der Vor- oder Nachnamens ein Leerzeichen ist				
		if (dozent.getVorname().substring(0, 1).equals(" ") || dozent.getVorname().lastIndexOf(" ") == dozent.getVorname().length() - 1 ||
				dozent.getNachname().substring(0, 1).equals(" ") || dozent.getNachname().lastIndexOf(" ") == dozent.getNachname().length() - 1) {
			throw new IllegalArgumentException("Es dürfen sich vor dem Vor-bzw.Nachname keine Leerzeichen befinden");
		}
		
		
		// Prüfung des Vor- und Nachnamens auf Zahlen und bestimmte Sonderzeichen, diese sind nicht erlaubt				
		if (!dozent.getVorname().matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*") || 
				!dozent.getNachname().matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen im Vor- bzw. Nachnamen");
		}
		
		// Prüfung der Personalnummer auf fünfstellige Ziffernfolge		
		if (!new Integer(dozent.getPersonalnummer()).toString().matches("[0-9]{5}")) {
			throw new IllegalArgumentException("Die Personalnummer ist nicht fünfstellig\noder es befinden sich darin nicht erlaubte Zeichen");
		}
		
		// Prüfen ob die Personalnummer bereits vergeben ist		
		Vector<Dozent> alleDozenten = this.dozentMapper.findAll(false);
		
		for (Dozent d : alleDozenten) {
			if (dozent.getPersonalnummer() == d.getPersonalnummer() && d.getId() != dozent.getId()) {
				throw new IllegalArgumentException("Diese Personalnummer ist bereits vergeben");
			}
		}
		
		// Laden der "alten" Version des Dozenten um einen Vergleich der Lehrveranstaltungen zu ermöglichen
		Vector<Integer> dozentID = new Vector<Integer>();
		dozentID.add(dozent.getId());
		Vector<Dozent> oldDozent = this.dozentMapper.findByKey(dozentID, true);
		
		// Vector mit ID's der Lehrveranstaltungen, welche nicht mehr vom Dozenten gehalten werden
		Vector<Integer> tempLehrveranstalungIDs = new Vector<Integer>();
		
		// Auslesen der ID's derer Lehrveranstaltungen, welche nicht mehr vom Dozenten gehalten werden
		if (oldDozent.elementAt(0).getLehrveranstaltungen() != null && oldDozent.elementAt(0).getLehrveranstaltungen().size() > 0) {
			
			Integer a = null;
			
			for (int i = 0; i < oldDozent.elementAt(0).getLehrveranstaltungen().size(); i++) {
				for (int j = 0; j < dozent.getLehrveranstaltungen().size(); j++) {
					if (oldDozent.elementAt(0).getLehrveranstaltungen().elementAt(i).getId() != dozent.getLehrveranstaltungen().elementAt(j).getId()) {
						a = oldDozent.elementAt(0).getLehrveranstaltungen().elementAt(i).getId();
					}
					else {
						a = null;
						break;
					}
				}
				if (a != null) {
					tempLehrveranstalungIDs.add(a);
					a = null;
				}
			}
		}
		
		/*
		 *  Prüfwert, falls Referenzen vom Dozent auf bestimmte Lehrveranstaltungen nicht gelöscht werden können,
		 *  da sie Belegungen mit einzig "diesem" Dozenten referenzieren -> false = kann nicht gelöscht werden
		 */		
		Boolean check = true;
		
		StringBuffer eText = new StringBuffer("Die nicht mehr gehaltenen Lehrveranstaltungen können nicht gelöscht werden.\nFolgende Belegungen müssen zuerst "
				+ "entfernt oder von einem anderen Dozenten übernommen werden: \n\nID\t\t\tWochentag\t\t\tZeit\t\t\tLV\n");
		
		// Zuerst wird geprüft, ob der Dozent bestimmte Lehrveranstaltungen nun nicht mehr hält		
		if (tempLehrveranstalungIDs.size() > 0) {
			// Die Lehrveranstaltungen, die der Dozent nicht halten soll bzw. wird, werden nun geladen
			Vector<Lehrveranstaltung> tempLehrveranstalungen = this.lehrveranstaltungMapper.findByKey(tempLehrveranstalungIDs, true);
			// Jede dieser Lehrveranstaltungen wird nun gepröft, ob sie Belegungen enthölt
			for (int i = 0; i < tempLehrveranstalungen.size(); i++) {
				if (tempLehrveranstalungen.elementAt(i).getBelegungen() != null) {
					// Sollte eine Lehrveranstaltung Belegungen referenzieren, werden diese nun geladen
					Vector<Belegung> tempBelegungen = this.belegungMapper.findByLehrveranstaltung(tempLehrveranstalungen.elementAt(i));
					/*
					 *  Bei jeder Belegung wird schließlich gepröft, ob sie nur von einem Dozenten durchgeführt wird 
					 *  und ob es sich dabei um den hier (Methoden-Parameter) zu ändernden Dozenten hält.
					 *  Sollte dies der Fall sein, kann eine Änderung nicht durchgeführt werden, da zuerst die
					 *  Belegungen der zu löschenden Lehrveranstaltung gelöscht werden müssen oder aber sie
					 *  werden von einem anderen Dozenten gehalten
					 */
					for (int j = 0; j < tempBelegungen.size(); j++) {
						if(tempBelegungen.elementAt(j).getDozenten().size() <= 1 && (tempBelegungen.elementAt(j).getDozenten().elementAt(0).getId() == dozent.getId())) {
							check = false;
							eText.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + " " + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t");
							eText.append(tempLehrveranstalungen.elementAt(i).getBezeichnung() + "\n");
						}
					}
				}
			}
		}
		
		if(!check) {
			throw new RuntimeException(eText.toString());
		}
		else {
			this.dozentMapper.update(dozent);
			return dozent;
		}
	}
	
	/**
	 * Methode um eine Lehrveranstaltung abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	lehrveranstaltung -Objekt welches geändert werden sollen
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Lehrveranstaltung aendernLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) throws RuntimeException {
		
		// Prüfung ob min ein Studiengang angegeben wurde		
		if (lehrveranstaltung.getStudiengaenge() == null || lehrveranstaltung.getStudiengaenge().size() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie mindestens einen Studiengang an!");
		}
		
		// Prüfen ob eine Bezeichnung angegeben wurde		
		if (lehrveranstaltung.getBezeichnung() == null || lehrveranstaltung.getBezeichnung().length() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie eine Bezeichnung an!");
		}
		
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (!lehrveranstaltung.getBezeichnung().matches("[^,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen in der Bezeichnung!");
		}
		
		// Prüfung ob Bezeichnung nicht länger als 45 Zeichen ist
		if (lehrveranstaltung.getBezeichnung().length() > 45) {
			throw new IllegalArgumentException("Die Bezeichnung darf nicht mehr als 45 Zeichen enthalten");
		}
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (lehrveranstaltung.getBezeichnung().matches("[0-9]*") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[0-9]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf keine Zahl sein oder mit einer beginnen!");
		}				
		if (lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[ ]{1}") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[/]{1}") || lehrveranstaltung.getBezeichnung().substring(0, 1).matches("[-]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf mit keinem Leerzeichen oder Sonderzeichen beginnen!");
		}
		
		/*
		 *  Prüfung ob die Bezichung mit einem Buchstabe beginnt und ob am ende nur eine Ziffer verwendet wurde
		 *  -> Prüfung deaktiviert, da sie nicht mehr den aktuellen BusinessRules entspricht
		 
		if (!lehrveranstaltung.getBezeichnung().matches("[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}[ ]{0,1}[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}")) {
			throw new IllegalArgumentException("Bitte beginnen Sie die Bezeichnung mit einem Buchstaben\n"
					+ "Bitte verwenden Sie nur ein Leerzeichen in Folge\nBitte benutzen Sie nur eine Ziffer in Folge");
		}
		*/		
		
		// Prüfung ob am Ende ein Lehrzeichen steht				
		if (lehrveranstaltung.getBezeichnung().lastIndexOf(" ") == lehrveranstaltung.getBezeichnung().length() - 1) {
			throw new IllegalArgumentException("Bitte entfernen Sie alle Leerzeichen am Ende der Bezeichnung!");
		}
		
		// Laden der "alten" Version der Lehrveranstaltung
		Vector<Integer> vI = new Vector<Integer>();
		vI.add(lehrveranstaltung.getId());
		Vector<Lehrveranstaltung> oldLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vI, true);
		
		// Prüfen ob die Bezeichnung oder das Kürzel bereits vorhanden sind		
		Vector<Lehrveranstaltung> alleLVs = this.lehrveranstaltungMapper.findAll(false);
		
		for (Lehrveranstaltung l : alleLVs) {
			if (lehrveranstaltung.getBezeichnung().equals(l.getBezeichnung()) && lehrveranstaltung.getId() != l.getId()) {
				throw new IllegalArgumentException("Diese Bezeichnung ist bereits vergeben, bitte ändern Sie diese!");
			}
		}
		
		// Vector mit ID's der Studiengänge, welche nicht mehr der hier zu ändernden Lehrveranstaltung zugeordnet sind
		Vector<Integer> tempStudiengangIDs = new Vector<Integer>();
				
		// Auslesen der ID's derer Studiengänge, welche nicht mehr der hier zu ändernden Lehrveranstaltung zugeordnet sind				
		Integer a1 = null;
		
		if ((oldLehrveranstaltung.elementAt(0).getStudiengaenge() != null && oldLehrveranstaltung.elementAt(0).getStudiengaenge().size() > 0) &&
				(lehrveranstaltung.getStudiengaenge() != null && lehrveranstaltung.getStudiengaenge().size() > 0)) {
			for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getStudiengaenge().size(); i++) {
				for (int j = 0; j < lehrveranstaltung.getStudiengaenge().size(); j++) {
					if (oldLehrveranstaltung.elementAt(0).getStudiengaenge().elementAt(i).getId() != lehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
						a1 = oldLehrveranstaltung.elementAt(0).getStudiengaenge().elementAt(i).getId();
					}
					else {
						a1 = null;
						break;
					}
				}
				if (a1 != null) {
					tempStudiengangIDs.add(a1);
					a1 = null;
				}
			}
		}
		
		/*
		 *  Prüfwert, falls Referenzen von der Lehrveranstaltung auf bestimmte Studiengänge nicht gelöscht werden können,
		 *  da Semesterverbände dieser Studiengänge eine Belegung mit "dieser" Lehrveranstaltung referenzieren
		 *  -> false = kann nicht gelöscht werden
		 */		
		Boolean check1 = true;
		
		StringBuffer eText1 = new StringBuffer("Verbindungen zu Studiengängen können nicht entfernt werden.\nFolgende Belegungen müssen zuerst "
				+ "entfernt oder einem anderen Semesterverband zugeordnet werden: \n\n");
		
		// Zuerst wird geprüft ob fehlende Studiengänge identifiziert wurden
		if (tempStudiengangIDs.size() > 0) {
			// Nun werden die Belegungen dieser Lehrveranstaltung geladen
			Vector<Belegung> tempBelegungen = this.belegungMapper.findByLehrveranstaltung(lehrveranstaltung);
			// Für jeden Studiengang, welcher "dieser" Lehrveranstaltung nicht mehr zugeordnet sein soll... 
			if (tempBelegungen != null && tempBelegungen.size() > 0) {
				for (int i = 0; i < tempStudiengangIDs.size(); i++) {
					// ...werden alle Belegungen "dieser" Lehrveranstaltung durchsucht und in jeder dieser Belegung...
					for (int j = 0; j < tempBelegungen.size(); j++) {
						// ...werden alle zugeordneten Semesterverbände durchsucht
						if (tempBelegungen.elementAt(j).getSemesterverbaende() != null && tempBelegungen.elementAt(j).getSemesterverbaende().size() > 0) {
							for (int k = 0; k < tempBelegungen.elementAt(j).getSemesterverbaende().size(); k++) {
								/*
								 *  Sollte ein Semesterverband den "entfernten" Studiengängen entsprechen,
								 *  wird check auf "false" gesetzt und die Lehrveranstaltung kann nicht
								 *  geändert werden
								 */
								if(tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getStudiengang().getId() == tempStudiengangIDs.elementAt(i)) {
									check1 = false;
									eText1.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + "\t\t" + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t" + tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getStudiengang().getKuerzel()+ " " + tempBelegungen.elementAt(j).getSemesterverbaende().elementAt(k).getJahrgang() + "\n");
								}
							}
						}
					}
				}
			}			
		}
		
		if(!check1) {
			throw new RuntimeException(eText1.toString());
		}
		
		// Vector mit ID's der Dozenten, welche nicht mehr der Lehrveranstaltung zugeordnet sein sollen
		Vector<Integer> tempDozentIDs = new Vector<Integer>();
				
		// Auslesen der ID's derer Dozenten, welche nicht mehr der Lehrveranstaltung zugeordnet sein sollen				
		Integer a2 = null;
		
		if (oldLehrveranstaltung.elementAt(0).getDozenten() != null && oldLehrveranstaltung.elementAt(0).getDozenten().size() > 0) {
			
			if (lehrveranstaltung.getDozenten() == null || lehrveranstaltung.getDozenten().size() == 0) {
				for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getDozenten().size(); i++) {
					tempDozentIDs.add(oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId());
				}
			}
			else {
				for (int i = 0; i < oldLehrveranstaltung.elementAt(0).getDozenten().size(); i++) {
					for (int j = 0; j < lehrveranstaltung.getDozenten().size(); j++) {						
						if (oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId() != lehrveranstaltung.getDozenten().elementAt(j).getId()) {							
							a2 = oldLehrveranstaltung.elementAt(0).getDozenten().elementAt(i).getId();
						}
						else {
							a2 = null;
							break;
						}
					}
					if (a2 != null) {
						tempDozentIDs.add(a2);
						a2 = null;
					}
				}
			}
		}
				
		/*
		 *  Prüfwert, falls Referenzen vom Dozent auf bestimmte Lehrveranstaltungen nicht gelöscht werden können,
		 *  da sie Belegungen mit einzig "diesem" Dozenten referenzieren -> false = kann nicht gelöscht werden
		 */				
		Boolean check2 = true;
				
		StringBuffer eText2 = new StringBuffer("Die Verbindung zu bestimmten Dozenten kann nicht gelöscht werden.\nFolgende Belegungen müssen zuerst "
				+ "entfernt oder von einem anderen Dozenten übernommen werden: ");
				
				// Zuerst wird geprüft, ob Dozenten "dieser" Lehrveranstaltung nicht mehr zugeordnet sein sollen 		
				if (tempDozentIDs.size() > 0) {
					// Die Dozenten die "dieser" Lehrveranstaltung nicht mehr zugeordnet sein sollen werden nun geladen
					Vector<Dozent> tempDozenten = this.dozentMapper.findByKey(tempDozentIDs, true);
					// Für jede dieser Dozenten wird nun auf vorhandene Belegungen geprüft
					if (tempDozenten != null && tempDozenten.size() > 0) {
						for (int i = 0; i < tempDozenten.size(); i++) {
							if (tempDozenten.elementAt(i).getBelegungen() != null && tempDozenten.elementAt(i).getBelegungen().size() > 0) {
								// Sollte ein Dozent Belegungen referenzieren, werden diese nun geladen
								Vector<Belegung> tempBelegungen = this.belegungMapper.findByDozent(tempDozenten.elementAt(i));
								/*
								 *  Bei jeder Belegung wird schließlich geprüft, ob diese der "hier" zu ändernden
								 *  Lehrveranstaltung (Methoden-Parameter) entspricht. Sollte dies der Fall sein, kann 
								 *  eine Änderung nicht durchgeführt werden, da zuerst die Belegungen der zu löschenden 
								 *  Dozenten gelöscht werden müssen oder aber sie müssen einem anderen Dozenten zugeordnet
								 *  werden
								 */
								for (int j = 0; j < tempBelegungen.size(); j++) {
									if(tempBelegungen.elementAt(j).getLehrveranstaltung().getId() == lehrveranstaltung.getId()) {
										check2 = false;
										eText2.append(tempBelegungen.elementAt(j).getZeitslot().getWochentag() + " " + tempBelegungen.elementAt(j).getZeitslot().getAnfangszeit() + "\t\t");
										eText2.append(tempDozenten.elementAt(i).getTitel() + " ");
										eText2.append(tempDozenten.elementAt(i).getVorname() + " ");
										eText2.append(tempDozenten.elementAt(i).getNachname() + "\n");
									}
								}
							}
						}
					}
				}		
		
		if(!check2) {
			throw new RuntimeException(eText2.toString());
		}
		
		if((lehrveranstaltung.getUmfang() != oldLehrveranstaltung.elementAt(0).getUmfang()) && (oldLehrveranstaltung.elementAt(0).getBelegungen() != null)) {
			throw new RuntimeException("Es müssen erst alle Belegungen dieser Lehrveranstaltung gelöscht werden, bevor deren Umfang geändert werden kann");
		}
		
		if((lehrveranstaltung.getStudiensemester() != oldLehrveranstaltung.elementAt(0).getStudiensemester()) && (oldLehrveranstaltung.elementAt(0).getBelegungen() != null)) {
			throw new RuntimeException("Es müssen erst alle Belegungen dieser Lehrveranstaltung gelöscht werden, bevor deren Studiensemester geändert werden kann");
		}
				
		this.lehrveranstaltungMapper.update(lehrveranstaltung);
		return lehrveranstaltung;						
		
	}
	
	/**
	 * Methode um einee Belegung abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	belegung -Objekt welches geändert werden sollen
	 * @return	Belegung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Belegung aendernBelegung(Belegung belegung) throws RuntimeException {
		
		// Prüfung ob ein Dozent ausgwählt wurde		
		if(belegung.getDozenten().size() <= 0) {
			throw new RuntimeException("Bitte fügen Sie einen Dozenten hinzu!");
		}
		
		// Prüfung ob ein Dozent mehrmals ausgewählt wurde		
		if(belegung.getDozenten().size() > 1) {
			for (int i = 0; i < belegung.getDozenten().size(); i++) {
				int multiplzitaetDozent = 0;
				for (int j = 0; j < belegung.getDozenten().size(); j++) {
					if (belegung.getDozenten().elementAt(i).getId() == belegung.getDozenten().elementAt(j).getId()) {
						multiplzitaetDozent = multiplzitaetDozent + 1;
					}
				}
				if(multiplzitaetDozent > 1) {
					throw new RuntimeException("Bitte fügen Sie jeden Dozenten nur einfach zu einer Belegung hinzu");
				}
			}
		}
				
		// Prüfung ob das Studiensemester der gewünschten Lehrveranstaltung mit dem des Semesterverbandsvereinbar ist		
		Integer semesterAlt = null;
		Integer semesterNeu = null;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			
			Semesterverband tempSemVerband = belegung.getSemesterverbaende().elementAt(i);
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(tempSemVerband.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};

			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {				
				
				int zaehler = 0; 
			
				for (int j = semMonat-1; j < calendar.length; j++ ) {
					
					zaehler++;
					
					if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
						break;
					}
					if(calendar[j] == 12) {
						j = -1;
						jahresDiff--;
					}
				}
	
				int studienSem = zaehler / 6;
				int studienSemMonat = zaehler;
				
				if (zaehler != 0) {
					studienSemMonat = zaehler % 6;
				}				
						
				if (studienSem == 0) {
					semesterAlt = 1;
					semesterNeu = 0;
				}
				
				else if (studienSemMonat == 0) {
					semesterAlt = studienSem;
					semesterNeu = studienSem + 1;
				}			
						
				else {
					semesterAlt = studienSem + 1;
				}
			}
			else {
				semesterAlt = 1;
			}
		}
		
		
		if (semesterNeu == null || semesterNeu == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
			}
		}
		
		else if (semesterAlt == null || semesterAlt == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
			}
		}		
		
		else {
			if ((belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) && (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu)) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
		}
		}
				
		
		// Prüfen ob der Raum genügend Kapazität aufweist für die referenzierten Semesterverbände		
		int studenten = 0;
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			studenten = studenten + belegung.getSemesterverbaende().elementAt(i).getAnzahlStudenten();
		}
		if (belegung.getRaum().getKapazitaet() < studenten) {
			throw new RuntimeException("Der gewuenschte Raum hat nicht genuegend Plätze");
		}
		
				
		/*
		 * Prüfen ob der Raum zum gewänschten Zeitslot noch verfügbar ist
		 * Prüfung deaktiviert, da nun nur freie Räume dem Client zur Verfügung gestellt werden
		 * Bei einem verteilten Arbeiten muss diese Prüfung wieder aktiviert werden um eine Konsistenz zu gewährleisten.
		 * Stand: 05.01.2013
		
		Vector<Belegung> raumBelegungen = this.belegungMapper.findByRaum(belegung.getRaum());
		
		for (int i = 0; i < raumBelegungen.size(); i++) {
			if ((belegung.getZeitslot().getId() == raumBelegungen.elementAt(i).getZeitslot().getId()) && (belegung.getId() != raumBelegungen.elementAt(i).getId())) {
				
				throw new RuntimeException("Der gewönschte Raum ist zum gewönschten Zeitslot schon belegt");
			}
		}
		*/
		
		/*
		 * Prüfen ob der Semesterverband zum gewünschten Zeitslot verfügbar ist	
		 * Prüfung deaktiviert, da Zeitslots vom Client nicht gewählt werden können
		 * Stand: 05.01.2013
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> semVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			for (int j = 0; j < semVerBelegungen.size(); j++) {
				if ((belegung.getZeitslot().getId() == semVerBelegungen.elementAt(j).getZeitslot().getId()) && (belegung.getId() != semVerBelegungen.elementAt(j).getId())) {
					throw new RuntimeException("Der Semesterverband ist zu diesem Zeitpunkt bereits eingeteilt");
				}
			}
		}
		*/

		/*
		 * Prüfen ob der Dozent zum gewönschten Zeitslot verfögbar ist
		 * Prüfung deaktiviert, da nun nur freie Dozenten dem Client zur Verfügung gestellt werden
		 * Bei einem verteilten Arbeiten muss diese Prüfung wieder aktiviert werden um eine Konsistenz zu gewährleisten.
		 * Stand 06.01.2014
		
		for (int i = 0; i < belegung.getDozenten().size(); i++) {			
			Vector<Belegung> dozentenBelegungen = this.belegungMapper.findByDozent(belegung.getDozenten().elementAt(i));
			if (dozentenBelegungen != null && dozentenBelegungen.size() > 0) {
				for (int j = 0; j < dozentenBelegungen.size(); j++) {
					if ((belegung.getZeitslot().getId() == dozentenBelegungen.elementAt(j).getZeitslot().getId()) && (belegung.getId() != dozentenBelegungen.elementAt(j).getId()) && (belegung.getLehrveranstaltung().getId() != dozentenBelegungen.elementAt(j).getLehrveranstaltung().getId())) {
						throw new RuntimeException("Der Dozent ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		*/
		
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getLehrveranstaltung().getId());
		
		// Prüfung ob sich die Lehrveranstaltung und die Semesterverbände im gleichen Studiengang befinden		
		Lehrveranstaltung tempLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vi, true).elementAt(0);
		
		boolean check = false;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			check = false;
			if (tempLehrveranstaltung.getStudiengaenge() != null && tempLehrveranstaltung.getStudiengaenge().size() > 0) {
				for (int j = 0; j < tempLehrveranstaltung.getStudiengaenge().size(); j++) {
					if (belegung.getSemesterverbaende().elementAt(i).getStudiengang().getId() == tempLehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
						check = true;
						break;
					}
				}
			}
			else {
				throw new RuntimeException("Diese Lehrveranstaltung ist keinem Studiengang zugeordnet!");
			}
		}
		
		if (!check) {
			throw new RuntimeException("Diese Lehrveranstaltung und dieser Semesterverband befinden sich nicht im gleichen Studiengang");
		}
			
		// Prüfung ob der Umfang (SWS) einer Lehrveranstaltung für einen Semesterverband bereits erreicht wurde		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> tempSemVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			if (tempSemVerBelegungen != null && tempSemVerBelegungen.size() > 0) {
				int countSWS = 0;
				for (int j = 0; j < tempSemVerBelegungen.size(); j++) {
					if((belegung.getLehrveranstaltung().getId() == tempSemVerBelegungen.elementAt(j).getLehrveranstaltung().getId()) && (belegung.getId() != tempSemVerBelegungen.elementAt(j).getId())) {
						countSWS = countSWS + 2;
					}
				}
				if (belegung.getLehrveranstaltung().getUmfang() < (countSWS + 2)) {
					throw new RuntimeException("Der vorgesehene Umfang (SWS) der Lehrveranstaltung wurde für Semesterverband " + belegung.getSemesterverbaende().elementAt(i).getStudiengang().getKuerzel() + " " + belegung.getSemesterverbaende().elementAt(i).getJahrgang() + "bereits erreicht!");
				}
			}
		}
		
		// Dozenten hinsichtlich ihrer Referenzen zu Lehrveranstaltungen aktualisieren
		
		/*
		 * Funktion deaktiviert, da die Programmpolitik derzeit es nicht vorsieht auch temporör gehaltene Lehrveranstaltungen
		 * zum Repertoir eines Dozenten hinzuzufügen
		 * Stand: 06.01.2014

		Vector<Integer> vi2 = new Vector<Integer>();
		for (int i = 0; i < belegung.getDozenten().size(); i++) {
			vi2.add(belegung.getDozenten().elementAt(i).getId());
		}
		Vector<Dozent> vd = this.dozentMapper.findByKey(vi2, true);
		for (int i = 0; i < vd.size(); i++) {
			boolean check1 = true;
			if (vd.elementAt(i).getLehrveranstaltungen() != null && vd.elementAt(i).getLehrveranstaltungen().size() > 0) {
				for (int j = 0; j < vd.elementAt(i).getLehrveranstaltungen().size(); j++) {
					if (belegung.getLehrveranstaltung().getId() == vd.elementAt(i).getLehrveranstaltungen().elementAt(j).getId()) {
						check1 = false;
					}
				}
				if (check1) {
					vd.elementAt(i).getLehrveranstaltungen().add(belegung.getLehrveranstaltung());
					this.dozentMapper.update(vd.elementAt(i));
				}
			}
		}
		
		*/
		
		
		return this.belegungMapper.update(belegung);
				
	}
	
	/**
	 * Methode um einen Studiengang abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	studiengang -Objekt welches geändert werden sollen
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Studiengang aendernStudiengang(Studiengang studiengang) throws RuntimeException {
		
		// Prüfung ob Bezeichung und Kürzel angegeben wurden		
		StringBuffer bezeichnung = new StringBuffer();
		bezeichnung.append(studiengang.getBezeichnung());
		StringBuffer kuerzel = new StringBuffer();
		kuerzel.append(studiengang.getKuerzel());
		
		if ((bezeichnung.length() == 0) || (kuerzel.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie eine Bezeichnung und das Kürzel an");
		}
		
		// Prüfung ob Bezeichnung nicht länger als 45 Zeichen ist
		if (bezeichnung.length() > 45) {
			throw new IllegalArgumentException("Die Bezeichnung darf nicht mehr als 45 Zeichen enthalten");
		}
		
		// Prüfung ob im Kürzel nur Zahlen angegeben wurden
		if (studiengang.getKuerzel().matches("[0-9]*")) {
			throw new IllegalArgumentException("Das Kürzel darf nicht nur aus Zahlen bestehen!");
		}
		
		if (!studiengang.getKuerzel().matches("[A-Z]{2,4}|[A-Z]{2,4}[-]([0-9]|[1][0-9]|[2][0])")) {
			throw new IllegalArgumentException("Das Kürzel muss anfangs 2 bis 4 Großbuchstaben enthalten und darf am Ende eine Zahl"
					+ " von 1 bis 20 mit vorhergehendem Bindestrich enthalten\nUmlaute sind nicht gestattet");
		}
		
		// Prüfen ob die Bezeichnung und/oder das Kürzel bereits vergeben sind		
		Vector<Studiengang> alleSGs = this.studiengangMapper.findAll(false);
				
		for (Studiengang s : alleSGs) {
			if((bezeichnung.equals(s.getBezeichnung()) || kuerzel.equals(s.getKuerzel())) && studiengang.getId() != s.getId()) {
				throw new IllegalArgumentException("Diese Bezeichnung und/oder das Kürzel sind bereits vergeben");
			}
		}
		
		// Prüfung ob eine entfernte Lehrveranstaltung noch durch einen anderen Studiengang referenziert wird
		
		// Laden der "alten" Version des Studiengangs
		Vector<Integer> vI = new Vector<Integer>();
		vI.add(studiengang.getId());
		Vector<Studiengang> oldStudiengang = this.studiengangMapper.findByKey(vI, true);
				
		// Vector mit ID's der Lehrveranstaltungen, welche nicht mehr dem hier zu ändernden Studiengang zugeordnet sind
		Vector<Integer> tempLehrveranstaltungIDs = new Vector<Integer>();
						
		// Auslesen der ID's derer Lehrveranstaltungen, welche nicht mehr dem hier zu ändernden Studiengang zugeordnet sind						
		Integer a1 = null;
				
		if ((oldStudiengang.elementAt(0).getLehrveranstaltungen() != null && oldStudiengang.elementAt(0).getLehrveranstaltungen().size() > 0) &&
				(studiengang.getLehrveranstaltungen() != null && studiengang.getLehrveranstaltungen().size() > 0)) {
			for (int i = 0; i < oldStudiengang.elementAt(0).getLehrveranstaltungen().size(); i++) {
				for (int j = 0; j < studiengang.getLehrveranstaltungen().size(); j++) {
					if (oldStudiengang.elementAt(0).getLehrveranstaltungen().elementAt(i).getId() != studiengang.getLehrveranstaltungen().elementAt(j).getId()) {
						a1 = oldStudiengang.elementAt(0).getLehrveranstaltungen().elementAt(i).getId();
					}
					else {
						a1 = null;
						break;
					}
				}
				if (a1 != null) {
					tempLehrveranstaltungIDs.add(a1);
					a1 = null;
				}
			}
		}
		
		// Auslesen der Lehrveranstaltungen aus der DB, welche nicht mehr dem Studiengang zugeordnet sein sollen
		
		if (tempLehrveranstaltungIDs.size() > 0) {
			Vector<Lehrveranstaltung> lostLV = this.lehrveranstaltungMapper.findByKey(tempLehrveranstaltungIDs, true);
			for (Lehrveranstaltung lv : lostLV) {
				if (lv.getStudiengaenge().size() == 1) {
					throw new RuntimeException("Diese Lehrveranstaltung " + lv.getBezeichnung() + " kann nicht entfernt werden, da sie durch keinen anderen Studiengang referenziert wird\n"
							+ "Bei Bedarf löschen Sie bitte diese Lehrveranstaltung oder weisen ihr einen anderen Studiengang zu");
				}
			}
		}
		
		
		return this.studiengangMapper.update(studiengang);
		
	}
	
	/**
	 * Methode um einen Raum abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	raum - Objekt welches geändert werden sollen
	 * @return	Raum-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Raum aendernRaum(Raum raum) throws RuntimeException {
		
		// Prüfung ob eine Bezeichung angegeben wurde
		if (raum.getBezeichnung() == null || raum.getBezeichnung().length() == 0) {
			throw new IllegalArgumentException("Bitte geben Sie eine Bezeichnung ein");
		}
		
		// Prüfung ob eine erlaubte Beizeichnung eingetragen wurde
		if (!raum.getBezeichnung().matches("[W]{1}[0-9]{3}|[W]{1}[-]{1}[N]{1}[0-9]{3}|[N]{1}[0-9]{3}")) {
			throw new IllegalArgumentException("Die Bezeichnung entspricht nicht den Vorgaben");
		}
		
		// Prüfung ob die Kapazität größer 0 ist
		if (raum.getKapazitaet() == 0) {
			throw new IllegalArgumentException("Die Kapazität darf nicht 0 sein ");
		}
		
		// Prüfung ob die Kapazität kleiner 1.000 ist
		if (!new Integer(raum.getKapazitaet()).toString().matches("[1-9]|[1-9][0-9]|[1-9][0-9][0-9]")) {
			throw new IllegalArgumentException("Die Kapazität muss 1 bis 999 betragen");
		}
		
		
		// Prüfziffer ("0" steht für Akzeptiert)
		int accepted = 0;

		
		// Auslesen aller Belegungen die den Raum referenzieren, der aktualisiert werden soll		
		Vector<Belegung> tempBelegungen = this.belegungMapper.findByRaum(raum);
		
		// Lokale Variable zum festhalten der SemesterverbandID's, welche von "vB" referenziert werden 
		Vector<Integer> vI = new Vector<Integer>();
		if (tempBelegungen != null && tempBelegungen.size() > 0) {
			// Hinzufügen der SemesterverbandID's zu "vI", falls sie nicht bereits enthalten sind
			for(int i = 0; i < tempBelegungen.size(); i++) {
				for (int j = 0; j < tempBelegungen.elementAt(i).getSemesterverbaende().size(); j++){
				
					if (!vI.contains((Integer)tempBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getId())) {
						vI.add(tempBelegungen.elementAt(i).getSemesterverbaende().elementAt(j).getId());
					}
				}			
			}
			//Laden der betroffenen Semesterverbönde mittels "vI"		
			Vector<Semesterverband> vS = SemesterverbandMapper.semesterverbandMapper().findByKey(vI, false);

			//Prüfen ob eine Kapazitätsänderung mit den referenzierten Semesterverbandsgrößen vereinbar ist
			for(int i = 0; i < vS.size(); i++) {
				if (raum.getKapazitaet() < vS.elementAt(i).getAnzahlStudenten()) {
					accepted++;
				}
			}
		}
		
		Vector<Raum> alleRaeume = raumMapper.findAll();
		
		for (Raum r : alleRaeume) {
			if (raum.getBezeichnung().equals(r.getBezeichnung()) && raum.getId() != r.getId()) {
				throw new IllegalArgumentException("Bitte wählen Sie eine andere Bezeichung, dieser Raum existiert bereits!");
			}
		}

		if ( accepted == 0) {
			return this.raumMapper.update(raum);
		}
		else {
			throw new RuntimeException("Die Kapazität ist für Semesterverbände, welche bereits Lehrveranstaltungen in diesem Raum zugeordnet sind, zu klein");
		}

	}
	
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
	 * @param	anzahlStudenten -Anzahl der Studenten des neuen Semesterverbands
	 * 			jahrgang -Jahrgang des neuen Semesterverbands
	 * 			studiengang -Studiengang-Objekt dem der neue Semesterverband zugeordnet sein
	 * @return	Semesterverband-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Semesterverband anlegenSemesterverband (String anzahlStudenten, String jahrgang, Studiengang studiengang) throws RuntimeException {
		
		// Prüfung ob die Anzahl der Studenten angegeben wurde
		if (anzahlStudenten == null || anzahlStudenten.length() == 0) {
			throw new IllegalArgumentException("Bitte geben Sie die Anzahl der Studenten an!");
		}
		
		// Prüfung ob der Jahrgang angeggeben wurde
		if (jahrgang == null || jahrgang.length() == 0) {
			throw new IllegalArgumentException("Bitte geben Sie den Jahrgang an!");
		}
		
		StringBuffer tempJahrgang = new StringBuffer();
		tempJahrgang.append(jahrgang);
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde		
		if (!tempJahrgang.substring(0,2).equals("SS") && !tempJahrgang.substring(0,2).equals("WS") || !tempJahrgang.substring(2,4).equals("20")) {
			throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nBeachten Sie auch die Gross-/Kleinschreibung");			
		}
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde
		if ((tempJahrgang.substring(0,2).equals("SS") && tempJahrgang.length() != 6) || (tempJahrgang.substring(0,2).equals("WS") && tempJahrgang.length() != 9) ||
				(tempJahrgang.substring(0,2).equals("WS") && tempJahrgang.charAt(6) != new Character('/'))) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
		}
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde
		if (tempJahrgang.substring(0,2).equals("SS")) {
			try {
				Integer.parseInt(tempJahrgang.substring(4,6));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
		}
		
		// Es wird geprüft, ob der Jahrgang semantisch und syntaktisch korrekt eingegeben wurde
		if (tempJahrgang.substring(0,2).equals("WS")) {
			int vJahr;
			int nJahr;
			try {
				vJahr = Integer.parseInt(jahrgang.substring(4,6));
				nJahr = Integer.parseInt(jahrgang.substring(7,9));
			}
			catch (NumberFormatException e1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben");
			}
			if (nJahr - vJahr != 1) {
				throw new IllegalArgumentException("Ihre Jahrgangsangabe entspricht nicht den Vorgaben\nNur ein Jahreswechsel erlaubt");
			}
		}
		
		// Prüfung ob das Feld "anzahlStudenten" nur Zahlen enthält		
		if (!anzahlStudenten.matches("[1-9]|[1-9][0-9]|[1-9][0-9][0-9]")) {
			throw new IllegalArgumentException("Die Anzahl der Studenten kann sich von 1 bis 999 bewegen\n(Bitte auch keine führende Null angeben)");
		}
		
		// Prüfung ob ein Studiengang angegeben wurde
		if (studiengang == null) {
			throw new IllegalArgumentException("Bitte geben Sie einen Studiengang an");
		}
		
		// Prüfung ob es den "neuen" Semesterverband bereits in dem gemeinten Studiengang gibt
		Vector<Semesterverband> sgNachSV = this.auslesenSemesterverbaendeNachStudiengang(studiengang);
		
		for (Semesterverband sv : sgNachSV) {
			if (jahrgang.equals(sv.getJahrgang())) {
				throw new IllegalArgumentException("Dieser Semesterverband existiert bereits");
			}
		}
		
		// Neues Semesterverband-Objekt erzeugen		
		Semesterverband neuSemVerband = new Semesterverband();
		neuSemVerband.setJahrgang(jahrgang);
		neuSemVerband.setAnzahlStudenten(Integer.parseInt(anzahlStudenten));
		neuSemVerband.setStudiengang(studiengang);
		
		return this.semesterverbandMapper.insertIntoDB(neuSemVerband);
	}
	
	/**
	 * Methode um einen neuen Dozenten mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	vorname -Vorname des Dozenten
	 * 			nachname -Nachname des Dozenten
	 * 			personalnummer -Personalnummer des Dozenten
	 * 			lehrveranstaltungen -Lehrveranstaltungs-Objekte die zum Komptenzbereich des Dozenten zählen
	 * @return	Dozent-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Dozent anlegenDozent(String vorname, String nachname, String personalnummer, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException {
		
		// Prüfung ob Vor- und Nachname angegeben wurden				
		if ((vorname == null ||vorname.length() == 0) || (nachname == null || nachname.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie Vor- und Nachname an");
		}
		
		// Prüfung ob am Ende der Vor- oder Nachnamens ein Leerzeichen ist		
		if (vorname.substring(0, 1).equals(" ") || vorname.lastIndexOf(" ") == vorname.length() - 1 ||
				nachname.substring(0, 1).equals(" ") || nachname.lastIndexOf(" ") == nachname.length() - 1) {
			throw new IllegalArgumentException("Es dürfen sich vor dem Vor-bzw.Nachname keine Leerzeichen befinden");
		}
		
		// Prüfung ob Vor- oder Nachname je nicht länger als 45 Zeichen sind
		if (vorname.length() > 45 || nachname.length() > 45) {
			throw new IllegalArgumentException("Der Vor- und Nachname darf je nicht mehr als 45 Zeichen enthalten");
		}		
		
		// Prüfung des Vor- und Nachnamens auf Zahlen und bestimmte Sonderzeichen, diese sind nicht erlaubt		
		if (!vorname.matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*") || 
				!nachname.matches("[^0-9\\,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen im Vor- bzw. Nachnamen");
		}
				
		// Prüfung der Personalnummer auf fünfstellige Ziffernfolge				
		if (!personalnummer.matches("[0-9]{5}")) {
			throw new IllegalArgumentException("Die Personalnummer ist nicht fünfstellig\noder es befinden sich darin nicht erlaubte Zeichen");
		}
		
		// Prüfen ob die Personalnummer bereits vergeben ist		
		Vector<Dozent> alleDozenten = this.dozentMapper.findAll(false);
				
		for (Dozent d : alleDozenten) {
			if (new Integer(personalnummer) == d.getPersonalnummer()) {
				throw new IllegalArgumentException("Diese Personalnummer ist bereits vergeben");
			}
		}
		
		// Neues Dozent-Objekt erzeugen		
		Dozent neuDozent = new Dozent();
		neuDozent.setVorname(vorname);
		neuDozent.setNachname(nachname);
		neuDozent.setPersonalnummer(new Integer(personalnummer));
		if (lehrveranstaltungen != null && lehrveranstaltungen.size() > 0) {
			neuDozent.setLehrveranstaltungen(lehrveranstaltungen);
		}
		
		return this.dozentMapper.insertIntoDB(neuDozent);
	}
	
	/**
	 * Methode um einen neue Lehrveranstaltung mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	umfang -Umfang also die SWS der neuen Lehrveranstaltung
	 * 			bezeichnung -Bezeichnung der neuen Lehrveranstaltung
	 * 			studiensemester -Studiensemester der neuen Lehrveranstaltung
	 * 			studiengaenge -Studiengang-Objekte zu denen siche die neue Lehrveranstaltung zählen soll
	 * 			dozenten -Dozent-Objekte die die neue Lehrveranstaltung voraussichltich halten werden
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, Vector<Dozent> dozenten) throws RuntimeException {
		
		// Prüfung ob mindestens ein Studiengang angegeben wurde		
		if (studiengaenge == null || studiengaenge.size() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie mindestens einen Studiengang an");
		}
		
		// Prüfung ob eine Bezeichnung angeben wurde		
		if (bezeichnung == null || bezeichnung.length() == 0) {
			throw new IllegalArgumentException("Bitten geben Sie eine Bezeichnung an");
		}
		
		// Prüfung ob Bezeichnung nicht länger als 45 Zeichen ist
		if (bezeichnung.length() > 45) {
			throw new IllegalArgumentException("Die Bezeichnung darf nicht mehr als 45 Zeichen enthalten");
		}	
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (!bezeichnung.matches("[^,\\_\\+\\*\\/\\=\\}\\{\\[\\]\\%\\$\\§\\\"\\!\\^\\°\\<\\>\\|\\;\\:\\#\\~\\@\\€\\?\\(\\)\\²\\³]*")) {
			throw new IllegalArgumentException("Es befinden sich nicht erlaubte Zeichen in der Bezeichnung");
		}
		
		//Prüfung ob die Bezeichnung der Lehrveranstaltung syntaktisch korrekt ist
		if (bezeichnung.matches("[0-9]*") || bezeichnung.substring(0, 1).matches("[0-9]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf keine Zahl sein oder mit einer beginnen");
		}
		
		if (bezeichnung.substring(0, 1).matches("[ ]{1}") || bezeichnung.substring(0, 1).matches("[/]{1}") || bezeichnung.substring(0, 1).matches("[-]{1}")) {
			throw new IllegalArgumentException("Die Bezeichnung darf mit keinem Leerzeichen oder Sonderzeichen beginnen");
		}
		
		
		/*
		 *  Prüfung ob die Bezichung mit einem Buchstabe beginnt und ob am ende nur eine Ziffer verwendet wurde
		 *  -> Prüfung deaktiviert, da dies nicht mehr den aktuellen BusinessRules entspricht - Stand: 19.01.2014
		 */
		/*
		if (!bezeichnung.matches("[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}[ ]{0,1}[a-zA-Z]{1,30}[ ]{0,1}[0-9]{0,1}|[a-zA-Z]{1,30}")) {
			throw new IllegalArgumentException("Bitte beginnen Sie die Bezeichnung mit einem Buchstaben\n"
					+ "Bitte verwenden Sie nur ein Leerzeichen in Folge\nBitte benutzen Sie nur eine Ziffer in Folge");
		}
		*/
		
		// Prüfung ob am Ende ein Leerzeichen steht		
		if (bezeichnung.lastIndexOf(" ") == bezeichnung.length() - 1) {
			throw new IllegalArgumentException("Bitte entfernen Sie alle Leerzeichen am Ende der Bezeichnung");
		}
			
		/*
		 *  Prüfung ob das Feld "umfang" nur Zahlen enthält und nicht leer ist
		 *  -> Prüfungen deaktiviert, da keine freie Eingabe mehr möglich
		 * 	Stand: 08.01.2014
		 */
		
		/*
		if (!new Integer(umfang).toString().matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Umfang muss eine Zahl sein");
		}
	
		 *  Prüfung ob der Inahlt Feld "umfang" durch 2 teilbar ist
		 *  -> Prüfungen deaktiviert, da keine freie Eingabe mehr möglich
		 * 	Stand: 08.01.2014
		
		if ((umfang % 2) != 0) {
			throw new IllegalArgumentException("Der Umfang muss durch 2 teilbar sein");
		}
		*/
		
		/*
		 *  Prüfung ob das Feld "Studiensemester" nur Zahlen enthält und nicht leer ist
		 *  -> Prüfungen deaktiviert, da keine freie Eingabe mehr möglich
		 * 	Stand: 08.01.2014
		 *
		
		if (!new Integer(umfang).toString().matches("[0-9]{1}|[0-9]*")) {
			throw new IllegalArgumentException("Bitten geben Sie das Studiensemester an");
		}
		*/
		
		// Prüfen ob die Bezeichnung bereits vorhanden ist		
		Vector<Lehrveranstaltung> alleLVs = this.lehrveranstaltungMapper.findAll(false);
				
		for (Lehrveranstaltung l : alleLVs) {
			if (bezeichnung.equals(l.getBezeichnung())) {
				throw new IllegalArgumentException("Die Bezeichnung ist bereits vergeben, bitte ändern Sie diese");
			}
		}
		
		// Neues Lehrveranstaltung-Objekt erzeugen		
		Lehrveranstaltung neuLehrveranstaltung = new Lehrveranstaltung();
		neuLehrveranstaltung.setUmfang(umfang);
		neuLehrveranstaltung.setBezeichnung(bezeichnung);
		neuLehrveranstaltung.setStudiensemester(studiensemester);
		neuLehrveranstaltung.setStudiengaenge(studiengaenge);
		if (dozenten != null) {
			neuLehrveranstaltung.setDozenten(dozenten);
		}
		
		return this.lehrveranstaltungMapper.insertIntoDB(neuLehrveranstaltung);
		
	}
	
	/**
	 * Methode um einen neue Lehrveranstaltung mittels Mapper-Objekt in der DB zu speichern
	 * (Überladen der Methode "anlegenLehrveranstaltung(...)")
	 * 
	 * @param	umfang -Umfang also die SWS der neuen Lehrveranstaltung
	 * 			bezeichnung -Bezeichnung der neuen Lehrveranstaltung
	 * 			studiensemester -Studiensemester der neuen Lehrveranstaltung
	 * 			studiengaenge -Studiengang-Objekte zu denen siche die neue Lehrveranstaltung zählen soll
	 * @return	Lehrveranstaltung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Lehrveranstaltung anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge) throws RuntimeException {
		
		Vector<Dozent> keineDozenten = null;
		
		return this.anlegenLehrveranstaltung(umfang, bezeichnung, studiensemester, studiengaenge, keineDozenten);
	}
	
	/**
	 * Methode um einen neue Belegung mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	lehrveranstaltung -Objekt welche Inhalt des Vorlesungstermin sein soll
	 * 			raum -Objekt in dem die Vorlesung stattfindet
	 * 			zeitsot -Objekt, welches den Zeitpunkt und die Zeitspanne der Vorlesung angibt
	 * 			semesterverbaende -Semesterverband-Objekte, welche die Rezipienten der Belegung darstellen
	 * 			dozenten -Dozent-Objekte, welche diese Vorlesung referieren sollen
	 * @return	Belegung-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Belegung anlegenBelegung(Lehrveranstaltung lehrveranstaltung, Raum raum, Zeitslot zeitslot, Vector<Dozent> dozenten, Vector<Semesterverband> semesterverbaende) throws RuntimeException {
		
		// Neues Belegung-Objekt erzeugen		
		Belegung belegung = new Belegung();
		belegung.setLehrveranstaltung(lehrveranstaltung);
		belegung.setRaum(raum);
		belegung.setZeitslot(zeitslot);
		belegung.setDozenten(dozenten);
		belegung.setSemesterverbaende(semesterverbaende);
		
		// Prüfung ob ein Dozent mehrmals ausgewählt wurde		
		if(belegung.getDozenten().size() > 1) {
			for (int i = 0; i < belegung.getDozenten().size(); i++) {
				int multiplzitaetDozent = 0;
				for (int j = 0; j < belegung.getDozenten().size(); j++) {
					if (belegung.getDozenten().elementAt(i).getId() == belegung.getDozenten().elementAt(j).getId()) {
						multiplzitaetDozent = multiplzitaetDozent + 1;
					}
				}
				if(multiplzitaetDozent > 1) {
					throw new RuntimeException("Bitte fügen Sie jeden Dozenten nur einmal zu einer Belegung");
				}
			}
		}
		
		// Prüfen ob der Raum genügend Kapazität aufweist für die referenzierten Semesterverbände		
		int studenten = 0;
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			studenten = studenten + belegung.getSemesterverbaende().elementAt(i).getAnzahlStudenten();
		}
		if (belegung.getRaum().getKapazitaet() < studenten) {
			throw new RuntimeException("Der gewuenschte Raum hat nicht genuegend Plätze");
		}
		
		// Prüfung ob das Studiensemester der gewünschten Lehrveranstaltung mit dem der Semesterverbände vereinbar ist		
		Integer semesterAlt = null;
		Integer semesterNeu = null;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			
			Semesterverband tempSemVerband = belegung.getSemesterverbaende().elementAt(i);
			
			Date datum = new Date();
			SimpleDateFormat datumFormat = new SimpleDateFormat("dd.MM.yyyy");
				
			String monatJahr = datumFormat.format(datum).toString();
			
			int semMonat = 0;
			int semJahr = 0;
			int aktMonat = Integer.parseInt(monatJahr.substring(3, 5));
			int aktJahr = Integer.parseInt(monatJahr.substring(6, 10));
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("WS")) {
				semMonat = 9;
			}
			
			if (tempSemVerband.getJahrgang().substring(0, 2).equals("SS")) {
				semMonat = 3;
			}
			
			semJahr = Integer.parseInt(tempSemVerband.getJahrgang().substring(2, 6));
			
			int jahresDiff = aktJahr - semJahr;
			
			int[] calendar = {1,2,3,4,5,6,7,8,9,10,11,12};

			
			
			if (!(aktMonat < semMonat && aktJahr <= semJahr)) {
							
				int zaehler = 0;
				
				for (int j = semMonat-1; j < calendar.length; j++ ) {
					
					zaehler++;
					
					if((calendar[j] == aktMonat) && (jahresDiff == 0)) {
						break;
					}
					if(calendar[j] == 12) {
						j = -1;
						jahresDiff--;
					}
				}
				
				int studienSem = zaehler / 6;
				int studienSemMonat = zaehler;
				
				if (zaehler != 0) {
					studienSemMonat = zaehler % 6;
				}
				
				if (studienSem == 0) {
					semesterAlt = 1;
					semesterNeu = 0;
				}
				
				else if (studienSemMonat == 0) {
					semesterAlt = studienSem;
					semesterNeu = studienSem + 1;
				}
										
				else {
					semesterAlt = studienSem + 1;
				}
			}
			else {
				semesterAlt = 1;
			}
		}
		
		if (semesterNeu == null || semesterNeu == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
			}
		}
		
		else if (semesterAlt == null || semesterAlt == 0) {
			if (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
			}
		}		
		
		else {
			if ((belegung.getLehrveranstaltung().getStudiensemester() != semesterAlt) && (belegung.getLehrveranstaltung().getStudiensemester() != semesterNeu)) {
				throw new RuntimeException("Diese Lehrveranstaltung ist für ein anderes Studiensemester vorgesehen");
			}
		}
				
		
		
		// Prüfen ob der Raum zum gewünschten Zeitslot schon noch verfügbar ist
		
		/*
		 * Prüfung deaktiviert, da dem Client nur freie Räume zur Verfügung gestellt werden
		 * bei einem verteilten Arbeiten muss diese Prüfung wieder aktiviert werden um eine Konsistenz zu gewährleisten
		 * Stand: 07.01.2014
		
		Vector<Belegung> raumBelegungen = this.belegungMapper.findByRaum(belegung.getRaum());
		if (raumBelegungen != null) {
			for (int i = 0; i < raumBelegungen.size(); i++) {
				if (belegung.getZeitslot().getId() == raumBelegungen.elementAt(i).getZeitslot().getId()) {
					throw new RuntimeException("Der gewönschte Raum ist zum gewönschten Zeitslot schon belegt");
				}
			}
		}
		*/
		
		// Prüfen ob die Semesterverbände zum gewünschten Zeitslot verfügbar sind		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> semVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			if (semVerBelegungen != null) {
				for (int j = 0; j < semVerBelegungen.size(); j++) {
					if (belegung.getZeitslot().getId() == semVerBelegungen.elementAt(j).getZeitslot().getId()) {
						throw new RuntimeException("Ein Semesterverband ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		
		// Prüfen ob der Dozent zum gewünschten Zeitslot verfügbar ist		
		for (int i = 0; i < belegung.getDozenten().size(); i++) {
			Vector<Belegung> dozentenBelegungen = this.belegungMapper.findByDozent(belegung.getDozenten().elementAt(i));
			if (dozentenBelegungen != null) {
				for (int j = 0; j < dozentenBelegungen.size(); j++) {
					if (belegung.getZeitslot().getId() == dozentenBelegungen.elementAt(j).getZeitslot().getId()) {
						throw new RuntimeException("Ein Dozent ist zu diesem Zeitpunkt bereits eingeteilt");
					}
				}
			}
		}
		
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(belegung.getLehrveranstaltung().getId());
		
		// Prüfung ob sich die Lehrveranstaltung und die Semesterverbände im gleichen Studiengang sind		
		Lehrveranstaltung tempLehrveranstaltung = this.lehrveranstaltungMapper.findByKey(vi, true).elementAt(0);
		
		boolean check = false;
		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			check = false;
			for (int j = 0; j < tempLehrveranstaltung.getStudiengaenge().size(); j++) {
				if (belegung.getSemesterverbaende().elementAt(i).getStudiengang().getId() == tempLehrveranstaltung.getStudiengaenge().elementAt(j).getId()) {
					check = true;
					break;
				}
			}
		}
		
		if (!check) {
			throw new RuntimeException("Lehrveranstaltung und Semesterverband befinden sich nicht im gleichen Studiengang");
		}
			
		// Prüfung ob der Umfang (SWS) einer Lehrveranstaltung für einen Semesterverband bereits erreicht wurde		
		for (int i = 0; i < belegung.getSemesterverbaende().size(); i++) {
			Vector<Belegung> tempSemVerBelegungen = this.belegungMapper.findBySemesterverband(belegung.getSemesterverbaende().elementAt(i));
			int countSWS = 0;
			if (tempSemVerBelegungen != null) {
				for (int j = 0; j < tempSemVerBelegungen.size(); j++) {
					if(belegung.getLehrveranstaltung().getId() == tempSemVerBelegungen.elementAt(j).getLehrveranstaltung().getId()) {
						countSWS = countSWS + 2;
					}
				}
			}
			if (belegung.getLehrveranstaltung().getUmfang() < (countSWS + 2)) {
				throw new RuntimeException("Der vorgesehene Umfang (SWS) der Lehrveranstaltung wurde für Semesterverband " + belegung.getSemesterverbaende().elementAt(i).getStudiengang().getKuerzel() + " " + belegung.getSemesterverbaende().elementAt(i).getJahrgang() + " bereits erreicht");
			}
		}
		
		return this.belegungMapper.insertIntoDB(belegung);
				
	}
	
	/**
	 * Methode um einen neuen Studiengang mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	bezeichnung -Bezeichnung des neuen Studiengangs
	 * 			kuerzel -Kürzel des neuen Studiengangs
	 * 			lehrveranstaltungen -Lehrveranstaltung-Objekte welche dem neuen Studiengang zugeordnet sein sollen
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Studiengang anlegenStudiengang(String bezeichnung, String kuerzel, Vector<Lehrveranstaltung> lehrveranstaltungen) throws RuntimeException {
		
		// Prüfung ob Bezeichung und Kürzel angegeben wurden		
		StringBuffer tempBezeichnung = new StringBuffer();
		tempBezeichnung.append(bezeichnung);
		StringBuffer tempKuerzel = new StringBuffer();
		tempKuerzel.append(kuerzel);
		
		if ((tempBezeichnung.length() == 0) || (tempKuerzel.length() == 0)) {
			throw new IllegalArgumentException("Bitte geben Sie die Bezeichnung und das Kürzel an");
		}
		
		// Prüfung ob Bezeichnung nicht länger als 45 Zeichen ist
		if (tempBezeichnung.length() > 45) {
			throw new IllegalArgumentException("Die Bezeichnung darf nicht mehr als 45 Zeichen enthalten");
		}		
		
		// Prüfung ob das Kürzel den Restriktionen entspricht
		if (!kuerzel.matches("[A-Z]{2,4}|[A-Z]{2,4}[-]([0-9]|[1][0-9]|[2][0])")) {
			throw new IllegalArgumentException("Das Kürzel muss anfangs 2 bis 4 Großbuchstaben enthalten und darf am Ende eine Zahl"
					+ " von 1 bis 20 mit vorhergehendem Bindestrich enthalten\nUmlaute sind nicht gestattet");
		}
		
		// Prüfen ob die Bezeichnung und/oder das Kürzel bereits vergeben sind		
		Vector<Studiengang> alleSGs = this.studiengangMapper.findAll(false);
		
		for (Studiengang s : alleSGs) {
			if(bezeichnung.equals(s.getBezeichnung()) || kuerzel.equals(s.getKuerzel())) {
				throw new IllegalArgumentException("Die Bezeichnung und/oder das Kürzel sind bereits vergeben");
			}
		}
		
		// Neues Studiengang-Objekt erzeugen		
		Studiengang neuStudiengang = new Studiengang();
		neuStudiengang.setBezeichnung(bezeichnung);
		neuStudiengang.setKuerzel(kuerzel);
		if (lehrveranstaltungen != null) {
			neuStudiengang.setLehrveranstaltungen(lehrveranstaltungen);
		}
		
		return this.studiengangMapper.insertIntoDB(neuStudiengang);
		
	}
	
	/**
	 * Methode um einen neuen Studiengang mittels Mapper-Objekt in der DB zu speichern
	 * (Überladen der Methode "anlegenStudiengang(...)")
	 * 
	 * @param	bezeichnung des neuen Studiengangs
	 * 			kuerzel des neuen Studiengangs
	 * @return	Studiengang-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Studiengang anlegenStudiengang(String bezeichnung, String kuerzel) throws RuntimeException {
		
		Vector<Lehrveranstaltung> keineLV = null;
		
		return anlegenStudiengang(bezeichnung, kuerzel, keineLV);
		
	}
	
	/**
	 * Methode um einen neuen Raum mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	bezeichnung des neuen Raumes
	 * 			kapazitaet des neuen Raumes
	 * @return	Raum-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Raum anlegenRaum(String bezeichnung, String kapazitaet) throws RuntimeException {
		
		// Prüfung ob Bezeichung und Kapazität angegeben wurden					
		if (((bezeichnung == null) || (bezeichnung.length() == 0)) || ((kapazitaet == null) || (kapazitaet.length() == 0))) {
			throw new IllegalArgumentException("Bitte geben Sie eine Bezeichnung und die Kapazität an");
		}
		
		StringBuffer tempBezeichnung = new StringBuffer();
		tempBezeichnung.append(bezeichnung);
		StringBuffer tempKapazitaet = new StringBuffer();
		tempKapazitaet.append(kapazitaet);
		
		// Prüfung ob die Bezeichnung den Restriktionen entspricht
		if (!bezeichnung.matches("[W]{1}[0-9]{3}|[W]{1}[-]{1}[N]{1}[0-9]{3}|[N]{1}[0-9]{3}")) {
			throw new IllegalArgumentException("Diese Bezeichnung entspricht nicht den Vorgaben");
		}
		
		// Prüfung ob die Kapazität gleich 0 ist und ob ausschließlich Zahlen angegeben wurden
		try {
			if (Integer.parseInt(kapazitaet) == 0) {
				throw new IllegalArgumentException("Die Kapazität darf nicht 0 sein");
			}
		}
		catch (NumberFormatException e) {
			throw new IllegalArgumentException("Bitte geben Sie nur Zahlen für die Kapazität ein");
		}
		
		if (!new Integer(Integer.parseInt(kapazitaet)).toString().matches("[1-9]|[1-9][0-9]|[1-9][0-9][0-9]")) {
			throw new IllegalArgumentException("Die Kapazität muss 1 bis 999 betragen");
		}
		
		Raum neuRaum = new Raum();
		neuRaum.setBezeichnung(bezeichnung);
		neuRaum.setKapazitaet(Integer.parseInt(kapazitaet));
		
		
		// Prüfen ob ein Raum mit der identischen Bezeichnung bereits existiert		
		Vector<Raum> alleRaeume = raumMapper.findAll();
		
		for (Raum r : alleRaeume) {
			if (bezeichnung.equals(r.getBezeichnung())) {
				throw new IllegalArgumentException("Der Raum existiert bereits");
			}
		}
		
		return this.raumMapper.insertIntoDB(neuRaum);
		
	}
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um die vom Client gewünschten neuen BusinessObjects zur erstellen
	 * 				und zu speichern
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode welche mittelbar und auf "Wunsch" des Clients, die DB-Verbindung kappt
	 * 
	 * @throws 	RuntimeException, welche von DBConnection.closeConnection();
	 * 			weitergereicht wird und hier ebenfalls wieder weitergegeben
	 * 			wird.
	 */
	public void closeConnection() throws RuntimeException {
		DBConnection.closeConnection();
	}
	
	
}
