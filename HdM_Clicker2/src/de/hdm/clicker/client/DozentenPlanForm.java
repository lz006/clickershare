package de.hdm.clicker.client;

import java.util.Vector;

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
import de.hdm.clicker.shared.bo.Dozent;
import de.hdm.clicker.shared.report.Dozentenplan;

/**
 * Diese Klasse stellt die zum Anfordern eines bestimmten Dozentenplans erforderliche
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 * 
 */
public class DozentenPlanForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	private ReportAsync report = null;

	/**
	 * Label welches darauf hinweist, dass es sich um 
	 * Dozenten in der ListBox handelt
	 */
	Label dozentLabel = new Label("Dozent: ");
	
	/**
	 * ListBox, welches dem User ermöglicht einen Dozenten auszuwählen
	 */
	ListBox dozentListBox = new ListBox();

	/**
	 * Tabelle (Grid), welches Widgets aufnimmt und anordnet
	 */
	Grid grid;

	/**
	 * Button um den Wunsch zu "signalisieren" einen bestimmten
	 * Dozentenplan generieren und anzeigen zu lassen
	 */
	Button generateButton = new Button("Report generieren");

	/**
	 * Container welche alle Dozenten enthält
	 */
	Vector<Dozent> dozentenVector = null;

	/**
	 * Panel, welches zur Anzeige des Dozentenplans dient
	 */
	VerticalPanel reportPanel;
	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet.
	 * Darüberhinaus werden mittelbar alle Dozenten geladen.
	 * 
	 * @param	reportA - Referenz auf ein Proxy-Objekt. 
	 */	
	public DozentenPlanForm(ReportAsync reportA) {
		this.report = reportA;

		dozentenLaden();

		grid = new Grid(1, 2);

		grid.setWidget(0, 0, dozentLabel);
		grid.setWidget(0, 1, dozentListBox);

		// Adden eines ClickHandlers zum "generateButton-Button"
		generateButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				
				DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");
				dozentListBox.setEnabled(false);
				generateButton.setEnabled(false);
				
				report.createDozentenplan(dozentenVector.elementAt(dozentListBox.getSelectedIndex()), new AsyncCallback<Dozentenplan>() {
					public void onFailure(Throwable caught) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						Window.alert(caught.getMessage());
						generateButton.setEnabled(true);
						dozentListBox.setEnabled(true);
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
					public void onSuccess(Dozentenplan result) {
						DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor",	"default");
						dozentListBox.setEnabled(true);
						generateButton.setEnabled(true);
						if (result.getHtmlTable() != null && (result.getHtmlTable().length() > 1)) {
						reportPanel.clear();
						reportPanel.add(new HTML(result.getHtmlTable()));
						createWindow(result.getHtmlTable());
						} 
						else {
								Window.alert("Zum gewählten Dozent sind keine Belegungen vorhanden");
						}
					}
				});
			}
		});

		/**
		 * Anordnen der Widgets
		 */	
		this.add(grid);
		this.add(generateButton);

		reportPanel = new VerticalPanel();
		this.add(reportPanel);
	}

	/**
	 * Methode die mittels Proxy-Objekt vom Server alle Dozenten anfordert und diese
	 * in den dafür vorgesehenen Conatainer ablegt sowie das entsprechende DropDown
	 * (ListBox) befüllt 
	 */
	public void dozentenLaden() {
		generateButton.setEnabled(false);
		report.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				generateButton.setEnabled(true);
				dozentListBox.setEnabled(true);
			}

			public void onSuccess(Vector<Dozent> result) {
				generateButton.setEnabled(true);
				dozentListBox.setEnabled(true);
				dozentenVector = result;
				for (Dozent dozent : result) {
					dozentListBox.addItem(dozent.getNachname() + " " + dozent.getVorname());
				}
			}

		});
	}

	/**
	 * JavaScript-Methode, welche ein mit HTML freidefinierbares PopUp erzeugt,
	 * das sich der Bildschirmgröße entsprechend als Vollbild darstellt. Dies
	 * soll dem User eine "ordentliche" Print-Version des Dozentenplans bieten.
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
		win.document.write("<HTML><HEAD><TITLE>Dozentenplan</TITLE></HEAD><BODY>"+ html + "</BODY></HTML>");
		win.document.close();
	}-*/;

}
