package de.hdm.clicker.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import de.hdm.clicker.shared.*;

/**
 * Entry-Point-Klasse des Projekts <b>Stundenplantool2</b>.
 * 
 * @author Thies, Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella
 * @version 1.0
 */
public class Clicker implements EntryPoint {

	/**
	 * Referenz auf das Proxy-Objekte um mit dem Server kommunizieren zu können
	 */
	private final VerwaltungAsync verwaltung = GWT.create(Verwaltung.class);
	private final ReportAsync report = GWT.create(Report.class);
	
	/**
	 * CellTree welcher dauerhaft auf der linken Seite der Benutzeroberfläche
	 * dem User zur Navigation durch die Funktionen des "Stundentools2" dient
	 */
	private CellTree cellTree;
	
	/**
	 * Klasse welche das TreeViewModel-Interface implementiert. Diese Referenz
	 * wird benötigt um Zugriff auf deren Methoden zu bekommen. Zusätzlich wird
	 * ein solches Objekt vom CellTree-Knostruktur als Argument verlangt
	 */
	private CustomTreeViewModel dtvm;
	
	/**
	 * Panel welches den gesamten Bildschirm in verschiedene Bereiche einteilt
	 */
	private DockLayoutPanel p;
	
	/**
	 * Referenz auf das Titelbild
	 */
	private Image image;
	
	/**
	 * ScrollPanel, welches den CellTree aufnimmt. Dadurch dass die Anzahl der Kind-Elemente
	 * nicht vorhersebar ist, muss der User bei Bedarf "scrollen" können, um den gewünschten
	 * Bereich sichtbar zu machen
	 */
	private ScrollPanel navi;
	
	/**
	 * Panel, welches den unteren Bereich der Benutzeroberfläche darstellt und weitere Widgets
	 * aufnimmt
	 */
	private VerticalPanel footPanel;
	
	/**
	 * HTML-Tag, welcher den Namen der Projektbeteiligten im Footer auflistet
	 */
	private HTML copyright;
	
	/**
	 * HTML-Tag, für den Titel im Kopf-Bereich
	 */
	private HTML titel;
	
	/**
	 * Panel für den Kopf-Bereich, welches zwei weitere Panels für
	 * für Titelbild und Titel aufnimmt
	 */
	private HorizontalPanel traeger;
	
	/**
	 * Panel welches das Titelbild aufnimmt im Kopf-Bereich aufnimmt
	 */
	private HorizontalPanel left;
	
	/**
	 * Panel welches den "HTML-Titel-Tag" aufnimmt und rechts vom Titelbild
	 * angeordnet wird
	 */
	private HorizontalPanel right;
	
	/**
	 * Panel am rechten Bildschirmrand, welches zwei "Info-Panels aufnimmt"
	 */
	private VerticalPanel traegerInfoPanel;
	
	/**
	 * Oberes Info-Panel am rechten Bildschirmrand, welches einen allgemeinen
	 * Leitfaden für die Benutzung des jeweiligen Benutzerinterface beinhaltet
	 */
	private VerticalPanel obenInfoPanel;
	
	/**
	 * Unteres Info-Panel am rechten Bildschirmrand, welches einen spezifschen
	 * Leitfaden für die Benutzung des jeweiligen Widgtes beinhaltet
	 */
	private VerticalPanel untenInfoPanel;
	
	/**
	 * Label welches beim Start der Applikation den Arbeitsbereich der 
	 * Benutzeroberfläche mit einem "Willkommenstext" versieht
	 */
	private Label welcomeLabel;

	/**
	 * Referenz auf das Formular für einen Dozenten
	 */
	private DozentForm dF;
	
	/**
	 * Referenz auf das Formular für eine Belegung
	 */
	private BelegungForm bF;
	
	/**
	 * Referenz auf das Formular für eine Lehrveranstaltung
	 */
	private LehrveranstaltungForm lF;
	
	/**
	 * Referenz auf das Formular für einen Studiengang
	 */
	private StudiengangForm sgF;
	
	/**
	 * Referenz auf das Formular für einen Semesterverband
	 */
	private SemesterverbandForm svF;
	
