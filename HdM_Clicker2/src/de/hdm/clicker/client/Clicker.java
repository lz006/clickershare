package de.hdm.clicker.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.mgwt.ui.client.MGWT;
import com.googlecode.mgwt.ui.client.MGWTSettings;
import com.googlecode.mgwt.ui.client.animation.AnimationHelper;
import com.googlecode.mgwt.ui.client.widget.animation.Animations;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;

import de.hdm.clicker.shared.*;

/**
 * Entry-Point-Klasse des Projekts <b>HdM-Clicker</b>.
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 */
public class Clicker implements EntryPoint {

	/**
	 * Referenz auf das Proxy-Objekte um mit dem Server kommunizieren zu können
	 */
	private final VerwaltungAsync verwaltung = GWT.create(Verwaltung.class);
	
	/**
	 * CellTree welcher dauerhaft auf der linken Seite der Benutzeroberfläche
	 * dem User zur Navigation dient
	 */
	private CellTree cellTree;
	
	/**
	 * Klassen welche das TreeViewModel-Interface implementiert. Diese Referenz
	 * wird benötigt um Zugriff auf deren Methoden zu bekommen. Zusätzlich wird
	 * ein solches Objekt vom CellTree-Knostruktur als Argument verlangt
	 */
	private AdminTreeViewModel atvm;
	private LecturerTreeViewModel ltvm;
	
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
	 * Dialogbox zur Admin- und Lecturerauthentifizierung mit den zugehörigen Widgets
	 */
	private DialogBox authDB;
	private Grid authDBGrid;
	private VerticalPanel authDBVP;
	private Label authDBUserLabel;
	private TextBox authDBUserTB;
	private Label authDBPWLabel;
	private PasswordTextBox authDBPWTB;
	private Button authDBButton;
	private Button authDBCloseButton;
	private Label authDBLabel;
	
	/**
	 * Label welches beim Start der Applikation den Arbeitsbereich der 
	 * Benutzeroberfläche mit einem "Willkommenstext" versieht
	 */
	private Label welcomeLabel;

	/**
	 * Referenz auf die Schaltfläche für die Quiz-Steuerung
	 */
	
	private QuizControlForm qcF;
	
	/**
	 * Referenz auf das Formular für einen Lecturer
	 */
	private LecturerForm lF;
	
	/**
	 * Referenz auf das Formular für ein Quiz
	 */
	private QuizForm qzF;
	
	/**
	 * Referenz auf das Formular für eine Category
	 */
	private CategoryForm cF;
	
	/**
	 * Referenz auf das Formular für eine Question
	 */
	private QuestionForm qF;
	
	/**
	 * Referenz auf die Quiz-Auswahl-Form
	 */
	private ChooseQuizForm cqf = null;
	
	/**
	 * Referent auf Play-QuizForm
	 */
	private PlayQuizForm pqf = null;
	
	/**
	 * Referenz auf ResultsForm
	 */
	ResultsForm rf = null;
	
	/**
	 *Referenz auf ReportForm
	 */
	ReportForm rpf = null;
	
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
	 * Wurzel aller Layout-Panels
	 */
	RootLayoutPanel rlp;
	
	/**
	 * Buttons für die Rollenbestimmung des Users
	 */
	Button entryParticipantButton;
	Button entryLecturerButton;
	Button entryAdminButton;
	
	/**
	 * Panel für die Rollenbestimmung
	 */
	VerticalPanel entryVP;
	
	/**
	 * Grid für die Buttons zur Rollenbestimmung
	 */
	
	/**
	 * Angemeldeter Teinehmer
	 */
	String signedParticipant = null;
	
	/**
	 * Elemente für die mobile Ansicht
	 */
	AnimationHelper animationHelper = null;
	ChooseQuizFormMobile cqfm = null;
	AuthoriseFormMobile afm = null;
	QuizPasswordFormMobile qpfm = null;
	PlayQuizFormMobile pqfm = null;
	ResultsFormMobile rfm = null;
	
