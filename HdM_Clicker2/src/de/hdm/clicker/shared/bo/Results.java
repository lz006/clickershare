package de.hdm.clicker.shared.bo;

/**
 * Realisierung eines Results.
 * Ein Objekt dieser Klasse ist die Repräsentation einer Stimmabgabe eines
 * Teinehmeres zu einer Frage (siehe Klasse: Question) 
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */

public class Results extends BusinessObject {
	
	private String hdmUser;
	
	private int questionID;
	
	private int quizID;
	
	private int quizVersion;
	
	private int answerNo;
	
	private boolean answered;
	
	private boolean successed;

	public String getHdmUser() {
		return hdmUser;
	}

	public void setHdmUser(String hdmUser) {
		this.hdmUser = hdmUser;
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public int getQuizID() {
		return quizID;
	}

	public void setQuizID(int quizID) {
		this.quizID = quizID;
	}

	public int getQuizVersion() {
		return quizVersion;
	}

	public void setQuizVersion(int quizVersion) {
		this.quizVersion = quizVersion;
	}

	public int getAnswerNo() {
		return answerNo;
	}

	public void setAnswerNo(int answerNo) {
		this.answerNo = answerNo;
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public boolean isSuccessed() {
		return successed;
	}

	public void setSuccessed(boolean successed) {
		this.successed = successed;
	}
	
	
	
}
