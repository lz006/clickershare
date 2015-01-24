package de.hdm.clicker.client;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten einer Category notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class PlayQuizForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;


	/**
	 * Referenz auf die Start-Klasse um Zugriff auf deren Methoden zu bekommen
	 */
	Clicker clicker = null;
	
	/**
	 * Aktuelles Quiz
	 */
	Quiz shownQuiz = null;

	/**
	 * Image zur Frage
	 */
	ImageDownloadCompositePlayQuiz img = null;
	
	/**
	 * Label welches den Fragetext anzeigt
	 */
	Label questionLabel = null;

	/**
	 * Button-Vektor welcher die Buttons mit den Antwortmöglichkeiten  aufnimmt
	 */
	Vector<Button> buttonVector = null;
	
	/**
	 * Panel welches die Antwort-Button aufnimmt
	 */
	VerticalPanel buttonPanel = null;
	
	/**
	 * FlexTable welcher die Antwort-Buttons aufnimmt
	 */
	FlexTable buttonFlexTable = null;
	
	/**
	 * Panel welches den Skip-Button und den Countdown anzeigt
	 */
	HorizontalPanel bottomPanel;
	
	/**
	 * Button welcher zum Skippen der Fragen dient
	 */
	Button skipButton = null;

	/**
	 * Label welches den Countdown zeigt
	 */
	Label countdownLabel = null;	

	/**
	 *Vector mit allen dem Quiz zugeordneten Questions 
	 */
	Vector<Question> questionVector = null;
	
	/**
	 * Timer
	 */
	Timer timer = null;
	
	/**
	 * Übrige Gesamtzeit
	 */
	int entireTimeLeft;
	
	/**
	 * Übrige Zeit je Frage
	 */
	int questionTimeLeft;
	
	/**
	 * Reihenfolge der Fragen
	 */
	Vector<Integer> qOrder = null;
	
	/**
	 * Reihenfolge der Fragen
	 */
	Vector<Integer> bOrder = null;
	
	/**
	 * Pointer auf die aktuelle Frage
	 */
	int qPointer;
	
	/**
	 * Das Aktuell erzeugte Ergebnis (Result) zu einer Frage
	 */
	Results result = null;
	
	/**
	 * Zähler für Resultate
	 */
	int wrongCount = 0;
	int correctCount = 0;
	
	/**
	 * Flag die anzeigt ob gerade eine Übermittlung an den Server stattfindet
	 */
	boolean isSending = false;
	
	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public PlayQuizForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		img = new ImageDownloadCompositePlayQuiz();
		//img.setPixelSize(200, 200);
		this.add(img);
		
		questionLabel = new Label();
		this.add(questionLabel);
		
		buttonPanel = new VerticalPanel();
		buttonFlexTable = new FlexTable();
		buttonPanel.add(buttonFlexTable);
		this.add(buttonPanel);
		
		bottomPanel = new HorizontalPanel();
		countdownLabel = new Label();
		skipButton = new Button("Skip");
		skipButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				createResult(4);
				nextQuestion();

			}
		});
		bottomPanel.add(countdownLabel);
		bottomPanel.add(skipButton);
		this.add(bottomPanel);
			

	}
	
	public void startQuiz() {
		qPointer = 0;
		questionLabel.setText(questionVector.elementAt(qOrder.elementAt(qPointer)).getQuestionBody());
		initButtonsAndImg();
		
		
		int timeYet;
		
		//Winterzeit
		if(new Integer(DateTimeFormat.getFormat("MMdd").format(new Date())) < 329 || new Integer(DateTimeFormat.getFormat("MMdd").format(new Date())) > 1024) {
			timeYet = (new Integer(DateTimeFormat.getFormat("HH").format(new Date()))*60*60) + (new Integer(DateTimeFormat.getFormat("mm").format(new Date()))*60)
					+ (new Integer(DateTimeFormat.getFormat("ss").format(new Date())));
		}
		//Sommerzeit
		else {
			timeYet = (new Integer(DateTimeFormat.getFormat("HH").format(new Date()))*60*60) + (new Integer(DateTimeFormat.getFormat("mm").format(new Date()))*60)
					+ (new Integer(DateTimeFormat.getFormat("ss").format(new Date())));
		}
		
		//Ermittlung der Restzeit
		if (shownQuiz.getDurationButton() == 0) {
			entireTimeLeft = (shownQuiz.getStartTime()*60 + (shownQuiz.getDurationQuestion()*questionVector.size())) - timeYet;
		}
		else {
			int buttonTime = shownQuiz.getStartTime()*60 + shownQuiz.getDurationButton() - timeYet;
			if (buttonTime > 0) {
				entireTimeLeft = ((shownQuiz.getStartTime()*60) + (shownQuiz.getDurationButton() - buttonTime) + (shownQuiz.getDurationQuestion()*questionVector.size())) - timeYet;
			}
			else {
				entireTimeLeft = ((shownQuiz.getStartTime()*60) + shownQuiz.getDurationButton() + (shownQuiz.getDurationQuestion()*questionVector.size())) - timeYet;
			}
		}
		
		questionTimeLeft = shownQuiz.getDurationQuestion();
		
		timer = new Timer() {

			@Override
			public void run() {
				if (entireTimeLeft > 0) {
					entireTimeLeft--;
				}
				else {
					if (!isSending) {
						sendAnswersLeft();
					}
				}
				if (questionTimeLeft > 0) {
					questionTimeLeft--;
					countdownLabel.setText("Noch " + questionTimeLeft + " Sekunden bis zur nächsten Frage");
				}
				else {
					createResult(4);
					nextQuestion();
				}
				
			}
			
		};
		timer.scheduleRepeating(1000);
		
	}
	
	public void nextQuestion() {
		
		if (!(qPointer == questionVector.size()-1)) {
			questionTimeLeft = shownQuiz.getDurationQuestion();
			sendAnswer();
		}
		else {
			sendAnswer();
			
		}
				
	}
	
	public void sendAnswer() {
		isSending = true;
		verwaltung.erfassenResult(result, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				clicker.setChooseFormToMain();
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					correctCount++;
				}
				else {
					wrongCount++;
				}
				
				
				if (!(qPointer == questionVector.size()-1)) {
					qPointer++;
					questionLabel.setText(questionVector.elementAt(qOrder.elementAt(qPointer)).getQuestionBody());
					buttonFlexTable.removeAllRows();
					initButtonsAndImg();
					isSending = false;
				}
				else {
					clicker.setResultsFormToMain(new ResultsForm(wrongCount, correctCount));
				}
				
			}
			
		});
		
	}
	
	public void sendAnswersLeft() {
		
		verwaltung.erfassenfehlenderResults(createResultsLeft(), new AsyncCallback<Vector<Boolean>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				clicker.setChooseFormToMain();
			}

			@Override
			public void onSuccess(Vector<Boolean> result) {
				for (Boolean b : result) {
					if (b) {
						correctCount++;
					}
					else {
						wrongCount++;
					}
				}
				clicker.setResultsFormToMain(new ResultsForm(wrongCount, correctCount));
				
			}
			
		});
	}
	
	public void initButtonsAndImg() {
		int answersCount = 0;
		
		if(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer1() != null && 
				!questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer1().equals("")) {
			answersCount++;
		}
		if(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer2() != null && 
				!questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer2().equals("")) {
			answersCount++;
		}
		if(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer3() != null && 
				!questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer3().equals("")) {
			answersCount++;
		}
		if(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer4() != null && 
				!questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer4().equals("")) {
			answersCount++;
		}
		
		Vector<Integer> vi = new Vector<Integer>();
		bOrder = new Vector<Integer>();
		for(int i = 0; i < answersCount; i++) {
			vi.add(new Integer(i));
		}
		if (shownQuiz.isRandom()) {
			while(vi.size() > 1) {
				int rdm = Random.nextInt(answersCount-1);
				for (int i = 0; i < vi.size(); i++) {
					if (vi.elementAt(i) == rdm) {
						bOrder.add(rdm);
						vi.removeElementAt(i);
					}
				}
			}
			bOrder.add(vi.elementAt(0));
		}
		
		for (Integer bo : bOrder) {
			switch (bo) {
				case 0:
					buttonFlexTable.setWidget(buttonFlexTable.getRowCount(), 0, createButton(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer1()));
					break;
				case 1:
					buttonFlexTable.setWidget(buttonFlexTable.getRowCount(), 0, createButton(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer2()));
					break;
				case 2:
					buttonFlexTable.setWidget(buttonFlexTable.getRowCount(), 0, createButton(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer3()));
					break;
				case 3:
					buttonFlexTable.setWidget(buttonFlexTable.getRowCount(), 0, createButton(questionVector.elementAt(qOrder.elementAt(qPointer)).getAnswer4()));
					break;
			}
		}
		
		if (questionVector.elementAt(qOrder.elementAt(qPointer)).isImage()) {
//			img.loadImg(""+questionVector.elementAt(qOrder.elementAt(qPointer)).getId());
			img.loadImg(shownQuiz.getId()+","+questionVector.elementAt(qOrder.elementAt(qPointer)).getId());
		}
		else {
			img.removeImg();
		}
		
	}
	
	public Button createButton(String btxt) {
		Button newButton = new Button(btxt);
		newButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				final int rowIndex = buttonFlexTable.getCellForEvent(event).getRowIndex();
				createResult(rowIndex);
				nextQuestion();

			}
		});
		return newButton;
	}
	
	public void createResult(int index) {
		//Wenn User "selbst" antwort abgegeben hat
		if (index <= 3) {
			result = new Results();
			result.setAnswered(true);
			result.setAnswerNo(bOrder.elementAt(index)+1);
			result.setQuestionID(questionVector.elementAt(qOrder.elementAt(qPointer)).getId());
			result.setQuizID(shownQuiz.getId());
			result.setQuizVersion(shownQuiz.getVersion());
		}
		//Wenn automatisch eine Antwort gesendet wurde
		else {
			result = new Results();
			result.setAnswered(false);
			result.setAnswerNo(0);
			result.setQuestionID(questionVector.elementAt(qOrder.elementAt(qPointer)).getId());
			result.setQuizID(shownQuiz.getId());
			result.setQuizVersion(shownQuiz.getVersion());
		}
		
	}
	
	public Vector<Results> createResultsLeft() {
		
		Vector<Results> vr = new Vector<Results>();
		for (int i = qPointer; i < qOrder.size(); i++) {
			result = new Results();
			result.setAnswered(false);
			result.setAnswerNo(0);
			result.setQuestionID(questionVector.elementAt(qOrder.elementAt(i)).getId());
			result.setQuizID(shownQuiz.getId());
			result.setQuizVersion(shownQuiz.getVersion());
			vr.add(result);
		}
		return vr;
		
		
		
	}

	public void setShownQuiz(Quiz shownQuiz) {
		this.shownQuiz = shownQuiz;
	}

	public void setQuestionVector(Vector<Question> questionVector) {
		this.questionVector = questionVector;
		
		Vector<Integer> vi = new Vector<Integer>();
		Vector<Integer> newOrder = new Vector<Integer>();
		for(int i = 0; i < questionVector.size(); i++) {
			vi.add(new Integer(i));
		}
		if (shownQuiz.isRandom()) {
			while(vi.size() > 1) {
				int rdm = Random.nextInt(questionVector.size());
				for (int i = 0; i < vi.size(); i++) {
					if (vi.elementAt(i) == rdm) {
						newOrder.add(rdm);
						vi.removeElementAt(i);
					}
				}
			}
			newOrder.add(vi.elementAt(0));
		}
		qOrder = newOrder;
	}

	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	/*
	 *  Registrieren eines ClosingHandlers, damit beim schließen des 
	 *  Fensters noch ausstehende Ergebnisse/Antworten versendet werden.
	 */
	public void addClosingHandler() {
		Window.addWindowClosingHandler(new Window.ClosingHandler() {			
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				
					closingEvent.setMessage("Wirklich beenden?");
					
					sendAnswersLeft();
				}
			
		});
	}
	
	
	

}
