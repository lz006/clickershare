package de.hdm.clicker.client;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

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
public class ChooseQuizForm extends VerticalPanel {

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
	FlexTable quizFlexTable = new FlexTable();
	
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
	 * Counter für die automatische Aktualisierung
	 */
	long timeCount = 0;
	
	/**
	 * Flag der anzeigt ob gerade noch eine Response aussteht
	 */
	boolean reqAnswered = true;
	
	/**
	 * Passwortabfrage bei entsprechenden Quizzen und deren untergeordneten Widgets
	 */
	DialogBox dialogBox = null;
	PasswordTextBox authDBPWTB = null;
	Button authDBButton = null;
	Grid authDBGrid = null;
	Button authDBCloseButton = null;
	
	/**
	 * Pointer auf das gwählte Quiz
	 */
	int quizPos;
	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public ChooseQuizForm(VerwaltungAsync verwaltungA) {
		
		t = null;
		this.verwaltung = verwaltungA;

		quizFlexTable.setBorderWidth(3);
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
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Quiz> result) {
				reqAnswered = true;
				ChooseQuizForm.this.quizVector = result;
				
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
		quizFlexTable.removeAllRows();

		
		if ((quizVector != null)	&& (quizVector.size() > 0)) {
			
			// Für jede Question der Category...
			for (Quiz q : quizVector) {
				
				//...wird im FlexTable ein Eintrag gesetzt und...
				final int row = quizFlexTable.getRowCount();
				quizFlexTable.setWidget(row, 0, new Label(q.getDescription()));

				if(!q.isAutomatic() && !q.isStarted()) {
					//...ein Button, mit dem der User ein Quiz ad-hoc starten kann
					final Button startButton = new Button("Starten");
					startButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
	
							t.cancel();
							clock.cancel();
							final int rowIndex = quizFlexTable.getCellForEvent(event).getRowIndex();
							quizPos = rowIndex;
							prepareQuiz();
							
	
						}
					});
					quizFlexTable.setWidget(row, 1, new Label("ad hoc"));
					startButton.setEnabled(false);
					quizFlexTable.setWidget(row, 2, startButton);
					
					labelButtonVector.add(new Label("                                       "));
					quizFlexTable.setWidget(row, 3, labelButtonVector.elementAt(row));
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
						startButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
		
								t.cancel();
								clock.cancel();
								final int rowIndex = quizFlexTable.getCellForEvent(event).getRowIndex();
								quizPos = rowIndex;
								prepareQuiz();
								
		
							}
						});
						if (!q.isStarted()) {
							startButton.setEnabled(false);
						}
						if (q.isStarted()) {
							if (q.getDurationButton() > 0) {
								timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
								if (timeGap <= 0) {
									startButton.setEnabled(false);
								}
							}
							if (q.getDurationButton() == 0) {
								timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
								if (timeGap <= 0) {
									startButton.setEnabled(false);
								}
							}
						}
						quizFlexTable.setWidget(row, 2, startButton);
					}
					quizFlexTable.setWidget(row, 1, timeLabel);
					
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
					quizFlexTable.setWidget(row, 3, labelButtonVector.elementAt(row));
					
				}
				else if (q.isStarted()) {
					//...ein Button, mit dem der User ein Quiz ad-hoc starten kann
					final Button startButton = new Button("Starten");
					startButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
	
							t.cancel();
							clock.cancel();
							final int rowIndex = quizFlexTable.getCellForEvent(event).getRowIndex();
							quizPos = rowIndex;
							prepareQuiz();
							
	
						}
					});
					quizFlexTable.setWidget(row, 1, new Label("gestartet"));
					startButton.setEnabled(true);
					quizFlexTable.setWidget(row, 2, startButton);
					
					if (q.getDurationButton() > 0) {
						int timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
						if (timeGap <= 0) {
							startButton.setEnabled(false);
						}
					}
					if (q.getDurationButton() == 0) {
						int timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
						if (timeGap <= 0) {
							startButton.setEnabled(false);
						}
					}
					
					if (labelButtonVector.size() < row+1) {
						labelButtonVector.add(new Label(""));
						setButtonLabelTxt(q, labelButtonVector.elementAt(row));
					}
					else {
						setButtonLabelTxt(q, labelButtonVector.elementAt(row));
					}
					quizFlexTable.setWidget(row, 3, labelButtonVector.elementAt(row));
				}
				
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
									Button tmpB = (Button)quizFlexTable.getWidget(j, 2);
									tmpB.setEnabled(true);
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
								labelButtonVector.elementAt(i).setText("Start-Button bleibt " + timeGap + " Sekunden aktiv");
								labelButtonVector.elementAt(i).setVisible(true);
							}
							else {
								Button tmpB = (Button)quizFlexTable.getWidget(i, 2);
								tmpB.setEnabled(false);
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
								Button tmpB = (Button)quizFlexTable.getWidget(i, 2);
								tmpB.setEnabled(false);
								labelButtonVector.elementAt(i).setVisible(false);
							}
						}
					}
				}
				
				if (timeCount % 10 == 0 && reqAnswered) {
					ChooseQuizForm.this.t.cancel();
					ladenQuizze();
				}
				
				//if (timerStop) {
				//	ChooseQuizForm.this.t.cancel();
				//}
				timeCount = timeCount + 2;
			}
			
		};
		t.scheduleRepeating(2000);
	}
	
	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	public void setButtonLabelTxt(Quiz q, Label lbl) {
		if (q.getDurationButton() > 0) {
			int timeGap = ((q.getStartTime() * 60) + q.getDurationButton()) - timeYet;
			if (timeGap > 0) {
				lbl.setText("Start-Button bleibt " + timeGap + " Sekunden aktiv");
				lbl.setVisible(true);
			}
		}
		if (q.getDurationButton() == 0) {
			int timeGap = ((q.getStartTime() * 60) + (q.getDurationQuestion() * q.getQuestionsCount())) - timeYet;
			if (timeGap > 0) {
				
				lbl.setText("Start-Button bleibt " + timeGap + " Sekunden aktiv");
				lbl.setVisible(true);
			}
		}
	}
	
	public void prepareQuiz() {
		quizFlexTable.setVisible(false);
		
		if (quizVector.elementAt(quizPos).getPassword() != null && !quizVector.elementAt(quizPos).getPassword().equals("")) {
			dialogBox = new DialogBox();
			dialogBox.setText("Authentifizierung!");
			dialogBox.setAnimationEnabled(true);
			
			VerticalPanel authDBVP = new VerticalPanel();
			authDBGrid = new Grid(2,3);
			authDBVP.add(authDBGrid);
			Label authDBPWLabel = new Label("Passwort: ");
			authDBPWTB = new PasswordTextBox();
			authDBButton = new Button("OK");
			authDBButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					authDBButton.setEnabled(false);
					authDBPWTB.setEnabled(false);
					if (!quizVector.elementAt(quizPos).getPassword().equals(authDBPWTB.getText())) {
						authDBGrid.setWidget(1, 1, new Label("Passwort nicht korrekt!"));
						authDBPWTB.setText("");
						authDBButton.setEnabled(true);
						authDBPWTB.setEnabled(true);
					}
					else {
						authDBButton.setEnabled(false);
						authDBPWTB.setEnabled(false);
						authDBCloseButton.setEnabled(false);
						verwaltung.preloadQuizPackage(quizVector.elementAt(quizPos), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								dialogBox.hide();
								clicker.setChooseFormToMain();
								
							}

							@Override
							public void onSuccess(Void result) {
								verwaltung.loadQuestions(quizVector.elementAt(quizPos), new AsyncCallback< Vector<Question>>() {

									@Override
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());
										dialogBox.hide();
										clicker.setChooseFormToMain();
										
										
									}
		
									@Override
									public void onSuccess( Vector<Question> result) {
										PlayQuizForm pqf = new PlayQuizForm(verwaltung);
										pqf.setShownQuiz(quizVector.elementAt(quizPos));
										pqf.setQuestionVector(result);
										pqf.setClicker(clicker);
										dialogBox.hide();
										clicker.setPlayQuizFormToMain(pqf);
									}
									
								});
								
							}
							
						});
					}
				}
				
			});
			
			authDBGrid.setWidget(0, 0, authDBPWLabel);
			authDBGrid.setWidget(0, 1, authDBPWTB);
			authDBGrid.setWidget(0, 2, authDBButton);
			
			authDBCloseButton = new Button("Close");
			authDBCloseButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					dialogBox.hide();
					clicker.setChooseFormToMain();
				}
				
			});
			authDBGrid.setWidget(1, 0, authDBCloseButton);
			
			dialogBox.setWidget(authDBVP);			
			dialogBox.center();
			authDBPWTB.setFocus(true);
		}
		else {
			verwaltung.preloadQuizPackage(quizVector.elementAt(quizPos), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					dialogBox.hide();
					clicker.setChooseFormToMain();
					
				}

				@Override
				public void onSuccess(Void result) {
					verwaltung.loadQuestions(quizVector.elementAt(quizPos), new AsyncCallback< Vector<Question>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							dialogBox.hide();
							clicker.setChooseFormToMain();
							
							
						}

						@Override
						public void onSuccess( Vector<Question> result) {
							PlayQuizForm pqf = new PlayQuizForm(verwaltung);
							pqf.setShownQuiz(quizVector.elementAt(quizPos));
							pqf.setQuestionVector(result);
							pqf.setClicker(clicker);
							clicker.setPlayQuizFormToMain(pqf);
						}
						
					});
					
				}
				
			});
		}
		
		
	}
	
	
}
