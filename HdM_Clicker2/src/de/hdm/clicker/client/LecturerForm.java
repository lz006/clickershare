package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Lecturers notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class LecturerForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;

	/**
	 * Referenz auf des TreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	AdminTreeViewModel atvm = null;
	LecturerTreeViewModel ltvm = null;

	/**
	 * Angezeigter Lecturer
	 */
	Lecturer shownLecturer = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Usernamens eines Lecturers
	 */
	Label userLabel = new Label("User: ");
	TextBox userTextBox = new TextBox();

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Passworts eines Lecturers
	 */
	Label passwortLabel = new Label("Passwort: ");
	TextBox passwortTextBox = new TextBox();
	
	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Vornamens eines Lecturers
	 */
	Label firstnameLabel = new Label("Vorname: ");
	TextBox firstnameTextBox = new TextBox();
	
	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Nachnamens eines Lecturers
	 */
	Label nameLabel = new Label("Nachname: ");
	TextBox nameTextBox = new TextBox();

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
	 * Panel um Buttons anzuordnen
	 */
	HorizontalPanel buttonPanel;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public LecturerForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		grid = new Grid(4, 2);

		// Anordnung der Widgets
		grid.setWidget(0, 0, userLabel);
		grid.setWidget(0, 1, userTextBox);
		grid.setWidget(1, 0, passwortLabel);
		grid.setWidget(1, 1, passwortTextBox);
		grid.setWidget(2, 0, firstnameLabel);
		grid.setWidget(2, 1, firstnameTextBox);
		grid.setWidget(3, 0, nameLabel);
		grid.setWidget(3, 1, nameTextBox);

		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);

		this.add(grid);
		this.add(buttonPanel);

	}

	/**
	 * Setzen der Referenz zum AdminTreeViewModel des CellTree und
	 * mittelbar setzen der Infotexte
	 * 
	 * @param	atvm - Referenz auf ein CustomTreeViewModel-Objekt. 
	 */
	public void setAtvm(AdminTreeViewModel atvm) {
		this.atvm = atvm;
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
	 * Setzen der aus dem CellTree gewählten Lecturer (Ändern-Maske)
	 * 
	 * @param	lecturer - Referenz auf ein Lecturer-Objekt. 
	 */
	public void setShownLecturer(Lecturer lecturer) {
		this.shownLecturer = lecturer;
	}

	/**
	 * TextBoxen mit Attributen des Lecturers füllen (Ändern-Maske)
	 */
	public void fillForm() {
		this.userTextBox.setText(shownLecturer.getUser());
		this.passwortTextBox.setText(shownLecturer.getPassword());
		this.firstnameTextBox.setText(shownLecturer.getFirstName());
		this.nameTextBox.setText(shownLecturer.getName());
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern eines
	 * Lecturer ermöglicht (wird von AdminTreeViewModel aus aufgerufen {@link AdminTreeViewModel})
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

				shownLecturer.setUser(userTextBox.getText());
				shownLecturer.setPassword(passwortTextBox.getText());
				shownLecturer.setFirstName(firstnameTextBox.getText());
				shownLecturer.setName(nameTextBox.getText());
				
				verwaltung.aendernLecturer(shownLecturer, new AsyncCallback<Lecturer>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenLecturer(shownLecturer, new AsyncCallback<Vector<Lecturer>>() {
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
							public void onSuccess(Vector<Lecturer> result) {
								if (atvm != null) {
									atvm.setSelectedLecturer(result.elementAt(0));
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
					public void onSuccess(Lecturer result) {
						Window.alert("Lehrender wurde erfolgreich geändert");
						if (atvm != null) {
							atvm.updateLecturer(result);
						}
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

				verwaltung.loeschenLecturer(shownLecturer, new AsyncCallback<Void>() {
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
						Window.alert("Lehrender wurde erfolgreich gelöscht");
						atvm.loeschenLecturer(shownLecturer);
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen eines
	 * Lecturers ermöglicht (wird von AdminTreeViewModel aus aufgerufen {@link AdminTreeViewModel})
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

				verwaltung.anlegenLecturer(userTextBox.getText(), passwortTextBox.getText(), firstnameTextBox.getText(), nameTextBox.getText(), new AsyncCallback<Lecturer>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}

					public void onSuccess(Lecturer result) {
						Window.alert("Lehrender wurde erfolgreich angelegt");
						atvm.addLecturer(result);
						speichernAnlegenButton.setEnabled(true);
						clearForm();
					}
				});
			}
		});
	}
	
	/**
	 * Blendet den Löschen-Button aus
	 */
	public void hideDeleteButton() {
		loeschenButton.setVisible(false);
	}
	
	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownLecturer = null;
		this.userTextBox.setText("");
		this.passwortTextBox.setText("");
		this.firstnameTextBox.setText("");
		this.nameTextBox.setText("");
	}

}
