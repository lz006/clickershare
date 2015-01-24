package de.hdm.clicker.shared.bo;

/**
 * Realisierung eines Datenbereichs eines Diagramms
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */

public class ChartInfo extends BusinessObject {
	
	private String questionBody = null;
	
	private int wrongs;
	
	private int corrects;

	public String getQuestionBody() {
		return questionBody;
	}

	public void setQuestionBody(String questionBody) {
		this.questionBody = questionBody;
	}

	public int getWrongs() {
		return wrongs;
	}

	public void setWrongs(int wrongs) {
		this.wrongs = wrongs;
	}

	public int getCorrects() {
		return corrects;
	}

	public void setCorrects(int corrects) {
		this.corrects = corrects;
	}
	
	
	
}