	/**
	 * Referenz auf das Formular für einen Raum
	 */
	private RaumForm rF;

	/**
	 * Referenz auf die Ausgabe-Maske für Studentenpläne
	 */
	private StudentenPlanForm spF;
	
	/**
	 * Referenz auf die Ausgabe-Maske für Dozentenpläne
	 */
	private DozentenPlanForm dpF;
	
	/**
	 * Referenz auf die Ausgabe-Maske für Raumpläne
	 */
	private RaumPlanForm rpF;

	/**
	 * Panel welches die Plattform für die jeweiligen Formulare bietet,
	 * also der Arbeitsbereich. Das es evtl. bei kleineren Bildschirmen
	 * zu einem "Platzmangel" kommen kann, muss dieser Bereich 
	 * "scrollbar" sein
	 */
	private ScrollPanel mainPanel;
	
	/**
	 * Button um den Celltree ein- und auszublenden (bei entsprechendem Platzbedarf)
	 */
	Button visibilityTreeButton;
	
	/**
	 * Button um die Infotexte ein- und auszublenden (bei entsprechendem Platzbedarf)
	 */
	Button visibilityInfoPanelsButton;
	
	/**
	 * Panel welches am unteren Bildschirmrand "visibilityTreeButton" und "visibilityInfoPanelsButton"
	 * aufnimmt und selbst dem "south-Bereich" des "DockLayoutPanel" zugwiesen wird
	 */
	Grid buttonGrid;
	
	/**
	 * Flag zur Bestimmung ob der CellTree sichtabr ist (sichtbar = true / nicht sichtbar = false)
	 */
	boolean check1 = false;
	
	/**
	 * Flag zur Bestimmung ob die InfoPanel sichtbar sind (sichtbar = true / nicht sichtbar = false)
	 */
	boolean check2 = false;

