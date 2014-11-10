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
	
	private Date startDate;
	
	private Time startTime;
	
	private String password;
	
	private int durationButton;
	
	private String description;
	
	private int lecturerID;
	
	private int durationQuestion;
	
	private int need2SS;

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
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

	public int getNeed2SS() {
		return need2SS;
	}

	public void setNeed2SS(int need2ss) {
		need2SS = need2ss;
	}
	
	
	
}
