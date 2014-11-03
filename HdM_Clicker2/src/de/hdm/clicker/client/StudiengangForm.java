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
 * Diese Klasse stellt die zum Anlegen und Bearbeiten eines Studiengangs notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class StudiengangForm extends VerticalPanel {

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
	 * Angezeigte Studiengang
	 */
	Studiengang shownStudiengang = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Bezeichnung eines Studienganges
	 */
	Label bezeichnungLabel = new Label("Bezeichnung :");
	TextBox bezeichnungTb = new TextBox();

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Kürzels eines Studienganges
	 */
	Label kuerzelLabel = new Label("Kürzel :");
	TextBox kuerzelTb = new TextBox();

	/**
	 * Widgets um das Hinzufügen von Lehrveranstaltungen zum Studiengang
	 * zu ermöglichen bzw. zu veranschaulichen
	 */
	Label lvLabel = new Label("Lehrveranstaltungen:");
	ListBox lvListBox;
	Button hinzufuegenButton;

	/**
	 * Label welches Summe aller Studenten anzeigt, welche sich in einem
	 * Studiengang befinden
	 */
	Label gesamtStudentenLabel = new Label("Gesamtzahl Studierender: ");

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) einen 
	 * Studiengang anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen eines Studiengangs
	 */
	Button loeschenButton;

	/**
	 * Dynamische Tabellen (FlexTable) welche die zum Studiengang 
	 * referenzierten Lehrveranstaltungen bzw. Semesterverbände
	 *  auflisten
	 */
	FlexTable lvTable;
	FlexTable svTable;

	/**
	 * Container welche alle Lehrveranstaltungen beinhaltet, 
	 * welche der User für einen Studiengang auswählen kann
	 */
	Vector<Lehrveranstaltung> lvVector = null;
	
	/**
	 * Container welche alle Lehrveranstaltungen beinhaltet, 
	 * welche der User für einem neuen Studiengang zugefügt hat
	 */
	Vector<Lehrveranstaltung> lvVonNeuemSGVector = null;
	Vector<Semesterverband> svVector = null;

	/**
	 * Tabellen (Grids) welche Widgets strukturiert aufnehmen und selbst
	 * wiederum Panels (dem Namen entsprechend) zugewiesen werden
	 */
	Grid obenGrid;
	Grid mitteGrid;
	Grid untenGrid;

	/**
	 * Panels, welche die Benutzeroberfläche in vier Bereiche einteilen
	 */
	VerticalPanel obenPanel;
	VerticalPanel mittePanel;
	VerticalPanel untenPanel;
	HorizontalPanel abschlussPanel;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public StudiengangForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		ladenLehrveranstaltungen();

		obenPanel = new VerticalPanel();

		// Anordnen von Widgets
		obenGrid = new Grid(2, 2);
		obenGrid.setWidget(0, 0, bezeichnungLabel);
		obenGrid.setWidget(0, 1, bezeichnungTb);
		obenGrid.setWidget(1, 0, kuerzelLabel);
		obenGrid.setWidget(1, 1, kuerzelTb);

		obenPanel.add(obenGrid);
		this.add(obenPanel);

		mittePanel = new VerticalPanel();
		lvListBox = new ListBox();

		mitteGrid = new Grid(1, 3);
		mitteGrid.setWidget(0, 0, lvLabel);
		mitteGrid.setWidget(0, 1, lvListBox);

		/*
		 * Initialisieren und adden eines ClickHandlers zum 
		 * (Lehrveranstaltung-)"Hinzufügen-Button"
		 */
		hinzufuegenButton = new Button("Hinzufügen");

		hinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				/*
				 *  Zuerst wird anhand "shownStudiengang" geprüft in welchem Zustand sich  
				 *  das Benutzerinterface befindet (anlegen/ändern), bei "null" befindet
				 *  sicher der User in der Anlegen-Variante
				 */
				if (shownStudiengang != null) {
					boolean check = true;
					
					/*
					 *  Bei Auswahl einer LV wird zunächst geprüft,
					 *  ob sich diese bereits im Repertoire des Studienganges befindet,
					 *  sollte dies der Fall sein, wird die gewüschte LV nicht
					 *  hinzugefügt und der User darauf hingewiesen
					 */
					if (shownStudiengang.getLehrveranstaltungen() != null) {
						for (Lehrveranstaltung lv : shownStudiengang.getLehrveranstaltungen()) {
							if (lv.getId() == lvVector.elementAt(lvListBox.getSelectedIndex()).getId()) {
								Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					// Falls die gewünschte LV noch nicht hinzugefügt wurde...
					if (check) {
						if (shownStudiengang.getLehrveranstaltungen() == null) {
							shownStudiengang.setLehrveranstaltungen(new Vector<Lehrveranstaltung>());
						}
						/*
						 * ...wird diese dem "shownStudiengang" hinzugefügt, dabei entspricht die Indexierung
						 * des DropDowns dem des LV-Containers in dem alle LVs vorgehalten werden
						 */
						shownStudiengang.getLehrveranstaltungen().addElement(lvVector.elementAt(lvListBox.getSelectedIndex()));
						lvAnzeigen();
					}
				}
				/*
				 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
				 *  "if-Zweig", nur dass für eine neue LV ein separater LV-Container die hinzugefügten 
				 *  LVs aufnehmen muss
				 */
				else {
					if (lvVonNeuemSGVector == null) {
						lvVonNeuemSGVector = new Vector<Lehrveranstaltung>();
					}
					boolean check = true;
					for (Lehrveranstaltung lv : lvVonNeuemSGVector) {
						if (lv.getId() == lvVector.elementAt(lvListBox.getSelectedIndex()).getId()) {
							Window.alert("Die Lehrveranstaltung ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						lvVonNeuemSGVector.addElement(lvVector.elementAt(lvListBox.getSelectedIndex()));
						lvAnzeigen();
					}
				}

			}
		});

		mitteGrid.setWidget(0, 2, hinzufuegenButton);

		// Initialiseren und anordnen des FlexTables für die LVs
		lvTable = new FlexTable();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "Studiensemster");

		mittePanel.add(mitteGrid);
		mittePanel.add(lvTable);
		this.add(mittePanel);

		// Initialisieren und anordnen des Grid für die Summe der Studierenden
		untenGrid = new Grid(1, 2);
		untenGrid.setWidget(0, 0, gesamtStudentenLabel);

		
		// Initialiseren und anordnen des FlexTables für die SVs
		svTable = new FlexTable();

		untenPanel = new VerticalPanel();
		untenPanel.add(untenGrid);
		untenPanel.add(svTable);
		this.add(untenPanel);

		/*
		 *  Erstellung und Anordnung des Schluss-Bereichs, welcher die Buttons
		 *  je nach Masken-Variante aufnimmt (Speichern/Anlegen/Löschen)
		 */
		abschlussPanel = new HorizontalPanel();

		speichernAnlegenButton = new Button();

		abschlussPanel.add(speichernAnlegenButton);
		this.add(abschlussPanel);

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
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("<b><u>Anleitung: </u></b></br>"
			+ "Hier können Sie einen Studiengang anlegen/ ändern."
			+ "</br><b>Außer der Angabe einer Lehrveranstaltung, sind alle Felder Pflichtfelder!</b></br></br>");

		bezeichnungTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der Studiengangsbezeichnung "
					+ "bitte folgende Restriktionen beachten:</b>"
					+ "</br>Die Bezeichnung muss mindestens aus 5 Buchstaben  bestehen!"
					+ "</br>Bsp. Musik"
					+ "</br>"
					+ "</br>Max. dürfen 45 Zeichen eingetragen/ verwendet werden!");
								
			}
		});

		kuerzelTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung des Kürzels eines "
						+ "Studienganges bitte folgende Restriktionen beachten:</b>"
						+ "</br>Das Kürzel muss anfangs 2 bis 4 Großbuchstaben enthalten und darf am Ende eine Zahl von"
						+ "1 bis 20 mit vorhergehendem Bindestrich enthalten! Umlaute sind nicht gestattet!"
						+ "</br>Bsp. SE-2");

			}
		});

	}

	/**
	 * Setzen der aus dem CellTree gewählten Studienganges (Ändern-Maske)
	 * 
	 * @param	sg - Referenz auf ein Studiengang-Objekt. 
	 */
	public void setShownStudiengang(Studiengang sg) {
		this.shownStudiengang = sg;
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle LVs anfordert und diese
	 * in den dafür vorgesehenen Conatainer ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void ladenLehrveranstaltungen() {
		verwaltung.auslesenAlleLehrveranstaltungen(new AsyncCallback<Vector<Lehrveranstaltung>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					public void onSuccess(Vector<Lehrveranstaltung> result) {
						if (lvVector == null) {
							lvVector = new Vector<Lehrveranstaltung>();
						}

						for (Lehrveranstaltung lv : result) {
							lvVector.add(lv);
							lvListBox.addItem(lv.getBezeichnung());
						}
					}
				});
	}

	/**
	 * TextBoxen entsprechend den Attributwerten des SG füllen
	 */
	public void fillForm() {
		this.bezeichnungTb.setText(shownStudiengang.getBezeichnung());
		this.kuerzelTb.setText(shownStudiengang.getKuerzel());
	}

	/**
	 * Methode um den FlexTable, welcher alle dem SG zugeordneten Lehrveranstaltungen
	 * auflistet, abzubilden. Dabei erhält jeder Eintrag mittels Button die
	 * Möglichkeit, diesen wieder zu entfernen. Die Methode wird in der
	 * Ändern-Maske zu Beginn und anschließend maskenunabhängig bei jeder
	 * neuen Auswahl einer Lehrveranstaltung bzw. deren Löschung aufgerufen 
	 */
	public void lvAnzeigen() {
		lvTable.removeAllRows();
		lvTable.setText(0, 0, "Lehrveranstaltung");
		lvTable.setText(0, 1, "Studiensemster");

		// "if-Zweig" falls Ändern-Maske gegenwärtig
		if (shownStudiengang != null) {
			if ((shownStudiengang.getLehrveranstaltungen() != null)	&& (shownStudiengang.getLehrveranstaltungen().size() > 0)) {
				
				// Für jede Lehveranstaltung des SG...
				for (Lehrveranstaltung lv : shownStudiengang.getLehrveranstaltungen()) {
					
					//...wird im FlexTable ein Eintrag gesetzt und...
					final int row = lvTable.getRowCount();
					lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					lvTable.setWidget(row, 1, new Label(new Integer(lv.getStudiensemester()).toString()));

					//...ein Button, mit dem der User die Lehrveranstaltung wieder entfernen kann
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event).getRowIndex();
							lvTable.removeRow(rowIndex);

							shownStudiengang.getLehrveranstaltungen().removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 2, loeschenButton);

				}
			}
		}
		/*
		 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
		 *  "if-Zweig", nur dass für ein neuer SG ein separater Lehrveranstaltung-Container die hinzugefügten 
		 *  Studiengänge aufnehmen muss
		 */
		else {
			if ((lvVonNeuemSGVector != null) && (lvVonNeuemSGVector.size() > 0)) {
				for (Lehrveranstaltung lv : lvVonNeuemSGVector) {
					final int row = lvTable.getRowCount();
					lvTable.setWidget(row, 0, new Label(lv.getBezeichnung()));
					lvTable.setWidget(row, 1, new Label(new Integer(lv.getStudiensemester()).toString()));

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = lvTable.getCellForEvent(event).getRowIndex();
							lvTable.removeRow(rowIndex);

							lvVonNeuemSGVector.removeElementAt(rowIndex - 1);

						}
					});

					lvTable.setWidget(row, 1, loeschenButton);

				}
			}
		}
	}

	/**
	 * Methode um den FlexTable, welcher alle dem SG zugeordneten 
	 * Semesterverbände auflistet, abzubilden. Die Methode wird nur in der
	 * Ändern-Maske und dort einmal zu Beginn aufgerufen
	 */
	public void semesterverbaendeAnzeigen() {
		svTable.removeAllRows();
		svTable.setText(0, 0, "Semesterverband");
		svTable.setText(0, 1, "Studierende");

		if (shownStudiengang != null) {

			if ((shownStudiengang.getSemesterverbaende() != null)
					&& (shownStudiengang.getSemesterverbaende().size() > 0)) {

				int tempGesamtStudenten = 0;

				for (Semesterverband sv : shownStudiengang.getSemesterverbaende()) {

					tempGesamtStudenten = tempGesamtStudenten + sv.getAnzahlStudenten();

					final int row = svTable.getRowCount();
					svTable.setWidget(row, 0, new Label(sv.getJahrgang()));
					svTable.setWidget(row, 1, new Label(new Integer(sv.getAnzahlStudenten()).toString()));

					untenGrid.setText(0, 1,	new Integer(tempGesamtStudenten).toString());

				}
			} 
			else {
				untenGrid.setText(0, 1, "0");
			}
		}
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern eines
	 * SG ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void aendernMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für die Abänderung eines SG erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownStudiengang.setBezeichnung(bezeichnungTb.getText());
				shownStudiengang.setKuerzel(kuerzelTb.getText());

				verwaltung.aendernStudiengang(shownStudiengang,	new AsyncCallback<Studiengang>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenStudiengang(shownStudiengang, new AsyncCallback<Vector<Studiengang>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor","default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung des SG, wird der SG wieder 
							 *  in seiner ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Studiengang> result) {
								lvTable.clear();
								svTable.clear();
								dtvm.setSelectedStudiengang(result.elementAt(0));
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
						});
					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an den User und
					 * der studiengangDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Studiengang result) {
						Window.alert("Der Studiengang wurde erfolgreich geändert");
						dtvm.updateStudiengang(result);
						dtvm.setSelectedStudiengang(result);
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);
					}
				});
			}
		});

		// Initialisieren und Konfigurieren des Löschen-Buttons
		loeschenButton = new Button("Löschen");
		abschlussPanel.add(loeschenButton);

		loeschenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				verwaltung.loeschenStudiengang(shownStudiengang, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
						loeschenButton.setEnabled(true);

					}

					public void onSuccess(Void result) {
						Window.alert("Studiengang wurde erfolgreich gelöscht");
						dtvm.loeschenStudiengang(shownStudiengang);
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen eines
	 * SG ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void anlegenMaske() {

		// Ein neuer SG hat bei seiner Anlegung logischerweise noch keine offiziellen Studenten
		untenGrid.setText(0, 1, "0");

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen eines SG erfoderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				// "if-Zweig" wenn Dozenten ausgewählt wurden (kein muss für den User)
				if (lvVonNeuemSGVector != null && lvVonNeuemSGVector.size() > 0) {
					verwaltung.anlegenStudiengang(bezeichnungTb.getText(), kuerzelTb.getText(), lvVonNeuemSGVector,	new AsyncCallback<Studiengang>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							speichernAnlegenButton.setEnabled(true);

						}

						/* 
						 * Bei Erfolgreicher Anlegung erfolgt Meldung an den User und
						 * der studiengangDataProvider wird mittelbar aktualisiert
						 */
						public void onSuccess(Studiengang result) {
							Window.alert("Studiengang wurde erfolgreich angelegt");
							dtvm.addStudiengang(shownStudiengang);
							speichernAnlegenButton.setEnabled(true);
							clearForm();
						}
					});
				} 
				// "else-Zweig" wenn keine LV ausgewählt wurden (kein muss für den User)
				else {
					verwaltung.anlegenStudiengang(bezeichnungTb.getText(), kuerzelTb.getText(),	new AsyncCallback<Studiengang>() {
						public void onFailure(Throwable caught) {
							speichernAnlegenButton.setEnabled(true);
							Window.alert(caught.getMessage());

						}

						/* 
						 * Bei Erfolgreicher Anlegung erfolgt Meldung an den User und
						 * der studiengangDataProvider wird mittelbar aktualisiert
						 */
						public void onSuccess(Studiengang result) {
							Window.alert("Studiengang wurde erfolgreich angelegt");
							dtvm.addStudiengang(result);
							speichernAnlegenButton.setEnabled(true);
							clearForm();
						}
					});
				}
			}
		});
	}

	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearForm() {
		this.shownStudiengang = null;
		this.bezeichnungTb.setText("");
		this.kuerzelTb.setText("");
		this.lvListBox.setSelectedIndex(0);
		this.lvTable.removeAllRows();
		this.lvVonNeuemSGVector = null;
	}

}
