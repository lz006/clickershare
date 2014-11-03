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
import com.google.gwt.user.client.ui.FlexTable;
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
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Dozenten notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class DozentForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;

	/**
	 * Textboxen zu Ein- und Ausgabe der textuellen Attribute eines Dozenten
	 */
	TextBox vornameTb = new TextBox();
	TextBox nachnameTb = new TextBox();
	TextBox personalNummerTb = new TextBox();

	/**
	 * Dynamische Tabelle (FlexTable) und DropDown (ListBox) um das Hinzufügen
	 * und das Entfernen von Lehrveranstaltungen abzubilden
	 */
	FlexTable lvTable;
	ListBox listBox;
	
	/**
	 * Container welcher alle Lehrveranstaltungen enthält, aus dem sich der 
	 * User mittels DropDown "bedienen" kann
	 */
	Vector<Lehrveranstaltung> alleLV = null;
	
	/**
	 * Container welcher alle Lehrveranstaltungen enthält, welche der User
	 * für einen neuen Dozenten ausgwählt hat
	 */
	Vector<Lehrveranstaltung> LVvonNeuerDozent = null;

	/**
	 * Angezeigter Dozent
	 */
	Dozent shownDozent = null;
	
	/**
	 * Referenz auf des CustomTreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	CustomTreeViewModel dtvm = null;

	/**
	 * Buttons mit den der User Aktionen wie Anlegen, Ändern oder Löschen
	 * einleiten kann
	 */
	Button aendernButton;
	Button dozentLoeschenButton;
	Button dozentAnlegenButton;

	/**
	 * Panel um die Buttons anzuordnen
	 */
	HorizontalPanel dozentButtonsPanel;

	/**
	 * Konstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public DozentForm(VerwaltungAsync verwaltungA) {
		this.verwaltung = verwaltungA;

		Grid dozentGrid = new Grid(3, 2);
		this.add(dozentGrid);

		Label vornameLabel = new Label("Vorname :");
		dozentGrid.setWidget(0, 0, vornameLabel);
		dozentGrid.setWidget(0, 1, vornameTb);

		Label nachnameLabel = new Label("Nachname :");
		dozentGrid.setWidget(1, 0, nachnameLabel);
		dozentGrid.setWidget(1, 1, nachnameTb);

		Label personalNummerLabel = new Label("Personalnummer :");
		dozentGrid.setWidget(2, 0, personalNummerLabel);
		dozentGrid.setWidget(2, 1, personalNummerTb);

		dozentButtonsPanel = new HorizontalPanel();
		this.add(dozentButtonsPanel);

		/*
		 *  Initialisierung des Buttons und Hinzufügen eines ClickHandlers
		 */
		aendernButton = new Button("Ändern");
		aendernButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				aendernButton.setEnabled(false);
				dozentLoeschenButton.setEnabled(false);
				aendernGewaehlterDozent();
			}
		});

		/*
		 *  Initialisierung des Buttons und Hinzufügen eines ClickHandlers
		 */
		dozentLoeschenButton = new Button("Dozent löschen");
		dozentLoeschenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				aendernButton.setEnabled(false);
				dozentLoeschenButton.setEnabled(false);
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"wait");
				
				verwaltung.loeschenDozent(shownDozent, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						Window.alert(caught.getMessage());
						aendernButton.setEnabled(true);
						dozentLoeschenButton.setEnabled(true);
					}

					public void onSuccess(Void result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						dtvm.loeschenDozent(shownDozent);
						Window.alert("Dozent wurde gelöscht");
						clearForm();
					}
				});

			}
		});

		/*
		 *  Initialisierung des Buttons und Hinzufügen eines ClickHandlers
		 */
		dozentAnlegenButton = new Button("Anlegen");
		dozentAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dozentAnlegenButton.setEnabled(false);
				
				verwaltung.anlegenDozent(vornameTb.getText(),nachnameTb.getText(), personalNummerTb.getText(),
						LVvonNeuerDozent, new AsyncCallback<Dozent>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						dozentAnlegenButton.setEnabled(true);
					}

					public void onSuccess(Dozent result) {
						dtvm.addDozent(result);
						clearForm();
						Window.alert("Dozent wurde angelegt");
						dozentAnlegenButton.setEnabled(true);
					}
				});
			}
		});

		// Anordnen der Buttons
		dozentButtonsPanel.add(aendernButton);
		dozentButtonsPanel.add(dozentLoeschenButton);
		dozentButtonsPanel.add(dozentAnlegenButton);

		// Der AnlegenButton ist anfangs nicht sichtbar
		dozentAnlegenButton.setVisible(false);

		// Inititalisieren des FlexTables
		lvTable = new FlexTable();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "entfernen");

		// Anordnen des FlexTables
		HorizontalPanel tablePanel = new HorizontalPanel();
		tablePanel.add(lvTable);
		this.add(tablePanel);

		// Initialisierung der ListBox
		listBox = new ListBox();
		tablePanel.add(listBox);
		
		// Erzeugen eines Buttons um Lehrveranstaltungen hinzuzufügen
		Button lvHinzufuegen = new Button("Hinzufügen");
		tablePanel.add(lvHinzufuegen);
		
		// Adden eines ClickHandlers zum "Hinzufügen-Button"
		lvHinzufuegen.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				/*
				 *  Zuerst wird anhand "shownDozent" geprüft in welchem Zustand sich  
				 *  das Benutzerinterface befindet (anlegen/ändern), bei "null" befindet
				 *  sicher der User in der Anlegen-Variante
				 */
				if (shownDozent != null) {
					boolean check = true;
					if (shownDozent.getLehrveranstaltungen() != null) {
						/*
						 *  Bei Auswahl einer Lehrveranstaltung wird zunächst geprüft,
						 *  ob sich diese bereits im Repertoire des Dozenten befindet,
						 *  sollte dies der Fall sein, wird die gewüschte Lehrveranstaltung nicht
						 *  hinzugefügt und der User darauf hingewiesen
						 */
						for (Lehrveranstaltung lv : shownDozent.getLehrveranstaltungen()) {
							if (lv.getId() == alleLV.elementAt(listBox.getSelectedIndex()).getId()) {
								Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					// Falls die gewünschte Lehrveranstaltung noch nicht hinzugefügt wurde...
					if (check) {
						if (shownDozent.getLehrveranstaltungen() == null) {
							shownDozent.setLehrveranstaltungen(new Vector<Lehrveranstaltung>());
						}
						/*
						 * ...wird diese dem "shownDozent" hinzugefügt, dabei entspricht die Indexierung
						 * des DropDowns dem des Lehrveranstaltung-Containers in dem alle Lehrveranstaltungen
						 * vorgehalten werden
						 */
						shownDozent.getLehrveranstaltungen().addElement(alleLV.elementAt(listBox.getSelectedIndex()));
						lehrveranstaltungenAnzeigen();
					}
				}
				/*
				 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
				 *  "if-Zweig", nur dass für einen neuen Dozenten ein separater LV-Container die hinzugefügten LVs
				 *  aufnehmen muss
				 */
				else {
					if (LVvonNeuerDozent == null) {
						LVvonNeuerDozent = new Vector<Lehrveranstaltung>();
					}
					boolean check = true;
					for (Lehrveranstaltung lv : LVvonNeuerDozent) {
						if (lv.getId() == alleLV.elementAt(listBox.getSelectedIndex()).getId()) {
							Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						if (LVvonNeuerDozent == null) {
							LVvonNeuerDozent = new Vector<Lehrveranstaltung>();
						}
						LVvonNeuerDozent.addElement(alleLV.elementAt(listBox.getSelectedIndex()));
						lehrveranstaltungenAnzeigen();
					}
				}

			}
		});

		// Laden aller LVs um den LV-Container und parallel dazu das DropDown zu füllen
		verwaltung.auslesenAlleLehrveranstaltungen(new AsyncCallback<Vector<Lehrveranstaltung>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Vector<Lehrveranstaltung> result) {
				alleLV = result;
				for (Lehrveranstaltung lv : result) {
					listBox.addItem(lv.getBezeichnung());
				}
			}
		});
	}

	/**
	 * Methode um den FlexTable, welcher alle dem Dozenten zugeordneten LVs
	 * auflistet, abzubilden. Dabei erhält jeder Eintrag mittels Button die
	 * Möglichkeit, diesen wieder zu entfernen. Die Methode wird in der
	 * Ändern-Maske zu Beginn und anschließend maskenunabhängig bei jeder
	 * neuen Auswahl einer LV bzw. deren Löschung aufgerufen 
	 */
	void lehrveranstaltungenAnzeigen() {
		lvTable.removeAllRows();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "entfernen");

		// "if-Zweig" falls Ändern-Maske gegenwärtig
		if (shownDozent != null) {
			if ((shownDozent.getLehrveranstaltungen() != null) && (shownDozent.getLehrveranstaltungen().size() > 0)) {
				
				// Für jede Lehrveranstaltung des Dozenten...
				for (Lehrveranstaltung lv : shownDozent.getLehrveranstaltungen()) {
					
					//...wird im FlexTable ein Eintrag gesetzt und...
					final int row = lvTable.getRowCount();
					if (lv.getBezeichnung().length() > 22) {
						StringBuffer modBezeichnung = new StringBuffer();
						modBezeichnung.append(lv.getBezeichnung().substring(0, 23));
						modBezeichnung.append(" ");
						modBezeichnung.append(lv.getBezeichnung().substring(23));
						lvTable.setWidget(row, 0, new Label(modBezeichnung.toString()));
					}
					else {
						lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					}
					
					//...ein Button, mit dem der User die LV wieder entfernen kann
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event).getRowIndex();
							lvTable.removeRow(rowIndex);

							shownDozent.getLehrveranstaltungen().removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 1, loeschenButton);

				}
			}
		} 
		/*
		 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
		 *  "if-Zweig", nur dass für einen neuen Dozenten ein separater LV-Container die hinzugefügten LVs
		 *  aufnehmen muss
		 */
		else {
			if ((LVvonNeuerDozent != null) && (LVvonNeuerDozent.size() > 0)) {
				for (Lehrveranstaltung lv : LVvonNeuerDozent) {
					final int row = lvTable.getRowCount();
					
					if (lv.getBezeichnung().length() > 22) {
						StringBuffer modBezeichnung = new StringBuffer();
						modBezeichnung.append(lv.getBezeichnung().substring(0, 23));
						modBezeichnung.append(" ");
						modBezeichnung.append(lv.getBezeichnung().substring(23));
						lvTable.setWidget(row, 0, new Label(modBezeichnung.toString()));
					}
					else {
						lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					}

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event).getRowIndex();
							lvTable.removeRow(rowIndex);

							LVvonNeuerDozent.removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 1, loeschenButton);

				}
			}
		}

	}

	/**
	 * Methode die durch Klick auf den Ändern-Button aufgerufen wird, diese
	 * ruft die entsprechende Methode auf Serverseite auf und aktualisiert
	 * mittelbar den dozentDataProvider {@link CustomTreeViewModel} des 
	 * CellTree
	 */
	void aendernGewaehlterDozent() {

		shownDozent.setVorname(this.vornameTb.getText());
		shownDozent.setNachname(this.nachnameTb.getText());
		try {
			shownDozent.setPersonalnummer(Integer.parseInt(this.personalNummerTb.getValue()));
		} 
		catch (NumberFormatException e) {
			Window.alert("Bitte geben Sie nur Zahlen bei Personalnummer ein");
		}

		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		verwaltung.aendernDozent(shownDozent, new AsyncCallback<Dozent>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
				/*
				 *  Bei fehlgeschlagener Änderung des Dozenten, wird der Dozent wieder 
				 *  in seiner ursprünglichen Form geladen und die Benutzeroberfläche neu
				 *  aufgesetzt
				 */
				verwaltung.auslesenDozent(shownDozent, new AsyncCallback<Vector<Dozent>>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						Window.alert(caught.getMessage());
						aendernButton.setEnabled(true);
						dozentLoeschenButton.setEnabled(true);
					}

					public void onSuccess(Vector<Dozent> result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						lvTable.clear();
						dtvm.setSelectedDozent(result.elementAt(0));
						aendernButton.setEnabled(true);
						dozentLoeschenButton.setEnabled(true);

					}
				});
			}

			/* 
			 * Bei Erfolgreicher Änderung erfolgt Meldung an den User und
			 * der dozentDataProvider wird mittelbar aktualisiert
			 */
			public void onSuccess(Dozent result) {
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
				dtvm.updateDozent(shownDozent);
				Window.alert("Dozent wurde geändert");
				aendernButton.setEnabled(true);
				dozentLoeschenButton.setEnabled(true);
			}
		});
	}

	/**
	 * Setzen der Referenz zum CustomTreeViewModel des CellTree und
	 * mittelbar setzen der Infotexte
	 * 
	 * @param	dtvm - Referenz auf ein CustomTreeViewModel-Objekt. 
	 */
	void setDtvm(CustomTreeViewModel dtvm) {
		this.dtvm = dtvm;
		setInfoText();

	}

	/**
	 * Setzen der InfoTexte um den User zu unterstützen, bei Klick in ein
	 * Textfeld werden widgetspezifische Informationen bzw. Restriktionen
	 * angezeigt
	 */
	void setInfoText() {
		// Allgemeiner Leitfaden
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("<b><u>Anleitung: </u></b></br>"
								+ "Hier können Sie eine/ n Dozentin/ Dozenten und deren/ dessen Lehrveranstaltungen anlegen."
								+ "</br><b>Außer der Angabe einer Lehrveranstaltung, sind alle Felder Pflichtfelder!</b>");
		
		// Info zur Eingabe des Vornamens
		vornameTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung des Vornamen einer/ eines Dozentin/ "
						+ "Dozenten bitte folgende Restriktionen beachten:</b>"
						+ "</br>Der Vorname darf keine Zahlen und Sonderzeichen enthalten und nicht mit einem Leerzeichen beginnen!"
						+ "</br>Einzig erlaubtes Sonderzeichen ist ein Bindestrich.</br>Bsp. Karl-Heinz"
						+ "</br>"
						+ "</br>Max. dürfen 45 Zeichen eingetragen/ verwendet werden!");
			}
		});

		// Info zur Eingabe des Nachnamens
		nachnameTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung des Nachnamen einer/ eines Dozentin/ "
						+ "Dozenten bitte folgende Restriktionen beachten:</b>"
						+ "</br>Der Nachname darf keine Zahlen und Sonderzeichen enthalten und nicht mit einem Leerzeichen beginnen!"
						+ "</br>Einzig erlaubtes Sonderzeichen ist ein Bindestrich.</br>Bsp. Häfner-Reuss"
						+ "</br>"
						+ "</br>Max. dürfen 45 Zeichen eingetragen/ verwendet werden");
			}
		});

		// Info zur Eingabe der Personalnummer
		personalNummerTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der Personalnummer einer/ eines "
						+ "Dozentin/ Dozenten bitte folgende Restriktionen beachten:</b>"
						+ "</br>Die Personalnummer darf nur Zahlen von 0-9 enthalten und muss 5-stellig sein!</br>Bsp. 12345");
			}
		});
	}

	/**
	 * Setzen des aus dem CellTree gewählten Dozenten (Ändern-Maske)
	 * 
	 * @param	dozent - Referenz auf ein Dozent-Objekt. 
	 */
	void setShownDozent(Dozent dozent) {
		this.shownDozent = dozent;
	}

	/**
	 * TextBoxen mit Attributen des Dozenten füllen (Ändern-Maske)
	 */
	void fillForm() {
		this.vornameTb.setText(shownDozent.getVorname());
		this.nachnameTb.setText(shownDozent.getNachname());
		this.personalNummerTb.setText(new Integer(shownDozent.getPersonalnummer()).toString());
	}

	/**
	 * Sichtbarkeit der Buttons im Falle Anlegen-Maske
	 */
	public void noVisibiltyAendernButtons() {
		aendernButton.setVisible(false);
		dozentLoeschenButton.setVisible(false);
		dozentAnlegenButton.setVisible(true);
	}

	/**
	 * Sichtbarkeit der Buttons im Falle Ändern-Maske
	 */
	public void visibiltyAendernButtons() {
		aendernButton.setVisible(true);
		dozentLoeschenButton.setVisible(true);
		dozentAnlegenButton.setVisible(false);
	}

	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownDozent = null;
		this.vornameTb.setText("");
		this.nachnameTb.setText("");
		this.personalNummerTb.setText("");
		this.lvTable.removeAllRows();
		this.LVvonNeuerDozent = null;
	}

}
