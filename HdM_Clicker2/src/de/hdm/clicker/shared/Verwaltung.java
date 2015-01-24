package de.hdm.clicker.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.hdm.clicker.shared.bo.Category;
import de.hdm.clicker.shared.bo.ChartInfo;
import de.hdm.clicker.shared.bo.Lecturer;
import de.hdm.clicker.shared.bo.Question;
import de.hdm.clicker.shared.bo.Quiz;
import de.hdm.clicker.shared.bo.Results;


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
	
	public Vector<Category> auslesenAlleCategoriesByLecturer() throws RuntimeException;
	
	public void loeschenCategory(Category category) throws RuntimeException;
	
	public Category anlegenCategory(String description) throws RuntimeException;
	
	public Question anlegenQuestion(String body, String answer1, String answer2, String answer3, String answer4, int severity, int categoryID) throws RuntimeException;
	
	public Question aendernQuestion(Question question) throws RuntimeException;
	
	public void loeschenQuestion(Question question) throws RuntimeException;
	
	public Vector<Question> auslesenQuestion(Question question) throws RuntimeException;
	
	public Vector<Question> auslesenAlleQuestionsByCategoryAndSeverity(Category cat, int severity) throws RuntimeException;
	
	public Vector<Question> auslesenAlleQuestionsByCategory(Category cat) throws RuntimeException;
	
	public Vector<Question> auslesenAlleQuestionsByQuiz(Quiz quiz) throws RuntimeException;
	
	public Quiz aendernQuiz(Quiz quiz, Vector<Question> vq) throws RuntimeException;
	
	public Quiz anlegenQuiz(String passwort, int buttonDuration, String description, int questionDuration, 
			int startDate, int startTime, boolean active, boolean automatic, boolean random, Vector<Question> vq) throws RuntimeException;
	
	public Quiz anlegenNewQuizVersion(Quiz quiz) throws RuntimeException;
	
	public Vector<Quiz> auslesenQuiz(Quiz quiz) throws RuntimeException;
	
	public void loeschenQuiz(Quiz quiz) throws RuntimeException;
	
	public Vector<Quiz> auslesenAlleQuizzeByLecturer() throws RuntimeException;
	
	public Vector<Quiz> auslesenAlleQuizzeByLecturerAndActive() throws RuntimeException;
	
	public Quiz startenQuiz(Quiz quiz) throws RuntimeException;
	
	public Vector<Integer> auslesenAlleQuizStartdatenByLecturer() throws RuntimeException;
	
	public Vector<Quiz> auslesenAlleQuizByLecturerAndStartdate(int date) throws RuntimeException;
	
	public Vector<ChartInfo> auslesenChartInfoByQuiz(Quiz quiz) throws RuntimeException;
	
	public String auslesenCSVDataByQuiz(Quiz quiz) throws RuntimeException;
	
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

	public void signInParticipant(String ptc) throws RuntimeException;
	
	public Vector<Quiz> auslesenAlleQuizzeActive() throws RuntimeException;
	
	public void preloadQuizPackage(Quiz quiz) throws RuntimeException;
	
	public void checkQuizzes() throws RuntimeException;
	
	public Vector<Question> loadQuestions(Quiz quiz) throws RuntimeException;
	
	public boolean erfassenResult(Results result) throws RuntimeException;
	
	public Vector<Boolean> erfassenfehlenderResults(Vector<Results> vr) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Participant
	 * ***********************************************************************************************
	 */
	
	public void openDBCon() throws RuntimeException;
}
