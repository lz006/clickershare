package de.hdm.clicker.client;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.dialog.AlertDialog;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;
import com.googlecode.mgwt.ui.client.widget.panel.scroll.ScrollPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zur Auswahl aktiver Quizze notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class ChooseQuizFormMobile extends ScrollPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;
	
	/**
	 * Referenz auf die Clicker-Start-Seite
	 */
	Clicker clicker;

	/**
	 * FlexTable welcher alle aktiven Quizze listet
	 */
	FlexPanel quizFlexTable = new FlexPanel();
	
	/**
	 * Container welcher die Quizze beherrbergt
	 */
	Vector<Quiz> quizVector = null;
	
	/**
	 * Container welche alle Labels und Zähler beinhalten die einen Quiz-Countdown darstellen
	 */
	Vector<Label> labelVector = null;
	Vector<Integer> integerVector = null;
	Timer t = null;
	
	/**
	 * Tageszeit in Sekunden
	 */
	Timer clock = null;
	int timeYet = 0;
	
	/**
	 * Akuteller Tag als YYYYMMDD
	 */
	int today = 0;
	
	/**
	 * Container für alle Labels und Zähler für den Start-Button-Countdown
	 */
	Vector<Label> labelButtonVector = new Vector<Label>();
	
	/**
	 * Container für alle StartButtons
	 */
	Vector<Button> buttonVector = new Vector<Button>();
	int buttonPointer = 0;
	
	/**
	 * Counter für die automatische Aktualisierung
	 */
	long timeCount = 0;
	
	/**
	 * Flag der anzeigt ob gerade noch eine Response aussteht
	 */
	boolean reqAnswered = true;
		
	/**
	 * Pointer auf das gwählte Quiz
	 */
	int quizPos;
	
	/**
	 * Dialog
	 */
	AlertDialog ad = null;
	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public ChooseQuizFormMobile(VerwaltungAsync verwaltungA) {
		
		t = null;
		this.verwaltung = verwaltungA;

		this.add(quizFlexTable);
		
		today = new Integer(DateTimeFormat.getFormat("yyyyMMdd").format(new Date()));
		
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
		
		clock = new Timer() {

			@Override
			public void run() {
				
				timeYet++;
			}
			
		};
		clock.scheduleRepeating(1000);

	}

	
	public void ladenQuizze() {
		reqAnswered = false;
		verwaltung.auslesenAlleQuizzeActive(new AsyncCallback<Vector<Quiz>>() {

			@Override
			public void onFailure(Throwable caught) {
				ad = new AlertDialog("Fehler",caught.getMessage());
				ad.addTapHandler(new TapHandler() {

				@Override
				public void onTap(TapEvent event) {
					ad.hide();
					clicker.setChooseQuizFormMobileToMain();
					}
					
				});
				ad.show();
				
			}

			@Override
			public void onSuccess(Vector<Quiz> result) {
				reqAnswered = true;
				ChooseQuizFormMobile.this.quizVector = result;
				
				labelVector = new Vector<Label>();
				integerVector = new Vector<Integer>();
				//labelButtonVector = new Vector<Label>();
				quizzeAnzeigen();
				
			}
			
		});
	}
	
	/**
	 * Methode um den FlexTable mit den aktiven Quizzen zu befüllen
	 */
	public void quizzeAnzeigen() {
		quizFlexTable.clear();
		buttonPointer = 0;
		
		if ((quizVector != null)	&& (quizVector.size() > 0)) {
			
			// Für jedes Quiz...
			for (Quiz q : quizVector) {
				final int row = buttonPointer;
				//...wird im FlexPanel ein Eintrag gesetzt und...
				quizFlexTable.add(new Label(q.getDescription()));

				if(!q.isAutomatic() && !q.isStarted()) {
					//...ein Button, mit dem der User ein Quiz ad-hoc starten kann
					Button startButton = new Button("Starten");
					startButton.addTapHandler(new TapHandler() {
						final int rowIndex = buttonPointer;
						public void onTap(TapEvent event) {
	
							
							t.cancel();
							clock.cancel();
							//final int rowIndex = buttonPointer;
							quizPos = rowIndex;
							prepareQuiz();
							
	
						}
					});
					quizFlexTable.add(new Label("ad hoc"));
					startButton.setDisabled(true);
					buttonVector.add(startButton);
					quizFlexTable.add(startButton);
					
					labelButtonVector.add(new Label(""));
					quizFlexTable.add(labelButtonVector.elementAt(row));
				}
				else if (q.isAutomatic() && !q.isStarted()) {
					Label timeLabel = null;
					if (today != q.getStartDate()) {
						timeLabel = new Label("Started am: " + DateTimeFormat.getFormat("yyyyMMdd").parse(new Integer(q.getStartDate()).toString()).toString());
					}
					else {
												
						int timeQuiz = q.getStartTime() * 60;
						
						Integer timeGap = timeQuiz - timeYet;
						integerVector.add(timeGap);
						if (timeGap > 0) {
							timeLabel = new Label("Started in " + timeGap + " Sekunden");
						}
						else {
							timeLabel = new Label("gestarted");
							q.setStarted(true);
						}
						
						labelVector.add(timeLabel);
						
						//...ein Button, mit dem der User ein Quiz ad-hoc starten kann
						final Button startButton = new Button("Starten");
						startButton.addTapHandler(new TapHandler() {
							final int rowIndex = buttonPointer;
							public void onTap(TapEvent event) {
		
								
								t.cancel();
								clock.cancel();
								//final int rowIndex = buttonPointer;
								quizPos = rowIndex;
								prepareQuiz();
								
		
							}
						});
						if (!q.isStarted()) {
							startButton.setDisabled(true);
						}
						if (q.isStarted()) {
							if (q.getDurationButton() > 0) {
								timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
								if (timeGap <= 0) {
									startButton.setDisabled(true);
								}
							}
							if (q.getDurationButton() == 0) {
								timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
								if (timeGap <= 0) {
									startButton.setDisabled(true);
								}
							}
						}
						quizFlexTable.add(startButton);
						buttonVector.add(startButton);
					}
					quizFlexTable.add(timeLabel);
					
					if (!q.isStarted()) {
						labelButtonVector.add(new Label(""));
					}
					else {
						if (labelButtonVector.size() < row+1) {
							labelButtonVector.add(new Label(""));
							setButtonLabelTxt(q, labelButtonVector.elementAt(row));
						}
						else {
							setButtonLabelTxt(q, labelButtonVector.elementAt(row));
						}
					}
					quizFlexTable.add(labelButtonVector.elementAt(row));
					
				}
				else if (q.isStarted()) {
					//...ein Button, mit dem der User ein Quiz ad-hoc starten kann
					final Button startButton = new Button("Starten");
					startButton.addTapHandler(new TapHandler() {
						final int rowIndex = buttonPointer;
						public void onTap(TapEvent event) {
	
							t.cancel();
							clock.cancel();
							//final int rowIndex = buttonPointer;
							quizPos = rowIndex;
							prepareQuiz();
							
	
						}
					});
					quizFlexTable.add(new Label("gestartet"));
					startButton.setDisabled(false);
					quizFlexTable.add(startButton);
					buttonVector.add(startButton);
					
					if (q.getDurationButton() > 0) {
						int timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
						if (timeGap <= 0) {
							startButton.setDisabled(true);
						}
					}
					if (q.getDurationButton() == 0) {
						int timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
						if (timeGap <= 0) {
							startButton.setDisabled(true);
						}
					}
					
					if (labelButtonVector.size() < row+1) {
						labelButtonVector.add(new Label(""));
						setButtonLabelTxt(q, labelButtonVector.elementAt(row));
					}
					else {
						setButtonLabelTxt(q, labelButtonVector.elementAt(row));
					}
					quizFlexTable.add(labelButtonVector.elementAt(row));
				}
				buttonPointer++;
			}
		}
	
		t = new Timer() {

			//boolean timerStop = true;
			@Override
			public void run() {
				
				if (labelVector.size() > 0) {
					for(int i = 0; i < labelVector.size(); i++) {
						if (integerVector.elementAt(i) > 0) {
							labelVector.elementAt(i).setText("Started in " + integerVector.elementAt(i) + " Sekunden");
							integerVector.setElementAt(integerVector.elementAt(i) - 2, i);
							//timerStop = false;
						}
						
						else if (integerVector.elementAt(i) <= 0) {
							labelVector.elementAt(i).setText("gestarted");
							
							int k = -1;
							for(int j = 0; j < quizVector.size(); j++) {
								if (quizVector.elementAt(j).isAutomatic()) {
									k++;
								}
								if (k == i) {
									Button tmpB = buttonVector.elementAt(j);
									tmpB.setDisabled(false);
									quizVector.elementAt(j).setStarted(true);
								}
								
							}
							
						}
					}
				}
				
				if (quizVector.size() > 0) {
					for(int i = 0; i < quizVector.size(); i++) {
						if (quizVector.elementAt(i).isStarted() && quizVector.elementAt(i).getDurationButton() > 0) {
							int timeGap = ((quizVector.elementAt(i).getStartTime() * 60) + quizVector.elementAt(i).getDurationButton()) - timeYet;
							if (timeGap > 0) {
								labelButtonVector.elementAt(i).setText("Button noch " + timeGap + " Sek. aktiv");
								labelButtonVector.elementAt(i).setVisible(true);
							}
							else {
								Button tmpB = buttonVector.elementAt(i);
								tmpB.setDisabled(true);
								labelButtonVector.elementAt(i).setVisible(false);
							}
						}
						if (quizVector.elementAt(i).isStarted() && quizVector.elementAt(i).getDurationButton() == 0) {
							int timeGap = ((quizVector.elementAt(i).getStartTime() * 60) + 
									(quizVector.elementAt(i).getDurationQuestion() * quizVector.elementAt(i).getQuestionsCount())) - timeYet;
							if (timeGap > 0) {
								
								labelButtonVector.elementAt(i).setText("Start-Button bleibt " + timeGap + " Sekunden aktiv");
								labelButtonVector.elementAt(i).setVisible(true);
							}
							else {
								Button tmpB = buttonVector.elementAt(i);
								tmpB.setDisabled(true);
								labelButtonVector.elementAt(i).setVisible(false);
							}
						}
					}
				}
				
				if (timeCount % 10 == 0 && reqAnswered) {
					ChooseQuizFormMobile.this.t.cancel();
					ladenQuizze();
				}
				
				//if (timerStop) {
				//	ChooseQuizForm.this.t.cancel();
				//}
				timeCount = timeCount + 2;
			}
			
		};
		t.scheduleRepeating(2000);
		//this.refresh();
	}
	
	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	public void setButtonLabelTxt(Quiz q, Label lbl) {
		if (q.getDurationButton() > 0) {
			int timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
			if (timeGap > 0) {
				lbl.setText("Button noch " + timeGap + " Sek. aktiv");
				lbl.setVisible(true);
			}
		}
		if (q.getDurationButton() == 0) {
			int timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
			if (timeGap > 0) {
				
				lbl.setText("Button noch " + timeGap + " Sek. aktiv");
				lbl.setVisible(true);
			}
		}
	}
	
	public void prepareQuiz() {
		quizFlexTable.setVisible(false);
		
		if (quizVector.elementAt(quizPos).getPassword() != null && !quizVector.elementAt(quizPos).getPassword().equals("")) {
			QuizPasswordFormMobile qpfm = new QuizPasswordFormMobile(verwaltung);
			qpfm.setChooseQuizFormMobile(this);
			qpfm.setClicker(clicker);
			qpfm.setQuiz(quizVector.elementAt(quizPos));
			clicker.setQuizPasswordFormMobileToMain(qpfm);
		}
		else {
			verwaltung.preloadQuizPackage(quizVector.elementAt(quizPos), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					ad = new AlertDialog("Fehler",caught.getMessage());
					ad.addTapHandler(new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						ad.hide();
						clicker.setChooseQuizFormMobileToMain();
						}
						
					});
					ad.show();
					
				}

				@Override
				public void onSuccess(Void result) {
					verwaltung.loadQuestions(quizVector.elementAt(quizPos), new AsyncCallback< Vector<Question>>() {

						@Override
						public void onFailure(Throwable caught) {
							ad = new AlertDialog("Fehler",caught.getMessage());
							ad.addTapHandler(new TapHandler() {

							@Override
							public void onTap(TapEvent event) {
								ad.hide();
								clicker.setChooseQuizFormMobileToMain();
								}
								
							});
							ad.show();
							
							
						}

						@Override
						public void onSuccess( Vector<Question> result) {
							PlayQuizFormMobile pqf = new PlayQuizFormMobile(verwaltung);
							pqf.setShownQuiz(quizVector.elementAt(quizPos));
							pqf.setQuestionVector(result);
							pqf.setClicker(clicker);
							clicker.setPlayQuizFormMobileToMain(pqf);
						}
						
					});
					
				}
				
			});
		}
		
		
	}
	
	
}
