package de.hdm.clicker.server;

import java.util.Vector;



import de.hdm.clicker.shared.bo.Quiz;

public class TempDatabase {
	
	/**
	 * Diese Klasse ist nur einmal instanziierbar und dient der Performancesteigerung,
	 * da Sie Daten aktiver und gestarteter Quizze temporär vorhält und somit den 
	 * Datenbankzugriff reduziert - Der Teilnehmer nutzt also ein temporäre 2-Tier-Architektur
	 * 
	 * @author Roth, Zimmermann, Zanella
	 * @version 1.0
	 * 
	 */
	private static TempDatabase td = null;
	
	private static Vector<Quiz> activeQuizVector = new Vector<Quiz>();
	private static Vector<QuizPackage> qpv = new Vector<QuizPackage>();
	
	private TempDatabase() throws RuntimeException {
	}
	
	public static TempDatabase get() throws RuntimeException {
		if (td == null) {
			td = new TempDatabase();
		}
		return td;
	}

	public Vector<Quiz> getActiveQuizVector() {
		return activeQuizVector;
	}

	public void setActiveQuizVector(Vector<Quiz> activeQuizVector) {
		TempDatabase.activeQuizVector = activeQuizVector;
	}

	public static Vector<QuizPackage> getQpv() {
		return qpv;
	}

	public static void setQpv(Vector<QuizPackage> qpv) {
		TempDatabase.qpv = qpv;
	}
	
	
	
	
	
}
