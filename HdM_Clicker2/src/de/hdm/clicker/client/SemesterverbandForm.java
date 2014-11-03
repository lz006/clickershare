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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Semesterverbands notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class SemesterverbandForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;

	/**
	 * Referenz auf des CustomTreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	CustomTreeViewModel dtvm = null;

	/**
	 * Angezeigte Semesterverband
	 */
	Semesterverband shownSemesterverband = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Jahrganges eines Semesterverbandes
	 */
	Label jahrgangLabel = new Label("Jahrgang: ");
	TextBox jahrgangTextBox = new TextBox();

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Studentenanzahl eines Semesterverbandes
	 */
	Label anzahlStudentenLabel = new Label("Anzahl Studenten: ");
	TextBox anzahlStudentenTextBox = new TextBox();

	/**
	 * ListBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Studiengangs eines Semesterverbandes
	 */
	Label studiengangLabel = new Label("Studiengang: ");
	ListBox studiengangListBox = new ListBox();

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) einen 
	 * Semesterverband anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen eines Semesterverbandes
	 */
	Button loeschenButton;

	/**
	 * Container welche alle Studiengänge enthält, 
	 * aus denen sich der User mittels DropDown "bedienen" kann
	 */
	Vector<Studiengang> studiengangVector = null;

	/**
	 * Tabelle (Grid) welches Widgets strukturiert aufnimmt und selbst
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
	public SemesterverbandForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		grid = new Grid(3, 2);

		// Anordnung der Widgets
		grid.setWidget(0, 0, jahrgangLabel);
		grid.setWidget(0, 1, jahrgangTextBox);
		grid.setWidget(1, 0, anzahlStudentenLabel);
		grid.setWidget(1, 1, anzahlStudentenTextBox);
		grid.setWidget(2, 0, studiengangLabel);
		grid.setWidget(2, 1, studiengangListBox);

		// Initialisierung und Anordnung des "speichernAnlegenButton"
		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);

		this.add(grid);
		this.add(buttonPanel);

	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Studiengänge anfordert und diese
	 * in den dafür vorgesehenen Container ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void ladenStudiengaenge() {

		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.toString());
					}

					public void onSuccess(Vector<Studiengang> result) {

						if (studiengangVector == null) {
							studiengangVector = new Vector<Studiengang>();
						}

						for (Studiengang studiengang : result) {
							studiengangVector.add(studiengang);
							studiengangListBox.addItem(studiengang.getBezeichnung());
						}

						// Falls sich der User in der Ändern-Maske befindet...
						if (shownSemesterverband != null) {
							
							/*
							 *  ...erfolgt ein Aufruf, der anschließend die 
							 *  die Widgets der Benutzeroberfläche gemäß den Attributen
							 *  des geählten Semesterverbandes konfiguriert
							 */
							fillForm();
						}
					}
				});

	}

	/**
	 * Setzen der Referenz zum CustomTreeViewModel des CellTree und
	 * mittelbar setzen der Infotexte
	 * 
	 * @param	dtvm - Referenz auf ein CustomTreeViewModel-Objekt. 
	 */
	public void setDtvm(CustomTreeViewModel dtvm) {
		this.dtvm = dtvm;
		setInfoText();
	}

	/**
	 * Setzen der InfoTexte um den User zu unterstützen, bei Klick in ein
	 * Textfeld werden widgetspezifische Informationen bzw. Restriktionen
	 * angezeigt
	 */
	void setInfoText() {
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("<b><u>Anleitung: </u></b></br>Hier können Sie einen Semesterverband anlegen/ ändern."
						+ "</br><b>Alle Felder sind Pflichtfelder!</b>");

		jahrgangTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung des Eintrittsjahrganges eines Semesterverbandes bitte "
					+ "folgende Restriktionen beachten:</b>"
					+ "</br>Der Jahrgang muss mit „SS“ oder „WS“ beginnen, gefolgt von einer Jahreszahl aus dem 21. Jahrhundert, ohne Leerzeichen dazwischen! Bsp.SS2014"
					+ "</br>Der Jahrgang ergibt sich aus dem Startsemester (SS für Sommersemester/ WS für Wintersemester), sowie dem aktuellen Jahr!"
					+ "</br>Bei SS reicht die Jahreszahl aus! Bsp. SS2014"
					+ "</br>Bei WS muss der Jahreswechsel angegeben werden!"
					+ "</br>Bsp. WS2014/15");
			}
		});

		anzahlStudentenTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der Studentenanzahl eines Semesterverbandes "
						+ "bitte folgende Restriktionen beachten:</b>"
						+ "</br>Die Anzahl Studenten darf nicht 0 und maximal 999 sein!");
			}
		});
	}

	/**
	 * Setzen der aus dem CellTree gewählten Semesterverband (Ändern-Maske)
	 * 
	 * @param	sv - Referenz auf ein Semesterverband-Objekt. 
	 */
	public void setShownSemesterverband(Semesterverband sv) {
		this.shownSemesterverband = sv;
	}

	/**
	 * TextBoxen mit dem Jahrgang und der Studentenanzahl füllen (Ändern-Maske) 
	 * und die Studiengang-ListBoxen entsprechend des Semesterverbandes vorauswählen
	 */
	public void fillForm() {
		this.jahrgangTextBox.setText(shownSemesterverband.getJahrgang());
		this.anzahlStudentenTextBox.setText(new Integer(shownSemesterverband.getAnzahlStudenten()).toString());

		for (int i = 0; i < studiengangVector.size(); i++) {
			if (shownSemesterverband.getStudiengang().getId() == studiengangVector.elementAt(i).getId()) {
				studiengangListBox.setSelectedIndex(i);
				break;
			}
		}

	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern eines
	 * Semesterverbandes ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void aendernMaske() {
		
		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für die Abänderung eines SV erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownSemesterverband.setJahrgang(jahrgangTextBox.getText());
				shownSemesterverband.setStudiengang(studiengangVector.elementAt(studiengangListBox.getSelectedIndex()));
				try {
					shownSemesterverband.setAnzahlStudenten(Integer.parseInt(anzahlStudentenTextBox.getText()));
				} catch (NumberFormatException e) {
					Window.alert("Bitte geben Sie nur Zahlen bei der Studentenanzahl ein");
				}

				verwaltung.aendernSemesterverband(shownSemesterverband,
						new AsyncCallback<Semesterverband>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());

								verwaltung.auslesenSemesterverband(shownSemesterverband, new AsyncCallback<Vector<Semesterverband>>() {
									public void onFailure(Throwable caught) {
										DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor","default");
										Window.alert(caught.getMessage());
										speichernAnlegenButton.setEnabled(true);
										loeschenButton.setEnabled(true);
									}

									/*
									 *  Bei fehlgeschlagener Änderung des SV, wird der SV wieder 
									 *  in seiner ursprünglichen Form geladen und die Benutzeroberfläche neu
									 *  aufgesetzt
									 */
									public void onSuccess(Vector<Semesterverband> result) {
										dtvm.setSelectedSemesterverband(result.elementAt(0));
										speichernAnlegenButton.setEnabled(true);
										loeschenButton.setEnabled(true);
									}
								});
							}

							/* 
							 * Bei Erfolgreicher Änderung erfolgt Meldung an den User und
							 * der semesterverbandDataProvider wird mittelbar aktualisiert
							 */
							public void onSuccess(Semesterverband result) {
								Window.alert("Der Semesterverband wurde erfolgreich geändert");
								dtvm.updateSemesterverband(result);
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

				verwaltung.loeschenSemesterverband(shownSemesterverband,
						new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);

							}

							public void onSuccess(Void result) {
								Window.alert("Semesterverband wurde erfolgreich gelöscht");
								dtvm.loeschenSemesterverband(shownSemesterverband);
								clearForm();
							}
						});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen eines
	 * SV ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen eines SV erfoderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				verwaltung.anlegenSemesterverband(anzahlStudentenTextBox.getText(), jahrgangTextBox.getText(),
						studiengangVector.elementAt(studiengangListBox.getSelectedIndex()),	new AsyncCallback<Semesterverband>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
							}

							/* 
							 * Bei Erfolgreicher Anlegung erfolgt Meldung an den User und
							 * der semesterverbandDataProvider wird mittelbar aktualisiert
							 */
							public void onSuccess(Semesterverband result) {
								Window.alert("Semesterverband wurde erfolgreich angelegt");
								dtvm.addSemesterverband(result);
								speichernAnlegenButton.setEnabled(true);
								clearForm();
							}
						});
			}
		});
	}

	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownSemesterverband = null;
		this.anzahlStudentenTextBox.setText("");
		this.jahrgangTextBox.setText("");
		this.studiengangListBox.setSelectedIndex(0);

	}

}
