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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die zum Anlegen und Bearbeiten einer Category notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class CategoryForm extends VerticalPanel {

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
	 * Angezeigter Category
	 */
	Category shownCategory = null;

	/**
	 * TextBox und Label zur Ein-, Ausgabe bzw. Veranschaulichung
	 * der Beschreibung/Titel einer Category
	 */
	Label descLabel = new Label("Beschreibung: ");
	TextBox catTextBox = new TextBox();

	/**
	 * Button der je nach Masken-Variante (Anlegen/Ändern) einen 
	 * Lecturer anlegt bzw. ändert
	 */
	Button speichernAnlegenButton;
	
	/**
	 * Button zum löschen einer Category
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
	public CategoryForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		grid = new Grid(1, 2);

		// Anordnung der Widgets
		grid.setWidget(0, 0, descLabel);
		grid.setWidget(0, 1, catTextBox);

		speichernAnlegenButton = new Button();
		buttonPanel = new HorizontalPanel();
		buttonPanel.add(speichernAnlegenButton);

		this.add(grid);
		this.add(buttonPanel);
		
		

	}
	
	public void bild() {
		Integer i = 6;
		Vector<Integer> vi = new Vector<Integer>();
		vi.add(i);
		verwaltung.auslesenImages(vi, new AsyncCallback<Vector<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<String> result) {
				System.out.println(result.elementAt(0));
				Image image = new Image(result.elementAt(0));
				/*
				Image image = new Image("data:image/png;base64,"
						+"iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAABGdBTUEAALGP"
						+"C/xhBQAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9YGARc5KB0XV+IA"
						+"AAAddEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIFRoZSBHSU1Q72QlbgAAAF1J"
						+"REFUGNO9zL0NglAAxPEfdLTs4BZM4DIO4C7OwQg2JoQ9LE1exdlYvBBeZ7jq"
						+"ch9//q1uH4TLzw4d6+ErXMMcXuHWxId3KOETnnXXV6MJpcq2MLaI97CER3N0"
						+"vr4MkhoXe0rZigAAAABJRU5ErkJggg==");
				*/
				image.addErrorHandler(new ErrorHandler() {
				      public void onError(ErrorEvent event) {
				    	  Window.alert(event.toDebugString());;
				      }
				    });
				image.setHeight("5em");
				FormPanel fp = new FormPanel();
				fp.add(image);
				CategoryForm.this.add(fp);
				Window.alert("geladen");
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
	 * Setzen der aus dem CellTree gewählten Category (Ändern-Maske)
	 * 
	 * @param	category - Referenz auf ein Category-Objekt. 
	 */
	public void setShownCategory(Category category) {
		this.shownCategory = category;
	}

	/**
	 * TextBoxen mit Attributen der Category füllen (Ändern-Maske)
	 */
	public void fillForm() {
		this.catTextBox.setText(shownCategory.getDescription());
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

				shownCategory.setDescription(catTextBox.getText());
				
				verwaltung.aendernCategory(shownCategory, new AsyncCallback<Category>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

						verwaltung.auslesenCategory(shownCategory, new AsyncCallback<Vector<Category>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
								Window.alert(caught.getMessage());
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);
							}

							/*
							 *  Bei fehlgeschlagener Änderung der Category, wird die Category wieder 
							 *  in ihrer ursprünglichen Form geladen und die Benutzeroberfläche neu
							 *  aufgesetzt
							 */
							public void onSuccess(Vector<Category> result) {
								ltvm.setSelectedCategory(result.elementAt(0));	
								speichernAnlegenButton.setEnabled(true);
								loeschenButton.setEnabled(true);								
							}
						});
					}

					/* 
					 * Bei Erfolgreicher Änderung erfolgt Meldung an der User und
					 * der categoryDataProvider wird mittelbar aktualisiert
					 */
					public void onSuccess(Category result) {
						Window.alert("Kategorie wurde erfolgreich geändert");
						ltvm.updateCategory(result);
						
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
				bild();
			}
			
			public void onCClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);
				loeschenButton.setEnabled(false);

				verwaltung.loeschenCategory(shownCategory, new AsyncCallback<Void>() {
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
						Window.alert("Kategrie wurde erfolgreich gelöscht");
						ltvm.loeschenCategory(shownCategory);
						clearForm();
					}
				});
			}
		});
	}

	/**
	 * Methode welche die Benutzeroberfläche so konfiguriert, dass sie das Anlegen einer
	 * Category ermöglicht (wird von LecturerTreeViewModel aus aufgerufen {@link LecturerTreeViewModel})
	 */
	public void anlegenMaske() {

		/*
		 *  "speichernAnlegenButton" wird entsprechend der Funktion
		 *  benannt und "bekommt" einen entsprechenden Clickhandler
		 *  zugewiesen, der für das Anlegen einer Category erforderlichen
		 *  Funktionalitäten impliziert
		 */
		speichernAnlegenButton.setText("Anlegen");

		speichernAnlegenButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				speichernAnlegenButton.setEnabled(false);

				verwaltung.anlegenCategory(catTextBox.getText(), new AsyncCallback<Category>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						speichernAnlegenButton.setEnabled(true);
					}

					public void onSuccess(Category result) {
						Window.alert("Kategorie wurde erfolgreich angelegt");
						ltvm.addCategory(result);
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
		this.shownCategory = null;
		this.catTextBox.setText("");
	}

}
