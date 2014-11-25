package de.hdm.clicker.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
 * Das asynchrone Gegenstück des Interface {@link Verwaltung}. 
 * 
 * @author Thies, Moser, Sonntag, Zanella
 * @version 1
 */
public interface VerwaltungAsync {
		
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Admin
	 * ***********************************************************************************************
	 */
	void auslesenAlleLecturer(AsyncCallback<Vector<Lecturer>> callback) throws RuntimeException;
	
	void auslesenLecturer(Lecturer lecturer, AsyncCallback<Vector<Lecturer>> callback) throws RuntimeException;
	
	void aendernLecturer(Lecturer lecturer, AsyncCallback<Lecturer> callback) throws RuntimeException;
	
	void loeschenLecturer(Lecturer lecturer, AsyncCallback<Void> callback) throws RuntimeException;
	
	void anlegenLecturer(String user, String password, String firstName, String name, AsyncCallback<Lecturer> callback) throws RuntimeException;
	
	void adminAuthenticate(String password, AsyncCallback<Boolean> callback) throws RuntimeException;
		
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
	
	void lecturerAuthenticate(String user, String password, AsyncCallback<Boolean> callback) throws RuntimeException;
	
	void getSignedLecturer(AsyncCallback<Lecturer> callback) throws RuntimeException;
	
	void aendernCategory(Category category, AsyncCallback<Category> callback) throws RuntimeException;
	
	void auslesenCategory(Category category, AsyncCallback<Vector<Category>> callback) throws RuntimeException;	

	void auslesenAlleCategoriesByLecturer(AsyncCallback<Vector<Category>> callback) throws RuntimeException;
	
	void loeschenCategory(Category category, AsyncCallback<Void> callback) throws RuntimeException;
	
	void anlegenCategory(String description, AsyncCallback<Category> callback) throws RuntimeException;
	
	
	void auslesenAlleSemesterverbaende(AsyncCallback<Vector<Semesterverband>> callback) throws RuntimeException;
	
	void auslesenSemesterverbaendeNachStudiengang(Studiengang sg, AsyncCallback<Vector<Semesterverband>> callback) throws RuntimeException;
	
	void auslesenSemesterverband(Semesterverband sv, AsyncCallback<Vector<Semesterverband>> callback) throws RuntimeException;

