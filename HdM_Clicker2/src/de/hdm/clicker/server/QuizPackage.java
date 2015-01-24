package de.hdm.clicker.server;

import java.sql.Blob;
import java.util.Hashtable;

import de.hdm.clicker.shared.bo.BusinessObject;
import de.hdm.clicker.shared.bo.Question;
import de.hdm.clicker.shared.bo.Quiz;

public class QuizPackage extends BusinessObject {

	private Quiz quiz = null;
	
	private Hashtable<Integer, Question> questionVector = null;
	
	private Hashtable<Integer, Blob> images = null;
	
	private int version = 0;

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public Hashtable<Integer, Question> getQuestionHT() {
		return questionVector;
	}

	public void setQuestionHT(Hashtable<Integer, Question> questionVector) {
		this.questionVector = questionVector;
	}

	public Hashtable<Integer, Blob> getImages() {
		return images;
	}

	public void setImages(Hashtable<Integer, Blob> images) {
		this.images = images;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	
	
	
}
