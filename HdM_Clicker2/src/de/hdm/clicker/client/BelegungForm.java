package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.hdm.clicker.shared.VerwaltungAsync;
import de.hdm.clicker.shared.bo.*;
import com.google.gwt.user.client.ui.Label;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten einer Belegung notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class BelegungForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;

	/**
	 * Referenz auf des CustomTreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	CustomTreeViewModel dtvm;

	/**
	 * Flag an dem zu erkennen ist, welche Variante der Benutzeroberfläche gegenwärtig ist
	 * (false = "Ändern-Maske" / true = "Anlegen-Maske")
	 */
	boolean anlegenMaske = false;

	/**
	 * Container für geladene und ausgewählte BusinessObjects
	 */
	Vector<Dozent> dozentenVectorforListBox = null;
	Vector<Raum> raeumeVectorforListBox = null;
	Vector<Lehrveranstaltung> lvVectorforListBox = null;
	Vector<Studiengang> studiengaengeVectorForListBox = null;
	Vector<Semesterverband> semesterverbaendeVectorForListBox = null;
	Vector<Semesterverband> semesterverbaendeVectorForListBoxAnlegen = null;
	Vector<Zeitslot> zeitslotsVectorForListBox = null;
	Vector<Belegung> svBelegungen = null;
	Vector<Semesterverband> SVvonNeuerBelegung = null;
	Vector<String> SGKuerzel = null;

	/**
	 * Widgets für den Kopfbereich der Maske
	 */
	Label studiengang;
	Label semesterverband;
	Grid auswahlGrid;
	ListBox studiengangListBox;
	ListBox semesterverbandListBox;
	Button tabVisibilityButton;
	Button tabsLeerenButton;
	Button auswahlAufhebenButton;
	VerticalPanel auswahlPanel;

	/**
	 * Widgets welche sich unterhalb des TabPanel befinden
	 * (welches zur Übersicht über die vorhanden Belegungen
	 * dient). Diese Widgets kommen nur in der Anlegen-Maske
	 * zum Einsatz.
	 */
	Grid anlegenGrid;
	Grid anlegenWSVGrid;
	ListBox anlegenTagListBox;
	ListBox anlegenUhrzeitListBox;
	ListBox anlegenLVListBox;
	ListBox anlegenRaumListBox;
	ListBox anlegenDozent1ListBox;
	ListBox anlegenDozent2ListBox;
	ListBox anlegenDozent3ListBox;
	Button anlegenButton;
	VerticalPanel anlegenPanel;
	VerticalPanel anlegenWSVPanel;

	/**
	 * Widgets welche für das Hinzufügen eines weiteren Semesterverbandes
	 * zu einer neuen Belegung benötigt werden. Sie bilden den unteren Schluss
	 * der Anlegen-Maske
	 */
	Button weitereSV;
	Label studiengangAnlegen;
	Label semesterverbandAnlegen;
	ListBox studiengangListBoxAnlegen;
	ListBox semesterverbandListBoxAnlegen;
	Button hinzufuegenButtonAnlegen;
	FlexTable svTable;

	/**
	 * Tabellen (Grids), welche je einen Wochentag abbilden und dem TabPanel
	 * zugeordnet werden. Zusätzlich noch ein Conatiner, welcher die einzelnen
	 * Grids aufnimmt um eine bessere Addressierung der einzelnen "Tage" zu
	 * ermöglichen 
	 */
	Vector<Grid> gridVector;
	Grid montagGrid;
	Grid dienstagGrid;
	Grid mittwochGrid;
	Grid donnerstagGrid;
	Grid freitagGrid;
	Grid samstagGrid;
	Grid sonntagGrid;
	TabPanel tabPanel;

	/**
	 * Container um die Widgtes welche den einzelnen Tagen (Grids) zugeordnet sind
	 * aufzunehmen, um deren Adressierung zu ermöglichen. Werden nur in der 
	 * Ändern-Maske benötigt  
	 */
	Vector<ListBox> lbv = new Vector<ListBox>();
	Vector<Button> bv = new Vector<Button>();

	/**
	 * Pointer welche bei der Initialisierung der Widgtes, welche dem TabPanel bzw.
	 * den einzelenen Wochentagen (Grids) zugeordnet werden, auf deren temporäre 
	 * Position in den ListBox- bzw. Button-Conatinern (lbv / bv) zeigen.
	 * Kommen nur bei Ändern-Maske zu Einsatz
	 */
	int belegungPointer = 0;

	int lvListBoxPointer = 0;
	int raumListBoxPointer = 1;
	int dozent1ListBoxPointer = 2;
	int dozent2ListBoxPointer = 3;
	int dozent3ListBoxPointer = 4;

	int aendernButtonPointer = 0;
	int loeschenButtonPointer = 1;

	int gridPointer;
	int rowPointer;

	/**
	 * Pointer welche durch ein ausgelöstes FocusEvent den Index des eventauslösenden
	 * Widgets als Wert annimmt. Anhand diesen Indexes können dann andere Methoden
	 * ermitteln, welche Widgets ggf. zu "disablen/enablen" sind und/oder welches Widget
	 * geladene Daten als Item erhält. Der "focusBelegungPointer" gibt immer an,
	 * aufgrund welcher Belegung weitere Aktionen ausgeführt werden, welche durch
	 * ein FokusEvent angestoßen wurden.
	 */
	Integer focusBelegungPointer = null;
	Integer focusRaumListBoxPointer = null;
	Integer focusDozentListBoxPointer = null;
	Integer focusOneOfThreeDozentListBoxPointer = null;
	Integer focusAendernButtonPointer = null;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltung - Referenz auf ein Proxy-Objekt. 
	 */
	public BelegungForm(VerwaltungAsync verwaltung) {
		this.verwaltung = verwaltung;

		// Initialisieren der Widgtes im Kopfbereich
		studiengang = new Label("Studiengang :");
		studiengangListBox = new ListBox();

		semesterverband = new Label("Semesterverband :");
		semesterverbandListBox = new ListBox();
		semesterverbandListBox.setEnabled(false);

		/*
		 *  Adden eines ChangeHandlers zur Semesterverband-Listbox,
		 *  so dass bei einer Auswahl automatisch die zugehörigen
		 *  LVs und und mittelbar die derzeitigen Belegungen des SVs
		 *  geladen werden
		 */
		semesterverbandListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"wait");
				
				/*
				 *  Nach dem Laden der Lehrveranstaltungen wird 
				 *  das Laden der Belegungen angestoßen
				 */
				ladenLehrveranstaltungen();
				tabsLeerenButton.setEnabled(false);
				disableAllTabWidgets();

				studiengangListBox.setEnabled(false);
				semesterverbandListBox.setEnabled(false);

				/*
				 *  Im Falle der Anlegen-Maske ist der Anlegen-Button
				 *  erst "enabled" wenn alle Daten geladen wurden
				 */
				if (anlegenMaske) {
					anlegenButton.setEnabled(false);
				}
			}
		});

		/*
		 *  Initialisieren eines Buttons der es ermöglicht die
		 *  Übersicht, also das TabPanel aus- und einzublenden,
		 *  dies kann bei einer niedrigen Bildschirmgröße hilfreich
		 *  sein (nur in der Anlegen-Maske verfügbar) 
		 */
		tabVisibilityButton = new Button("Übersicht verdecken");
		tabVisibilityButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				tabVisibility();
			}
		});

		/*
		 *  Initialisieren eines Buttons der es ermöglicht die
		 *  Übersicht zu leeren, damit sich er User wieder einem
		 *  anderen Semesterverband "widmen" kann 
		 */
		tabsLeerenButton = new Button("Tabs Leeren");
		tabsLeerenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				clearEntries();
			}
		});

		/*
		 *  Initialisieren eines Buttons der es ermöglicht die
		 *  "deaktivierte" Widgtes wieder zu "enablen"
		 *  (nur in der Ändern-Maske verfügbar) 
		 */
		auswahlAufhebenButton = new Button("Auswahl aufheben");
		auswahlAufhebenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				enableAllTabWidgets();
			}
		});
		auswahlAufhebenButton.setVisible(false);

		// Anordnen der Widgets im Kopfbereich
		auswahlGrid = new Grid(3, 3);
		auswahlGrid.setWidget(0, 0, studiengang);
		auswahlGrid.setWidget(0, 1, studiengangListBox);
		auswahlGrid.setWidget(1, 0, semesterverband);
		auswahlGrid.setWidget(1, 1, semesterverbandListBox);
		auswahlGrid.setWidget(2, 0, tabsLeerenButton);
		auswahlGrid.setWidget(2, 1, auswahlAufhebenButton);
		auswahlGrid.setWidget(2, 2, tabVisibilityButton);

		auswahlPanel = new VerticalPanel();
		auswahlPanel.add(auswahlGrid);

		this.add(auswahlPanel);

		/* 
		 * Initialisieren und anordnen der Widgets,
		 * welche in Summe eine 7-Tage-Woche abbilden
		 */
		gridVector = new Vector<Grid>();

		montagGrid = new Grid(8, 8);
		gridVector.add(montagGrid);

		dienstagGrid = new Grid(8, 8);
		gridVector.add(dienstagGrid);

		mittwochGrid = new Grid(8, 8);
		gridVector.add(mittwochGrid);

		donnerstagGrid = new Grid(8, 8);
		gridVector.add(donnerstagGrid);

		freitagGrid = new Grid(8, 8);
		gridVector.add(freitagGrid);

		samstagGrid = new Grid(8, 8);
		gridVector.add(samstagGrid);

		sonntagGrid = new Grid(8, 8);
		gridVector.add(sonntagGrid);

		tabPanel = new TabPanel();
		tabPanel.add(gridVector.elementAt(0), "Montag");
		tabPanel.add(gridVector.elementAt(1), "Dienstag");
		tabPanel.add(gridVector.elementAt(2), "Mittwoch");
		tabPanel.add(gridVector.elementAt(3), "Donnerstag");
		tabPanel.add(gridVector.elementAt(4), "Freitag");
		tabPanel.add(gridVector.elementAt(5), "Samstag");
		tabPanel.add(gridVector.elementAt(6), "Sonntag");
		tabPanel.selectTab(0);

		this.add(tabPanel);

	}

	/**
	 *  Hinzufügen eines ChangeHandlers zur "studiengangListBox".
	 *  Dadruch wird bei Auswahl eines Studienganges die zugeordneten
	 *  Semesterverbände in die "Studiengang-ListBox geladen"
	 */
	public void addChangeHandlerTosStudiengangListBox() {
		studiengangListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				ladenSemesterverbaende(false);
			}
		});
	}

	/**
	 *  Hinzufügen eines ChangeHandlers zur "studiengangListBoxAnlgen".
	 *  Dadruch wird bei Auswahl eines Studienganges die zugeordneten
	 *  Semesterverbände in die "Studiengang-ListBox geladen"
	 *  (nur in der Anlegen-Maske verfügbar und dient dem weiteren
	 *  hinzufügen von Semesterverbänden zur neuen Belegung)
	 */
	public void addChangeHandlerTosStudiengangListBoxAnlegen() {
		studiengangListBoxAnlegen.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				ladenSemesterverbaende(true);
			}
		});
	}

	/**
	 *  Hinzufügen eines ChangeHandlers zur "anlegenTagListBox".
	 *  Dadruch wird bei Auswahl eines Tages die "anlegenUhrzeitListBox" 
	 *  "enabled" (nur in der Anlegen-Maske verfügbar)
	 */
	public void addChangeHandlerToAnlegenTagListBox() {
		anlegenTagListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {

				if (anlegenTagListBox.getSelectedIndex() == 0) {
					anlegenUhrzeitListBox.setSelectedIndex(0);
					anlegenUhrzeitListBox.setEnabled(false);
				} 
				else if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
					anlegenRaumListBox.setEnabled(true);
					anlegenRaumListBox.clear();

					anlegenLVListBox.setEnabled(false);

					ladenRaeume();
				} 
				else {
					anlegenUhrzeitListBox.setEnabled(true);
				}

				raeumeVectorforListBox = null;
				anlegenRaumListBox.clear();
			}
		});
	}

	/**
	 *  Hinzufügen eines ChangeHandlers zur "anlegenUhrzeitListBox".
	 *  Dadruch wird bei Auswahl einer Uhrzeit eine Methode aufgerufen,
	 *  welche die zum gewählten Zeitpunkt verfügbaren (und der größe
	 *  des Semesterverbandes entsprechend) Räume läd
	 *  (nur in der Anlegen-Maske verfügbar).
	 */
	public void addChangeHandlerToAnlegenUhrzeitListBox() {
		anlegenUhrzeitListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				raeumeVectorforListBox = null;
				anlegenRaumListBox.clear();

				anlegenLVListBox.setEnabled(false);

				// Anstoß der Methode, welche die verfügbaren Räume läd
				ladenRaeume();
			}
		});
	}

	/**
	 *  Hinzufügen eines ChangeHandlers zur "anlegenLVListBox".
	 *  Dadruch wird bei Auswahl einer LV eine Methode aufgerufen,
	 *  welche die zur gewählten LV vorgesehenen Dozenten läd
	 *  (nur in der Anlegen-Maske verfügbar).
	 */
	public void addChangeHandlerToAnlegenLVListBox() {
		anlegenLVListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				anlegenLVListBox.setEnabled(false);
				dozentenVectorforListBox = null;
				anlegenDozent1ListBox.clear();
				anlegenDozent2ListBox.clear();
				anlegenDozent3ListBox.clear();

				anlegenTagListBox.setEnabled(false);
				anlegenUhrzeitListBox.setEnabled(false);

				// Anstoß der Methode, welche die vorgesehenen Dozenten läd
				ladenDozenten();
			}
		});
	}

	/**
	 * Methode die zu mittels Proxy-Objekt vom Server alle Studiengänge anfordert und diese
	 * in den dafür vorgesehenen Container ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void ladenStudiengaenge() {

		studiengangListBox.setEnabled(false);

		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
		studiengaengeVectorForListBox = new Vector<Studiengang>();
		studiengangListBox.clear();
		semesterverbandListBox.clear();

		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
						Window.alert(caught.getMessage());
					}

			public void onSuccess(Vector<Studiengang> result) {
				addChangeHandlerTosStudiengangListBox();
				studiengangListBox.addItem("Bitte wählen");
				if (anlegenMaske) {
					studiengangListBoxAnlegen.addItem("Bitte wählen");
				}
				for (Studiengang sG : result) {
					studiengangListBox.addItem(sG.getBezeichnung());
					studiengaengeVectorForListBox.add(sG);
					if (anlegenMaske) {
						studiengangListBoxAnlegen.addItem(sG.getBezeichnung());
					}
				}
				DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
				ladenZeitslots();
			}
		});
	}

	/**
	 * Methode die zu mittels Proxy-Objekt vom Server alle Semesterverbände anhand des in einer
	 * "Studiengang-Listbox" gewälten Studienganges anfordert und diese
	 * in den dafür vorgesehenen Container ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt
	 * 
	 *  @param	info - boolean welcher signalisiert, ob es sich um zusätzliche Semesterverbände,
	 *  		welche zur neuen Belegung hinzugefügt werden sollen, handelt oder nicht.
	 */
	public void ladenSemesterverbaende(boolean info) {

		// Dieser "if-Zweig" für Semesterverband-ListBox im Kopfbereich
		if (!info) {
			
			semesterverbandListBox.clear();
			
			if (studiengangListBox.getSelectedIndex() != 0) {
				
				verwaltung.auslesenSemesterverbaendeNachStudiengang(studiengaengeVectorForListBox.elementAt(studiengangListBox.getSelectedIndex() - 1),
						new AsyncCallback<Vector<Semesterverband>>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Vector<Semesterverband> result) {

								if (result == null || result.size() == 0) {
									Window.alert("Dem Studiengang sind momentan keine Semesterverbände zugeordnet");

								} 
								else {
									semesterverbaendeVectorForListBox = result;
									semesterverbandListBox.addItem("bitte wählen");
									for (Semesterverband sv : semesterverbaendeVectorForListBox) {
										semesterverbandListBox.addItem(sv.getJahrgang());
									}
									semesterverbandListBox.setEnabled(true);
								}
							}
						});
			}
		}
		// Dieser "if-Zweig" für Semesterverband-ListBox am untern Ende (Anlegen-Maske)
		if (info) {
			
			semesterverbandListBoxAnlegen.clear();
			
			if (studiengangListBoxAnlegen.getSelectedIndex() != 0) {
				verwaltung.auslesenSemesterverbaendeNachStudiengang(studiengaengeVectorForListBox.elementAt(studiengangListBoxAnlegen.getSelectedIndex() - 1),
						new AsyncCallback<Vector<Semesterverband>>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Vector<Semesterverband> result) {
								if (result == null || result.size() == 0) {
									Window.alert("Dem Studiengang sind momentan keine Semesterverbände zugeordnet");
									semesterverbandListBoxAnlegen.clear();
								} 
								else {
									semesterverbaendeVectorForListBoxAnlegen = result;
									semesterverbandListBoxAnlegen.clear();
									
									for (Semesterverband sv : semesterverbaendeVectorForListBoxAnlegen) {
										semesterverbandListBoxAnlegen.addItem(sv.getJahrgang());
									}
									semesterverbandListBoxAnlegen.setEnabled(true);
								}
							}
				});
			}
		}
	}

	/**
	 * Methode die zu mittels Proxy-Objekt vom Server alle Belegungen des gewählten
	 * Semesterverbandes läd und diese in den dafür vorgesehenen Container ablegt
	 */
	public void ladenBelegungen() {

		if (semesterverbaendeVectorForListBox != null) {
			verwaltung.auslesenBelegungenNachSV(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1),
					new AsyncCallback<Vector<Belegung>>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							tabsLeerenButton.setEnabled(true);

							studiengangListBox.setEnabled(true);
							semesterverbandListBox.setEnabled(true);
							if (anlegenMaske) {
								anlegenButton.setEnabled(true);
							}
						}

						public void onSuccess(Vector<Belegung> result) {
							tabsLeerenButton.setEnabled(true);

							if (result == null || result.size() == 0) {
								if (!anlegenMaske) {
									Window.alert("Es sind keine Belegungen zum gewählten Semesterverband angelegt");
									studiengangListBox.setEnabled(true);
									semesterverbandListBox.setEnabled(true);
								} else {
									Window.alert("Hinweis: Es sind noch keine Belegungen zum gewählten Semesterverband angelegt");
									anlegenTagListBox.setEnabled(true);
									anlegenLVListBox.setEnabled(true);

									anlegenButton.setEnabled(true);

								}
							} else {
								svBelegungen = result;
								if (!anlegenMaske) {
									multipleSemesterverbaendeWarning();
									fillContentTab();
								} else {
									fillContentTabAnlegen();

									anlegenButton.setEnabled(true);

									anlegenTagListBox.setEnabled(true);
									anlegenLVListBox.setEnabled(true);

									anlegenTagListBox.setSelectedIndex(0);
									anlegenUhrzeitListBox.setSelectedIndex(0);
									anlegenLVListBox.setSelectedIndex(0);
									if (raeumeVectorforListBox != null) {
										anlegenRaumListBox.setSelectedIndex(0);
									}
									if (dozentenVectorforListBox != null) {
										anlegenDozent1ListBox.setSelectedIndex(0);
										anlegenDozent2ListBox.setSelectedIndex(0);
										anlegenDozent3ListBox.setSelectedIndex(0);
									}
								}
							}
						}
					});
		} else {
			Window.alert("Kein Semesterverband vorhanden");
			tabsLeerenButton.setEnabled(true);
		}
	}

	/**
	 * Methode welchen den User darauf hinweist, wenn eine der geladenen Belegungen mehrere
	 * Semesterverbände referenziert, damit dieser gewarnt ist, dass sich Änderungen an solch
	 * Belegung auf mehrere Semesterverbände auswirkt
	 */
	public void multipleSemesterverbaendeWarning() {

		for (Belegung belegung : svBelegungen) {
			if (belegung.getSemesterverbaende().size() > 1) {
				Window.alert("Es sind Belegungen vorhanden, welche mehrere Semesterverbäende referenzieren"
					+ "\nÄndern-Button wird dann in roter Schrift angezeigt"
					+ "\nÄnderungen wirken sich auf alle referenzierten Semesterverbände aus"
					+ "\nDas Löschen einer Belegung hat jedoch nur auf den gewählten Semesterverband Einfluss");
				break;
			}
		}
	}

	/**
	 * Methode die zu mittels Proxy-Objekt vom Server alle Zeitslots läd und 
	 * diese in den dafür vorgesehenen Container ablegt
	 */
	public void ladenZeitslots() {
		verwaltung.auslesenAlleZeitslots(new AsyncCallback<Vector<Zeitslot>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Vector<Zeitslot> result) {
				zeitslotsVectorForListBox = result;
				fillContextTab();
			}
		});
	}

	/**
	 * Methode welche die Struktur aller Tage (Grids) des TabPanels erzeugt,
	 * also die Spalten- und Reihenüberschriften - Zeiten, LV, Raum und Dozenten
	 */
	public void fillContextTab() {

		int AnfangsWochentagZaehler = 0;
		int EndWochentagZaehler = 6;
		int gridRowZaehler = 0;

		for (int i = 0; i < 7; i++) {

			Grid tempGrid = (Grid) tabPanel.getWidget(i);
			tempGrid.setWidget(0, 0, new Label("Zeit"));
			tempGrid.setWidget(0, 1, new Label("Lehrveranstaltung"));
			tempGrid.setWidget(0, 2, new Label("Raum"));
			tempGrid.setWidget(0, 3, new Label("Dozent"));
			tempGrid.setWidget(0, 4, new Label("Dozent"));
			tempGrid.setWidget(0, 5, new Label("Dozent"));

			for (int j = AnfangsWochentagZaehler; j <= EndWochentagZaehler; j++) {
				int azStunde = zeitslotsVectorForListBox.elementAt(j).getAnfangszeit() / 60;
				int azMinute = zeitslotsVectorForListBox.elementAt(j).getAnfangszeit() % 60;
				int ezStunde = zeitslotsVectorForListBox.elementAt(j).getEndzeit() / 60;
				int ezMinute = zeitslotsVectorForListBox.elementAt(j).getEndzeit() % 60;

				StringBuffer tempAzStunde = new StringBuffer();
				tempAzStunde.append(azStunde);
				if (tempAzStunde.length() < 2) {
					tempAzStunde.insert(0, "0");
				}
				StringBuffer tempAzMinute = new StringBuffer();
				tempAzMinute.append(azMinute);
				if (tempAzMinute.length() < 2) {
					tempAzMinute.insert(0, "0");
				}
				StringBuffer tempEzStunde = new StringBuffer();
				tempEzStunde.append(ezStunde);
				if (tempEzStunde.length() < 2) {
					tempEzStunde.insert(0, "0");
				}
				StringBuffer tempEzMinute = new StringBuffer();
				tempEzMinute.append(ezMinute);
				if (tempEzMinute.length() < 2) {
					tempEzMinute.insert(0, "0");
				}

				StringBuffer zeitAnzeige = new StringBuffer();
				zeitAnzeige.append(tempAzStunde);
				zeitAnzeige.append(":");
				zeitAnzeige.append(tempAzMinute);
				zeitAnzeige.append(" bis ");
				zeitAnzeige.append(tempEzStunde);
				zeitAnzeige.append(":");
				zeitAnzeige.append(tempEzMinute);

				tempGrid.setWidget(gridRowZaehler + 1, 0, new Label(zeitAnzeige.toString()));
				gridRowZaehler++;
			}

			AnfangsWochentagZaehler = AnfangsWochentagZaehler + 7;
			EndWochentagZaehler = EndWochentagZaehler + 7;
			gridRowZaehler = 0;
		}
		studiengangListBox.setEnabled(true);
		if (anlegenMaske) {
			studiengangListBoxAnlegen.setEnabled(true);
		}
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle temporär erforderlichen/passenden
	 * Dozenten anfordert und diese in den dafür vorgesehenen Conatainer ablegt 
	 * sowie das entsprechende DropDown (ListBox(en)) befüllt 
	 */
	public void ladenDozenten() {

		tabsLeerenButton.setEnabled(false);

		/*
		 * Dieser "if-Zweig" im Falle Ändern-Maske, dabei werden alle zum Zeitpunkt 
		 * verfügbaren Dozenten in die drei ListBoxen einer Reihe, welche eine Belegung 
		 * darstellt geladen. Zusätzlich werden die Dozenten auch in einem Container
		 * abgelegt.
		 */
		if (!anlegenMaske) {
			verwaltung.auslesenDozentenNachZeitslot(svBelegungen.elementAt(focusBelegungPointer).getZeitslot(),	new AsyncCallback<Vector<Dozent>>() {
				
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
					auswahlAufhebenButton.setVisible(true);

					tabsLeerenButton.setEnabled(true);
				}

				public void onSuccess(Vector<Dozent> result) {
					dozentenVectorforListBox = result;
					for (Dozent dozent : result) {
						lbv.elementAt(focusDozentListBoxPointer).addItem(dozent.getNachname() + " "	+ dozent.getVorname());
					}

					lbv.elementAt(focusDozentListBoxPointer).addItem("kein Dozent");

					if (result != null && result.size() > 0) {
						if (focusOneOfThreeDozentListBoxPointer == 1) {
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 2).setEnabled(true);
						} 
						else if (focusOneOfThreeDozentListBoxPointer == 2) {
							lbv.elementAt(focusDozentListBoxPointer - 2).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer + 1).setEnabled(true);
						} 
						else {
							lbv.elementAt(focusDozentListBoxPointer - 3).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 2).setEnabled(true);
							lbv.elementAt(focusDozentListBoxPointer - 1).setEnabled(true);
						}
					}

					bv.elementAt(focusAendernButtonPointer).setEnabled(true);
					bv.elementAt(focusAendernButtonPointer + 1).setEnabled(true);

					auswahlAufhebenButton.setVisible(true);

					tabsLeerenButton.setEnabled(true);
				}
			});
		}
		/*
		 * Dieser "if-Zweig" im Falle Anlegen-Maske, dabei werden alle Dozenten geladen,
		 * welche die gewählte LV unterrichten und in den Dozent-ListBoxen unterhalb des
		 * TabPanels zur Verfügung gestellt. Zusätzlich werden diese auch in einem Container
		 * abgelegt.
		 */
		if (anlegenMaske) {
			if (anlegenLVListBox.getSelectedIndex() == 0) {
				Window.alert("Bitte wählen Sie eine Lehrveranstaltung aus");
				dozentenVectorforListBox = null;
				anlegenDozent1ListBox.setEnabled(false);
				anlegenDozent2ListBox.setEnabled(false);
				anlegenDozent3ListBox.setEnabled(false);

				anlegenTagListBox.setEnabled(true);
				anlegenUhrzeitListBox.setEnabled(true);

				anlegenDozent1ListBox.clear();
				anlegenDozent2ListBox.clear();
				anlegenDozent3ListBox.clear();

				tabsLeerenButton.setEnabled(true);
			} 
			else {
				verwaltung.auslesenDozentenNachLV(lvVectorforListBox.elementAt(anlegenLVListBox.getSelectedIndex() - 1),
						new AsyncCallback<Vector<Dozent>>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								anlegenTagListBox.setEnabled(true);
								anlegenUhrzeitListBox.setEnabled(true);

								tabsLeerenButton.setEnabled(true);
								anlegenLVListBox.setEnabled(true);
							}

							public void onSuccess(Vector<Dozent> result) {
								dozentenVectorforListBox = result;

								anlegenDozent1ListBox.clear();
								anlegenDozent2ListBox.clear();
								anlegenDozent3ListBox.clear();

								anlegenDozent1ListBox.addItem("bitte wählen");
								anlegenDozent2ListBox.addItem("optional");
								anlegenDozent3ListBox.addItem("optional");
								for (Dozent dozent : dozentenVectorforListBox) {
									anlegenDozent1ListBox.addItem(dozent.getNachname()+ " "	+ dozent.getVorname());
									anlegenDozent2ListBox.addItem(dozent.getNachname()+ " "	+ dozent.getVorname());
									anlegenDozent3ListBox.addItem(dozent.getNachname()+ " "	+ dozent.getVorname());	
									}

								anlegenTagListBox.setEnabled(true);
								anlegenUhrzeitListBox.setEnabled(true);

								tabsLeerenButton.setEnabled(true);
								anlegenLVListBox.setEnabled(true);

								if (result != null && result.size() > 0) {
									anlegenDozent1ListBox.setEnabled(true);
									anlegenDozent2ListBox.setEnabled(true);
									anlegenDozent3ListBox.setEnabled(true);
								} 
								else {
									Window.alert("Zur gewünschten Lehrveranstaltung ist kein Dozent vorgesehen");
									if (anlegenUhrzeitListBox.getSelectedIndex() == 0) {
										anlegenUhrzeitListBox.setEnabled(false);
									}
								}

							}
				});
			}
		}

	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle temporär erforderlichen/verfügbaren
	 * Räume anfordert und diese in den dafür vorgesehenen Conatainer ablegt 
	 * sowie das entsprechende DropDown (ListBox(en)) befüllt 
	 */
	public void ladenRaeume() {

		tabsLeerenButton.setEnabled(false);

		/*
		 * Dieser "if-Zweig" im Falle Ändern-Maske, dabei werden alle zum Zeitpunkt 
		 * verfügbaren Räume in die ListBox einer Reihe, welche eine Belegung 
		 * darstellt, geladen. Zusätzlich werden die Räume auch in einem Container
		 * abgelegt.
		 */
		if (!anlegenMaske) {
			verwaltung.auslesenVerfuegbareRaeumeZuZeitslotuSV(svBelegungen.elementAt(focusBelegungPointer).getZeitslot(),
					svBelegungen.elementAt(focusBelegungPointer).getSemesterverbaende(), new AsyncCallback<Vector<Raum>>() {
						public void onFailure(Throwable caught) {

							if (caught.getMessage().substring(0, 1).equals("K")) {
								Window.alert("Es ist kein anderer Raum zu diesem Zeitslot verf�gbar");
							} else {
								Window.alert(caught.getMessage());
							}
							auswahlAufhebenButton.setVisible(true);
							tabsLeerenButton.setEnabled(true);
						}

						public void onSuccess(Vector<Raum> result) {
							raeumeVectorforListBox = result;
							for (Raum raum : result) {
								lbv.elementAt(focusRaumListBoxPointer).addItem(raum.getBezeichnung());
							}

							lbv.elementAt(focusRaumListBoxPointer + 1).setEnabled(true);
							lbv.elementAt(focusRaumListBoxPointer + 2).setEnabled(true);
							lbv.elementAt(focusRaumListBoxPointer + 3).setEnabled(true);

							bv.elementAt(focusAendernButtonPointer).setEnabled(	true);
							bv.elementAt(focusAendernButtonPointer + 1).setEnabled(true);

							auswahlAufhebenButton.setVisible(true);

							tabsLeerenButton.setEnabled(true);
						}
					});
		}
		/*
		 * Dieser "if-Zweig" im Falle Anlegen-Maske, dabei werden alle Räume geladen,
		 * welche zum zuvor gewählten Zeitpunkt verfügbar sind. Zusätzlich werden 
		 * diese auch in einem Container abgelegt.
		 */
		if (anlegenMaske) {

			int zeitslotIndex;

			if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
				zeitslotIndex = ((anlegenTagListBox.getSelectedIndex() - 1) * 7) + anlegenUhrzeitListBox.getSelectedIndex();
			} 
			else {
				Window.alert("Bitte wählen Sie einen Tag und eine Uhrzeit aus");
				anlegenTagListBox.setSelectedIndex(0);
				anlegenUhrzeitListBox.setSelectedIndex(0);
				anlegenUhrzeitListBox.setEnabled(false);

				anlegenLVListBox.setEnabled(true);

				tabsLeerenButton.setEnabled(true);

				return;
			}

			Vector<Semesterverband> vSV = new Vector<Semesterverband>();
			vSV.add(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1));

			verwaltung.auslesenVerfuegbareRaeumeZuZeitslotuSV(zeitslotsVectorForListBox.elementAt(zeitslotIndex - 1),
					vSV, new AsyncCallback<Vector<Raum>>() {
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							anlegenTagListBox.setSelectedIndex(0);
							anlegenUhrzeitListBox.setSelectedIndex(0);
							anlegenUhrzeitListBox.setEnabled(false);

							raeumeVectorforListBox = null;
							anlegenRaumListBox.clear();

							tabsLeerenButton.setEnabled(true);
						}

						public void onSuccess(Vector<Raum> result) {
							raeumeVectorforListBox = result;
							anlegenRaumListBox.clear();

							anlegenRaumListBox.addItem("bitte wählen");
							for (Raum raum : raeumeVectorforListBox) {
								anlegenRaumListBox.addItem(raum.getBezeichnung());
							}
							anlegenRaumListBox.setEnabled(true);

							anlegenLVListBox.setEnabled(true);

							tabsLeerenButton.setEnabled(true);
						}
					});
		}

	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle LVs angefordert, welche dem Profil des 
	 * gewälten Semesterverbandes entsprechen und diese in den dafür vorgesehenen Conatainer ablegt 
	 * sowie das entsprechende DropDown (ListBox(en)) befüllt 
	 */
	public void ladenLehrveranstaltungen() {
		verwaltung.auslesenLehrveranstaltungenNachSV(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox
			.getSelectedIndex() - 1),studiengaengeVectorForListBox.elementAt(studiengangListBox.getSelectedIndex() - 1),
					new AsyncCallback<Vector<Lehrveranstaltung>>() {
						
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							tabsLeerenButton.setEnabled(true);
						}

						public void onSuccess(Vector<Lehrveranstaltung> result) {
							lvVectorforListBox = result;
							if (anlegenMaske) {
								anlegenLVListBox.clear();
								anlegenLVListBox.addItem("bitte wählen");
								
								for (Lehrveranstaltung lv : lvVectorforListBox) {
										anlegenLVListBox.addItem(lv.getBezeichnung());
									}
									anlegenGrid.setWidget(1, 2,	anlegenLVListBox);
								}
								ladenBelegungen();
							}
						});
	}

	/**
	 * Methode welche alle Widgets im TabPanel "enabled" 
	 * (Ändern-Maske)
	 */
	public void enableAllTabWidgets() {
		for (int i = 0; i < lbv.size(); i++) {
			lbv.elementAt(i).setSelectedIndex(0);
			lbv.elementAt(i).setEnabled(true);
		}
		for (int i = 0; i < bv.size(); i++) {
			bv.elementAt(i).setEnabled(true);

		}
	}

	/**
	 * Methode welche alle Widgets im TabPanel "disabled"
	 * (Ändern-Maske) 
	 */
	public void disableAllTabWidgets() {
		for (int i = 0; i < lbv.size(); i++) {
			lbv.elementAt(i).setEnabled(false);
		}
		for (int i = 0; i < bv.size(); i++) {
			bv.elementAt(i).setEnabled(false);

		}
	}

	/**
	 * Methode welche alle Widgets einer Reihe im TabPanel "disabled".
	 * Diese Mehtode wird durch ein FocusEvent-auslösendes Widget der
	 * selben Reihe ausgelöst, diesem Fall die Raum-ListBox 
	 * (Ändern-Maske)
	 */
	public void disableTabWidgetsDueRaumListBox() {
		for (int i = 0; i < lbv.size(); i++) {
			if (!((i > focusRaumListBoxPointer - 2) && (i < focusRaumListBoxPointer + 4))) {
				lbv.elementAt(i).setEnabled(false);
			}
		}
		for (int i = 0; i < bv.size(); i++) {
			if (!((i > focusAendernButtonPointer - 1) && (i < focusAendernButtonPointer + 2))) {
				bv.elementAt(i).setEnabled(false);
			}
		}
	}

	/**
	 * Methode welche alle Widgets einer Reihe im TabPanel "disabled".
	 * Diese Mehtode wird durch ein FocusEvent-auslösendes Widget der
	 * selben Reihe ausgelöst, diesem Fall die Dozent-ListBox 
	 * (Ändern-Maske)
	 */
	public void disableTabWidgetsDueDozentListBox(int dlb) {

		int leftward;
		int rightward;

		if (dlb == 1) {
			leftward = -3;
			rightward = 3;
		} else if (dlb == 2) {
			leftward = -4;
			rightward = 2;
		} else {
			leftward = -5;
			rightward = 1;
		}

		for (int i = 0; i < lbv.size(); i++) {
			if (!((i > focusDozentListBoxPointer + leftward) && (i < focusDozentListBoxPointer
					+ rightward))) {
				lbv.elementAt(i).setEnabled(false);
			}
		}
		for (int i = 0; i < bv.size(); i++) {
			if (!((i > focusAendernButtonPointer - 1) && (i < focusAendernButtonPointer + 2))) {
				bv.elementAt(i).setEnabled(false);
			}
		}
	}

	/**
	 * Methode welche die gewünschte Anzahl an ListBox-Objekten und 
	 * Buttons erzeugt und diese in einem Conatiner "verstaut", damit
	 * sie einzeln addressierbar sind 
	 * (Ändern-Maske)
	 * 
	 * @param	listboxen - int Anzahl an gewünschten ListBoxen 
	 * 			buttons - int Anzahl an gewünschten Buttons
	 */
	public void dynamicWidgets(int listboxen, int buttons) {
		lbv.clear();
		for (int i = 0; i < listboxen; i++) {
			ListBox tempListBox = new ListBox();
			lbv.add(tempListBox);
		}
		bv.clear();
		for (int i = 0; i < buttons; i++) {
			Button tempAendernButton = new Button("ändern");
			bv.add(tempAendernButton);
			Button tempLoeschenButton = new Button("löschen");
			bv.add(tempLoeschenButton);
		}
	}

	/**
	 * Methode welche das TabPanel entsprechend der Belegungen des gewählten Semesterverbands
	 * befüllt, so dass das TabPanel im Ganzen als Wochenübersicht für einen Semesterverband
	 * verstanden werden kann. Jede Reihe an Widgets stellt dabei eine Belegung dar, bei der die
	 * einzelnen Attributwerte mittels einer ListBox anzgezeigt werden, welche es dem User
	 * erlauben unmittelbar Änderungen an der gemeinten Belegung durchzuführen.
	 * (Ändern-Maske)
	 */
	public void fillContentTab() {

		// Pointer für die Adressierung der Wigdets (siehe Klassenanfang oben)
		belegungPointer = 0;

		lvListBoxPointer = 0;
		raumListBoxPointer = 1;
		dozent1ListBoxPointer = 2;
		dozent2ListBoxPointer = 3;
		dozent3ListBoxPointer = 4;

		aendernButtonPointer = 0;
		loeschenButtonPointer = 1;

		/*
		 *  Eine besteht aus 5 ListBoxen, einem Ändern-Button  und einem Löschen-Button,
		 *  dies mit der Anzahl der Belegungen multipliziert ergibt die benötigte Anzahl
		 *  an Widgets, welche durch folgenden Aufruf erzeugt werden
		 */
		dynamicWidgets(svBelegungen.size() * 5, svBelegungen.size());

		// Für jede Belegung...
		for (int i = 0; i < svBelegungen.size(); i++) {
			
			// Dieser Index wird bez. Sichbarkeit zusätzlich ausgelagert
			belegungPointer = i;

			/*
			 * ...wird anhand der Zeitslot-ID bestimmt, in welchem Tag (Grid) und welchen
			 * Reihe sie sich befindet
			 */
			
			// Montag
			if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				gridPointer = 0;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
			}
			
			// Dienstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 7	&& svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {
				gridPointer = 1;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			
			// Mittwoch
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				gridPointer = 2;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			
			// Donnerstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				gridPointer = 3;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			
			// Freitag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				gridPointer = 4;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			
			// Samstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				gridPointer = 5;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			
			// Sonntag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				gridPointer = 6;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}
			
			// Setzen des ersten Eintrags der LV-ListBox, welcher der LV, der temporären Belegung entspricht
			lbv.elementAt(lvListBoxPointer).clear();
			lbv.elementAt(lvListBoxPointer).addItem(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			
			// Setzen der restlichen Einträge in die LV-ListBox, aus denen der User eine andere LV wählen kann
			for (Lehrveranstaltung lv : lvVectorforListBox) {
				lbv.elementAt(lvListBoxPointer).addItem(lv.getBezeichnung());
			}
			
			// Anordnen der LV-ListBox an die richtige Position
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 1, lbv.elementAt(lvListBoxPointer));

			// Setzen des ersten Eintrags der Raum-ListBox, welcher dem Raum, der temporären Belegung entspricht
			lbv.elementAt(raumListBoxPointer).clear();
			lbv.elementAt(raumListBoxPointer).addItem(svBelegungen.elementAt(i).getRaum().getBezeichnung());

			/*
			 *  Hinzufügen eines FocusHandlers zur Raum-ListBox, damit bei "Click" auf die ListBox,
			 *  die verfügbaren Räume sozusagen "onDemand" der ListBox ergänzt werden
			 */
			lbv.elementAt(raumListBoxPointer).addFocusHandler(new FocusHandler() {

				// Zwischenspeicherung von erforderlichen Pointern bez. Sichtbarkeit
				int tempAendernButtonPointer = aendernButtonPointer;
				int tempRaumListBoxPointer = raumListBoxPointer;
				int tempBelegungPointer = belegungPointer;

				public void onFocus(FocusEvent event) {
					
					/*
					 *  Pointer (siehe Klassenanfang oben) um anderen Methoden zu zeigen, 
					 *  wohin ihr Ergebnis muss
					 */
					focusBelegungPointer = tempBelegungPointer;
					focusRaumListBoxPointer = tempRaumListBoxPointer;
					focusAendernButtonPointer = tempAendernButtonPointer;

					// "Merken" des ersten Items, da diese auch nach mehrmaligem Laden beibehalten werden soll
					String firstItem = lbv.elementAt(tempRaumListBoxPointer).getValue(0);
					lbv.elementAt(tempRaumListBoxPointer).clear();
					lbv.elementAt(tempRaumListBoxPointer).addItem(firstItem);

					/*
					 *  Disablen anderer Widgtes um eine weitere Datenbankkommunikation zu unterbinden
					 *  noch bevor das Ergebnis da ist
					 */
					lbv.elementAt(tempRaumListBoxPointer + 1).setEnabled(false);
					lbv.elementAt(tempRaumListBoxPointer + 2).setEnabled(false);
					lbv.elementAt(tempRaumListBoxPointer + 3).setEnabled(false);

					bv.elementAt(tempAendernButtonPointer).setEnabled(false);
					bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);

					tabsLeerenButton.setEnabled(false);

					// Laden der Räume
					ladenRaeume();

					disableTabWidgetsDueRaumListBox();
				}
			});

			// Anordnen der Raum-ListBox
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 2,lbv.elementAt(raumListBoxPointer));

			/*
			 *  Setzen des ersten Eintrags bzw. der ersten Einträge der Dozent-ListBoxen, welche den Dozenten, 
			 *  der temporären Belegung entsprechen
			 */
			lbv.elementAt(dozent1ListBoxPointer).clear();
			lbv.elementAt(dozent1ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(0).getNachname()+ " " 
			+ svBelegungen.elementAt(i).getDozenten().elementAt(0).getVorname());
			
			if (svBelegungen.elementAt(i).getDozenten().size() > 1) {
				lbv.elementAt(dozent2ListBoxPointer).clear();
				lbv.elementAt(dozent2ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(1).getNachname()
					+ " " + svBelegungen.elementAt(i).getDozenten().elementAt(1).getVorname());
			} 
			else {
				lbv.elementAt(dozent2ListBoxPointer).clear();
				lbv.elementAt(dozent2ListBoxPointer).addItem("---");
			}
			if (svBelegungen.elementAt(i).getDozenten().size() > 2) {
				lbv.elementAt(dozent3ListBoxPointer).clear();
				lbv.elementAt(dozent3ListBoxPointer).addItem(svBelegungen.elementAt(i).getDozenten().elementAt(2).getNachname()
					+ " " + svBelegungen.elementAt(i).getDozenten().elementAt(2).getVorname());
			} 
			else {
				lbv.elementAt(dozent3ListBoxPointer).clear();
				lbv.elementAt(dozent3ListBoxPointer).addItem("---");
			}

			/*
			 *  Hinzufügen eines FocusHandlers zur ersten Dozent-ListBox, damit bei "Click" auf die ListBox,
			 *  die verfügbaren Dozenten sozusagen "onDemand" der ListBox ergänzt werden
			 */
			lbv.elementAt(dozent1ListBoxPointer).addFocusHandler(new FocusHandler() {

				/*
				 *  Pointer (siehe Klassenanfang oben) um anderen Methoden zu zeigen, 
				 *  wohin ihr Ergebnis muss
				 */
				int tempAendernButtonPointer = aendernButtonPointer;
				int tempDozent1ListBoxPointer = dozent1ListBoxPointer;
				int tempBelegungPointer = belegungPointer;

				public void onFocus(FocusEvent event) {
					focusBelegungPointer = tempBelegungPointer;
					focusDozentListBoxPointer = tempDozent1ListBoxPointer;
					focusAendernButtonPointer = tempAendernButtonPointer;

					// "Merken" des ersten Items, da diese auch nach mehrmaligem Laden beibehalten werden soll
					String firstItem = lbv.elementAt(tempDozent1ListBoxPointer).getValue(0);
					lbv.elementAt(tempDozent1ListBoxPointer).clear();
					lbv.elementAt(tempDozent1ListBoxPointer).addItem(firstItem);

					/*
					 *  Disablen anderer Widgtes um eine weitere Datenbankkommunikation zu unterbinden
					 *  noch bevor das Ergebnis da ist
					 */
					lbv.elementAt(tempDozent1ListBoxPointer - 1).setEnabled(false);
					lbv.elementAt(tempDozent1ListBoxPointer + 1).setEnabled(false);
					lbv.elementAt(tempDozent1ListBoxPointer + 2).setEnabled(false);

					bv.elementAt(tempAendernButtonPointer).setEnabled(false);
					bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);

					tabsLeerenButton.setEnabled(false);

					focusOneOfThreeDozentListBoxPointer = 1;
					
					// Laden der Dozenten
					ladenDozenten();

					disableTabWidgetsDueDozentListBox(1);

				}
			});

			// (siehe "lbv.elementAt(dozent1ListBoxPointer)")
			lbv.elementAt(dozent2ListBoxPointer).addFocusHandler(new FocusHandler() {

				int tempAendernButtonPointer = aendernButtonPointer;
				int tempDozent2ListBoxPointer = dozent2ListBoxPointer;
				int tempBelegungPointer = belegungPointer;

				public void onFocus(FocusEvent event) {
					focusBelegungPointer = tempBelegungPointer;
					focusDozentListBoxPointer = tempDozent2ListBoxPointer;
					focusAendernButtonPointer = tempAendernButtonPointer;

					String firstItem = lbv.elementAt(tempDozent2ListBoxPointer).getValue(0);
					lbv.elementAt(tempDozent2ListBoxPointer).clear();
					lbv.elementAt(tempDozent2ListBoxPointer).addItem(firstItem);

					lbv.elementAt(tempDozent2ListBoxPointer - 2).setEnabled(false);
					lbv.elementAt(tempDozent2ListBoxPointer - 1).setEnabled(false);
					lbv.elementAt(tempDozent2ListBoxPointer + 1).setEnabled(false);

					bv.elementAt(tempAendernButtonPointer).setEnabled(false);
					bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);

					tabsLeerenButton.setEnabled(false);

					focusOneOfThreeDozentListBoxPointer = 2;
					
					ladenDozenten();

					disableTabWidgetsDueDozentListBox(2);

				}
			});

			// (siehe "lbv.elementAt(dozent1ListBoxPointer)")
			lbv.elementAt(dozent3ListBoxPointer).addFocusHandler(new FocusHandler() {

				int tempAendernButtonPointer = aendernButtonPointer;
				int tempDozent3ListBoxPointer = dozent3ListBoxPointer;
				int tempBelegungPointer = belegungPointer;

				public void onFocus(FocusEvent event) {
					focusBelegungPointer = tempBelegungPointer;
					focusDozentListBoxPointer = tempDozent3ListBoxPointer;
					focusAendernButtonPointer = tempAendernButtonPointer;

					String firstItem = lbv.elementAt(tempDozent3ListBoxPointer).getValue(0);
					lbv.elementAt(tempDozent3ListBoxPointer).clear();
					lbv.elementAt(tempDozent3ListBoxPointer).addItem(firstItem);

					lbv.elementAt(tempDozent3ListBoxPointer - 3).setEnabled(false);
					lbv.elementAt(tempDozent3ListBoxPointer - 2).setEnabled(false);
					lbv.elementAt(tempDozent3ListBoxPointer - 1).setEnabled(false);

					bv.elementAt(tempAendernButtonPointer).setEnabled(false);
					bv.elementAt(tempAendernButtonPointer + 1).setEnabled(false);

					tabsLeerenButton.setEnabled(false);

					focusOneOfThreeDozentListBoxPointer = 3;
					
					ladenDozenten();

					disableTabWidgetsDueDozentListBox(3);

				}
			});

			// Anordnen der Dozent-ListBoxen
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 3, lbv.elementAt(dozent1ListBoxPointer));
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, lbv.elementAt(dozent2ListBoxPointer));
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 5, lbv.elementAt(dozent3ListBoxPointer));

			/*
			 *  Der Ändern-Button einer Belegung, welche mehrere Semesterverbände referenziert,
			 *  wird farblich hervorgehoben
			 */
			if (svBelegungen.elementAt(i).getSemesterverbaende().size() > 1) {
				bv.elementAt(aendernButtonPointer).setStyleName("gwt-MultipleSVs-Button");
			}
			
			/*
			 *  Hinzufügen eines ClickHandlers zum Ändern-Button, welcher anschließend 
			 *  Funktionalitäten impliziert um mit dem Server zu kommunizieren um die bearbeitete
			 *  Belegung zu übertragen
			 */
			bv.elementAt(aendernButtonPointer).addClickHandler(new ClickHandler() {

				int tempBelegungPointer = belegungPointer;

				int tempLvListBoxPointer = lvListBoxPointer;
				int tempRaumListBoxPointer = raumListBoxPointer;
				int tempDozent1ListBoxPointer = dozent1ListBoxPointer;
				int tempDozent2ListBoxPointer = dozent2ListBoxPointer;
				int tempDozent3ListBoxPointer = dozent3ListBoxPointer;

				public void onClick(ClickEvent event) {

					tabsLeerenButton.setEnabled(false);

						auswahlAufhebenButton.setVisible(false);
						disableAllTabWidgets();

						// Prüfwert, bleibt diese "true", wurde nichts geändert und es erfolgt keine Anfrage an den Server
						boolean check1 = true;

						/*
						 * Zunächst wird aus allen ListBoxen der gewählte Index ausgelesen um damit die richtigen
						 * BusinessObjects aus den jeweiligen Containern zu adressieren, damit man diese wiederum
						 * der zu ändernden Belegung hinzufügen kann (und es auch tut)
						 */
						if (lbv.elementAt(tempLvListBoxPointer).getSelectedIndex() > 0) {
								
							svBelegungen.elementAt(tempBelegungPointer)
								.setLehrveranstaltung(lvVectorforListBox.elementAt(lbv.elementAt(tempLvListBoxPointer).getSelectedIndex() - 1));
								check1 = false;
							}
							if (lbv.elementAt(tempRaumListBoxPointer).getSelectedIndex() > 0 && tempBelegungPointer == focusBelegungPointer) {
								
								svBelegungen.elementAt(tempBelegungPointer)
								.setRaum(raeumeVectorforListBox.elementAt(lbv.elementAt(tempRaumListBoxPointer).getSelectedIndex() - 1));
								check1 = false;
							}

							Vector<Dozent> tempDozenten = new Vector<Dozent>();
							
							// 1.Dozent
							if (lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() > 0	&& 
									(!(lbv.elementAt(tempDozent1ListBoxPointer).getValue(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() - 1));
								check1 = false;

							}
							if (lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex() == 0 && 
									(!(lbv.elementAt(tempDozent1ListBoxPointer).getValue(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(0));
							}

							if (lbv.elementAt(tempDozent1ListBoxPointer).getValue(lbv.elementAt(tempDozent1ListBoxPointer).getSelectedIndex()).equals("kein Dozent") 
									&& svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(0) != null) {
								check1 = false;
							}
							
							// 2.Dozent
							if (lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex() > 0	&& 
									(!(lbv.elementAt(tempDozent2ListBoxPointer).getValue(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex() - 1));
								check1 = false;

							}
							if (lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex() == 0 && svBelegungen.elementAt(tempBelegungPointer).getDozenten().size() > 1 && 
									(!(lbv.elementAt(tempDozent2ListBoxPointer).getValue(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(1));

							}
							if (lbv.elementAt(tempDozent2ListBoxPointer).getValue(lbv.elementAt(tempDozent2ListBoxPointer).getSelectedIndex()).equals("kein Dozent") 
									&& svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(1) != null) {
								check1 = false;
							}
							
							// 3.Dozent
							if (lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex() > 0	&& 
									(!(lbv.elementAt(tempDozent3ListBoxPointer).getValue(lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(dozentenVectorforListBox.elementAt(lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex() - 1));
								check1 = false;

							}
							if (lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex() == 0 && svBelegungen.elementAt(tempBelegungPointer).getDozenten().size() > 2 && 
									(!(lbv.elementAt(tempDozent3ListBoxPointer).getValue(lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex()).equals("kein Dozent")))) {
								
								tempDozenten.add(svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(2));
							}
							if (lbv.elementAt(tempDozent3ListBoxPointer).getValue(lbv.elementAt(tempDozent3ListBoxPointer).getSelectedIndex()).equals("kein Dozent") 
									&& svBelegungen.elementAt(tempBelegungPointer).getDozenten().elementAt(2) != null) {
								check1 = false;
							}

							svBelegungen.elementAt(tempBelegungPointer).setDozenten(tempDozenten);

							if (!check1) {
								// Methoden-Aufruf über Proxy-Objekt um eine Beleung zu ändern
								verwaltung.aendernBelegung(svBelegungen.elementAt(tempBelegungPointer),	new AsyncCallback<Belegung>() {
									public void onFailure(Throwable caught) {
										Window.alert(caught.getMessage());

										enableAllTabWidgets();
										tabsLeerenButton.setEnabled(true);

										verwaltung.auslesenBelegung(svBelegungen.elementAt(tempBelegungPointer), new AsyncCallback<Vector<Belegung>>() {
											
											public void onFailure(Throwable caught) {
												Window.alert(caught.getMessage());
											}

											/*
											 *   Bei fehlgeschlagener Änderung, wird die ursprüngliche Belegung
											 *   erneut nach geladen und der "alte" Zustand wiedehergestellt
											 */
											public void onSuccess(Vector<Belegung> result) {
												svBelegungen.setElementAt(result.elementAt(0), tempBelegungPointer);

												raeumeVectorforListBox = null;
												focusBelegungPointer = null;
												focusRaumListBoxPointer = null;
												focusAendernButtonPointer = null;
											}
										});
									}

									// Bei erfolgreicher Änderung erfolgt Meldung an den User									 
									public void onSuccess(Belegung result) {
										Window.alert("Belegung wurde erfolgreich geaendert");

										/*
										 * Die gewählten "Indices" der Listboxen müssen nun festgehalten werden
										 * um nach dem "clearen" der Listboxen, die Änderungen als neue erste
										 * Items in die ListBoxen hinzufügen zu können
										 */
										if (focusRaumListBoxPointer != null) {
											String tempFirstRaumItem = lbv.elementAt(focusRaumListBoxPointer)
												.getValue(lbv.elementAt(focusRaumListBoxPointer).getSelectedIndex());
											lbv.elementAt(focusRaumListBoxPointer).clear();
											lbv.elementAt(focusRaumListBoxPointer).addItem(tempFirstRaumItem);

											raeumeVectorforListBox = null;
											focusBelegungPointer = null;
											focusRaumListBoxPointer = null;
											focusAendernButtonPointer = null;
										}
										
										if (focusDozentListBoxPointer != null) {
											String tempFirstDozent1Item;
											String tempFirstDozent2Item;
											String tempFirstDozent3Item;
											
											if (focusOneOfThreeDozentListBoxPointer == 1) {
												tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
											    tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer + 1).getValue(lbv.elementAt(focusDozentListBoxPointer + 1).getSelectedIndex());
												tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer + 2).getValue(lbv.elementAt(focusDozentListBoxPointer + 2).getSelectedIndex());
												
												lbv.elementAt(focusDozentListBoxPointer).clear();
												lbv.elementAt(focusDozentListBoxPointer + 1).clear();
												lbv.elementAt(focusDozentListBoxPointer + 2).clear();
												
												// ListBox-Auswahl wird nach links gerückt
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent3Item;
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (!tempFirstDozent2Item.equals("kein Dozent") || !tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent2Item;
													}
													if (!tempFirstDozent3Item.equals("kein Dozent") || !tempFirstDozent3Item.equals("---")) {
														tempFirstDozent2Item = tempFirstDozent3Item;
														tempFirstDozent3Item = "---";
													}
													else {
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (!tempFirstDozent1Item.equals("kein Dozent") && (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) &&
														((!tempFirstDozent3Item.equals("kein Dozent") && !tempFirstDozent3Item.equals("---")))) {
													tempFirstDozent2Item = tempFirstDozent3Item;
													tempFirstDozent3Item = "---";
												}
												
												lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent1Item);
												lbv.elementAt(focusDozentListBoxPointer + 1).addItem(tempFirstDozent2Item);
												lbv.elementAt(focusDozentListBoxPointer + 2).addItem(tempFirstDozent3Item);
												
											}
											else if (focusOneOfThreeDozentListBoxPointer == 2) {
												tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer - 1).getValue(lbv.elementAt(focusDozentListBoxPointer - 1).getSelectedIndex());
												tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
												tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer + 1).getValue(lbv.elementAt(focusDozentListBoxPointer + 1).getSelectedIndex());
												
												lbv.elementAt(focusDozentListBoxPointer - 1).clear();
												lbv.elementAt(focusDozentListBoxPointer).clear();
												lbv.elementAt(focusDozentListBoxPointer + 1).clear();
												
												// ListBox-Auswahl wird nach links gerückt
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent3Item;
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (!tempFirstDozent2Item.equals("kein Dozent") || !tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent2Item;
													}
													if (!tempFirstDozent3Item.equals("kein Dozent") || !tempFirstDozent3Item.equals("---")) {
														tempFirstDozent2Item = tempFirstDozent3Item;
														tempFirstDozent3Item = "---";
													}
													else {
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (!tempFirstDozent1Item.equals("kein Dozent") && (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) &&
														((!tempFirstDozent3Item.equals("kein Dozent") && !tempFirstDozent3Item.equals("---")))) {
													tempFirstDozent2Item = tempFirstDozent3Item;
													tempFirstDozent3Item = "---";
												}
												
												lbv.elementAt(focusDozentListBoxPointer - 1).addItem(tempFirstDozent1Item);
												lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent2Item);
												lbv.elementAt(focusDozentListBoxPointer + 1).addItem(tempFirstDozent3Item);
											}
											else {
												tempFirstDozent1Item = lbv.elementAt(focusDozentListBoxPointer - 2).getValue(lbv.elementAt(focusDozentListBoxPointer - 2).getSelectedIndex());
												tempFirstDozent2Item = lbv.elementAt(focusDozentListBoxPointer - 1).getValue(lbv.elementAt(focusDozentListBoxPointer - 1).getSelectedIndex());
												tempFirstDozent3Item = lbv.elementAt(focusDozentListBoxPointer).getValue(lbv.elementAt(focusDozentListBoxPointer).getSelectedIndex());
												
												lbv.elementAt(focusDozentListBoxPointer - 2).clear();
												lbv.elementAt(focusDozentListBoxPointer - 1).clear();
												lbv.elementAt(focusDozentListBoxPointer).clear();
												
												// ListBox-Auswahl wird nach links gerückt
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent3Item;
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (tempFirstDozent1Item.equals("kein Dozent")) {
													if (!tempFirstDozent2Item.equals("kein Dozent") || !tempFirstDozent2Item.equals("---")) {
														tempFirstDozent1Item = tempFirstDozent2Item;
													}
													if (!tempFirstDozent3Item.equals("kein Dozent") || !tempFirstDozent3Item.equals("---")) {
														tempFirstDozent2Item = tempFirstDozent3Item;
														tempFirstDozent3Item = "---";
													}
													else {
														tempFirstDozent2Item = "---";
														tempFirstDozent3Item = "---";
													}
												}
												if (!tempFirstDozent1Item.equals("kein Dozent") && (tempFirstDozent2Item.equals("kein Dozent") || tempFirstDozent2Item.equals("---")) &&
														((!tempFirstDozent3Item.equals("kein Dozent") && !tempFirstDozent3Item.equals("---")))) {
													tempFirstDozent2Item = tempFirstDozent3Item;
													tempFirstDozent3Item = "---";
												}
												
												lbv.elementAt(focusDozentListBoxPointer - 2).addItem(tempFirstDozent1Item);
												lbv.elementAt(focusDozentListBoxPointer - 1).addItem(tempFirstDozent2Item);
												lbv.elementAt(focusDozentListBoxPointer).addItem(tempFirstDozent3Item);
											}
											
											dozentenVectorforListBox = null;
											focusBelegungPointer = null;
											focusDozentListBoxPointer = null;
											focusOneOfThreeDozentListBoxPointer = null;
											focusAendernButtonPointer = null;										
										}

										String itemLVListBox = lbv.elementAt(tempLvListBoxPointer).getValue(lbv.elementAt(tempLvListBoxPointer).getSelectedIndex());

										enableAllTabWidgets();

										tabsLeerenButton.setEnabled(true);

										lbv.elementAt(tempLvListBoxPointer).clear();
										lbv.elementAt(tempLvListBoxPointer).addItem(itemLVListBox);

										for (Lehrveranstaltung lv : lvVectorforListBox) {
											lbv.elementAt(tempLvListBoxPointer).addItem(lv.getBezeichnung());
										}

									}
								});
							}
							if (check1) {
								Window.alert("Es wurde keine Aenderung vorgenommen");

								enableAllTabWidgets();
								tabsLeerenButton.setEnabled(true);
							}
						}
					});

			// Anordnen der Ändern-Buttons
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 6, bv.elementAt(aendernButtonPointer));

			/*
			 *  Hinzufügen eines ClickHandlers zum Löschen-Button, welcher anschließend 
			 *  Funktionalitäten impliziert um mit dem Server zu kommunizieren um die gemeinte
			 *  Belegung zu löschen
			 */
			bv.elementAt(loeschenButtonPointer).addClickHandler(new ClickHandler() {

				int tempRowPointer = rowPointer;
				int tempGridPointer = gridPointer;
				int tempBelegungPointer = belegungPointer;

				public void onClick(ClickEvent event) {
					auswahlAufhebenButton.setVisible(false);
					disableAllTabWidgets();
					tabsLeerenButton.setEnabled(false);
							
					verwaltung.loeschenBelegungen(svBelegungen.elementAt(tempBelegungPointer), 
							semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1), new AsyncCallback<Void>() {
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());

									enableAllTabWidgets();
									tabsLeerenButton.setEnabled(true);

								}

								// Bei erfolgreicher Löschung wird die entsprechende Reihe von Widgets "befreit"
								public void onSuccess(Void result) {
									Window.alert("Die Belegung wurde erfolgreich geloescht!");
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 1, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 2, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 3, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 4, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 5, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 6, null);
									gridVector.elementAt(tempGridPointer).setWidget(tempRowPointer, 7, null);
									// svBelegungen.removeElementAt(tempBelegungPointer);

									enableAllTabWidgets();

									tabsLeerenButton.setEnabled(true);

								}
							});
						}
					});

			// Anordnen des Löschen-Buttons
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 7, bv.elementAt(loeschenButtonPointer));

			// Hochzählen von Pointern für die nächste Iteration bzw. Belegung
			lvListBoxPointer = lvListBoxPointer + 5;
			raumListBoxPointer = raumListBoxPointer + 5;
			dozent1ListBoxPointer = dozent1ListBoxPointer + 5;
			dozent2ListBoxPointer = dozent2ListBoxPointer + 5;
			dozent3ListBoxPointer = dozent3ListBoxPointer + 5;

			aendernButtonPointer = aendernButtonPointer + 2;
			loeschenButtonPointer = loeschenButtonPointer + 2;

			gridPointer = 0;
			rowPointer = 0;
		}

		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");

	}

	/**
	 * Methode welche das TabPanel entsprechend der Belegungen des gewählten Semesterverbands
	 * befüllt, so dass das TabPanel im Ganzen als Wochenübersicht für einen Semesterverband
	 * verstanden werden kann. Jede Reihe mit Einträgen stellt dabei eine Belegung dar.
	 * (Anlegen-Maske)
	 */
	public void fillContentTabAnlegen() {

		belegungPointer = 0;

		// Für jede Belegung...
		for (int i = 0; i < svBelegungen.size(); i++) {
			belegungPointer = i;

			/*
			 * ...wird anhand der Zeitslot-ID bestimmt, in welchem Tag (Grid) und welchen
			 * Reihe sie sich befindet
			 */
			
			// Montag
			if (svBelegungen.elementAt(i).getZeitslot().getId() <= 7) {
				gridPointer = 0;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId();
			}			
			// Dienstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 7	&& svBelegungen.elementAt(i).getZeitslot().getId() <= 14) {
				gridPointer = 1;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 7;
			}
			// Mittwoch
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 14 && svBelegungen.elementAt(i).getZeitslot().getId() <= 21) {
				gridPointer = 2;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 14;
			}
			// Donnerstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 21 && svBelegungen.elementAt(i).getZeitslot().getId() <= 28) {
				gridPointer = 3;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 21;
			}
			// Freitag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 28 && svBelegungen.elementAt(i).getZeitslot().getId() <= 35) {
				gridPointer = 4;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 28;
			}
			// Samstag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 35 && svBelegungen.elementAt(i).getZeitslot().getId() <= 42) {
				gridPointer = 5;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 35;
			}
			// Sonntag
			if (svBelegungen.elementAt(i).getZeitslot().getId() > 42 && svBelegungen.elementAt(i).getZeitslot().getId() <= 49) {
				gridPointer = 6;
				rowPointer = svBelegungen.elementAt(i).getZeitslot().getId() - 42;
			}

			// Initialisieren, beschriften und andordnen des LV-Labels
			Label lvLabel = new Label();
			lvLabel.setText(svBelegungen.elementAt(i).getLehrveranstaltung().getBezeichnung());
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 1, lvLabel);

			// Initialisieren, beschriften und andordnen des Raum-Labels
			Label raumLabel = new Label();
			raumLabel.setText(svBelegungen.elementAt(i).getRaum().getBezeichnung());
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 2,raumLabel);

			// Initialisieren, beschriften und andordnen des Dozent1-Labels
			Label dozentLabel1 = new Label();
			dozentLabel1.setText(svBelegungen.elementAt(i).getDozenten().elementAt(0).getNachname()	+ " "
					+ svBelegungen.elementAt(i).getDozenten().elementAt(0).getVorname());
			gridVector.elementAt(gridPointer).setWidget(rowPointer, 3, dozentLabel1);

			// Initialisieren, beschriften und andordnen des Dozent2-Labels
			if (svBelegungen.elementAt(i).getDozenten().size() > 1) {
				Label dozentLabel2 = new Label();
				dozentLabel2.setText(svBelegungen.elementAt(i).getDozenten().elementAt(1).getNachname()	+ " "
						+ svBelegungen.elementAt(i).getDozenten().elementAt(1).getVorname());
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, dozentLabel2);
			}

			// Initialisieren, beschriften und andordnen des Dozent3-Labels
			if (svBelegungen.elementAt(i).getDozenten().size() > 2) {
				Label dozentLabel3 = new Label();
				dozentLabel3.setText(svBelegungen.elementAt(i).getDozenten().elementAt(2).getNachname()	+ " "
						+ svBelegungen.elementAt(i).getDozenten().elementAt(2).getVorname());
				gridVector.elementAt(gridPointer).setWidget(rowPointer, 4, dozentLabel3);
			}

			gridPointer = 0;
			rowPointer = 0;
		}

		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");

	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen einer
	 * Belegung (vorrangig) ermöglicht 
	 * (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 * 
	 * @param	info - boolean zur Angabe der Masken-Variante (true = anlegen / false = ändern)
	 */
	public void aendernMaske(boolean info) {
		
		// Flag die die Funktionsweise anderer Methoden beeinflusst
		this.anlegenMaske = info;
		
		if (info) {
			
			/*
			 * Instanziieren, "befüllen" und anordnen von Widgets
			 * welche sich unterhalb des TabPanels befinden
			 */
			anlegenGrid = new Grid(4, 6);
			Label tagLabel = new Label("Tag");
			Label uhrzeitLabel = new Label("Uhrzeit");
			Label lvLabel = new Label("Lehrveranstaltung");
			Label raumLabel = new Label("Raum");
			Label dozentLabel = new Label("Dozent");

			anlegenGrid.setWidget(0, 0, tagLabel);
			anlegenGrid.setWidget(0, 1, uhrzeitLabel);
			anlegenGrid.setWidget(0, 2, lvLabel);
			anlegenGrid.setWidget(0, 3, raumLabel);
			anlegenGrid.setWidget(0, 4, dozentLabel);

			// Befüllen der "anlegenTagListBox" mit vordefinierten Items
			anlegenTagListBox = new ListBox();
			anlegenTagListBox.addItem("bitte wählen");
			anlegenTagListBox.addItem("Montag");
			anlegenTagListBox.addItem("Dienstag");
			anlegenTagListBox.addItem("Mittwoch");
			anlegenTagListBox.addItem("Donnerstag");
			anlegenTagListBox.addItem("Freitag");
			anlegenTagListBox.addItem("Samstag");
			anlegenTagListBox.addItem("Sonntag");

			addChangeHandlerToAnlegenTagListBox();
			anlegenGrid.setWidget(1, 0, anlegenTagListBox);

			// Befüllen der "anlegenUhrzeitListBox" mit vordefinierten Items
			anlegenUhrzeitListBox = new ListBox();
			anlegenUhrzeitListBox.addItem("bitte wählen");
			anlegenUhrzeitListBox.addItem("08:15 - 09:45");
			anlegenUhrzeitListBox.addItem("10:00 - 11:30");
			anlegenUhrzeitListBox.addItem("11:45 - 13:15");
			anlegenUhrzeitListBox.addItem("14:15 - 15:45");
			anlegenUhrzeitListBox.addItem("16:00 - 17:30");
			anlegenUhrzeitListBox.addItem("17:45 - 19:15");
			anlegenUhrzeitListBox.addItem("19:30 - 21:00");

			addChangeHandlerToAnlegenUhrzeitListBox();
			anlegenGrid.setWidget(1, 1, anlegenUhrzeitListBox);

			anlegenLVListBox = new ListBox();
			anlegenRaumListBox = new ListBox();
			anlegenDozent1ListBox = new ListBox();
			anlegenDozent2ListBox = new ListBox();
			anlegenDozent3ListBox = new ListBox();

			addChangeHandlerToAnlegenLVListBox();
			anlegenGrid.setWidget(1, 2, anlegenLVListBox);

			anlegenGrid.setWidget(1, 3, anlegenRaumListBox);
			anlegenGrid.setWidget(1, 4, anlegenDozent1ListBox);
			anlegenGrid.setWidget(2, 4, anlegenDozent2ListBox);
			anlegenGrid.setWidget(3, 4, anlegenDozent3ListBox);

			anlegenTagListBox.setEnabled(false);
			anlegenUhrzeitListBox.setEnabled(false);
			anlegenLVListBox.setEnabled(false);
			anlegenRaumListBox.setEnabled(false);
			anlegenDozent1ListBox.setEnabled(false);
			anlegenDozent2ListBox.setEnabled(false);
			anlegenDozent3ListBox.setEnabled(false);

			/*
			 * Hinzufügen eines ClickHandlers zum Anlegen-Button um
			 * mittelbar das Anlegen einer Belegung auf Serverseite 
			 * anzustoßen, außerdem werden andere Widgets "disabled"
			 * um eine sich blockierende Datenbankkommunikation 
			 * auszuschließen
			 */
			anlegenButton = new Button("Belegung anlegen");
			anlegenButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					anlegenButton.setEnabled(false);
					tabsLeerenButton.setEnabled(false);
					anlegenTagListBox.setEnabled(false);
					anlegenUhrzeitListBox.setEnabled(false);
					anlegenLVListBox.setEnabled(false);
					anlegenRaumListBox.setEnabled(false);
					anlegenDozent1ListBox.setEnabled(false);
					anlegenDozent2ListBox.setEnabled(false);
					anlegenDozent3ListBox.setEnabled(false);
					belegungAnlegen();
				}
			});
			anlegenButton.setEnabled(false);

			anlegenGrid.setWidget(3, 5, anlegenButton);

			this.anlegenPanel = new VerticalPanel();
			this.anlegenPanel.add(anlegenGrid);

			/*
			 * Instanziieren und mittels ClickHandler konfigurieren eines Buttons,
			 * um bei Wunsch ein Panel am unteren Rand sichtbar zu machen, das es 
			 * dem User ermöglicht, weitere Semesterverbände zur neuen Belegung
			 * hinzuzufügen
			 */
			weitereSV = new Button("Weitere Semesterverbände hinzufügen");
			weitereSV.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (studiengangListBox.getSelectedIndex() != 0) {
						anlegenWSVPanel.setVisible(true);
						studiengangListBox.setEnabled(false);
						semesterverbandListBox.setEnabled(false);
					} else {
						Window.alert("Bitte wählen Sie zuerst einen Hauptsemesterverband");
					}
				}
			});
			this.anlegenPanel.add(weitereSV);

			this.add(anlegenPanel);

			/*
			 *  Instaziieren von Widgets, welche das Hinzufügen eines
			 *  weiteren SVs beschreiben und ermöglichen
			 */
			studiengangAnlegen = new Label("Studiengang :");
			studiengangListBoxAnlegen = new ListBox();
			addChangeHandlerTosStudiengangListBoxAnlegen();

			semesterverbandAnlegen = new Label("Semesterverband :");
			semesterverbandListBoxAnlegen = new ListBox();
			semesterverbandListBoxAnlegen.setEnabled(false);

			// Adden eines ClickHandlers zum "Semesterverband-Hinzufügen-Button" (Zusätzliche Semesterverbände)
			hinzufuegenButtonAnlegen = new Button("Hinzufuegen");
			hinzufuegenButtonAnlegen.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (SVvonNeuerBelegung == null) {
						SVvonNeuerBelegung = new Vector<Semesterverband>();
					}
					if (SGKuerzel == null) {
						SGKuerzel = new Vector<String>();
					}
					if (semesterverbaendeVectorForListBoxAnlegen == null || semesterverbaendeVectorForListBoxAnlegen.size() < 1) {
						Window.alert("Bitte wählen Sie zuerst einen Semesterverband");
					} 
					else {
						boolean check = true;
						if (semesterverbaendeVectorForListBox == null || semesterverbaendeVectorForListBox.size() < 1) {
							Window.alert("Bitte wählen Sie zuerst den Hauptsemesterverband aus");
							check = false;

							studiengangListBox.setEnabled(true);
							semesterverbandListBox.setEnabled(true);
						}
						if (check) {
							
							/*
							 *  Bei Auswahl eines Semesterverbandes wird zunächst geprüft,
							 *  ob dieser bereits hinzugefügt wurde,
							 *  sollte dies der Fall sein, wird der gewüschte Studiengang nicht
							 *  hinzugefügt und der User darauf hingewiesen
							 */
							for (Semesterverband sv : SVvonNeuerBelegung) {
								if (sv.getId() == semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()).getId() || 
										sv.getId() == semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1).getId()) {
									Window.alert("Der Semesterverband ist bereits hinzugefuegt");
									check = false;
									break;
								}
							}
						}
						/*
						 *  Bei Auswahl eines Semesterverbandes wird geprüft,
						 *  ob dieser bereits als Hauptsemesterverband hinzugefügt wurde,
						 *  sollte dies der Fall sein, wird der gewüschte Studiengang nicht
						 *  hinzugefügt und der User darauf hingewiesen
						 */
						if (check) {
							if (semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()).getId() == 
									semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1).getId()) {
								Window.alert("Der Semesterverband ist bereits hinzugefuegt");
								check = false;
							}
						}
						// Falls der gewünschte Semesterverband noch nicht hinzugefügt wurde...
						if (check) {
							/*
							 * ...wird dieser dem entsprechenden Container hinzugefügt und der 
							 * FlexTable, welcher die zusätzlichen Semesterverbände auflistet
							 * neu aufgesetzt
							 */
							SVvonNeuerBelegung.addElement(semesterverbaendeVectorForListBoxAnlegen.elementAt(semesterverbandListBoxAnlegen.getSelectedIndex()));
							SGKuerzel.addElement(studiengaengeVectorForListBox.elementAt(studiengangListBoxAnlegen.getSelectedIndex() - 1).getKuerzel());
							createFlexTable();
						}

					}
				}
			});

			// Anordnen der Widgets für das Hinzufügen von weiteren Semesterverbänden
			anlegenWSVGrid = new Grid(3, 3);
			anlegenWSVGrid.setWidget(0, 0, studiengangAnlegen);
			anlegenWSVGrid.setWidget(0, 1, studiengangListBoxAnlegen);
			anlegenWSVGrid.setWidget(1, 0, semesterverbandAnlegen);
			anlegenWSVGrid.setWidget(1, 1, semesterverbandListBoxAnlegen);
			anlegenWSVGrid.setWidget(2, 0, hinzufuegenButtonAnlegen);

			this.anlegenWSVPanel = new VerticalPanel();
			this.anlegenWSVPanel.add(anlegenWSVGrid);

			svTable = new FlexTable();

			this.anlegenWSVPanel.add(svTable);
			this.anlegenWSVPanel.setVisible(false);

			this.add(anlegenWSVPanel);
		}

	}

	/**
	 * Methode um den FlexTable, welcher alle zusätzlichen Semesterverbände,
	 * welcher der neuen Belegung zugeordnet sein sollen auflistet, 
	 * abzubilden. Dabei erhält jeder Eintrag mittels Button die
	 * Möglichkeit, diesen wieder zu entfernen. 
	 */
	public void createFlexTable() {
		svTable.removeAllRows();
		svTable.setText(0, 0, "Studiengang");
		svTable.setText(0, 1, "Semesterverband");

		if (SVvonNeuerBelegung != null && SVvonNeuerBelegung.size() > 0) {
			
			// Für jeden Semesterverband der Belegung...
			for (int i = 0; i < SVvonNeuerBelegung.size(); i++) {
				
				//...wird im FlexTable ein Eintrag gesetzt und...
				final int row = svTable.getRowCount();
				svTable.setWidget(row, 0, new Label(SGKuerzel.elementAt(i)));
				svTable.setWidget(row, 1, new Label(SVvonNeuerBelegung.elementAt(i).getJahrgang()));

				//...ein Button, mit dem der User den Semesterverband wieder entfernen kann
				Button loeschenButton = new Button("X");
				loeschenButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {

						int rowIndex = svTable.getCellForEvent(event).getRowIndex();
						svTable.removeRow(rowIndex);

						SVvonNeuerBelegung.removeElementAt(rowIndex - 1);
						SGKuerzel.removeElementAt(rowIndex - 1);
					}
				});

				svTable.setWidget(row, 2, loeschenButton);
			}
		}
	}

	/**
	 * Methode welche mittels einem Proxy-Objekt eine neu erstellte Belegung
	 * an den Server zum ablegen in die DB übermittelt 
	 */
	public void belegungAnlegen() {

		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");

		int zeitslotIndex = 0;
		Vector<Dozent> vd = new Vector<Dozent>();
		Vector<Semesterverband> vsv = new Vector<Semesterverband>();
		boolean check = true;

		// Prüfung ob eine vollständige Belegung vorliegt
		if (anlegenLVListBox.getSelectedIndex() <= 0) {
			check = false;
		}
		if (anlegenRaumListBox.getSelectedIndex() <= 0) {
			check = false;
		}
		if ((anlegenDozent1ListBox.getSelectedIndex() <= 0)	&& (anlegenDozent2ListBox.getSelectedIndex() <= 0)
				&& (anlegenDozent3ListBox.getSelectedIndex() <= 0)) {
			check = false;
		}
		if (anlegenTagListBox.getSelectedIndex() > 0 && anlegenUhrzeitListBox.getSelectedIndex() > 0) {
			zeitslotIndex = ((anlegenTagListBox.getSelectedIndex() - 1) * 7) + anlegenUhrzeitListBox.getSelectedIndex();
		} 
		else {
			check = false;
		}

		vsv.addElement(semesterverbaendeVectorForListBox.elementAt(semesterverbandListBox.getSelectedIndex() - 1));
		if (SVvonNeuerBelegung != null) {
			for (Semesterverband sv : SVvonNeuerBelegung) {
				vsv.addElement(sv);
			}
		}

		if (anlegenDozent1ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent1ListBox.getSelectedIndex() - 1));
		}
		if (anlegenDozent2ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent2ListBox.getSelectedIndex() - 1));
		}
		if (anlegenDozent3ListBox.getSelectedIndex() > 0) {
			vd.addElement(dozentenVectorforListBox.elementAt(anlegenDozent3ListBox.getSelectedIndex() - 1));
		}
		// Wenn alle erforderlichen Attributswerte vom User ausgewählt wurde...
		if (check) {
			/*
			 * ...dann wird mit Hilfe der Inidces der ListBoxen die entsprechenden BusinessObjects aus Ihren
			 * Containern adressiert. Anschließend werden diese BusinessObjects als Argumente der 
			 * "Belegung-Anlegen-Methode" auf der Serverseite übergeben
			 */
			verwaltung.anlegenBelegung(lvVectorforListBox.elementAt(anlegenLVListBox.getSelectedIndex() - 1),
					raeumeVectorforListBox.elementAt(anlegenRaumListBox.getSelectedIndex() - 1), zeitslotsVectorForListBox
							.elementAt(zeitslotIndex - 1), vd, vsv,	new AsyncCallback<Belegung>() {
						
				public void onFailure(Throwable caught) {
					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
					Window.alert(caught.getMessage());
					svTable.removeAllRows();
					if (SVvonNeuerBelegung != null) {
						SVvonNeuerBelegung.clear();
					}
					anlegenButton.setEnabled(true);
					tabsLeerenButton.setEnabled(true);
					anlegenTagListBox.setEnabled(true);
					anlegenUhrzeitListBox.setEnabled(true);
					anlegenLVListBox.setEnabled(true);
					anlegenRaumListBox.setEnabled(true);
					anlegenDozent1ListBox.setEnabled(true);
					anlegenDozent2ListBox.setEnabled(true);
					anlegenDozent3ListBox.setEnabled(true);
				}

				public void onSuccess(Belegung result) {
					DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
					Window.alert("Belegung wurde erfolgreich angelegt");
					ladenBelegungen();
					svTable.removeAllRows();
					if (SVvonNeuerBelegung != null) {
						SVvonNeuerBelegung.clear();
					}
				}
			});
		} 
		// Falls die neue Belegung unvollständig ist, wird der User daraufhingewiesen
		else {
			DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
			Window.alert("Bitte vervollstaendigen Sie die neue Belegung");

			anlegenButton.setEnabled(true);
			tabsLeerenButton.setEnabled(true);
			anlegenTagListBox.setEnabled(true);
			if (anlegenTagListBox.getSelectedIndex() > 0) {
				anlegenUhrzeitListBox.setEnabled(true);
			}
			anlegenLVListBox.setEnabled(true);
			if (!(raeumeVectorforListBox == null || raeumeVectorforListBox.size() == 0)) {
				anlegenRaumListBox.setEnabled(true);
			}
			if (!(dozentenVectorforListBox == null || dozentenVectorforListBox.size() == 0)) {
				anlegenDozent1ListBox.setEnabled(true);
				anlegenDozent2ListBox.setEnabled(true);
				anlegenDozent3ListBox.setEnabled(true);
			}
		}
	}

	/**
	 * Methode welche die Übersicht (TabPanel) aus- bzw. einblendet 
	 */
	public void tabVisibility() {
		if (tabVisibilityButton.getText().equals("Übersicht verdecken")) {
			tabPanel.setVisible(false);
			tabVisibilityButton.setText("Übersicht einblenden");
			return;
		}
		if (tabVisibilityButton.getText().equals("Übersicht einblenden")) {
			tabPanel.setVisible(true);
			tabVisibilityButton.setText("Übersicht verdecken");
			return;
		}
	}

	/**
	 * Neutralisiert die Benutzeroberfläche
	 */
	public void clearEntries() {
		semesterverbandListBox.clear();
		svBelegungen = null;

		if (!anlegenMaske) {
			auswahlAufhebenButton.setVisible(false);
		}

		if (anlegenMaske) {
			semesterverbandListBoxAnlegen.clear();
			SVvonNeuerBelegung = null;
			SGKuerzel = null;
			if (svTable != null) {
				svTable.removeAllRows();
			}
			anlegenWSVPanel.setVisible(false);
		}
		for (int i = 0; i < gridVector.size(); i++) {
			for (int j = 1; j < gridVector.elementAt(i).getRowCount(); j++) {
				for (int k = 1; k < gridVector.elementAt(i).getColumnCount(); k++) {
					gridVector.elementAt(i).setWidget(j, k, null);
				}
			}
		}
		if (anlegenMaske) {
			anlegenTagListBox.setSelectedIndex(0);
			anlegenUhrzeitListBox.setSelectedIndex(0);
			anlegenLVListBox.setSelectedIndex(0);
			anlegenRaumListBox.setSelectedIndex(0);
			anlegenDozent1ListBox.setSelectedIndex(0);
			anlegenDozent2ListBox.setSelectedIndex(0);
			anlegenDozent3ListBox.setSelectedIndex(0);

			anlegenTagListBox.setEnabled(false);
			anlegenUhrzeitListBox.setEnabled(false);
			anlegenLVListBox.setEnabled(false);
			anlegenRaumListBox.setEnabled(false);
			anlegenDozent1ListBox.setEnabled(false);
			anlegenDozent2ListBox.setEnabled(false);
			anlegenDozent3ListBox.setEnabled(false);

			studiengangListBoxAnlegen.setSelectedIndex(0);
			ladenSemesterverbaende(true);
			semesterverbandListBoxAnlegen.setSelectedIndex(0);
			semesterverbandListBoxAnlegen.setEnabled(false);
		}

		studiengangListBox.setSelectedIndex(0);
		ladenSemesterverbaende(false);
		semesterverbandListBox.setSelectedIndex(0);
		semesterverbandListBox.setEnabled(false);

		studiengangListBox.setEnabled(true);
		semesterverbandListBox.setEnabled(true);

		if (semesterverbaendeVectorForListBox == null
				|| semesterverbaendeVectorForListBox.size() == 0) {
			semesterverbandListBox.setEnabled(false);
		}
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
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben(
				"<b><u>Anleitung: </u></b></br>"
						+ "Hier können Sie eine Belegung anlegen/ ändern."
						+ "</br><b>Alle Felder sind Pflichtfelder!</b>"
						+ "</br>"
						+ "</br>Zu einer Belegung können mehrere Semesterverbände"
						+ "</br>hinzugefügt werden!"
						+ "</br>"
						+ "</br>"
						+ "</br><b><u>Achtung:</u></b>"
						+ "</br>Beim Anlegen einer neuen Belegung können"
						+ "</br>nur Dozentinnen/ Dozenten mit dem jeweiligen Profilfach"
						+ "</br>einer Lehrveranstaltung hinzugefügt werden!"
						+ "</br>"
						+ "</br>Beim Ändern einer Belegung können allerdings alle"
						+ "</br>Dozentinnen/ Dozenten ausgewählt werden (damit eine Lehrveranstaltung z.B. auch bei Krankheit von einer anderen Dozentin/ einem anderen Dozenten durchgeführt werden kann."
						+ "</br>In diesem Fall können allerdings nur Dozentinnen/ Dozenten ausgewählt werden, die zu dem jeweiligen Zeitraum noch keine andere Veranstaltung haben!)!");
	}

}
