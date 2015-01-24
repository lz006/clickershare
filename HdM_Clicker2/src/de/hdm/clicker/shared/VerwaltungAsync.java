package de.hdm.clicker.shared;


import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.hdm.clicker.shared.bo.Category;
import de.hdm.clicker.shared.bo.ChartInfo;
import de.hdm.clicker.shared.bo.Lecturer;
import de.hdm.clicker.shared.bo.Question;
import de.hdm.clicker.shared.bo.Quiz;
import de.hdm.clicker.shared.bo.Results;

/**
 * Das asynchrone Gegenstück des Interface {@link Verwaltung}. 
 * 
 * @author Thies, Moser, Sonntag, Zanella
 * @version 1
 */
public interface VerwaltungAsync {
	
	void preloadQuizPackage(Quiz quiz, AsyncCallback<Void> callback) throws RuntimeException;
	
	void loadQuestions(Quiz quiz, AsyncCallback<Vector<Question>> callback) throws RuntimeException;
	
	void checkQuizzes(AsyncCallback<Void> callback) throws RuntimeException;
		
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
	
	void anlegenQuestion(String body, String answer1, String answer2, String answer3, String answer4, int severity, int categoryID, AsyncCallback<Question> callback) throws RuntimeException;
	
	void aendernQuestion(Question question, AsyncCallback<Question> callback) throws RuntimeException;
	
	void loeschenQuestion(Question question, AsyncCallback<Void> callback) throws RuntimeException;
	
	void auslesenQuestion(Question question, AsyncCallback<Vector<Question>> callback) throws RuntimeException;
	
	void auslesenAlleQuestionsByCategoryAndSeverity(Category cat, int severity, AsyncCallback<Vector<Question>> callback) throws RuntimeException;
	
	void auslesenAlleQuestionsByCategory(Category cat, AsyncCallback<Vector<Question>> callback) throws RuntimeException;
	
	void auslesenAlleQuestionsByQuiz(Quiz quiz, AsyncCallback<Vector<Question>> callback) throws RuntimeException;
	
	void aendernQuiz(Quiz quiz, Vector<Question> vq, AsyncCallback<Quiz> callback) throws RuntimeException;
	
	void anlegenQuiz(String passwort, int buttonDuration, String description, int questionDuration, 
			int startDate, int startTime, boolean active, boolean automatic, boolean random, Vector<Question> vq, AsyncCallback<Quiz> callback) throws RuntimeException;
	
	void anlegenNewQuizVersion(Quiz quiz, AsyncCallback<Quiz> callback) throws RuntimeException;
	
	void auslesenQuiz(Quiz quiz, AsyncCallback<Vector<Quiz>> callback) throws RuntimeException;
	
	void loeschenQuiz(Quiz quiz,  AsyncCallback<Void> callback) throws RuntimeException;
	
	void auslesenAlleQuizzeByLecturer(AsyncCallback<Vector<Quiz>> callback) throws RuntimeException;
	
	void auslesenAlleQuizzeByLecturerAndActive(AsyncCallback<Vector<Quiz>> callback) throws RuntimeException;
	
	void startenQuiz(Quiz quiz,  AsyncCallback<Quiz> callback) throws RuntimeException;
	
	void auslesenAlleQuizStartdatenByLecturer(AsyncCallback<Vector<Integer>> callback) throws RuntimeException;
	
	void auslesenAlleQuizByLecturerAndStartdate(int date, AsyncCallback<Vector<Quiz>> callback) throws RuntimeException;
	
	void auslesenChartInfoByQuiz(Quiz quiz, AsyncCallback<Vector<ChartInfo>> callback) throws RuntimeException;
	
	void auslesenCSVDataByQuiz(Quiz quiz, AsyncCallback<String> callback) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Beginn: Services für den Participant
	 * ***********************************************************************************************
	 */

	void signInParticipant(String ptc, AsyncCallback<Void> callback) throws RuntimeException;
	
	void auslesenAlleQuizzeActive(AsyncCallback<Vector<Quiz>> callback) throws RuntimeException;
	
	void erfassenResult(Results result, AsyncCallback<Boolean> callback) throws RuntimeException;
	
	void erfassenfehlenderResults(Vector<Results> vr, AsyncCallback<Vector<Boolean>> callback) throws RuntimeException;
	
	/*
	 * ***********************************************************************************************
	 * ABSCHNITT, Ende: Services für den Participant
	 * ***********************************************************************************************
	 */
	
	
	void openDBCon(AsyncCallback<Void> callback) throws RuntimeException;

}
