package de.hdm.clicker.server;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import de.hdm.clicker.server.db.*;
import de.hdm.clicker.shared.Verwaltung;
import de.hdm.clicker.shared.bo.*;

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
	   * Referenzen auf den bereits instantiierten DatenbankMappern, der Businessobjekte 
	   * mit der Datenbank abgleicht.
	   */
	public LecturerMapper lecturerMapper = LecturerMapper.lecturerMapper();
	public CategoryMapper categoryMapper = CategoryMapper.categoryMapper();
	public QuestionMapper questionMapper = QuestionMapper.questionMapper();
	public QuizMapper quizMapper = QuizMapper.quizMapper();
	public ResultsMapper resultsMapper = ResultsMapper.resultsMapper();
	public QuizPackageMapper quizPackageMapper = QuizPackageMapper.quizPackageMapper();
	
	/**
	 * Flag, welche angibt, ob sich der Admin bereits authentifiziert hat
	 */
	private boolean isAdmin = false;

	/**
	 * Der aktuell angemeldete Lecturer
	 */
	private Lecturer signedInLecturer = null;
	
	/**
	 * Der aktuell angemeldete Participant
	 */
	private String signedInParticipant = null;
	
	TempDatabase tempDB = null;
	DBConnection dbcon = new DBConnection();
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services - Userunabhängig
	 * ***********************************************************************************************
	 */
	
	public void preloadQuizPackage(Quiz quiz) throws RuntimeException {
		for(QuizPackage q : TempDatabase.getQpv()) {
			if(q.getId() == quiz.getId() && q.getQuiz().getVersion() == quiz.getVersion()) {
				return;
			}
		}
		QuizPackage qp = this.quizPackageMapper.findByQuiz(quiz);
		TempDatabase.getQpv().add(qp);
	}
	
	
	public Vector<Question> loadQuestions(Quiz quiz) throws RuntimeException {
		for (QuizPackage qp : TempDatabase.getQpv()) {
			if (qp.getId() == quiz.getId() && qp.getQuiz().getVersion() == quiz.getVersion()) {
				Vector<Question> v = new Vector<Question>(qp.getQuestionHT().values());
				return v;
			}
		}
		return null;
	}
	
	/**
	 * Methode um alle Quizze bei Applikations-Start zu öffnen bzw. zu schließen
	 * 
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void checkQuizzes() throws RuntimeException {
		tempDB = TempDatabase.get();
		closeOverdueQuizzes();
		openAutoQuizzes();
		//if (!loaded) {
			loadActiveQuizzes();
			loaded = true;
		//}
	}
	
	
	/**
	 * Flag zur Steuerung des Ladens von aktiven Quizzen in die TempDatabase
	 */
	private static boolean loaded = false;

	public void closeOverdueQuizzes() throws RuntimeException {
		Vector<Quiz> startedQuizzes = this.quizMapper.findAllStarted();
		for (Quiz q : startedQuizzes) {			
			int timeNow;
			//Winterzeit
			if(new Integer(new SimpleDateFormat("MMdd").format(new Date())) < 329 || new Integer(new SimpleDateFormat("MMdd").format(new Date())) > 1024) {
				timeNow = ((new Integer(new SimpleDateFormat("HH").format(new Date()))*60*60) + 3600) + (new Integer(new SimpleDateFormat("mm").format(new Date()))*60)
						+ (new Integer(new SimpleDateFormat("ss").format(new Date())));
			}
			//Sommerzeit
			else {
				timeNow = (new Integer(new SimpleDateFormat("HH").format(new Date()))*60*60) + (new Integer(new SimpleDateFormat("mm").format(new Date()))*60)
						+ (new Integer(new SimpleDateFormat("ss").format(new Date())));
			}
			int timeQuiz = q.getStartTime() * 60;
			Integer timeGap = timeNow - timeQuiz;
			if (q.getDurationButton() > 0) {
				if (timeGap > q.getDurationButton()) {
					q.setStarted(false);
					q.setActive(false);
					this.quizMapper.startUpdate(q);
				}
			}
			else {
				if (timeGap > (q.getQuestionsCount() * q.getDurationQuestion())) {
					q.setStarted(false);
					q.setActive(false);
					this.quizMapper.startUpdate(q);
				}
			}
		}
	}
	
	public void openAutoQuizzes() throws RuntimeException {
		Vector<Quiz> startedQuizzes = this.quizMapper.findAllActiveAndAuto();
		for (Quiz q : startedQuizzes) {
			int timeNow;
			//Winterzeit
			if(new Integer(new SimpleDateFormat("MMdd").format(new Date())) < 329 || new Integer(new SimpleDateFormat("MMdd").format(new Date())) > 1024) {
				timeNow = ((new Integer(new SimpleDateFormat("HH").format(new Date()))*60*60) + 3600) + (new Integer(new SimpleDateFormat("mm").format(new Date()))*60)
						+ (new Integer(new SimpleDateFormat("ss").format(new Date())));
			}
			//Sommerzeit
			else {
				timeNow = (new Integer(new SimpleDateFormat("HH").format(new Date()))*60*60) + (new Integer(new SimpleDateFormat("mm").format(new Date()))*60)
						+ (new Integer(new SimpleDateFormat("ss").format(new Date())));
			}
			
			int timeQuiz = q.getStartTime() * 60;
			Integer timeGap = timeNow - timeQuiz;
			if (q.getDurationButton() > 0) {
				if (timeGap > 0 && timeGap < q.getDurationButton()) {
					q.setStarted(true);
					this.quizMapper.startUpdate(q);
					this.anlegenNewQuizVersion(q);
				}
			}
			else {
				if (timeGap > 0 && timeGap < (q.getQuestionsCount() * q.getDurationQuestion())) {
					q.setStarted(true);
					this.quizMapper.startUpdate(q);
					this.anlegenNewQuizVersion(q);
				}
			}
		}
	}
	
	public void loadActiveQuizzes() throws RuntimeException {
		this.tempDB.setActiveQuizVector(this.quizMapper.findAllActive());
	}
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services - Userunabhängig
	 * ***********************************************************************************************
	 */
	
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
			//Prüfung ob noch aktive Questions der Category zugeorndet sind
			Vector<Question> vq = this.questionMapper.findByCategory(category);
			for(Question q : vq) {
				if (q.isActive()) {
					throw new RuntimeException("Dieser Kategorie sind noch aktive Fragen zueordnet\n"
							+ "bitte löschen Sie diese Fragen zuerst");
				}
			}
			//Falls keine aktive Question zugeordnet ist, wird die Kategorie gelöscht
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
	 * Methode um eine neue Question mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	body der neuen Question
	 * 			answer1
	 * 			answer2
	 * 			answer3
	 * 			answer4
	 * 			severity
	 * 			categoryID 
	 * @return	Question-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Question anlegenQuestion(String body, String answer1, String answer2, String answer3, String answer4, int severity, int categoryID) throws RuntimeException {
		if (signedInLecturer != null) {
			
			Question newQuestion = new Question();
			newQuestion.setQuestionBody(body);
			newQuestion.setAnswer1(answer1);
			newQuestion.setAnswer2(answer2);
			newQuestion.setAnswer3(answer3);
			newQuestion.setAnswer4(answer4);
			newQuestion.setSeverity(severity);
			newQuestion.setCategoryID(categoryID);
			newQuestion.setActive(true);
			newQuestion.setImage(false);
			
			return this.questionMapper.insertIntoDB(newQuestion);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um eine Question abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	question - Objekt welches geändert werden sollen
	 * @return	Question-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 */
	public Question aendernQuestion(Question question) throws RuntimeException {
		if (signedInLecturer != null) {
			// Falls die Frage nun kein Bild mehr referenzieren soll, wird dieses gelöscht
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(question.getId());
			if (this.questionMapper.findByKey(vi).elementAt(0).isImage() && !question.isImage()) {
				this.questionMapper.deleteImage(question);
			}
			// Update der Question
			Question tmpQ = this.questionMapper.update(question);
			// Ggf. TempDatabase aktualisieren
			for(int i = 0; i < TempDatabase.getQpv().size(); i++) {
				if (TempDatabase.getQpv().elementAt(i).getQuestionHT().containsKey(new Integer(question.getId()))) {
					TempDatabase.getQpv().setElementAt(this.quizPackageMapper.findByQuiz(TempDatabase.getQpv().elementAt(i).getQuiz()), i);
				}
			}
			return tmpQ;
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um eine bestimmte Question zu löschen
	 * 
	 * @param	question -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenQuestion(Question question) throws RuntimeException {
		if (signedInLecturer != null) {
			
			/*
			 * Prüfung ob die Question einem Quiz (jüngste Version) zugeordnet ist,
			 * im Falle "ja" kann Sie zunächst nicht gelöscht werden
			 */
			String quizzes2del = null;		
			Vector<Quiz> vq = this.quizMapper.findByQuestion(question);
			if (vq != null && vq.size() > 0) {
				for (Quiz q: vq) {
					quizzes2del = quizzes2del + q.getId() + " " + q.getDescription() + "\n";
				}
				throw new RuntimeException("Bitte heben Sie zuerst die Zuordnung der Frage zu folgenden Quizze auf:\n" + quizzes2del);
			}
			
			// Das zugehörige Image wird in jedem Fall gelöscht
			if (question.isImage()) {
				this.questionMapper.deleteImage(question);
			}
			/*
			 *  Falls es bereits Results zu dieser Question gibt,
			 *  wird sie "nur" auf inaktiv gesetzt, andernfalls 
			 *  kann Sie komlett aus der DB entfernt werden	
			 */
			if (this.resultsMapper.countByQuestion(question) > 0) {
				question.setActive(false);
				this.questionMapper.update(question);
			}
			else {
				this.questionMapper.delete(question);
			}
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um alle Questions zu einem Category mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Question> auslesenAlleQuestionsByCategory(Category cat) throws RuntimeException {
		if (signedInLecturer != null) {
			return questionMapper.findByCategory(cat);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um alle Questions zu einer Quiz mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Question> auslesenAlleQuestionsByQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			return questionMapper.findByQuiz(quiz);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um alle Questions zu einer Category und einer entsprechenden Schrierigkeitsstufe
	 * mittels einem Mapper-Objekt dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Question> auslesenAlleQuestionsByCategoryAndSeverity(Category cat, int severity) throws RuntimeException {
		if (signedInLecturer != null) {
			return questionMapper.findByCategoryAndSeverity(cat,severity);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um eine Question erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	question - Question-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einer Question
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Question> auslesenQuestion(Question question) throws RuntimeException {
		if (signedInLecturer != null) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(question.getId());
			return questionMapper.findByKey(vi);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um ein Quiz abgeändert mittels Mapper-Objekt in der DB zu überspeichern
	 * 
	 * @param	quiz - Objekt welches geändert werden sollen
	 * 			vq - Vector mit den zugeordneten Questions
	 * @return	Quiz-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 */
	public Quiz aendernQuiz(Quiz quiz, Vector<Question> vq) throws RuntimeException {
		if (signedInLecturer != null) {
			
			if (vq != null) {
				quiz.setQuestionsCount(vq.size());
			}
			
			Quiz q = this.quizMapper.update(quiz, vq);
			loadActiveQuizzes();
			// Ggf. TempDatabase aktualisieren
			for(int i = 0; i < TempDatabase.getQpv().size(); i++) {
				if (TempDatabase.getQpv().elementAt(i).getId() == quiz.getId() && TempDatabase.getQpv().elementAt(i).getQuiz().getVersion() == quiz.getVersion()) {
					TempDatabase.getQpv().setElementAt(this.quizPackageMapper.findByQuiz(TempDatabase.getQpv().elementAt(i).getQuiz()), i);
				}
			}
			return q;
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um ein Quiz abgeändert mittels Mapper-Objekt in der DB zu überspeichern,
	 * die hier vorgenommenen Attributändeungen entrsprechen einem gesarteten Quiz
	 * 
	 * @param	quiz - Objekt welches geändert werden sollen
	 * @return	Quiz-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 */
	public Quiz startenQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			
			return this.quizMapper.startUpdate(quiz);
			
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um eine neues Quiz mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	passwort der neuen Question
	 * 			buttonDuration
	 * 			description
	 * 			questionDuration
	 * 			startDate
	 * 			startTime
	 * 			active 
	 * 			automatic
	 * 			random
	 * @return	Quiz-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Quiz anlegenQuiz(String passwort, int buttonDuration, String description, int questionDuration, 
			int startDate, int startTime, boolean active, boolean automatic, boolean random, Vector<Question> vq) throws RuntimeException {
		if (signedInLecturer != null) {
			
			Quiz newQuiz = new Quiz();
			newQuiz.setPassword(passwort);
			newQuiz.setDurationButton(buttonDuration);
			newQuiz.setDescription(description);
			newQuiz.setDurationQuestion(questionDuration);
			newQuiz.setStartDate(startDate);
			newQuiz.setStartTime(startTime);
			newQuiz.setActive(active);
			newQuiz.setActive(automatic);
			newQuiz.setRandom(random);
			newQuiz.setLecturerID(signedInLecturer.getId());
			newQuiz.setVersion(1);
			
			if(vq != null) {
				newQuiz.setQuestionsCount(vq.size());
			}
			
			newQuiz = this.quizMapper.firstInsertIntoDB(newQuiz, vq);
			
			loadActiveQuizzes();
			return newQuiz;
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um eine neue Quiz-Version mittels Mapper-Objekt in der DB zu speichern
	 * 
	 * @param	quiz- Objekt
	 * @return	Quiz-Objekt (falls keine semantischen Fehler auftraten)
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht.
	 * 			Außerdem erzeugen semantische Fehler Instanzen von IllegalArgumentException,
	 * 			welche ebenfalls an den Client weitergereicht werden 
	 */
	public Quiz anlegenNewQuizVersion(Quiz quiz) throws RuntimeException {
		
		quiz.setStarted(false);
		quiz.setActive(false);
		
		return this.quizMapper.insertIntoDB(quiz);		
	}
	
	/**
	 * Methode um ein Quz erneut anhand "sich selbst" dem Client zur Verfügung zu stellen
	 * 
	 * @param	quiz - Quiz-Objekt welches erneut ausgelesen werden sollen
	 * @return	Vector mit einem Quiz
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Quiz> auslesenQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			Vector<Integer> vi = new Vector<Integer>();
			vi.add(quiz.getId());
			return quizMapper.findByKeyHV(vi);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um ein bestimmtes Quiz zu löschen
	 * 
	 * @param	quiz -Objekt welches gelöscht werden sollen
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public void loeschenQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			
			this.quizMapper.delete(quiz);
			loadActiveQuizzes();
			// Ggf. TempDatabase aktualisieren
			for(int i = 0; i < TempDatabase.getQpv().size(); i++) {
				if (TempDatabase.getQpv().elementAt(i).getId() == quiz.getId() && TempDatabase.getQpv().elementAt(i).getQuiz().getVersion() == quiz.getVersion()) {
					TempDatabase.getQpv().removeElementAt(i);
				}
			}
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}
	}
	
	/**
	 * Methode um alle Quizze eines Lecturers dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Quiz> auslesenAlleQuizzeByLecturer() throws RuntimeException {
		if (signedInLecturer != null) {
			return quizMapper.findByLecturer(signedInLecturer);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um alle aktiven Quizze eines Lecturers dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Quiz> auslesenAlleQuizzeByLecturerAndActive() throws RuntimeException {
		if (signedInLecturer != null) {
			return quizMapper.findByLecturerAndActive(signedInLecturer);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um alle Startdaten von Quizze eines Lecturers dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Integer
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Integer> auslesenAlleQuizStartdatenByLecturer() throws RuntimeException {
		if (signedInLecturer != null) {
			return quizMapper.findDatesByLecturer(signedInLecturer);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um Quizze eines Lecturers anhand einem Startdatum dem Client zur Verfügung zu stellen
	 * 
	 * @param	int -Date 
	 * @return	Vector mit Quizzes
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Quiz> auslesenAlleQuizByLecturerAndStartdate(int date) throws RuntimeException {
		if (signedInLecturer != null) {
			return quizMapper.findByLecturerAndDate(signedInLecturer, date);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um ChartInfos anhand eines Quizzes dem Client zur Verfügung zu stellen
	 * 
	 * @param	quiz -Objekt 
	 * @return	Vector mit ChartInfos
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<ChartInfo> auslesenChartInfoByQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			return this.resultsMapper.findByQuizReport(quiz);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	/**
	 * Methode um Results in Form eines CSV-Strings anhand eines Quizzes dem Client zur 
	 * Verfügung zu stellen
	 * 
	 * @param	quiz -Objekt 
	 * @return	String
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public String auslesenCSVDataByQuiz(Quiz quiz) throws RuntimeException {
		if (signedInLecturer != null) {
			return this.resultsMapper.getCSVStringByQuiz(quiz);
		}
		else {
			throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		}		
	}
	
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Lecturer
	 * ***********************************************************************************************
	 */
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Participant
	 * ***********************************************************************************************
	 */
	
	/**
	 * Methode um Participant symbolisch einuzloggen
	 * 
	 * @throws	Auftretende Fehler werden an den Client weitergereicht
	 */
	public void signInParticipant(String ptc) throws RuntimeException {
		this.signedInParticipant = ptc;
	}
	
	/**
	 * Methode um alle aktiven Quizze dem Client zur Verfügung zu stellen
	 * 
	 * @return	Vector mit Questions
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public Vector<Quiz> auslesenAlleQuizzeActive() throws RuntimeException {
		//if (signedInParticipant != null) {
			return quizMapper.findAllActive();
		//}
		//else {
		//	throw new RuntimeException("Es ist ein Fehler aufgetreten, bitte melden Sie sich erneut an.");
		//}			
				
	}
	
	/**
	 * Methode um das Ergebnis einer Frage entegenzunehmen und in die DB mittels Mapper zu schreiben
	 * 
	 * @param	Result -Objekt
	 * @throws	Beim Aufruf der Mapper-Methode kann dort eine Exception auftreten. Diese
	 * 			Exception wird bis zur Client-Methode, welche den Service in Anspruch nimmt
	 * 			weitergereicht. 
	 */
	public boolean erfassenResult(Results result) throws RuntimeException {
		result.setHdmUser(this.signedInParticipant);
		if (result.getAnswerNo() != 1) {
			result.setSuccessed(false);
			this.resultsMapper.insertIntoDB(result);
			return false;
		}
		else {
			result.setSuccessed(true);
			this.resultsMapper.insertIntoDB(result);
			return true;
		}
	}
	
	public Vector<Boolean> erfassenfehlenderResults(Vector<Results> vr) throws RuntimeException {
		
		return this.resultsMapper.insertMultipleIntoDB(vr);
	}
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Participant
	 * ***********************************************************************************************
	 */
	
	public void openDBCon() throws RuntimeException {
		dbcon.openOneOfTenConnection();
	}
	
	
}