	/**
	   * Da diese Klasse die Implementierung des Interface <code>EntryPoint</code>
	   * zusichert, benötigen wir eine Methode
	   * <code>public void onModuleLoad()</code>. Diese ist das GWT-Pendant der
	   * <code>main()</code>-Methode normaler Java-Applikationen.
	   */
	public void onModuleLoad() {
		if (!MGWT.getFormFactor().isPhone()) {
				
			mainPanel = new ScrollPanel();
			
			// Initialisierung der Buttons zur Rollenbestimmung
			entryParticipantButton = new Button("Teilnehmer");
			entryLecturerButton = new Button("Lehrende");
			entryAdminButton = new Button("Admin");
			addClickHandlerToEAB();
			addClickHandlerToELB();
			addClickHandlerToESB();
			
			// Größe der Buttons definieren
			entryParticipantButton.setHeight("100px");
			entryParticipantButton.setWidth("400px");
			entryLecturerButton.setHeight("100px");
			entryLecturerButton.setWidth("400px");
			entryAdminButton.setHeight("100px");
			entryAdminButton.setWidth("400px");
			
			// Setzen von Style-Attributen zu den EntryButtons
			entryParticipantButton.getElement().getStyle().setFontSize(15, Unit.PT);
			entryLecturerButton.getElement().getStyle().setFontSize(15, Unit.PT);
			entryAdminButton.getElement().getStyle().setFontSize(15, Unit.PT);
			
			entryParticipantButton.getElement().getStyle().setMarginTop(10, Unit.PX);
			entryParticipantButton.getElement().getStyle().setMarginBottom(10, Unit.PX);
			
			entryLecturerButton.getElement().getStyle().setMarginTop(10, Unit.PX);
			entryLecturerButton.getElement().getStyle().setMarginBottom(10, Unit.PX);
			
			entryAdminButton.getElement().getStyle().setMarginTop(10, Unit.PX);
			entryAdminButton.getElement().getStyle().setMarginBottom(10, Unit.PX);
			
			/*
			 * Initialisieren des VP welches die Button zur Rollenbestimmung aufnimmt
			 */		
			entryVP = new VerticalPanel();
			entryVP.add(entryParticipantButton);
			entryVP.add(entryLecturerButton);
			entryVP.add(entryAdminButton);
			
			RootPanel.get().add(entryVP);
			RootPanel.get().setWidgetPosition(entryVP, (Window.getClientWidth() / 2) - (entryVP.getOffsetWidth() / 2), (Window.getClientHeight() / 2) - (entryVP.getOffsetHeight() / 2));
			
			Window.addResizeHandler(new ResizeHandler() {
				
				@Override
				public void onResize(ResizeEvent event) {
					RootPanel.get().setWidgetPosition(entryVP, (Window.getClientWidth() / 2) - (entryVP.getOffsetWidth() / 2), (Window.getClientHeight() / 2) - (entryVP.getOffsetHeight() / 2));
					
				}
			});
			
			initDBCon();
		}
		else {
			MGWT.applySettings(MGWTSettings.getAppSetting());
	        animationHelper = new AnimationHelper();
	        RootPanel.get().add(animationHelper);
	        afm = new AuthoriseFormMobile(this.verwaltung);
	        afm.setClicker(this);
	        	        
	        animationHelper.goTo(afm, Animations.SLIDE);  
	        initDBCon();
		}
	}
	
	public void setChooseQuizFormMobileToMain() {
		if (afm != null) {
			afm.removeFromParent();
		}
		if (rfm != null) {
			rfm.removeFromParent();
		}
		this.cqfm = new ChooseQuizFormMobile(verwaltung);
		this.cqfm.setClicker(this);
		animationHelper.goTo(cqfm, Animations.SLIDE);
		cqfm.ladenQuizze();
		afm = null;
		rfm = null;
		
	}
	
	public void setQuizPasswordFormMobileToMain(QuizPasswordFormMobile qpfm) {
		if (cqfm != null) {
			cqfm.removeFromParent();
		}
		this.qpfm = qpfm;
		this.qpfm.setClicker(this);
		animationHelper.goTo(qpfm, Animations.SLIDE);
		cqfm = null;
	}
	
	public void setPlayQuizFormMobileToMain(PlayQuizFormMobile pqfm) {
		if (cqfm != null) {
			cqfm.removeFromParent();
		}
		if (qpfm != null) {
			qpfm.removeFromParent();
		}
		this.pqfm = pqfm;
		this.pqfm.setClicker(this);
		this.pqfm.startQuiz();
		animationHelper.goTo(pqfm, Animations.SLIDE);
				
		cqfm = null;
		qpfm = null;
	}
	
