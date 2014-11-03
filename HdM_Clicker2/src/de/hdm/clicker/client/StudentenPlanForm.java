package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.hdm.clicker.shared.ReportAsync;
import de.hdm.clicker.shared.bo.*;
import de.hdm.clicker.shared.report.Studentenplan;

/**
 * Diese Klasse stellt die zum Anfordern eines bestimmten Studentenplans erforderliche
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class StudentenPlanForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	private ReportAsync report = null;

	/**
	 * Label welches darauf hinweist, dass es sich um 
	 * Studiengänge in der ListBox handelt
	 */
	Label sgLabel = new Label("Studiengang: ");
	
	/**
	 * ListBox, welches dem User ermöglicht einen Studiengang auszuwählen
	 */
	ListBox sgListBox = new ListBox();

	/**
	 * Label welches darauf hinweist, dass es sich um 
	 * Semesterverbände in der ListBox handelt
	 */
	Label svLabel = new Label("Semesterverband: ");
	
	/**
	 * ListBox, welches dem User ermöglicht einen Semesterverband auszuwählen
	 */
	ListBox svListBox = new ListBox();

	/**
	 * Tabelle (Grid), welches Widgets aufnimmt und anordnet
	 */
	Grid grid;

	/**
	 * Button um den Wunsch zu "signalisieren" einen bestimmten
	 * Studentenplan generieren und anzeigen zu lassen
	 */
	Button generateButton = new Button("Report generieren");

	/**
	 * Container welche alle Studiengänge enthält
	 */
	Vector<Studiengang> sgVector = null;
	
	/**
	 * Container welche alle Semesterverbände enthält, die dem gewählten
	 * Studiengang zugeordnet sind
	 */
	Vector<Semesterverband> svVector = null;

	/**
	 * Panel, welches zur Anzeige des Dozentenplans dient
	 */
	VerticalPanel reportPanel;

	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet
	 * Darüberhinaus werden mittelbar alle Studiengänge geladen.
	 * 
	 * @param	reportA - Referenz auf ein Proxy-Objekt. 
	 */	
	public StudentenPlanForm(ReportAsync reportA) {
		this.report = reportA;

		svListBox.setEnabled(false);

		studiengaengeLaden();

		grid = new Grid(2, 2);

		grid.setWidget(0, 0, sgLabel);
		grid.setWidget(0, 1, sgListBox);
		grid.setWidget(1, 0, svLabel);
		grid.setWidget(1, 1, svListBox);

		// Adden eines ClickHandlers zum "generateButton-Button"
		generateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
				sgListBox.setEnabled(false);
				svListBox.setEnabled(false);
				generateButton.setEnabled(false);
				
				report.createStudentenplan(svVector.elementAt(svListBox.getSelectedIndex()), new AsyncCallback<Studentenplan>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						Window.alert(caught.getMessage());
						sgListBox.setEnabled(true);
						svListBox.setEnabled(true);
						generateButton.setEnabled(true);
					}

					/**
					 * Bei erfolgreicher Generierung wird dem "reportPanel" ein neues HTML-Objekt
					 * zugewiesen, welches wiederum den Ergebnis-String mit den Tags zugewiesen
					 * wurde, welche die "Ergebnistabelle" darstellen. Zusätzlich wird eine
					 * JavaScript-Methode aufgerufen, welche ein PopUp mit der "Ergebnistabelle"
					 * in Vollbild anazeigt.
					 * 
					 * @param	result - String, welcher HTML-Tags enthält
					 */
					public void onSuccess(Studentenplan result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						sgListBox.setEnabled(true);
						svListBox.setEnabled(true);
						generateButton.setEnabled(true);
						if (result.getHtmlTable() != null && result.getHtmlTable().length() > 1) {
							reportPanel.clear();
							reportPanel.add(new HTML(result.getHtmlTable()));
							createWindow(result.getHtmlTable());
						} 
						else {
							Window.alert("Zum gewählten Semesterverband sind keine Belegungen vorhanden");
						}
					}
				});
			}
		});

		/*
		 * Der Studiengang-Listbox wird einen ChangeHandler hinzugefügt,
		 * welcher Änderungen registriert und zum gewählten Studiengang
		 * die entsprechenden Semesterverbände in der Semesterverbände-
		 * ListBox verfügbar macht
		 */
		sgListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				generateButton.setEnabled(false);
				sgListBox.setEnabled(false);
				svListBox.setEnabled(false);
				svListBox.clear();
				ladenSemesterverbaende();
			}
		});

		sgListBox.setEnabled(false);
		generateButton.setEnabled(false);

		/**
		 * Anordnen der Widgets
		 */	
		this.add(grid);
		this.add(generateButton);

		reportPanel = new VerticalPanel();
		this.add(reportPanel);
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Studiengänge anfordert und diese
	 * in den dafür vorgesehenen Conatainer ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void studiengaengeLaden() {
		report.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
			public void onFailure(Throwable caught) {
				sgListBox.setEnabled(true);
				Window.alert(caught.getMessage());
			}

			public void onSuccess(Vector<Studiengang> result) {
				sgListBox.setEnabled(true);
				sgVector = result;
				for (Studiengang sg : result) {
					sgListBox.addItem(sg.getBezeichnung());
				}
				ladenSemesterverbaende();
			}

		});
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Semesterverbände, die einem bestimmten
	 * Studiengang zugeordnet sind, anfordert und diese in den dafür vorgesehenen Conatainer 
	 * ablegt sowie das entsprechende DropDown (ListBox) befüllt 
	 */
	public void ladenSemesterverbaende() {
		report.auslesenSemesterverbaendeNachStudiengang(
				sgVector.elementAt(sgListBox.getSelectedIndex()), new AsyncCallback<Vector<Semesterverband>>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						sgListBox.setEnabled(true);
					}

					public void onSuccess(Vector<Semesterverband> result) {
						generateButton.setEnabled(true);
						sgListBox.setEnabled(true);
						svListBox.setEnabled(true);
						svListBox.clear();

						if (result != null && result.size() > 0) {
							svVector = result;
							for (Semesterverband sv : result) {
								svListBox.addItem(sv.getJahrgang());
							}
						} else {
							Window.alert("Zum gewählten Studiengang sind keine Semesterverbände vorhanden");
						}
					}
				});
	}

	/**
	 * JavaScript-Methode, welche ein mit HTML freidefinierbares PopUp erzeugt,
	 * das sich der Bildschirmgröße entsprechend als Vollbild darstellt. Dies
	 * soll dem User eine "ordentliche" Print-Version des Studentenplans bieten.
	 * Grund für eine JavaScript-Methode ist, dass ein "GWT-PopUp" nicht oder
	 * nicht ohne Weiters mit inidividuellem "HTML" erzeugt werden kann.
	 */
	public static native void createWindow(String html) /*-{
		var myWidth; // = 1366; 
		var myHeight; // = 768;
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
		var win = window.open("", "win", myWidth, myHeight);
		win.document.open("text/html", "replace");
		win.document.write("<HTML><HEAD><TITLE>Studentenplan</TITLE></HEAD><BODY>" + html + "</BODY></HTML>");
		win.document.close();
	}-*/;

}
