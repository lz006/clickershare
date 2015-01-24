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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zur Steuerung aktiver Quizze notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class QuizControlForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;

	/**
	 * Referenz auf des TreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	LecturerTreeViewModel ltvm = null;

	/**
	 * FlexTable welcher alle aktiven Quizze listet
	 */
	FlexTable quizFlexTable = new FlexTable();
	
	/**
	 * Container welcher die Quizze beherrbergt
	 */
	Vector<Quiz> quizVector = null;
	
	/**
	 * Container welche alle Labels und Zähler beinhalten die einen Countdown darstellen
	 */
	Vector<Label> labelVector = new Vector<Label>();
	Vector<Integer> integerVector = new Vector<Integer>();
	Timer t = null;
	
	/**
	 * Pointer 
	 */
	int quizVectorPointer;
	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public QuizControlForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		this.add(quizFlexTable);		

	}
	
	/**
	 * Setzen der Referenz zum LecturerTreeViewModel des CellTree und
	 * mittelbar setzen der Infotexte
	 * 
	 * @param	atvm - Referenz auf ein CustomTreeViewModel-Objekt. 
	 */
	public void setLtvm(LecturerTreeViewModel ltvm) {
		this.ltvm = ltvm;
	}

	
	public void ladenQuizze() {
		verwaltung.auslesenAlleQuizzeByLecturerAndActive(new AsyncCallback<Vector<Quiz>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Quiz> result) {
				QuizControlForm.this.quizVector = result;
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
	
							final int rowIndex = quizFlexTable.getCellForEvent(event).getRowIndex();
							//Button tmpB = (Button)quizFlexTable.getWidget(rowIndex, 1);
							//tmpB.setEnabled(false);
							
							quizVector.elementAt(rowIndex).setStarted(true);
							quizVector.elementAt(rowIndex).setStartDate(new Integer(DateTimeFormat.getFormat("yyyyMMdd").format(new Date())));
							quizVector.elementAt(rowIndex).setStartTime((new Integer(DateTimeFormat.getFormat("HH").format(new Date()))*60) + (new Integer(DateTimeFormat.getFormat("mm").format(new Date()))));
							verwaltung.startenQuiz(quizVector.elementAt(rowIndex), new AsyncCallback<Quiz>() {
	
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}
	
								@Override
								public void onSuccess(Quiz result) {
									quizVector.setElementAt(result, rowIndex);
									startButton.setVisible(false);
									quizFlexTable.setWidget(row, 1, new Label("gestartet"));
									
									verwaltung.anlegenNewQuizVersion(quizVector.elementAt(rowIndex), new AsyncCallback<Quiz>() {
	
										@Override
										public void onFailure(Throwable caught) {
											Window.alert(caught.getMessage());
											
										}
			
										@Override
										public void onSuccess(Quiz result) {
											ltvm.updateQuiz(result);
										}
										
									});
								}
								
							});
	
						}
					});
					quizFlexTable.setWidget(row, 1, startButton);
				}
				else if (q.isAutomatic() && !q.isStarted()) {
					Label timeLabel = null;
					if (new Integer(DateTimeFormat.getFormat("yyyyMMdd").format(new Date())) != q.getStartDate()) {
						timeLabel = new Label("Started am: " + DateTimeFormat.getFormat("yyyyMMdd").parse(new Integer(q.getStartDate()).toString()).toString());
					}
					else {
						int timeNow = (new Integer(DateTimeFormat.getFormat("HH").format(new Date()))*60*60) + (new Integer(DateTimeFormat.getFormat("mm").format(new Date()))*60)
						+ (new Integer(DateTimeFormat.getFormat("ss").format(new Date())));
						int timeQuiz = q.getStartTime() * 60;
						
						Integer timeGap = timeQuiz - timeNow;
						integerVector.add(timeGap);
						timeLabel = new Label("Started in " + timeGap + " Sekunden");
						labelVector.add(timeLabel);
					}
					quizFlexTable.setWidget(row, 1, timeLabel);
				}
				else {
					quizFlexTable.setWidget(row, 1, new Label("gestartet"));
				}
				
			}
		}
	
		t = new Timer() {

			boolean timerStop = true;
			@Override
			public void run() {
				
				if (labelVector.size() > 0) {
					for(int i = 0; i < labelVector.size(); i++) {
						if (integerVector.elementAt(i) > 0) {
							labelVector.elementAt(i).setText("Started in " + integerVector.elementAt(i) + " Sekunden");
							integerVector.setElementAt(integerVector.elementAt(i) - 1, i);
							timerStop = false;
						}
						
						else if (integerVector.elementAt(i) <= 0) {
							labelVector.elementAt(i).setText("gestarted");
							
							int k = -1;
							for(int j = 0; j < quizVector.size(); j++) {
								if (quizVector.elementAt(j).isAutomatic()) {
									k++;
									quizVectorPointer = j;
								}
								if (k == i) {
									verwaltung.startenQuiz(quizVector.elementAt(quizVectorPointer), new AsyncCallback<Quiz>() {
										
										@Override
										public void onFailure(Throwable caught) {
											Window.alert(caught.getMessage());
											
										}
			
										@Override
										public void onSuccess(Quiz result) {
											quizVector.setElementAt(result, quizVectorPointer);
											
											verwaltung.anlegenNewQuizVersion(quizVector.elementAt(quizVectorPointer), new AsyncCallback<Quiz>() {
			
												@Override
												public void onFailure(Throwable caught) {
													Window.alert(caught.getMessage());
													
												}
					
												@Override
												public void onSuccess(Quiz result) {
													ltvm.updateQuiz(result);
												}
												
											});
										}
										
									});
								}
								
							}
						}
					}
				}
				if (timerStop) {
					QuizControlForm.this.t.cancel();
				}
				
			}
			
		};
		t.scheduleRepeating(1000);
	}
	
	
}