	public void setResultsFormMobileToMain(ResultsFormMobile rfm) {
		if (pqfm != null) {
			pqfm.removeFromParent();
		}
		this.rfm = rfm;
		this.rfm.setClicker(this);
		animationHelper.goTo(rfm, Animations.SLIDE);
				
		pqfm = null;
	}
	
	public void initDBCon() {
		verwaltung.openDBCon(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("initDBCon " + caught.getMessage());
				
			}

			@Override
			public void onSuccess(Void result) {
				// Eine von zehn DB-Verb. wurde aufgebaut
				checkQuizzes();
				
			}
			
		});
	}
	
	public void checkQuizzes() {
		verwaltung.checkQuizzes(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("checkQuizzes " + caught.getMessage());
				
			}

			@Override
			public void onSuccess(Void result) {
				// Eine von zehn DB-Verb. aufgebaut
				
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
	 * Methode welche das QuizControl-Formular in den Hauptbereich läd
	 */
	public void setQuizControlFormToMain() {
		qcF = new QuizControlForm(verwaltung);
		ltvm.setQuizControlForm(qcF);
		
		mainPanel.clear();
		mainPanel.add(qcF);
	}
	
	/**
	 * Methode welche die Reporting-GUI in den Hauptbereich läd
	 */
	public void setReportFormToMain() {
		rpf = new ReportForm(verwaltung);
		ltvm.setReportForm(rpf);
		
		mainPanel.clear();
		mainPanel.add(rpf);
	}

	/**
	 * Methode welche das Lecturer-Formular in den Hauptbereich läd
	 */
	public void setLecturerFormToMain() {
		lF = new LecturerForm(verwaltung);
		if (ltvm == null) {
			atvm.setLecturerForm(lF);
		}
		else {
			ltvm.setLecturerForm(lF);	
		}
		
		mainPanel.clear();
		mainPanel.add(lF);
	}
	
	/**
	 * Methode welche das Quiz-Formular in den Hauptbereich läd
	 */
	public void setQuizFormToMain() {
		qzF = new QuizForm(verwaltung);
		ltvm.setQuizForm(qzF);	
		
		
		mainPanel.clear();
		mainPanel.add(qzF);
	}
	
	/**
	 * Methode welche das Category-Formular in den Hauptbereich läd
	 */
	public void setCategoryFormToMain() {
		cF = new CategoryForm(verwaltung);
		ltvm.setCategoryForm(cF);	
		
		mainPanel.clear();
		mainPanel.add(cF);
	}
	
	/**
	 * Methode welche das Question-Formular in den Hauptbereich läd
	 */
	public void setQuestionFormToMain() {
		qF = new QuestionForm(verwaltung);	
		ltvm.setQuestionForm(qF);
		
		mainPanel.clear();
		mainPanel.add(qF);
	}

	public void setChooseFormToMain() {
		if (cqf != null) {
			cqf.removeFromParent();
		}
		if (rf != null) {
			rf.removeFromParent();
			rf = null;
		}
		if (pqf != null) {
			pqf.removeFromParent();
			pqf = null;
		}
				
		cqf = new ChooseQuizForm(verwaltung);
		cqf.setClicker(this);
		RootPanel.get().add(cqf);
		cqf.ladenQuizze();
	}
	
	public void setPlayQuizFormToMain(PlayQuizForm pqf) {
		if (cqf != null) {
			cqf.removeFromParent();
		}
		this.pqf = pqf;
		RootPanel.get().add(pqf);
		pqf.startQuiz();
	}
	
	public void setResultsFormToMain(ResultsForm rf) {
		pqf.removeFromParent();
		this.rf = rf;
		rf.setClicker(this);
		pqf = null;
		RootPanel.get().add(rf);
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
	
	public void addClickHandlerToEAB() {
				
		entryAdminButton.addClickHandler(new ClickHandler() {

			
			@Override
			public void onClick(ClickEvent event) {
				entryVP.setVisible(false);
				
				authDB = new DialogBox();
				authDB.setText("Adminauthentifizierung!");
				authDB.setAnimationEnabled(true);
				
				authDBVP = new VerticalPanel();
				authDBGrid = new Grid(2,3);
				authDBVP.add(authDBGrid);
				
				authDBPWLabel = new Label("Passwort: ");
				authDBPWTB = new PasswordTextBox();
				authDBButton = new Button("OK");
				
				authDBGrid.setWidget(0, 0, authDBPWLabel);
				authDBGrid.setWidget(0, 1, authDBPWTB);
				authDBGrid.setWidget(0, 2, authDBButton);
				
				authDBLabel = new Label();
				authDBVP.add(authDBLabel);
				
				authDB.setWidget(authDBVP);
				
				authDB.center();
				authDBPWTB.setFocus(true);
				
				addClickHandlerToADBButton();
				
				authDBCloseButton = new Button("Close");
				authDBCloseButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						authDB.hide();
						entryVP.setVisible(true);
					}
					
				});
				authDBGrid.setWidget(1, 0, authDBCloseButton);
			}
			
		});
	}
	
	public void addClickHandlerToADBButton() {
		authDBButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				authDBButton.setEnabled(false);
				authDBPWTB.setEnabled(false);
				
				verwaltung.adminAuthenticate(authDBPWTB.getText(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						authDBPWTB.setText("");
						authDBLabel.setText(caught.getMessage());
						
						authDBButton.setEnabled(true);
						authDBPWTB.setEnabled(true);
					}

					@Override
					public void onSuccess(Boolean result) {
						authDBPWTB.setText("");
						authDB.hide();
						
						// Initialisieren des DockLayoutPanels
						createDockLayoutPanel();
						
						// Initialisierung eines Objekts vom Typ "TreeViewModel"
						atvm = new AdminTreeViewModel(verwaltung);
						
						// Setzen einer Selbst-Referenz zur "TreeViewModel-Instanz"
						atvm.setClicker(Clicker.this);
								
						// Initialisierung des CellTrees
						cellTree = new CellTree(atvm, "Root");
						// Anordnen des CellTrees
						navi.add(cellTree);
						
						// Navigationsbereich einblenden
						p.setWidgetHidden(navi, false);
						
						// Clearen des FlowPanels, welches die EntryButtons beihaltet hat
						entryVP.clear();
						
						// DockLayOutPanel dem Body-html-Tag zuweisen
						RootPanel.get().add(rlp);
						
					}
					
				});
				
			}
			
		});
	}
	
	public void addClickHandlerToELB() {
		entryLecturerButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				entryVP.setVisible(false);
				
				authDB = new DialogBox();
				authDB.setText("Lehrender-Authentifizierung!");
				authDB.setAnimationEnabled(true);
				
				authDBVP = new VerticalPanel();
				authDBGrid = new Grid(3,3);
				authDBVP.add(authDBGrid);
				
				authDBUserLabel = new Label("User: ");
				authDBUserTB = new TextBox();
				authDBPWLabel = new Label("Passwort: ");
				authDBPWTB = new PasswordTextBox();
				authDBButton = new Button("OK");
				
				authDBGrid.setWidget(0, 0, authDBUserLabel);
				authDBGrid.setWidget(0, 1, authDBUserTB);
				authDBGrid.setWidget(1, 0, authDBPWLabel);
				authDBGrid.setWidget(1, 1, authDBPWTB);
				authDBGrid.setWidget(1, 2, authDBButton);
				
				authDBLabel = new Label();
				authDBVP.add(authDBLabel);
				
				authDB.setWidget(authDBVP);
				
				authDB.center();
				authDBUserTB.setFocus(true);
				
				addClickHandlerToLDBButton();
				
				authDBCloseButton = new Button("Close");
				authDBCloseButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						authDB.hide();
						entryVP.setVisible(true);
					}
					
				});
				authDBGrid.setWidget(2, 0, authDBCloseButton);
			}
			
		});
	}
	
	public void addClickHandlerToLDBButton() {
		authDBButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				authDBUserTB.setEnabled(false);
				authDBButton.setEnabled(false);
				authDBPWTB.setEnabled(false);
				
				verwaltung.lecturerAuthenticate(authDBUserTB.getText(), authDBPWTB.getText(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						authDBUserTB.setText("");
						authDBPWTB.setText("");
						authDBLabel.setText(caught.getMessage());
						
						authDBUserTB.setEnabled(true);
						authDBButton.setEnabled(true);
						authDBPWTB.setEnabled(true);
					}

					@Override
					public void onSuccess(Boolean result) {
						authDBUserTB.setText("");
						authDBPWTB.setText("");
						authDB.hide();
						
						// Initialisieren des DockLayoutPanels
						createDockLayoutPanel();
						
						// Initialisierung eines Objekts vom Typ "TreeViewModel"
						ltvm = new LecturerTreeViewModel(verwaltung);
						
						// Setzen einer Selbst-Referenz zur "TreeViewModel-Instanz"
						ltvm.setClicker(Clicker.this);
								
						// Initialisierung des CellTrees
						cellTree = new CellTree(ltvm, "Root");
						
						// Anordnen des CellTrees
						navi.add(cellTree);
						
						// Navigationsbereich einblenden
						p.setWidgetHidden(navi, false);
						
						// Clearen des FlowPanels, welches die EntryButtons beihaltet hat
						entryVP.clear();
						
						// DockLayOutPanel dem Body-html-Tag zuweisen
						RootPanel.get().add(rlp);
					}
					
				});
				
			}
			
		});
	}
	
	public void addClickHandlerToESB() {
		entryParticipantButton.addClickHandler(new ClickHandler() {
						
			public void onClick(ClickEvent event) {
				entryVP.setVisible(false);
				
				authDB = new DialogBox();
				authDB.setText("Teilnehmer-Authentifizierung!");
				authDB.setAnimationEnabled(true);
				
				authDBVP = new VerticalPanel();
				authDBGrid = new Grid(3,3);
				authDBVP.add(authDBGrid);
				
				authDBUserLabel = new Label("HdM-Kürzel: ");
				authDBUserTB = new TextBox();
				authDBPWLabel = new Label("Passwort: ");
				authDBPWTB = new PasswordTextBox();
				authDBButton = new Button("OK");
				
				authDBGrid.setWidget(0, 0, authDBUserLabel);
				authDBGrid.setWidget(0, 1, authDBUserTB);
				authDBGrid.setWidget(1, 0, authDBPWLabel);
				authDBGrid.setWidget(1, 1, authDBPWTB);
				authDBGrid.setWidget(1, 2, authDBButton);
				
				authDBLabel = new Label();
				authDBVP.add(authDBLabel);
				
				authDB.setWidget(authDBVP);
				
				authDB.center();
				authDBUserTB.setFocus(true);
				
				addClickHandlerToSDBButton();
				
				authDBCloseButton = new Button("Close");
				authDBCloseButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						authDB.hide();
						entryVP.setVisible(true);
					}
					
				});
				authDBGrid.setWidget(2, 0, authDBCloseButton);
			}
			
		});
	}
	
	public void addClickHandlerToSDBButton() {
		authDBButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				authDBUserTB.setEnabled(false);
				authDBButton.setEnabled(false);
				authDBPWTB.setEnabled(false);
				
				/*
				 * Negative Authentifizierung
				 */
				/*
				authDBUserTB.setText("");
				authDBPWTB.setText("");
				authDBLabel.setText("Anmeldung nicht erfolgreich");
				
				authDBUserTB.setEnabled(true);
				authDBButton.setEnabled(true);
				authDBPWTB.setEnabled(true);
				*/
				
				verwaltung.signInParticipant("lz006", new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
					}

					@Override
					public void onSuccess(Void result) {
						authDBUserTB.setText("");
						authDBPWTB.setText("");
						authDB.hide();
						
						setChooseFormToMain();
						
					}
					
				});
				
				
								
			}
			
		});
	}
	
	public void createDockLayoutPanel() {
		// Initialisierung eines DockLayoutPanels
		p = new DockLayoutPanel(Unit.EM);
				
		// Initialisierung des Panels, welches den CellTree aufnimmt 
		navi = new ScrollPanel();
		// Panel den linken Bildschirmbereich zuweisen und zunächst verbergen
		p.addWest(navi, 25);
		p.setWidgetHidden(navi, true);		
		
		// Panel den Kopfbereich zuweisen
		traeger = new HorizontalPanel();
		p.addNorth(traeger, 5);
		p.setWidgetHidden(traeger, true);	
		
		// Panel den Fußbereich zuweisen
		footPanel = new VerticalPanel();
		p.addSouth(footPanel, 5);
		p.setWidgetHidden(footPanel, true);	
		
		// Panel den rechten Bildschirmbereich zuweisen
		traegerInfoPanel = new VerticalPanel();
		p.addEast(traegerInfoPanel, 30);
		p.setWidgetHidden(traegerInfoPanel, true);	
		
		// Panel dem Hauptbereich (Mitte) zuweisen
		p.add(mainPanel);
		
		rlp = RootLayoutPanel.get();
		rlp.add(p);
	}

}
