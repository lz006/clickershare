package de.hdm.clicker.client;

import java.util.Date;
import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Quizzes notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class QuizForm extends VerticalPanel {

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
	 * Angezeigtes Quiz
	 */
	Quiz shownQuiz = null;

	/**
	 * IntegerBox und Label zur Ausgabe bzw. Veranschaulichung
	 * der ID des Quizzes
	 */
	Label idLabel = new Label("ID: ");
	IntegerBox idIntBox = new IntegerBox();
	
	/**
	 * IntegerBox und Label zur Ausgabe bzw. Veranschaulichung
	 * der ID des Quizzes
	 */
	Label versionLabel = new Label("Version: ");
	IntegerBox versionIntBox = new IntegerBox();
	
	/**
	 * IntegerBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Bearbeitungszeit je Frage in Sekunden
	 */
	Label questionDurationLabel = new Label("Bearbeitungszeit in Sek: ");
	IntegerBox questionDurationIntBox = new IntegerBox();
	
	/**
	 * IntegerBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Zeit die der Start-Button aktiv bleibt
	 */
	Label buttonDurationLabel = new Label("Button-Aktiv in Sek: ");
	IntegerBox buttonDurationIntBox = new IntegerBox();
	
	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Description des Quizzes
	 */
	Label descLabel = new Label("Beschreibung: ");
	TextBox descTextBox = new TextBox();

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Passworts eines Lecturers
	 */
	Label passwortLabel = new Label("Passwort: ");
	TextBox passwortTextBox = new TextBox();
	
	/**
	 * DatePicker für das Festlegen des Startdatums
	 */
	Label startDateLabel = new Label("Start-Datum: ");
	DatePicker datepicker = new DatePicker();
	
	/**
	 * IntegerBoxen und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Start-Zeit die der Start-Button aktiv bleibt
	 */
	Label startTimeLabel = new Label("Start-Zeit: ");
	IntegerBox startHourIntBox = new IntegerBox();
	Label colonLabel = new Label(" : ");
	IntegerBox startMinutesIntBox = new IntegerBox();
	
	/**
	 * Checkbox zur Festlegung ob Quiz aktiv is oder nicht
	 */
	Label activeLabel = new Label("Aktiv: ");
	CheckBox activeCheckBox = new CheckBox();
	
	/**
	 * Checkbox zur Festlegung ob Quiz automatisch Starten soll oder nicht
	 */
	Label automaticLabel = new Label("Automatischer Quiz-Start: ");
	CheckBox automaticCheckBox = new CheckBox();
	
	/**
	 * Checkbox zur Festlegung ob Fragen in zufälliger Reihenfolge ablaufen
	 */
	Label randomLabel = new Label("Zufällige Reihenfolge: ");
	CheckBox randomCheckBox = new CheckBox();

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) einen 
	 * Lecturer anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen eines Lecturers
	 */
	Button loeschenButton;

	/**
	 * Tabelle (Grid) welche Widgets strukturiert aufnehmen und selbst
	 * wiederum einem Panel zugewiesen wird
	 */
	Grid grid;
	
	/**
	 * Time-Grid
	 */
	Grid timeGrid;
	
	/**
	 * Panel um Buttons anzuordnen
	 */
	HorizontalPanel buttonPanel;
	
	/**
	 * FlexTable um die zugeordneten Questions anzuzeigen
	 */
	FlexTable questionFlexTable;
	
	/**
	 *Vector mit allen dem Quiz zugeordneten Questions 
	 */
	Vector<Question> questionVector = null;
	
	/**
	 * Elemente für die Auswahl neuer Questions
	 */
	Label categoryLabel = new Label("Kategorie: ");
	ListBox categoryListBox = new ListBox();
	Vector<Category> categoryVectorLB = null;
	
	Label severityLabel = new Label("Schwierigkeit: ");
	ListBox severityListBox = new ListBox();
	
	Label questionLabel = new Label("Frage: ");
	ListBox questionListBox = new ListBox();
	Vector<Question> questionVectorLB = null;
	
	Button hinzufuegenButton = new Button("Hinzufügen");
	Grid questionGrid = null;
	
	/**
	 * Linker und rechter Bereich der "Ein-/Ausgabemaske"
	 */
	VerticalPanel leftVP;
	VerticalPanel rightVP;
	HorizontalPanel carrierHP;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public QuizForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		grid = new Grid(11,2);
		timeGrid = new Grid(1,3);
		
		leftVP = new VerticalPanel();
		rightVP = new VerticalPanel();
		carrierHP = new HorizontalPanel();
		
		grid.setWidget(0, 0, idLabel);
		
		grid.setWidget(0, 1, idIntBox);
		idIntBox.setEnabled(false);
		
		grid.setWidget(1, 0, versionLabel);
		
		grid.setWidget(1, 1, versionIntBox);
		versionIntBox.setEnabled(false);
		
		grid.setWidget(2, 0, questionDurationLabel);
		grid.setWidget(2, 1, questionDurationIntBox);
		grid.setWidget(3, 0, buttonDurationLabel);
		grid.setWidget(3, 1, buttonDurationIntBox);
		grid.setWidget(4, 0, descLabel);
		grid.setWidget(4, 1, descTextBox);
		grid.setWidget(5, 0, passwortLabel);
		grid.setWidget(5, 1, passwortTextBox);
		grid.setWidget(6, 0, startDateLabel);
		grid.setWidget(6, 1, datepicker);
		grid.setWidget(7, 0, startTimeLabel);
		
		timeGrid.setWidget(0, 0, startHourIntBox);
		timeGrid.setWidget(0, 1, colonLabel);
		timeGrid.setWidget(0, 2, startMinutesIntBox);
		
		grid.setWidget(7, 1, timeGrid);
		grid.setWidget(8, 0, activeLabel);
		grid.setWidget(8, 1, activeCheckBox);
		grid.setWidget(9, 0, automaticLabel);
		grid.setWidget(9, 1, automaticCheckBox);
		grid.setWidget(10, 0, randomLabel);
		grid.setWidget(10, 1, randomCheckBox);
				
		leftVP.add(grid);
		carrierHP.add(leftVP);
		
		questionGrid = new Grid(4,2);
		questionGrid.setWidget(0, 0, categoryLabel);
		questionGrid.setWidget(0, 1, categoryListBox);
		questionGrid.setWidget(1, 0, severityLabel);
		questionGrid.setWidget(1, 1, severityListBox);
		questionGrid.setWidget(2, 0, questionLabel);
		questionGrid.setWidget(2, 1, questionListBox);
		questionGrid.setWidget(3, 1, hinzufuegenButton);
		rightVP.add(questionGrid);
		
		questionFlexTable = new FlexTable();
		rightVP.add(questionFlexTable);
		carrierHP.add(rightVP);
		this.add(carrierHP);
		
		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);
		this.add(buttonPanel);
		
		severityListBox.addItem("1");
		severityListBox.addItem("2");
		severityListBox.addItem("3");
		
		categoryListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ladenQuestionsForListBox();
				
			}
			
		});
		
		severityListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ladenQuestionsForListBox();
				
			}
			
		});
		
		hinzufuegenButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (questionVector != null) {
					for (Question q : questionVector) {
						if (q.getId() == questionVectorLB.elementAt(
								questionListBox.getSelectedIndex()).getId()) {
							Window.alert("Diese Frage wurde bereits hinzugefügt");
							return;
						}
					}
				}
				else {
					questionVector = new Vector<Question>();
				}
				questionVector.add(questionVectorLB.elementAt(questionListBox.getSelectedIndex()));
				questionsAnzeigen();
				
			}
			
		});
		
		
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

	/**
	 * Setzen der aus dem CellTree gewählten Quizzes (Ändern-Maske)
	 * 
	 * @param	lecturer - Referenz auf ein Lecturer-Objekt. 
	 */
	public void setShownQuiz(Quiz quiz) {
		this.shownQuiz = quiz;
	}

	/**
	 * TextBoxen mit Attributen des Quizzes füllen (Ändern-Maske)
	 */
	public void fillForm() {
		this.idIntBox.setValue(shownQuiz.getId());
		this.versionIntBox.setValue(shownQuiz.getVersion());
		this.questionDurationIntBox.setValue(shownQuiz.getDurationQuestion());
		this.buttonDurationIntBox.setValue(shownQuiz.getDurationButton());
		this.descTextBox.setText(shownQuiz.getDescription());
		this.passwortTextBox.setText(shownQuiz.getPassword());
		if (shownQuiz.getStartDate() > 0 && shownQuiz.getStartTime() > 0) {
			this.datepicker.setValue(DateTimeFormat.getFormat("yyyyMMdd").parse(new Integer(shownQuiz.getStartDate()).toString()));
			this.datepicker.setCurrentMonth(DateTimeFormat.getFormat("yyyyMMdd").parse(new Integer(shownQuiz.getStartDate()).toString()));
			this.startHourIntBox.setValue(shownQuiz.getStartTime() / 60);
			this.startMinutesIntBox.setValue(shownQuiz.getStartTime() % 60);
		}
		this.activeCheckBox.setValue(shownQuiz.isActive());
		this.automaticCheckBox.setValue(shownQuiz.isAutomatic());
		this.randomCheckBox.setValue(shownQuiz.isRandom());
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern eines
	 * Quizzes ermöglicht 
	 */
	public void aendernMaske() {
		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für die Abänderung eines Quizzes erfoderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownQuiz.setActive(activeCheckBox.getValue());
				shownQuiz.setAutomatic(automaticCheckBox.getValue());
				shownQuiz.setDescription(descTextBox.getText());
				shownQuiz.setDurationButton(buttonDurationIntBox.getValue());
				shownQuiz.setDurationQuestion(questionDurationIntBox.getValue());
				shownQuiz.setPassword(passwortTextBox.getText());
				shownQuiz.setRandom(randomCheckBox.getValue());
				if (datepicker.getValue() != null) {
					shownQuiz.setStartDate(new Integer(DateTimeFormat.getFormat("yyyyMMdd").format(datepicker.getValue())));
					shownQuiz.setStartTime((startHourIntBox.getValue()*60)+(startMinutesIntBox.getValue()));
				}
				
				
				verwaltung.aendernQuiz(shownQuiz, questionVector, new AsyncCallback<Quiz>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenQuiz(shownQuiz, new AsyncCallback<Vector<Quiz>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung des Lecturers, wird der Lecturer wieder 
							 *  in seiner ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Quiz> result) {
								if (ltvm != null) {
									ltvm.setSelectedQuiz(result.elementAt(0));
								}	
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);								
							}
						});
					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an der User und
					 * der lecturerDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Quiz result) {
						Window.alert("Quiz wurde erfolgreich geändert");
						ltvm.updateQuiz(result);
						
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
					}
				});
			}
		});

		// Initialisieren und Konfigurieren des Löschen-Buttons
		loeschenButton = new Button("Löschen");
		buttonPanel.add(loeschenButton);

		loeschenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				verwaltung.loeschenQuiz(shownQuiz, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);

					}

					/* 
					 * Bei Erfolgreicher Löschung erfolgt Meldung an den User und
					 * der lecturerDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Void result) {
						Window.alert("Quiz wurde erfolgreich gelöscht");
						ltvm.loeschenQuiz(shownQuiz);
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen eines
	 * Quizzes ermöglicht 
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen eines Lectures erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);				
				
				if (datepicker.getValue() != null) {
					verwaltung.anlegenQuiz(passwortTextBox.getText(), buttonDurationIntBox.getValue(), descTextBox.getText(), questionDurationIntBox.getValue(),
							new Integer(new Integer(DateTimeFormat.getFormat("yyyyMMdd").format(datepicker.getValue()))), (startHourIntBox.getValue()*60)+(startMinutesIntBox.getValue()),
							activeCheckBox.getValue(), automaticCheckBox.getValue(), randomCheckBox.getValue(), questionVector, new AsyncCallback<Quiz>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							speichernAnlegenButton.setEnabled(true);
						}
	
						public void onSuccess(Quiz result) {
							Window.alert("Quiz wurde erfolgreich angelegt");
							ltvm.addQuiz(result);
							speichernAnlegenButton.setEnabled(true);
							clearForm();
						}
					});
				}
				else {
					verwaltung.anlegenQuiz(passwortTextBox.getText(), buttonDurationIntBox.getValue(), descTextBox.getText(), questionDurationIntBox.getValue(),
							0, 0, activeCheckBox.getValue(), automaticCheckBox.getValue(), randomCheckBox.getValue(), questionVector, new AsyncCallback<Quiz>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							speichernAnlegenButton.setEnabled(true);
						}
	
						public void onSuccess(Quiz result) {
							Window.alert("Quiz wurde erfolgreich angelegt");
							ltvm.addQuiz(result);
							speichernAnlegenButton.setEnabled(true);
							clearForm();
						}
					});
				}
			}
		});
	}
	
	public void ladenQuestions() {
		verwaltung.auslesenAlleQuestionsByQuiz(shownQuiz, new AsyncCallback<Vector<Question>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Question> result) {
				QuizForm.this.questionVector = result;
				questionsAnzeigen();
				ladenCategories();
				
			}
			
		});
	}
	
	/**
	 * Methode um den FlexTable, welcher alle dem Quiz zugeordneten 
	 * Questions auflistet, abzubilden. Dabei erhält jeder Eintrag mittels 
	 * Button die Möglichkeit, diesen wieder zu entfernen.
	 */
	public void questionsAnzeigen() {
		questionFlexTable.removeAllRows();
		questionFlexTable.setVisible(true);
		questionFlexTable.setText(0, 0, "Frage: ");

		
		if ((questionVector != null)	&& (questionVector.size() > 0)) {
			
			// Für jede Question der Category...
			for (Question q : questionVector) {
				
				//...wird im FlexTable ein Eintrag gesetzt und...
				final int row = questionFlexTable.getRowCount();
				questionFlexTable.setWidget(row, 1, new Label(q.getQuestionBody()));

				//...ein Button, mit dem der User die Question wieder entfernen kann
				Button loeschenButton = new Button("X");
				loeschenButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {

						int rowIndex = questionFlexTable.getCellForEvent(event).getRowIndex();
						questionFlexTable.removeRow(rowIndex);
						questionVector.removeElementAt(rowIndex - 1);

						for(int i = 1; i < questionFlexTable.getRowCount(); i++) {
							questionFlexTable.setWidget(i, 0, new Label(new Integer(i).toString()));
						}
					}
				});

				questionFlexTable.setWidget(row, 2, loeschenButton);
			}
		}
		
		for(int i = 1; i < questionFlexTable.getRowCount(); i++) {
			questionFlexTable.setWidget(i, 0, new Label(new Integer(i).toString()));
		}
		
	}
	
	public void ladenCategories() {
		
		verwaltung.auslesenAlleCategoriesByLecturer(new AsyncCallback<Vector<Category>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Vector<Category> categories) {
				categoryVectorLB = new Vector<Category>();
				for (Category category : categories) {
					categoryVectorLB.add(category);
					categoryListBox.addItem(category.getDescription());
				}
				ladenQuestionsForListBox();
			}
		});
	}
	
	public void ladenQuestionsForListBox() {
		verwaltung.auslesenAlleQuestionsByCategoryAndSeverity(categoryVectorLB.elementAt(categoryListBox.getSelectedIndex()), 
				severityListBox.getSelectedIndex() + 1, new AsyncCallback<Vector<Question>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Question> result) {
				questionListBox.clear();
				if (result != null & result.size() > 0) {
					questionVectorLB = new Vector<Question>();
					for (Question question : result) {
						questionVectorLB.add(question);
						if (question.getQuestionBody().length() > 30) {
							questionListBox.addItem(question.getQuestionBody().substring(0, 30));
						}
						else {
							questionListBox.addItem(question.getQuestionBody());
						}
						
					}
				}
				
				
			}
			
		});
	}
		
	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownQuiz = null;
		this.idIntBox.setValue(0);
		this.versionIntBox.setValue(0);
		this.questionDurationIntBox.setValue(0);
		this.buttonDurationIntBox.setValue(0);
		this.descTextBox.setText("");
		this.passwortTextBox.setText("");
		
		this.datepicker.setValue(new Date());
		this.datepicker.setCurrentMonth(new Date());
		
		this.startHourIntBox.setValue(0);
		this.startMinutesIntBox.setValue(0);
		this.activeCheckBox.setValue(false);
		this.automaticCheckBox.setValue(false);
		this.randomCheckBox.setValue(false);
	}

}
