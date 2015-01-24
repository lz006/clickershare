package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten einer Question notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class QuestionForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;
	
	/**
	 * Referenz auf die CategoryForm für den Methodenzugriff
	 */
	CategoryForm cF = null;

	/**
	 * Referenz auf des TreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	LecturerTreeViewModel ltvm = null;

	/**
	 * Angezeigter Category
	 */
	Question shownQuestion = null;

	/**
	 * Question Body
	 */
	Label questionLabel = new Label("Frage: ");
	TextArea questionTextArea = new TextArea();
	
	/**
	 * Answers
	 */
	Label answer1Label = new Label("1. Antwort \"richtig\": ");
	TextArea answer1TextArea = new TextArea();
	Label answer2Label = new Label("2. Antwort \"falsch\": ");
	TextArea answer2TextArea = new TextArea();
	Label answer3Label = new Label("3. Antwort \"falsch\" (optional): ");
	TextArea answer3TextArea = new TextArea();
	Label answer4Label = new Label("4. Antwort \"falsch\" (optional): ");
	TextArea answer4TextArea = new TextArea();
	
	/**
	 * Image
	 */
	Label imageLabel = new Label("Bild (optional): ");
	ImageUploadComposite uploadComposite = null;
	ImageDownloadComposite downloadComposite = null;
	
	/**
	 * Severity
	 */
	Label severityLabel = new Label("Schwierigkeit: ");
	ListBox severityListBox = new ListBox();
	
	/**
	 * Category
	 */
	Label categoryLabel = new Label("Kategorie: ");
	ListBox categoryListBox = new ListBox();

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) eine 
	 * Question anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen einer Question
	 */
	Button loeschenButton;

	/**
	 * Tabelle (Grid) welche Widgets strukturiert aufnehmen und selbst
	 * wiederum einem Panel zugewiesen wird
	 */
	Grid grid;

	/**
	 * Panel um Buttons anzuordnen
	 */
	HorizontalPanel buttonPanel;
	
	/**
	 * Linker und rechter Bereich der "Ein-/Ausgabemaske"
	 */
	VerticalPanel leftVP;
	VerticalPanel rightVP;
	HorizontalPanel carrierHP;
	
	/**
	 * Container für alle wählbaren Kategorien
	 */
	Vector<Category> cVector = null;
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public QuestionForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;
		
		uploadComposite = new ImageUploadComposite(this);
		downloadComposite = new ImageDownloadComposite(this);

		grid = new Grid(2, 2);
		
		leftVP = new VerticalPanel();
		rightVP = new VerticalPanel();
		carrierHP = new HorizontalPanel();
		
		leftVP.add(questionLabel);
		leftVP.add(questionTextArea);
		leftVP.add(answer1Label);
		leftVP.add(answer1TextArea);
		leftVP.add(answer2Label);
		leftVP.add(answer2TextArea);
		leftVP.add(answer3Label);
		leftVP.add(answer3TextArea);
		leftVP.add(answer4Label);
		leftVP.add(answer4TextArea);
		
		rightVP.add(grid);
		rightVP.add(imageLabel);
		rightVP.add(uploadComposite);
		rightVP.add(downloadComposite);
		
		severityListBox.addItem("1");
		severityListBox.addItem("2");
		severityListBox.addItem("3");
		
		grid.setWidget(0, 0, severityLabel);
		grid.setWidget(0, 1, severityListBox);
		grid.setWidget(1, 0, categoryLabel);
		grid.setWidget(1, 1, categoryListBox);
		
		carrierHP.add(leftVP);
		carrierHP.add(rightVP);
		
		this.add(carrierHP);
		
		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);

		
		this.add(buttonPanel);		
		

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
	 * Setzen der aus dem CellTree gewählten Question (Ändern-Maske)
	 * 
	 * @param	question - Referenz auf ein Question-Objekt. 
	 */
	public void setShownQuestion(Question question) {
		this.shownQuestion = question;
	}

	/**
	 * TextBoxen mit Attributen der Category füllen (Ändern-Maske)
	 */
	public void fillForm() {
		this.questionTextArea.setText(this.shownQuestion.getQuestionBody());
		this.answer1TextArea.setText(this.shownQuestion.getAnswer1());
		this.answer2TextArea.setText(this.shownQuestion.getAnswer2());
		this.answer3TextArea.setText(this.shownQuestion.getAnswer3());
		this.answer4TextArea.setText(this.shownQuestion.getAnswer4());
	}
	
	public void loadCategories() {
		verwaltung.auslesenAlleCategoriesByLecturer(new AsyncCallback<Vector<Category>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Category> result) {
				if (cVector == null) {
					cVector = new Vector<Category>();
				}

				for (Category c : result) {
					cVector.add(c);
					categoryListBox.addItem(c.getDescription());
				}
				
			}
			
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern einer
	 * Category ermöglicht (wird von AdminTreeViewModel aus aufgerufen {@link AdminTreeViewModel})
	 */
	public void aendernMaske() {
		
		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für die Abänderung eines Raumes erfoderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownQuestion.setQuestionBody(questionTextArea.getText());
				shownQuestion.setAnswer1(answer1TextArea.getText());
				shownQuestion.setAnswer2(answer2TextArea.getText());
				shownQuestion.setAnswer3(answer3TextArea.getText());
				shownQuestion.setAnswer4(answer4TextArea.getText());
				shownQuestion.setSeverity(severityListBox.getSelectedIndex() + 1);
				shownQuestion.setCategoryID(cVector.elementAt(categoryListBox.getSelectedIndex()).getId());
				
				
				verwaltung.aendernQuestion(shownQuestion, new AsyncCallback<Question>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenQuestion(shownQuestion, new AsyncCallback<Vector<Question>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung der Question, wird die Question wieder 
							 *  in ihrer ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Question> result) {
								cF.bearbeitenQuestions(result.elementAt(0));								
							}
						});
					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an der User
					 */
					public void onSuccess(Question result) {
						uploadComposite.aendernMaske(downloadComposite);
						uploadComposite.setImgID(new Integer(result.getId()).toString());
						uploadComposite.submitForm();
						
						Window.alert("Frage wurde erfolgreich geändert");
						
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

				verwaltung.loeschenQuestion(shownQuestion, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);

					}

					/* 
					 * Bei Erfolgreicher Löschung erfolgt Meldung an den User
					 */
					public void onSuccess(Void result) {
						Window.alert("Frage wurde erfolgreich gelöscht");
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen einer
	 * Question ermöglicht (wird von LecturerTreeViewModel aus aufgerufen {@link LecturerTreeViewModel})
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen einer Question erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				verwaltung.anlegenQuestion(questionTextArea.getText(), answer1TextArea.getText(), answer2TextArea.getText(), answer3TextArea.getText(), 
						answer4TextArea.getText(), severityListBox.getSelectedIndex(), cVector.elementAt(categoryListBox.getSelectedIndex()).getId(), 
						new AsyncCallback<Question>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}

					public void onSuccess(Question result) {
						uploadComposite.setImgID(new Integer(result.getId()).toString());
						uploadComposite.submitForm();
						//downloadComposite.loadImg();
						//Window.alert("Frage wurde erfolgreich angelegt");
						speichernAnlegenButton.setEnabled(true);
						clearForm();
					}
				});
			}
		});
	}
	
	/**
	 * Setzen der Referenz auf die CategoryForm für den Methodenzugriff
	 */
	public void setCategoryForm(CategoryForm cF) {
		this.cF = cF;
	}
	
	
	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownQuestion = null;
		this.questionTextArea.setText("");
		this.answer1TextArea.setText("");
		this.answer2TextArea.setText("");
		this.answer3TextArea.setText("");
		this.answer4TextArea.setText("");
		this.severityListBox.setSelectedIndex(0);
		this.categoryListBox.setSelectedIndex(0);
	}
	
	/**
	 * Image der Question laden
	 */
	public void loadImage() {
		this.downloadComposite.loadImg(new Integer(this.shownQuestion.getId()).toString());
	}
	
	/**
	 * Bestimmung ob Question eine Image-Zuordnung hat
	 */
	public void setHasImage(boolean bool) {
		this.shownQuestion.setImage(bool);
	}
}
