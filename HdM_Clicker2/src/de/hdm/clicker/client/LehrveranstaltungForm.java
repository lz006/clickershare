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
 * Diese Klasse stellt die zum Anlegen und Bearbeiten einer Lehrveranstaltung notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class LehrveranstaltungForm extends VerticalPanel {

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
	 * Angezeigte Lehrveranstaltung
	 */
	Lehrveranstaltung shownLehrveranstaltung = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Bezeichnung einer Lehrveranstaltung
	 */
	Label bezeichnungLabel = new Label("Bezeichnung:");
	TextBox lvBezeichnungTb = new TextBox();

	/**
	 * ListBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Studiensemesters einer Lehrveranstaltung
	 */
	Label studiensemesterLabel = new Label("Studiensemester:");
	ListBox studiensemesterListBox = new ListBox();

	/**
	 * ListBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * des Umfangs einer Lehrveranstaltung
	 */
	Label umfangLabel = new Label("Umfang in SWS:");
	ListBox umfangListBox = new ListBox();

	/**
	 * Widgets um das Hinzufügen von Dozenten zur Lehrveranstaltung
	 * zu ermöglichen bzw. zu veranschaulichen
	 */
	Label dozentLabel = new Label("Dozenten:");
	ListBox dozentListBox = new ListBox();
	Button dozentHinzufuegenButton = new Button("Hinzufügen");

	/**
	 * Widgets um das Hinzufügen von Studiengängen zur Lehrveranstaltung
	 * zu ermöglichen bzw. zu Veranschaulichen
	 */
	Label studiengangLabel = new Label("Studiengänge:");
	ListBox studiengangListBox = new ListBox();
	Button studiengangHinzufuegenButton = new Button("Hinzufügen");

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) eine 
	 * Lehrveranstaltung anlegt bzw. ändert
	 */
	Button speichernAnlegenButton = new Button();
	
	/**
	 * Button zum löschen einer Lehrveranstaltung
	 */
	Button loeschenButton;

	/**
	 * Container welche alle Studiengänge bzw. Dozenten enthalten, 
	 * aus denen sich der User mittels DropDown "bedienen" kann
	 */
	Vector<Studiengang> studiengangVector = null;
	Vector<Dozent> dozentenVector = null;

	/**
	 * Container welche alle Studiengänge bzw. Dozenten enthalten, 
	 * welche der User für eine neue Lehrveranstaltung ausgwählt hat
	 */
	Vector<Studiengang> studiengaengeVonNeuerLVVector = null;
	Vector<Dozent> dozentenVonNeuerLVVector = null;

	/**
	 * Dynamische Tabellen (FlexTable) welche die zur Lehrveranstaltung 
	 * referenzierten Dozenten bzw. Studiengänge auflisten
	 */
	FlexTable dozentTable;
	FlexTable sgTable;

	/**
	 * Panels, welche die Benutzeroberfläche in vier Bereiche einteilen
	 */
	VerticalPanel obenPanel;
	VerticalPanel mittePanel;
	VerticalPanel untenPanel;
	HorizontalPanel abschlussPanel;

	/**
	 * Tabellen (Grids) welche Widgets strukturiert aufnehmen und selbst
	 * wiederum Panels (dem Namen entsprechend) zugewiesen werden
	 */
	Grid obenGrid;
	Grid mitteGrid;
	Grid untenGrid;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public LehrveranstaltungForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		studiensemesterListBox.addStyleName("customListBox");

		obenPanel = new VerticalPanel();
		
		// Anordnen von Widgets
		obenGrid = new Grid(3, 2);
		obenGrid.setWidget(0, 0, bezeichnungLabel);
		obenGrid.setWidget(0, 1, lvBezeichnungTb);
		obenGrid.setWidget(1, 0, studiensemesterLabel);
		obenGrid.setWidget(1, 1, studiensemesterListBox);
		obenGrid.setWidget(2, 0, umfangLabel);
		obenGrid.setWidget(2, 1, umfangListBox);

		// Füllen der "studiensemesterListBox" mit vordefinierten Einträgen
		studiensemesterListBox.addItem("1.Semester");
		studiensemesterListBox.addItem("2.Semester");
		studiensemesterListBox.addItem("3.Semester");
		studiensemesterListBox.addItem("4.Semester");
		studiensemesterListBox.addItem("5.Semester");
		studiensemesterListBox.addItem("6.Semester");
		studiensemesterListBox.addItem("7.Semester");
		studiensemesterListBox.addItem("8.Semester");
		studiensemesterListBox.addItem("9.Semester");
		studiensemesterListBox.addItem("10.Semester");
		studiensemesterListBox.addItem("11.Semester");

		// Füllen der "umfangListBox" mit vordefinierten Einträgen
		umfangListBox.addItem("2");
		umfangListBox.addItem("4");
		umfangListBox.addItem("6");
		umfangListBox.addItem("8");
		umfangListBox.addItem("10");
		umfangListBox.addItem("12");

		obenPanel.add(obenGrid);
		this.add(obenPanel);

		// Initialisierung und Anordnung der Widgets für den "Dozenten-Bereich"
		mittePanel = new VerticalPanel();

		mitteGrid = new Grid(1, 3);
		mitteGrid.setWidget(0, 0, dozentLabel);
		mitteGrid.setWidget(0, 1, dozentListBox);
		mitteGrid.setWidget(0, 2, dozentHinzufuegenButton);

		// Füllen des Dozenten- und mittelbar des Studiengang-Containers
		ladenDozenten();

		// Adden eines ClickHandlers zum "Dozent-Hinzufügen-Button"
		dozentHinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				/*
				 *  Zuerst wird anhand "shownLehrveranstaltung" geprüft in welchem Zustand sich  
				 *  das Benutzerinterface befindet (anlegen/ändern), bei "null" befindet
				 *  sicher der User in der Anlegen-Variante
				 */
				if (shownLehrveranstaltung != null) {
					boolean check = true;
					/*
					 *  Bei Auswahl eines Dozenten wird zunächst geprüft,
					 *  ob sich dieser bereits im Repertoire der Lehrveranstaltung befindet,
					 *  sollte dies der Fall sein, wird der gewüschte Dozent nicht
					 *  hinzugefügt und der User darauf hingewiesen
					 */
					if (shownLehrveranstaltung.getDozenten() != null) {
						for (Dozent dozent : shownLehrveranstaltung.getDozenten()) {
							if (dozent.getId() == dozentenVector.elementAt(dozentListBox.getSelectedIndex()).getId()) {
								Window.alert("Der Dozent ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					// Falls der gewünschte Dozent noch nicht hinzugefügt wurde...
					if (check) {
						if (shownLehrveranstaltung.getDozenten() == null) {
							shownLehrveranstaltung.setDozenten(new Vector<Dozent>());
						}
						/*
						 * ...wird diese der "shownLehrveranstaltung" hinzugefügt, dabei entspricht die Indexierung
						 * des DropDowns dem des Dozenten-Containers in dem alle Dozenten vorgehalten werden
						 */
						shownLehrveranstaltung.getDozenten().addElement(dozentenVector.elementAt(dozentListBox.getSelectedIndex()));
						dozentenAnzeigen();
					}
				} 
				/*
				 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
				 *  "if-Zweig", nur dass für ein neuer Dozent ein separater Dozenten-Container die hinzugefügten 
				 *  Dozenten aufnehmen muss
				 */
				else {
					if (dozentenVonNeuerLVVector == null) {
						dozentenVonNeuerLVVector = new Vector<Dozent>();
					}
					boolean check = true;
					for (Dozent dozent : dozentenVonNeuerLVVector) {
						if (dozent.getId() == dozentenVector.elementAt(dozentListBox.getSelectedIndex()).getId()) {
							Window.alert("Der Dozent ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						dozentenVonNeuerLVVector.addElement(dozentenVector.elementAt(dozentListBox.getSelectedIndex()));
						dozentenAnzeigen();
					}
				}
			}
		});

		// Initialiseren des FlexTables für die Dozenten
		dozentTable = new FlexTable();
		dozentTable.setText(0, 0, "Dozent");

		// Anordnung der Widgets, welche den Dozenten-Bereich betreffen
		mittePanel.add(mitteGrid);
		mittePanel.add(dozentTable);
		this.add(mittePanel);

		// Initialisierung und Anordnung der Widgets für den "Studiengänge-Bereich"
		untenPanel = new VerticalPanel();

		untenGrid = new Grid(1, 3);
		untenGrid.setWidget(0, 0, studiengangLabel);
		untenGrid.setWidget(0, 1, studiengangListBox);
		untenGrid.setWidget(0, 2, studiengangHinzufuegenButton);

		// Adden eines ClickHandlers zum "Studiengang-Hinzufügen-Button"
		studiengangHinzufuegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				/*
				 *  Zuerst wird anhand "shownLehrveranstaltung" geprüft in welchem Zustand sich  
				 *  das Benutzerinterface befindet (anlegen/ändern), bei "null" befindet
				 *  sicher der User in der Anlegen-Variante
				 */
				if (shownLehrveranstaltung != null) {
					boolean check = true;
					/*
					 *  Bei Auswahl eines Studienganges wird zunächst geprüft,
					 *  ob sich dieser bereits im Repertoire der Lehrveranstaltung befindet,
					 *  sollte dies der Fall sein, wird der gewüschte Studiengang nicht
					 *  hinzugefügt und der User darauf hingewiesen
					 */
					if (shownLehrveranstaltung.getStudiengaenge() != null) {
						for (Studiengang sg : shownLehrveranstaltung.getStudiengaenge()) {
							if (sg.getId() == studiengangVector.elementAt(studiengangListBox.getSelectedIndex()).getId()) {
								Window.alert("Der Studiengang ist bereits hinzugefügt");
								check = false;
								break;
							}
						}
					}
					// Falls der gewünschte Studiengang noch nicht hinzugefügt wurde...
					if (check) {
						if (shownLehrveranstaltung.getStudiengaenge() == null) {
							shownLehrveranstaltung.setStudiengaenge(new Vector<Studiengang>());
						}
						/*
						 * ...wird diese der "shownLehrveranstaltung" hinzugefügt, dabei entspricht die Indexierung
						 * des DropDowns dem des Dozenten-Containers in dem alle Dozenten vorgehalten werden
						 */
						shownLehrveranstaltung.getStudiengaenge().addElement(studiengangVector.elementAt(studiengangListBox.getSelectedIndex()));
						studiengaengeAnzeigen();
					}
				}
				/*
				 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
				 *  "if-Zweig", nur dass für ein neuer Studiengang ein separater Studiengang-Container die hinzugefügten 
				 *  Studiengänge aufnehmen muss
				 */
				else {
					if (studiengaengeVonNeuerLVVector == null) {
						studiengaengeVonNeuerLVVector = new Vector<Studiengang>();
					}
					boolean check = true;
					for (Studiengang sg : studiengaengeVonNeuerLVVector) {
						if (sg.getId() == studiengangVector.elementAt(studiengangListBox.getSelectedIndex()).getId()) {
							Window.alert("Lie Lehrveranstaltung ist bereits hinzugefügt");
							check = false;
							break;
						}
					}
					if (check) {
						studiengaengeVonNeuerLVVector.addElement(studiengangVector.elementAt(studiengangListBox.getSelectedIndex()));
						studiengaengeAnzeigen();
					}
				}

			}
		});

		// Initialiseren des FlexTables für die Studiengänge
		sgTable = new FlexTable();
		sgTable.setText(0, 0, "Studiengang");

		// Anordnung der Widgets, welche den Studiengang-Bereich betreffen
		untenPanel.add(untenGrid);
		untenPanel.add(sgTable);
		this.add(untenPanel);

		/*
		 *  Erstellung und Anordnung des Schluss-Bereichs, welcher die Buttons
		 *  je nach Masken-Variante aufnimmt (Speichern/Anlegen/Löschen)
		 */
		abschlussPanel = new HorizontalPanel();
		abschlussPanel.add(speichernAnlegenButton);
		this.add(abschlussPanel);

	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Dozenten anfordert und diese
	 * in den dafür vorgesehenen Container ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void ladenDozenten() {
		verwaltung.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Vector<Dozent> result) {
				dozentenVector = result;
				for (Dozent dozent : result) {
					dozentListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
				}
				/*
				 *  Erst nach erfolgreichem "Ladevorgang" werden die Studiengänge geladen
				 *  um einen sich gegenseitig blockierende Datenbank-Kommunikation zu verhindern
				 */
				ladenStudiengaenge();
			}
		});
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Studiengänge anfordert und diese
	 * in den dafür vorgesehenen Conatainer ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void ladenStudiengaenge() {
		verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					public void onSuccess(Vector<Studiengang> result) {
						studiengangVector = result;
						for (Studiengang studiengang : result) {
							studiengangListBox.addItem(studiengang.getBezeichnung());
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
		this.dtvm.getStundenplantool2().setTextToInfoPanelOben("<b><u>Anleitung: </u></b></br>Hier können Sie eine Lehrveranstaltung anlegen/ ändern."										
			+ "</br>"
			+ "</br>Eine Dozentin/ ein Dozent muss einer Lehrveranstaltung nicht zwangsläufig zugewiesen werden!"
			+ "</br>"
			+ "</br>Einer Lehrveranstaltung muss <b>mindestens ein</b> Studiengang zugewiesen werden!"
			+ "</br>"
			+ "</br><b>Außer der Angabe einer Dozentin/ eines Dozenten, sind alle Felder Pflichtfelder!</b>");

		lvBezeichnungTb.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				dtvm.getStundenplantool2().setTextToInfoPanelUnten("<b></br>Für die Bearbeitung der "
					+ "Lehrveranstaltungsbezeichnung bitte folgende Restriktionen beachten:</b>"
					+ "</br>Die Bezeichnung darf nicht mit einem Sonderzeichen, einem Leerzeichen oder einer Zahl beginnen!"
					+ "</br>Eine Zahl am Ende der Bezeichnung einer Lehrveranstaltung ist erlaubt!</br>Bsp. Softwareentwicklung-2 oder Softwareentwicklung 2"
					+ "</br>"
					+ "</br>Max. dürfen 45 Zeichen eingetragen/ verwendet werden!");
			}
		});
	}

	/**
	 * Setzen der aus dem CellTree gewählten Lehrveranstaltung (Ändern-Maske)
	 * 
	 * @param	lv - Referenz auf ein Lehrveranstaltung-Objekt. 
	 */
	public void setShownLehrveranstaltung(Lehrveranstaltung lv) {
		this.shownLehrveranstaltung = lv;
	}

	/**
	 * Methode um den FlexTable, welcher alle der LV zugeordneten Dozenten
	 * auflistet, abzubilden. Dabei erhält jeder Eintrag mittels Button die
	 * Möglichkeit, diesen wieder zu entfernen. Die Methode wird in der
	 * Ändern-Maske zu Beginn und anschließend maskenunabhängig bei jeder
	 * neuen Auswahl eines Dozenten bzw. deren Löschung aufgerufen 
	 */
	public void dozentenAnzeigen() {
		dozentTable.removeAllRows();
		dozentTable.setText(0, 0, "Dozent");

		// "if-Zweig" falls Ändern-Maske gegenwärtig
		if (shownLehrveranstaltung != null) {
			if ((shownLehrveranstaltung.getDozenten() != null) && (shownLehrveranstaltung.getDozenten().size() > 0)) {
				
				// Für jeden Dozenten der LV...
				for (Dozent dozent : shownLehrveranstaltung.getDozenten()) {
					
					//...wird im FlexTable ein Eintrag gesetzt und...
					final int row = dozentTable.getRowCount();
					dozentTable.setWidget(row, 0, new Label(dozent.getNachname() + " " + dozent.getVorname()));

					//...ein Button, mit dem der User den Dozenten wieder entfernen kann
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = dozentTable.getCellForEvent(event).getRowIndex();
							dozentTable.removeRow(rowIndex);

							shownLehrveranstaltung.getDozenten().removeElementAt(rowIndex - 1);

						}
					});
					dozentTable.setWidget(row, 1, loeschenButton);
				}
			}
		} 
		/*
		 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
		 *  "if-Zweig", nur dass für eine neue LV ein separater Dozenten-Container die hinzugefügten 
		 *  Dozenten aufnehmen muss
		 */
		else {
			if ((dozentenVonNeuerLVVector != null) && (dozentenVonNeuerLVVector.size() > 0)) {
				for (Dozent dozent : dozentenVonNeuerLVVector) {
					final int row = dozentTable.getRowCount();
					dozentTable.setWidget(row, 0, new Label(dozent.getNachname() + " " + dozent.getVorname()));

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = dozentTable.getCellForEvent(event).getRowIndex();
							dozentTable.removeRow(rowIndex);

							dozentenVonNeuerLVVector.removeElementAt(rowIndex - 1);

						}
					});

					dozentTable.setWidget(row, 1, loeschenButton);

				}
			}
		}
	}

	/**
	 * Methode um den FlexTable, welcher alle der LV zugeordneten Studiengänge
	 * auflistet, abzubilden. Dabei erhält jeder Eintrag mittels Button die
	 * Möglichkeit, diesen wieder zu entfernen. Die Methode wird in der
	 * Ändern-Maske zu Beginn und anschließend maskenunabhängig bei jeder
	 * neuen Auswahl eines Dozenten bzw. deren Löschung aufgerufen 
	 */
	public void studiengaengeAnzeigen() {
		sgTable.removeAllRows();
		sgTable.setText(0, 0, "Studiengang");

		// "if-Zweig" falls Ändern-Maske gegenwärtig
		if (shownLehrveranstaltung != null) {
			if ((shownLehrveranstaltung.getStudiengaenge() != null)	&& (shownLehrveranstaltung.getStudiengaenge().size() > 0)) {
				
				// Für jeden Studiengang der LV...
				for (Studiengang sg : shownLehrveranstaltung.getStudiengaenge()) {
					
					//...wird im FlexTable ein Eintrag gesetzt und...
					final int row = sgTable.getRowCount();
					sgTable.setWidget(row, 0, new Label(sg.getBezeichnung()));

					//...ein Button, mit dem der User den Dozenten wieder entfernen kann
					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = sgTable.getCellForEvent(event).getRowIndex();
							sgTable.removeRow(rowIndex);

							shownLehrveranstaltung.getStudiengaenge().removeElementAt(rowIndex - 1);

						}
					});

					sgTable.setWidget(row, 1, loeschenButton);

				}
			}
		} 
		/*
		 *  "else-Zweig" wird durchlaufen wenn die Anlegen-Variante in Gebrauch ist nahezu identisch zum
		 *  "if-Zweig", nur dass für eine neue LV ein separater Studiengang-Container die hinzugefügten 
		 *  Studiengänge aufnehmen muss
		 */
		else {
			if ((studiengaengeVonNeuerLVVector != null)	&& (studiengaengeVonNeuerLVVector.size() > 0)) {
				for (Studiengang sg : studiengaengeVonNeuerLVVector) {
					final int row = sgTable.getRowCount();
					sgTable.setWidget(row, 0, new Label(sg.getBezeichnung()));

					Button loeschenButton = new Button("X");
					loeschenButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {

							int rowIndex = sgTable.getCellForEvent(event).getRowIndex();
							sgTable.removeRow(rowIndex);

							studiengaengeVonNeuerLVVector.removeElementAt(rowIndex - 1);

						}
					});

					sgTable.setWidget(row, 1, loeschenButton);

				}
			}
		}
	}

	/**
	 * TextBox mit der LV-Bezeichnung füllen (Ändern-Maske) und ListBoxen
	 * den Attributwerten der LV entsprechend vorauswählen
	 */
	public void fillForm() {
		this.lvBezeichnungTb.setText(shownLehrveranstaltung.getBezeichnung());
		this.studiensemesterListBox.setSelectedIndex(shownLehrveranstaltung.getStudiensemester() - 1);
		this.umfangListBox.setSelectedIndex((shownLehrveranstaltung.getUmfang() / 2) - 1);
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Ändern einer
	 * LV ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void aendernMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für die Abänderung einer LV erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Speichern");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				shownLehrveranstaltung.setBezeichnung(lvBezeichnungTb.getText());
				shownLehrveranstaltung.setStudiensemester(studiensemesterListBox.getSelectedIndex() + 1);
				shownLehrveranstaltung.setUmfang((umfangListBox.getSelectedIndex() * 2) + 2);

				verwaltung.aendernLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Lehrveranstaltung>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Vector<Lehrveranstaltung>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor","default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung der LV, wird die LV wieder 
							 *  in ihrer ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Lehrveranstaltung> result) {
								dozentTable.clear();
								sgTable.clear();
								dtvm.setSelectedLehrveranstaltung(result.elementAt(0));
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}
						});

					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an den User und
					 * der lehrveranstaltungDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Lehrveranstaltung result) {
						Window.alert("Lehrveranstaltung wurde erfolgreich geändert");
						dtvm.updateLehrveranstaltung(shownLehrveranstaltung);
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

				verwaltung.loeschenLehrveranstaltung(shownLehrveranstaltung, new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);

							}

							public void onSuccess(Void result) {
								Window.alert("Lehrveranstaltung wurde erfolgreich gelöscht");
								dtvm.loeschenLehrveranstaltung(shownLehrveranstaltung);
								clearForm();
							}
						});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen einer
	 * LV ermöglicht (wird von CustomTreeViewModel aus aufgerufen {@link CustomTreeViewModel})
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen einer LV erfoderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				speichernAnlegenButton.setEnabled(false);

				// "if-Zweig" wenn Dozenten ausgewählt wurden (kein muss für den User)
				if (dozentenVonNeuerLVVector != null && dozentenVonNeuerLVVector.size() > 0) {
					verwaltung.anlegenLehrveranstaltung((umfangListBox.getSelectedIndex() * 2) + 2, lvBezeichnungTb.getText(),
							studiensemesterListBox.getSelectedIndex() + 1, studiengaengeVonNeuerLVVector, dozentenVonNeuerLVVector,
							new AsyncCallback<Lehrveranstaltung>() {
						
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									speichernAnlegenButton.setEnabled(true);

								}

								/* 
								 * Bei Erfolgreicher Anlegung erfolgt Meldung an den User und
								 * der lehrveranstaltungDataProvider wird mittelbar aktualisiert
								 */
								public void onSuccess(Lehrveranstaltung result) {
									Window.alert("Lehrveranstaltung wurde erfolgreich angelegt");
									dtvm.addLehrveranstaltung(result);
									clearForm();
									speichernAnlegenButton.setEnabled(true);
								}
							});
				} 
				// "else-Zweig" wenn keine Dozenten ausgewählt wurden (kein muss für den User)
				else {
					verwaltung.anlegenLehrveranstaltung((umfangListBox.getSelectedIndex() * 2) + 2, lvBezeichnungTb.getText(),
							studiensemesterListBox.getSelectedIndex() + 1, studiengaengeVonNeuerLVVector, new AsyncCallback<Lehrveranstaltung>() {
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									speichernAnlegenButton.setEnabled(true);

								}

								/* 
								 * Bei Erfolgreicher Anlegung erfolgt Meldung an der User und
								 * der lehrveranstaltungDataProvider wird mittelbar aktualisiert
								 */
								public void onSuccess(Lehrveranstaltung result) {
									Window.alert("Lehrveranstaltung wurde erfolgreich angelegt");
									dtvm.addLehrveranstaltung(result);
									clearForm();
									speichernAnlegenButton.setEnabled(true);
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
		this.shownLehrveranstaltung = null;
		this.lvBezeichnungTb.setText("");
		this.studiensemesterListBox.setSelectedIndex(0);
		this.umfangListBox.setSelectedIndex(0);
		this.dozentListBox.setSelectedIndex(0);
		this.dozentTable.removeAllRows();
		this.studiengangListBox.setSelectedIndex(0);
		this.sgTable.removeAllRows();
		this.studiengaengeVonNeuerLVVector = null;
		this.dozentenVonNeuerLVVector = null;
	}

}