	void auslesenAlleDozenten(AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;
	
	void auslesenDozentenNachLV(Lehrveranstaltung lv, AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;
	
	void auslesenDozentenNachZeitslot(Zeitslot lv, AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;
	
	void auslesenDozent(Dozent dozent, AsyncCallback<Vector<Dozent>> callback) throws RuntimeException;

	void auslesenAlleZeitslots(AsyncCallback<Vector<Zeitslot>> callback) throws RuntimeException;

	void auslesenAlleLehrveranstaltungen(AsyncCallback<Vector<Lehrveranstaltung>> callback) throws RuntimeException;
	
	void auslesenLehrveranstaltung(Lehrveranstaltung lv, AsyncCallback<Vector<Lehrveranstaltung>> callback) throws RuntimeException;
	
	void auslesenLehrveranstaltungenNachSV(Semesterverband sv, Studiengang sg, AsyncCallback<Vector<Lehrveranstaltung>> callback) throws RuntimeException;
	
	void auslesenLehrveranstaltungenNachSG(Studiengang sg, AsyncCallback<Vector<Lehrveranstaltung>> callback) throws RuntimeException;

	void auslesenAlleBelegungen(AsyncCallback<Vector<Belegung>> callback) throws RuntimeException;
	
	void auslesenBelegung(Belegung belegung, AsyncCallback<Vector<Belegung>> callback) throws RuntimeException;
	
	void auslesenBelegungenNachSV(Semesterverband semesterverband, AsyncCallback<Vector<Belegung>> callback) throws RuntimeException;

	void auslesenAlleStudiengaenge(AsyncCallback<Vector<Studiengang>> callback) throws RuntimeException;
	
	void auslesenStudiengang(Studiengang studiengang, AsyncCallback<Vector<Studiengang>> callback) throws RuntimeException;
	
	void auslesenAlleStudiengaengeOhneSVuLV(AsyncCallback<Vector<Studiengang>> callback) throws RuntimeException;

	void auslesenAlleRaeume(AsyncCallback<Vector<Raum>> callback) throws RuntimeException;
	
	void auslesenRaum(Raum raum, AsyncCallback<Vector<Raum>> callback) throws RuntimeException;
	
	void auslesenVerfuegbareRaeumeZuZeitslotuSV(Zeitslot zeitslot, Vector<Semesterverband> sv, AsyncCallback<Vector<Raum>> callback) throws RuntimeException;
	
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

	void loeschenSemesterverband(Semesterverband semesterverband, AsyncCallback<Void> callback) throws RuntimeException;

	void loeschenDozent(Dozent dozent, AsyncCallback<Void> callback) throws RuntimeException;

	void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung, AsyncCallback<Void> callback) throws RuntimeException;

	void loeschenBelegungen(Belegung belegung, Semesterverband semesterverband, AsyncCallback<Void> callback) throws RuntimeException;

	void loeschenStudiengang(Studiengang studiengang, AsyncCallback<Void> callback) throws RuntimeException;

	void loeschenRaum(Raum raum, AsyncCallback<Void> callback) throws RuntimeException;
	
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

	void aendernSemesterverband(Semesterverband semesterverband, AsyncCallback<Semesterverband> callback) throws RuntimeException;
	
	void aendernDozent(Dozent dozent, AsyncCallback<Dozent> callback) throws RuntimeException;

	void aendernLehrveranstaltung(Lehrveranstaltung lehrveranstaltung, AsyncCallback<Lehrveranstaltung> callback) throws RuntimeException;

	void aendernBelegung(Belegung belegung, AsyncCallback<Belegung> callback) throws RuntimeException;

	void aendernStudiengang(Studiengang studiengang, AsyncCallback<Studiengang> callback) throws RuntimeException;

	void aendernRaum(Raum raum, AsyncCallback<Raum> callback) throws RuntimeException;
	
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

	void anlegenSemesterverband(String anzahlStudenten, String jahrgang, Studiengang studiengang, AsyncCallback<Semesterverband> callback) throws RuntimeException;

	void anlegenDozent(String vorname, String nachname, String personalnummer, Vector<Lehrveranstaltung> lehrveranstaltungen, AsyncCallback<Dozent> callback) throws RuntimeException;

	void anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, Vector<Dozent> dozenten, AsyncCallback<Lehrveranstaltung> callback) throws RuntimeException;

	void anlegenLehrveranstaltung(int umfang, String bezeichnung, int studiensemester, Vector<Studiengang> studiengaenge, AsyncCallback<Lehrveranstaltung> callback) throws RuntimeException;

	void anlegenBelegung(Lehrveranstaltung lehrveranstaltung, Raum raum, Zeitslot zeitslot, Vector<Dozent> dozenten, Vector<Semesterverband> semesterverbaende,	AsyncCallback<Belegung> callback) throws RuntimeException;

	void anlegenStudiengang(String bezeichnung, String kuerzel,	Vector<Lehrveranstaltung> lehrveranstaltungen, AsyncCallback<Studiengang> callback) throws RuntimeException;

	void anlegenStudiengang(String bezeichnung, String kuerzel,	AsyncCallback<Studiengang> callback) throws RuntimeException;

	void anlegenRaum(String bezeichnung, String kapazitaet, AsyncCallback<Raum> callback) throws RuntimeException;

	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Methoden um die vom Client gewünschten neuen BusinessObjects zur erstellen
	 * 				und zu speichern
	 * ***********************************************************************************************
	 */	
	
	void closeConnection(AsyncCallback<Void> callback) throws RuntimeException;

}