	/**
	   * Da diese Klasse die Implementierung des Interface <code>EntryPoint</code>
	   * zusichert, benötigen wir eine Methode
	   * <code>public void onModuleLoad()</code>. Diese ist das GWT-Pendant der
	   * <code>main()</code>-Methode normaler Java-Applikationen.
	   */
	public void onModuleLoad() {

		// Initialisieren des Buttons zum ein- und ausblenden des CellTrees
		visibilityTreeButton = new Button("Navigation ausblenden");
		
		// Hinzufügen eines ClickHandlers zum "visibilityTreeButton"
		visibilityTreeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				visibilityTree();
			}
		});

		// Initialisieren des Buttons zum ein- und ausblenden der InfoPanels
		visibilityInfoPanelsButton = new Button("Infotext ausblenden");
		
		// Hinzufügen eines ClickHandlers zum "visibilityInfoPanelsButton"
		visibilityInfoPanelsButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				visibilityInfoPanels();
				
			}
		});

		// Anrodnen der Buttons
		buttonGrid = new Grid(1, 2);
		buttonGrid.setWidget(0, 0, visibilityTreeButton);
		buttonGrid.setWidget(0, 1, visibilityInfoPanelsButton);

		// Initialisierung der "Träger-Info-Panels" am rechten Bildschirmrand
		traegerInfoPanel = new VerticalPanel();
		
		// Initialisierung oberen "Info-Panels" am rechten Bildschirmrand
		obenInfoPanel = new VerticalPanel();
		
		/*
		 *  Setzen eines eigenen StyleName für das obere "Info-Panels" um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		obenInfoPanel.setStyleName("infotextOben");

		// Initialisierung unteren "Info-Panels" am rechten Bildschirmrand
		untenInfoPanel = new VerticalPanel();
		
		/*
		 *  Setzen eines eigenen StyleName für das untere "Info-Panels" um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		untenInfoPanel.setStyleName("infotextUnten");

		// Anordnen der InfoPanels
		traegerInfoPanel.add(obenInfoPanel);
		traegerInfoPanel.add(untenInfoPanel);

		// Initialisierung des Hauptpanels in der Bildschirmmitte und des Panels		
		mainPanel = new ScrollPanel();
		
		// Initialisierung des Panels, welches den CellTree aufnimmt 
		navi = new ScrollPanel();

		// Initialisierung des Labels für den "Willkommenstext"
		welcomeLabel = new Label("Herzlich willkommen im Stundenplantool des Studiengangs Wirtschaftsinformatik und digitale Medien des 4. Semesters!");
		
		/*
		 *  Setzen eines eigenen StyleName für das "welcomeLabel" um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		welcomeLabel.setStyleName("start");
		
		// Dem Hauptpanel (Arbeitsbereich) wird zu Beginn der Willkommenstext zugewiesen
		mainPanel.add(welcomeLabel);
		
		// Initialisierung eines Objekts vom Typ "TreeViewModel"
		dtvm = new CustomTreeViewModel(verwaltung);
		
		// Initialisierung des CellTrees
		cellTree = new CellTree(dtvm, "Root");
		
		// Anordnen des CellTrees
		navi.add(cellTree);
		
		// Setzen einer Referenz auf die "RootNode" in die "TreeViewModel-Instanz"
		dtvm.setRootNode(cellTree.getRootTreeNode());
		
		// Setzen einer Referenz auf den CellTree in die "TreeViewModel-Instanz"
		dtvm.setCellTree(cellTree);

		// Initialisierung eines Image-Objekts
		image = new Image();
		
		// Setzen der Quelle des Titelbilds
		image.setUrl("http://www.pm-graphics.com/1.png");
		
		// Setzen der Größe des Titelbilds
		image.setHeight("5em");
		
		// Setzen eines "MouseOver-Textes" zum Titelbild
		image.setAltText("WI-Logo");

		/*
		 *  Initialisierung des "HTML-Tag-Objekts", welcher den Namen der 
		 *  Projektbeteiligten im Footer auflistet
		 */
		copyright = new HTML("IT-Projekt im 4. Semester &copy; 2014 by Timm Roth (tr047), Tobias Moser (tm066), "
				+ "Lucas Zanella (lz006), Stefan Sonntag (ss305), Gino Sidney (gk024) & Mathias Zimmermann (mz048)!");
		
		/*
		 *  Setzen eines eigenen StyleName für das "HTML-Tag-Objekt" um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		copyright.addStyleName("cpLabel");

		// Initialisierung eines Panels, welches im Fuß-Bereich alle Widgets aufnimmt
		footPanel = new VerticalPanel();
		
		// Hinzufügen von Widgets zum Panel im Fuß-Bereich
		footPanel.add(buttonGrid);
		footPanel.add(copyright);
		
		/*
		 *  Setzen eines eigenen StyleName für das "footPanel-Objekt" um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		footPanel.addStyleName("foot");

		// Initialisierung des Titels
		titel = new HTML("Stundenplan der HdM");
		
		/*
		 *  Setzen eines eigenen StyleName für den Tiel um per
		 *  CSS ein eignens Aussehen definieren zu können
		 */
		titel.addStyleName("Titel");

		// Initialisierung des Panels welches das Titelbild aufnimmt im Kopf-Bereich aufnimmt
		left = new HorizontalPanel();
		left.add(image);

		/*
		 *  Initialisierung des Panels welches den "HTML-Titel-Tag" aufnimmt und rechts vom 
		 *  Titelbild angeordnet wird
		 */
		right = new HorizontalPanel();
		right.add(titel);

		// Initialisierung des "Träger-Panels" im Kopfbereich
		traeger = new HorizontalPanel();
		
		// Anordnen der Panels mit Titelbild und HTML-Titel-Tag
		traeger.add(left);
		traeger.add(right);

		// Initialisierung eines DockLayoutPanels
		p = new DockLayoutPanel(Unit.EM);
		
		// Panel den Kopfbereich zuweisen
		p.addNorth(traeger, 5);
		
		// Panel den Fußbereich zuweisen
		p.addSouth(footPanel, 5);
		
		// Panel den linken Bildschirmbereich zuweisen
		p.addWest(navi, 25);
		
		// Panel den rechten Bildschirmbereich zuweisen
		p.addEast(traegerInfoPanel, 30);
		
		// Panel den Hauptbereich in der Bildschirmmitte (Arbeitsbereich) zuweisen
		p.add(mainPanel);

		// Initialtext dem oberen InfoPanels hinzufügen
		setTextToInfoPanelOben("</br><b><u>Anleitung:</b></u>"
				+ "</br>Links unter dem Menüpunkt <b><i>„Editor - Anlegen“</b></i> können Sie Lehrveranstaltungen,"
				+ "Dozenten, Belegungen, Räume, Semesterverbände und Studiengänge anlegen."
				+"</br>"
				+ "</br>Unter <b><i>„Editor – Verwalten“</b></i> können Sie die zuvor angelegten Datensätze ändern und löschen."
				+ "</br>"
				+ "</br>"
				+ "</br>Der Punkt <b><i>„Report“</b></i> ermöglicht es Ihnen, sich Studenten-, Dozenten- und Raumpläne generieren und ausgeben zu lassen."
				+ "</br>"
				+ "</br>"
				+ "</br>"
				+ "</br>Wenn Sie links unten auf den Button <b><i>„Navigation ausblenden“</b></i> klicken, wird das Navigationsmenü links (Editor/ Report) komplett ausgeblendet."
				+ "</br>Mit einem anschließenden Klick auf denselben Button, wird die Navigation wieder eingeblendet."
				+ "</br>"
				+ "</br>Wenn Sie links unten auf den Button <b><i>„Infotext ausblenden“</b></i> klicken, werden die Hinweise rechts komplett ausgeblendet."
				+ "</br>Mit einem anschließenden Klick auf denselben Button, werden die Hinweise wieder eingeblendet.");

	
		
		
		/*
		 *   Erzeugen eines LayoutPanels, welches die Wurzel
		 *   aller LayoutPanles darstellt
		 */
		RootLayoutPanel rlp = RootLayoutPanel.get();
		
		// Dem RootLayoutPanel das DockLayoutPanel unterordnen
		rlp.add(p);
		
		// Dem <body>-Bereich der stundenplatool2.html das RootLayoutPanel zuweisen
		RootPanel.get().add(rlp);

		// Setzen einer Selbst-Referenz zur "TreeViewModel-Instanz"
		dtvm.setStundenplantool2(this);
		
		/*
		 *  Registrieren eines ClosingHandlers, damit beim schließen des 
		 *  Fensters die Datenbankverbindung geschlossen wird.
		 */
		Window.addWindowClosingHandler(new Window.ClosingHandler() {			
			public void onWindowClosing(Window.ClosingEvent closingEvent) {
				
				closingEvent.setMessage("Wirklich beenden?");
				
				verwaltung.closeConnection(new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
					public void onSuccess(Void result) {
						
					}
				});
			}
			
		});

	}

	/**
	 * Methode welche den Bereich des CellTrees ein- und ausblendet
	 */
	public void visibilityTree() {
		if (!check1) {
			p.setWidgetHidden(navi, true);
			visibilityTreeButton.setText("Navigation einblenden");
			check1 = true;
			return;
		}
		if (check1) {
			p.setWidgetHidden(navi, false);
			visibilityTreeButton.setText("Navigation ausblenden");
			check1 = false;
			return;
		}
	}

	/**
	 * Methode welche den Bereich der InfoPanels ein- und ausblendet
	 */
	public void visibilityInfoPanels() {
		if (!check2) {
			p.setWidgetHidden(traegerInfoPanel, true);
			visibilityInfoPanelsButton.setText("Infotext einblenden");
			check2 = true;
			return;
		}
		if (check2) {
			p.setWidgetHidden(traegerInfoPanel, false);
			visibilityInfoPanelsButton.setText("Infotext ausblenden");
			check2 = false;
			return;
		}
	}

	/**
	 * Methode welche das Dozent-Formular in den Hauptbereich läd
	 */
	public void setDozentFormToMain() {
		dF = new DozentForm(verwaltung);
		dtvm.setDozentForm(dF);
		mainPanel.clear();
		mainPanel.add(dF);
	}

	/**
	 * Methode welche das Belegung-Formular in den Hauptbereich läd
	 */
	public void setBelegungFormToMain() {
		bF = new BelegungForm(verwaltung);
		dtvm.setBelegungForm(bF);
		mainPanel.clear();
		mainPanel.add(bF);
	}

	/**
	 * Methode welche das Lehrveranstaltung-Formular in den Hauptbereich läd
	 */
	public void setLehrveranstaltungFormToMain() {
		lF = new LehrveranstaltungForm(verwaltung);
		dtvm.setLehrveranstaltungForm(lF);
		mainPanel.clear();
		mainPanel.add(lF);
	}

	/**
	 * Methode welche das Studiengang-Formular in den Hauptbereich läd
	 */
	public void setStudiengangFormToMain() {
		sgF = new StudiengangForm(verwaltung);
		dtvm.setStudiengangForm(sgF);
		mainPanel.clear();
		mainPanel.add(sgF);
	}

	/**
	 * Methode welche das Semesterverband-Formular in den Hauptbereich läd
	 */
	public void setSemesterverbandFormToMain() {
		svF = new SemesterverbandForm(verwaltung);
		dtvm.setSemesterverbandForm(svF);
		mainPanel.clear();
		mainPanel.add(svF);
	}

	/**
	 * Methode welche das Raum-Formular in den Hauptbereich läd
	 */
	public void setRaumFormToMain() {
		rF = new RaumForm(verwaltung);
		dtvm.setRaumForm(rF);
		mainPanel.clear();
		mainPanel.add(rF);
	}

	/**
	 * Methode welche das StudentenPlan-Formular in den Hauptbereich läd
	 */
	public void setStudentenPlanFormToMain() {
		spF = new StudentenPlanForm(report);
		mainPanel.clear();
		mainPanel.add(spF);
		
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setText("Infotext einblenden");
		check2 = true;
		
		visibilityInfoPanelsButton.setVisible(false);
	}

	/**
	 * Methode welche das DozentenPlan-Formular in den Hauptbereich läd
	 */
	public void setDozentenPlanFormToMain() {
		dpF = new DozentenPlanForm(report);
		mainPanel.clear();
		mainPanel.add(dpF);
		
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setText("Infotext einblenden");
		check2 = true;
		
		visibilityInfoPanelsButton.setVisible(false);
	}

	/**
	 * Methode welche das RaumPlan-Formular in den Hauptbereich läd
	 */
	public void setRaumPlanFormToMain() {
		rpF = new RaumPlanForm(report);
		mainPanel.clear();
		mainPanel.add(rpF);
		
		p.setWidgetHidden(traegerInfoPanel, true);
		visibilityInfoPanelsButton.setText("Infotext einblenden");
		check2 = true;
		
		visibilityInfoPanelsButton.setVisible(false);
	}

	/**
	 * Methode welche den User auf die Deaktivierung seines PopUp-Blockers hinweist
	 */
	public void popupInfo() {
		Window.alert("Bitte aktivieren Sie Popups in Ihrem Browser wenn Sie den Report im Vollbild betrachten möchten");
	}

	/**
	 * Methode welche Infotext im oberen InfoPanel platziert
	 * 
	 * @param	anleitung - String-Objekt, welches des Infotext enthält
	 */
	public void setTextToInfoPanelOben(String anleitung) {
		visibilityInfoPanelsButton.setVisible(true);
		HTML infoTextObenLabel = new HTML(anleitung);
		infoTextObenLabel.setStyleName("infoTextObenLabel");
		obenInfoPanel.add(infoTextObenLabel);

	}
	
	/**
	 * Methode welche Infotext im unteren InfoPanel platziert
	 * 
	 * @param	restricts - String-Objekt, welches des Infotext enthält
	 */
	public void setTextToInfoPanelUnten(String restricts) {
		untenInfoPanel.clear();
		HTML infoTextUntenLabel = new HTML(restricts);
		infoTextUntenLabel.setStyleName("infoTextUntenLabel");
		untenInfoPanel.add(infoTextUntenLabel);
	}

	/**
	 * Methode welche Infotexte aus den InfoPanels entfernt
	 */
	public void clearInfoPanels() {
		obenInfoPanel.clear();
		untenInfoPanel.clear();
	}

	/**
	 * Ausgeben der Referenz auf ein CellTree-Objekt
	 * 
	 * @return	CellTree-Objekt-Referenz
	 */
	public CellTree getCellTree() {
		return cellTree;
	}

}
