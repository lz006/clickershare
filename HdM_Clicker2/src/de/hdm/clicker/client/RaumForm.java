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
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Raumes notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class RaumForm extends VerticalPanel {

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
	 * Angezeigter Raum
	 */
	Raum shownRaum = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Bezeichnung eines Raumes
	 */
	Label bezeichnungLabel = new Label("Bezeichnung: ");
	TextBox bezeichnungTextBox = new TextBox();

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Kapazität eines Raumes
	 */
	Label kapazitaetLabel = new Label("Kapazität: ");
	TextBox kapazitaetTextBox = new TextBox();

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) einen 
	 * Raum anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen eines Raumes
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
	public RaumForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		grid = new Grid(3, 2);

		// Anordnung der Widgets
		grid.setWidget(0, 0, bezeichnungLabel);
		grid.setWidget(0, 1, bezeichnungTextBox);
		grid.setWidget(1, 0, kapazitaetLabel);
		grid.setWidget(1, 1, kapazitaetTextBox);

		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);

		this.add(grid);
		this.add(buttonPanel);

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
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("<b><u>Anleitung: </u></b></br>Hier können Sie einen Raum anlegen/ ändern."
				+ "</br><b>Alle Felder sind Pflichtfelder!</b>");

		bezeichnungTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der Raumbezeichnung bitte "
					+ "folgende Restriktionen beachten:</b>"
					+ "</br>Die Bezeichnung darf nur mit einem „W“/ „N“/ „W-N“  beginnen, gefolgt von einer dreistelligen Zahl von 0-9!"
					+ "</br>Bsp. W111/ N111/ W-N111");
			}
		});

		kapazitaetTextBox.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der Kapazität eines Raumes bitte "
					+ "folgende Restriktionen beachten:</b>"
					+ "</br>Die Kapazität darf nicht 0 und maximal 999 sein!");
			}
		});
	}

	/**
	 * Setzen der aus dem CellTree gewählten Raum (Ändern-Maske)
	 * 
	 * @param	raum - Referenz auf ein Raum-Objekt. 
	 */
	public void setShownRaum(Raum raum) {
		this.shownRaum = raum;
	}

	/**
	 * TextBoxen mit Attributen des Raumes füllen (Ändern-Maske)
	 */
	public void fillForm() {
		this.bezeichnungTextBox.setText(shownRaum.getBezeichnung());
		this.kapazitaetTextBox.setText(new Integer(shownRaum.getKapazitaet()).toString());
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern eines
	 * Raumes ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
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

				shownRaum.setBezeichnung(bezeichnungTextBox.getText());
				try {
					shownRaum.setKapazitaet(Integer.parseInt(kapazitaetTextBox.getText()));
				} catch (NumberFormatException e) {
					Window.alert("Bitte geben Sie nur Zahlen bei der Kapazität ein");
				}

				verwaltung.aendernRaum(shownRaum, new AsyncCallback<Raum>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenRaum(shownRaum, new AsyncCallback<Vector<Raum>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung des Raumes, wird der Raum wieder 
							 *  in seiner ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Raum> result) {
								dtvm.setSelectedRaum(result.elementAt(0));
										speichernAnlegenButton.setEnabled(true);
										loeschenButton.setEnabled(true);
							}
						});
					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an der User und
					 * der raumDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Raum result) {
						Window.alert("Der Raum wurde erfolgreich geändert");
						dtvm.updateRaum(result);
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

				verwaltung.loeschenRaum(shownRaum, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);

					}

					/* 
					 * Bei Erfolgreicher Anlegung erfolgt Meldung an den User und
					 * der raumDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Void result) {
						Window.alert("Raum wurde erfolgreich gelöscht");
						dtvm.loeschenRaum(shownRaum);
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen eines
	 * Raumes ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen eines Raumes erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				verwaltung.anlegenRaum(bezeichnungTextBox.getText(), kapazitaetTextBox.getText(), new AsyncCallback<Raum>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}

					public void onSuccess(Raum result) {
						Window.alert("Raum wurde erfolgreich angelegt");
						dtvm.addRaum(result);
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
		this.shownRaum = null;
		this.bezeichnungTextBox.setText("");
		this.kapazitaetTextBox.setText("");
	}

}
