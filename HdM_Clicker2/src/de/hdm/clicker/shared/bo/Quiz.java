package de.hdm.clicker.shared.bo;

import java.sql.Time;
import java.util.Date;

/**
 * Realisierung eines Quiz.
 * Ein Objekt dieser Klasse ist die Repräsentation eines realen Quizzes 
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */

public class Quiz extends BusinessObject {

	// Initialwert ist "0"...
	private int version = 0;
	
	private int startDate;
	
	private int startTime;
	
	private String password;
	
	private int durationButton;
	
	private String description;
	
	private int lecturerID;
	
	private int durationQuestion;

	private boolean active;
	
	private boolean automatic;
	
	private boolean random;
	
	private boolean started;
	
	private int questionsCount;
	

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getStartDate() {
		return startDate;
	}

	public void setStartDate(int startDate) {
		this.startDate = startDate;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDurationButton() {
		return durationButton;
	}

	public void setDurationButton(int durationButton) {
		this.durationButton = durationButton;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLecturerID() {
		return lecturerID;
	}

	public void setLecturerID(int lecturerID) {
		this.lecturerID = lecturerID;
	}

	public int getDurationQuestion() {
		return durationQuestion;
	}

	public void setDurationQuestion(int durationQuestion) {
		this.durationQuestion = durationQuestion;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public int getQuestionsCount() {
		return questionsCount;
	}

	public void setQuestionsCount(int questionsCount) {
		this.questionsCount = questionsCount;
	}
	
	
	
}
