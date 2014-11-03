package de.hdm.clicker.client;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

import java.util.Vector;
import java.util.List;

import com.google.gwt.view.client.ListDataProvider;

/**
 * Diese Klasse stellt die Funktionalität des im Projekt verwendeten CellTree
 * bereit. Sie definiert die Knoten eines Baumes und dessen Reaktionen auf
 * Ereignisse. 
 * 
 * @author Rathke (implement: Roth, Klatt, Zimmermann, Moser, Sonntag, Zanella)
 * @version 1.0
 * 
 */
public class CustomTreeViewModel implements TreeViewModel {

	/**
	 * Referenzen auf alle Instanzen der "Formklassen" um Zugriff auf deren Methoden
	 * zu haben
	 */
	private DozentForm dF;
	private BelegungForm bF;
	private LehrveranstaltungForm lF;
	private StudiengangForm sgF;
	private SemesterverbandForm svF;
	private RaumForm rF;

	/**
	 * Referenz auf die Entry-Point-Klasse um Zugriff auf deren Methoden zu haben
	 */
	Clicker spt2;

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	private VerwaltungAsync verwaltung = null;

	/**
	 * Container welche alle Objekte enthalten, welche im Tree als Blätter oder
	 * Knoten abgebildet werden
	 */
	private ListDataProvider<Lehrveranstaltung> lehrveranstaltungDataProvider;
	private ListDataProvider<Dozent> dozentDataProvider;
	private ListDataProvider<Raum> raumDataProvider;
	private ListDataProvider<Semesterverband> semesterVerbandDataProvider;
	private ListDataProvider<Studiengang> studiengangDataProvider;
	private ListDataProvider<String> dummyDataProvider;
	
	/**
	 * Hilfs-Container um den richtigen Knoten zu ermitteln, dessen Kindelemente
	 * aktualisert werden sollen
	 */
	private Vector<ListDataProvider<Semesterverband>> hVectorSV = new Vector<ListDataProvider<Semesterverband>>();
	private Vector<Studiengang> hVectorSGforSV = new Vector<Studiengang>();
	private Vector<ListDataProvider<Lehrveranstaltung>> hVectorLV = new Vector<ListDataProvider<Lehrveranstaltung>>();
	private Vector<Studiengang> hVectorSGforLV = new Vector<Studiengang>();
	
	/**
	 * Hilfs-Container um den richtigen Studiengang-Knoten zu ermitteln, um diese 
	 * aktualisieren zu können. Da Studiengänge zur weiteren Strukturierung von
	 * Lehrveranstaltungen und Semesterverbänden dienen
	 */
	private ListDataProvider<Studiengang> studiengangForLVDataProvider = null;
	private ListDataProvider<Studiengang> studiengangForSVDataProvider = null;
	
	/**
	 * Flags die zur Bestimmung dienen, wann angezeigte Studiengänge "leaf" sind,
	 * da Studiengänge auch zu weiteren Untergliederung von Lehrveranstaltungen
	 * und Semesterverbände benutzt werden
	 */
	private boolean unterMenuLVzuSG = false;
	private boolean unterMenuSVzuSG = false;

	/**
	 * Referenz auf das CellTree-Objekt um Zugriff auf dessen Methoden zu haben
	 */
	private CellTree cellTree = null;

	/**
	 * Referenz auf das (Root)TreeNode-Objekt des CellTrees um Zugriff auf dessen 
	 * Kindknoten zu bekommen, damit Knoten automatisch geschlossen werden können.
	 * Dies dient dem Zweck, zu verhindern, dass Studiengänge mehrfach und unter
	 * verschiedenen Gesichtspunkten im CellTree sichtbar sind, was zu Problemen
	 * führt
	 */
	private TreeNode rootNode;

	/**
	 * ProvidesKey<Object>-Objekt welches der Konstruktor der SingleSelectionModel<Object>-Klasse
	 * als Argument verlangt. Dies dient zur eindeutigen Identifikation eines jeden Kindelements
	 * im CellTree
	 */
	private ProvidesKey<Object> keyProvider = new ProvidesKey<Object>() {
		public Integer getKey(Object object) {

			if (object == null) {
				return null;
			} 
			else if (object instanceof Lehrveranstaltung) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Dozent) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Raum) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Semesterverband) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Studiengang) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof Belegung) {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Editor") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Report") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Anlegen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozent") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Lehrveranstaltung") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Belegung") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Raum") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Semesterverband") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studiengang") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Verwalten") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozenten") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Lehrveranstaltungen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Belegungen") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Räume") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Semesterverbände") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studiengänge") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Dozentenplan") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Raumplan") {
				return new Integer(object.hashCode());
			} 
			else if (object instanceof String && (String) object == "Studentenplan") {
				return new Integer(object.hashCode());
			} 
			else {
				return null;
			}
		}
	};
	
	/**
	 * SingleSelectionModel<Object>-Objekt welches der Konstruktor der DefaultNodeInfo<String>-Klasse
	 * als Argument verlangt. Das SingleSelectionModel<Object>-Objekt enthält einen "SelectionChangeHandler",
	 * in welchem die Reaktionen definiert werden, welche bei Auswahl eines spezifischen Kind-Element-Typs
	 * angestoßen werden
	 */
	private SingleSelectionModel<Object> selectionModel = new SingleSelectionModel<Object>(keyProvider);

	/**
	 * Komstruktor welcher den "SelectionChangeHandler" definiert und dem 
	 * "selectionModel" hinzufügt, so dass der CellTree-Funktionsbereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */
	public CustomTreeViewModel(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		// Initialisieren, definieren und "adden" eines "SelectionChangeHandlers"
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {
				Object selection = selectionModel.getSelectedObject();

				/*
				 *  Bei Klick auf ein "Dozent-Objekt", wird dieses erneut geladen und
				 *  mittelbar die DozentForm in den Arbeitsbereich der GUI geladen
				 */
				if (selection instanceof Dozent) {

					DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "wait");

					verwaltung.auslesenDozent((Dozent) selection, new AsyncCallback<Vector<Dozent>>() {
						public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
						}
	
						public void onSuccess(Vector<Dozent> result) {
							setSelectedDozent(result.elementAt(0));
						}
					});

				} 
				/*
				 *  Bei Klick auf ein "Lehrveranstaltung-Objekt", wird dieses erneut geladen und
				 *  mittelbar die LehrveranstaltungForm in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Lehrveranstaltung) {

					verwaltung.auslesenLehrveranstaltung((Lehrveranstaltung) selection,	new AsyncCallback<Vector<Lehrveranstaltung>>() {
							public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
							}

							public void onSuccess(Vector<Lehrveranstaltung> result) {
								setSelectedLehrveranstaltung(result.elementAt(0));
							}
					});

				} 
				/*
				 *  Bei Klick auf ein "Raum-Objekt", wird  mittelbar die RaumForm 
				 *  in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Raum) {
					setSelectedRaum((Raum) selection);
					
				} 
				/*
				 *  Bei Klick auf ein "Semesterverband-Objekt", wird dieses erneut geladen und
				 *  mittelbar die SemesterverbandForm in den Arbeitsbereich der GUI geladen
				 */
				else if (selection instanceof Semesterverband) {

					verwaltung.auslesenSemesterverband((Semesterverband) selection,	new AsyncCallback<Vector<Semesterverband>>() {
						public void onFailure(Throwable caught) {
								DOM.setStyleAttribute(RootPanel.getBodyElement(),"cursor", "default");
								Window.alert(caught.getMessage());
						}
						
						public void onSuccess(Vector<Semesterverband> result) {
								setSelectedSemesterverband(result.elementAt(0));
						}
					});
				} 
				/*
				 *  Bei Klick auf ein "Semesterverband-Objekt", wird dieses erneut geladen und
				 *  mittelbar die SemesterverbandForm in den Arbeitsbereich der GUI geladen
				 *  (Voraussetzung: unterMenuLVzuSG = false && unterMenuSVzuSG = false)
				 */
				else if (selection instanceof Studiengang && !unterMenuLVzuSG && !unterMenuSVzuSG) {

					verwaltung.auslesenStudiengang((Studiengang) selection,	new AsyncCallback<Vector<Studiengang>>() {
						public void onFailure(Throwable caught) {
							DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
							Window.alert(caught.getMessage());
						}

						public void onSuccess(Vector<Studiengang> result) {
							setSelectedStudiengang(result.elementAt(0));
						}
					});

				} 
				
				/*
				 *  Bei Klick auf ein "String-Objekt" wird die entsprechende Formklasse in der
				 *  "Anlegen-Variante" in die in den Arbeitsbereich GUI geladen
				 */
				else if (selection instanceof String && (String) selection == "Belegungen") {
					setSelectedBelegung();
				} 
				else if (selection instanceof String && (String) selection == "Belegung") {
					belegungAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Dozent") {
					dozentAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Lehrveranstaltung") {
					lehrveranstaltungAnlegenMaske();
				}
				else if (selection instanceof String && (String) selection == "Studiengang") {
					studiengangAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Semesterverband") {
					semesterverbandAnlegenMaske();
				} 
				else if (selection instanceof String && (String) selection == "Raum") {
					raumAnlegenMaske();
				}
				else if (selection instanceof String && (String) selection == "Studentenplan") {
					setSelectedStudentenplan();
				}
				else if (selection instanceof String && (String) selection == "Dozentenplan") {
					setSelectedDozentenplan();
				}
				else if (selection instanceof String && (String) selection == "Raumplan") {
					setSelectedRaumplan();
				}
			}
		});

	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Dozenten zu bearbeiten
	 * 
	 * @param	dozent - Referenz auf ein Dozent-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedDozent(Dozent dozent) {
		spt2.clearInfoPanels();
		spt2.setDozentFormToMain();
		this.dF.setDtvm(this);
		this.dF.visibiltyAendernButtons();
		this.dF.setShownDozent(dozent);
		this.dF.fillForm();
		this.dF.lehrveranstaltungenAnzeigen();
		DOM.setStyleAttribute(RootPanel.getBodyElement(), "cursor", "default");
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Dozenten anzulegen
	 */
	public void dozentAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setDozentFormToMain();
		this.dF.setDtvm(this);
		dF.noVisibiltyAendernButtons();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Lehrveranstaltung zu bearbeiten
	 * 
	 * @param	lehrveranstaltung - Referenz auf ein Lehrveranstaltung-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {
		spt2.clearInfoPanels();
		spt2.setLehrveranstaltungFormToMain();
		this.lF.setDtvm(this);
		this.lF.setShownLehrveranstaltung(lehrveranstaltung);
		this.lF.fillForm();
		this.lF.dozentenAnzeigen();
		this.lF.studiengaengeAnzeigen();
		this.lF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Lehrveranstaltung anzulegen
	 */
	public void lehrveranstaltungAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setLehrveranstaltungFormToMain();
		this.lF.setDtvm(this);
		this.lF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Raum zu bearbeiten
	 * 
	 * @param	raum - Referenz auf ein Raum-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedRaum(Raum raum) {
		spt2.clearInfoPanels();
		spt2.setRaumFormToMain();
		this.rF.setDtvm(this);
		this.rF.setShownRaum(raum);
		this.rF.fillForm();
		this.rF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Raum anzulegen
	 */
	public void raumAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setRaumFormToMain();
		this.rF.setDtvm(this);
		this.rF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Semesterverband zu bearbeiten
	 * 
	 * @param	semesterverband - Referenz auf ein Semesterverband-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedSemesterverband(Semesterverband semesterverband) {
		spt2.clearInfoPanels();
		spt2.setSemesterverbandFormToMain();
		this.svF.setDtvm(this);
		this.svF.setShownSemesterverband(semesterverband);
		this.svF.ladenStudiengaenge();
		this.svF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Semesterverband anzulegen
	 */
	public void semesterverbandAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setSemesterverbandFormToMain();
		this.svF.setDtvm(this);
		this.svF.ladenStudiengaenge();
		this.svF.anlegenMaske();

	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Studiengang zu bearbeiten
	 * 
	 * @param	studiengang - Referenz auf ein Studiengang-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedStudiengang(Studiengang studiengang) {
		spt2.clearInfoPanels();
		spt2.setStudiengangFormToMain();
		this.sgF.setDtvm(this);
		this.sgF.setShownStudiengang(studiengang);
		this.sgF.fillForm();
		this.sgF.lvAnzeigen();
		this.sgF.semesterverbaendeAnzeigen();
		this.sgF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Studiengang anzulegen
	 */
	public void studiengangAnlegenMaske() {
		spt2.clearInfoPanels();
		spt2.setStudiengangFormToMain();
		this.sgF.setDtvm(this);
		this.sgF.anlegenMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Belegung zu bearbeiten
	 */
	public void setSelectedBelegung() {
		spt2.clearInfoPanels();
		this.spt2.setBelegungFormToMain();
		this.bF.setDtvm(this);
		this.bF.aendernMaske(false);
		this.bF.ladenStudiengaenge();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen eine Belegung anzulegen
	 */
	public void belegungAnlegenMaske() {
		spt2.clearInfoPanels();
		this.spt2.setBelegungFormToMain();
		this.bF.setDtvm(this);
		this.bF.aendernMaske(true);
		this.bF.ladenStudiengaenge();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Studentenplan
	 * erzeugen zu lassen
	 */
	public void setSelectedStudentenplan() {
		this.spt2.setStudentenPlanFormToMain();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Dozentenplan
	 * erzeugen zu lassen
	 */
	public void setSelectedDozentenplan() {
		this.spt2.setDozentenPlanFormToMain();
	}

	/**
	 * Methode welche wiederum die Oberfläche in die Arbeitsfläche der
	 * GUI läd, die es dem User ermöglicht sich einen Raumplan
	 * erzeugen zu lassen
	 */
	public void setSelectedRaumplan() {
		this.spt2.setRaumPlanFormToMain();
	}

	/**
	 * Methode welche das Interface "TreeViewModel" vorschreibt. Hier werden die Kind-Elemente
	 * eines jeden Knoten im CellTree definiert
	 * 
	 * @param	value - Generic, welches das gegenwärtig gewählte Knoten-Element repräsentiert  
	 */
	public <T> NodeInfo<?> getNodeInfo(T value) {

		// Wurzelknoten enthält "Editor" und "Report" als Kind-Elemente
		if (value instanceof String && (String) value == "Root") {

			dummyDataProvider = new ListDataProvider<String>();

			String editor = "Editor";
			String report = "Report";

			dummyDataProvider.getList().add(editor);
			dummyDataProvider.getList().add(report);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}
		
		// "Editor" enthält "Anlegen" und "Verwalten" als Kind-Elemente		
		if (value instanceof String && (String) value == "Editor") {

			dummyDataProvider = new ListDataProvider<String>();

			String anlegen = "Anlegen";
			String verwalten = "Verwalten";

			dummyDataProvider.getList().add(anlegen);
			dummyDataProvider.getList().add(verwalten);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Anlegen" enthält die anlegbaren BusinessObjects in String-Repräsentation als Kind-Elemente
		if (value instanceof String && (String) value == "Anlegen") {

			dummyDataProvider = new ListDataProvider<String>();

			String lehrveranstaltung = "Lehrveranstaltung";
			String dozent = "Dozent";
			String belegung = "Belegung";
			String raum = "Raum";
			String semesterverband = "Semesterverband";
			String studiengang = "Studiengang";

			dummyDataProvider.getList().add(lehrveranstaltung);
			dummyDataProvider.getList().add(dozent);
			dummyDataProvider.getList().add(belegung);
			dummyDataProvider.getList().add(raum);
			dummyDataProvider.getList().add(semesterverband);
			dummyDataProvider.getList().add(studiengang);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Anlegen" enthält die editierbaren BusinessObjects in String-Repräsentation als Kind-Elemente
		if (value instanceof String && (String) value == "Verwalten") {

			addOpenHandler();

			dummyDataProvider = new ListDataProvider<String>();

			String lehrveranstaltungen = "Lehrveranstaltungen";
			String dozenten = "Dozenten";
			String belegungen = "Belegungen";
			String raeume = "Räume";
			String semesterverbaende = "Semesterverbände";
			String studiengaenge = "Studiengänge";

			dummyDataProvider.getList().add(lehrveranstaltungen);
			dummyDataProvider.getList().add(dozenten);
			dummyDataProvider.getList().add(belegungen);
			dummyDataProvider.getList().add(raeume);
			dummyDataProvider.getList().add(semesterverbaende);
			dummyDataProvider.getList().add(studiengaenge);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		/*
		 *  "Lehrveranstaltungen" enthält alle Studiengang-Objekte als Kind-Elemente,
		 *  diese dienen nur zu weiteren Untergliederung der eigentlichen Lehrveranstaltungen
		 */
		if (value instanceof String && (String) value == "Lehrveranstaltungen") {

			unterMenuLVzuSG = true;
			unterMenuSVzuSG = false;

			studiengangForLVDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangForLVDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangForLVDataProvider, new StudiengangCell(), selectionModel, null);
		}

		/*
		 *  Studiengang-Objekte enthalten alle Lehrveranstaltungs-Objekte als Kind-Elemente
		 *  (Voraussetzung: unterMenuLVzuSG = true)
		 */
		if (value instanceof Studiengang && unterMenuLVzuSG) {

			unterMenuLVzuSG = true;
			unterMenuSVzuSG = false;

			lehrveranstaltungDataProvider = new ListDataProvider<Lehrveranstaltung>();
			verwaltung.auslesenLehrveranstaltungenNachSG((Studiengang) value,
					new AsyncCallback<Vector<Lehrveranstaltung>>() {
						public void onFailure(Throwable t) {
							Window.alert(t.toString());
						}

						public void onSuccess(Vector<Lehrveranstaltung> lehrveranstaltungen) {
							for (Lehrveranstaltung lehrveranstaltung : lehrveranstaltungen) {
								lehrveranstaltungDataProvider.getList().add(lehrveranstaltung);
							}
						}
					});
			
			hVectorLV.add(lehrveranstaltungDataProvider);
			hVectorSGforLV.add((Studiengang)value);

			return new DefaultNodeInfo<Lehrveranstaltung>(lehrveranstaltungDataProvider, new LehrveranstaltungCell(),
					selectionModel, null);
		}

		// "Dozenten" enthält Dozenten-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Dozenten") {

			dozentDataProvider = new ListDataProvider<Dozent>();

			verwaltung.auslesenAlleDozenten(new AsyncCallback<Vector<Dozent>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Dozent> dozenten) {
					for (Dozent dozent : dozenten) {
						dozentDataProvider.getList().add(dozent);
					}
				}
			});

			return new DefaultNodeInfo<Dozent>(dozentDataProvider, new DozentCell(), selectionModel, null);
		}

		// "Räume" enthält Raum-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Räume") {

			raumDataProvider = new ListDataProvider<Raum>();
			verwaltung.auslesenAlleRaeume(new AsyncCallback<Vector<Raum>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Raum> raeume) {
					for (Raum raum : raeume) {
						raumDataProvider.getList().add(raum);
					}
				}
			});

			return new DefaultNodeInfo<Raum>(raumDataProvider, new RaumCell(), selectionModel, null);
		}

		/*
		 *  "Semesterverbände" enthält alle Studiengang-Objekte als Kind-Elemente,
		 *  diese dienen nur zu weiteren Untergliederung der eigentlichen Semesterverbände
		 */
		if (value instanceof String && (String) value == "Semesterverbände") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = true;

			studiengangForSVDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangForSVDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangForSVDataProvider, new StudiengangCell(), selectionModel, null);
		}

		/*
		 *  Studiengang-Objekte enthalten alle Semesterverband-Objekte als Kind-Elemente
		 *  (Voraussetzung: unterMenuSVzuSG = true)
		 */
		if (value instanceof Studiengang && unterMenuSVzuSG) {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = true;

			semesterVerbandDataProvider = new ListDataProvider<Semesterverband>();
			verwaltung.auslesenSemesterverbaendeNachStudiengang((Studiengang) value, new AsyncCallback<Vector<Semesterverband>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Semesterverband> semesterverbaende) {
					for (Semesterverband semesterverband : semesterverbaende) {
						semesterVerbandDataProvider.getList().add(semesterverband);
					}
				}
			});
			
			hVectorSV.add(semesterVerbandDataProvider);
			hVectorSGforSV.add((Studiengang)value);

			return new DefaultNodeInfo<Semesterverband>(semesterVerbandDataProvider, new SemesterverbandCell(),	selectionModel, null);
		}

		// "Studiengänge" enthält Studiengang-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Studiengänge") {

			unterMenuLVzuSG = false;
			unterMenuSVzuSG = false;

			studiengangDataProvider = new ListDataProvider<Studiengang>();
			verwaltung.auslesenAlleStudiengaengeOhneSVuLV(new AsyncCallback<Vector<Studiengang>>() {
				public void onFailure(Throwable t) {
					Window.alert(t.toString());
				}

				public void onSuccess(Vector<Studiengang> studiengaenge) {
					for (Studiengang studiengang : studiengaenge) {
						studiengangDataProvider.getList().add(studiengang);
					}
				}
			});

			return new DefaultNodeInfo<Studiengang>(studiengangDataProvider, new StudiengangCell(), selectionModel, null);
		}

		// "Report" enthält "Studentenplan", "Dozentenplan" und "Raumplan" als Kind-Elemente
		if (value instanceof String && (String) value == "Report") {

			spt2.popupInfo();

			dummyDataProvider = new ListDataProvider<String>();

			String a = "Studentenplan";
			String b = "Dozentenplan";
			String c = "Raumplan";

			dummyDataProvider.getList().add(a);
			dummyDataProvider.getList().add(b);
			dummyDataProvider.getList().add(c);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		return null;

	}

	/**
	 * Methode welche die List des "dozentDataProviders" hinsichtlich eines geänderten
	 * Dozenten aktualisiert. Damit bspw. eine Namensänderung eines Dozenten auch im
	 * CellTree ersichtlich wird
	 * 
	 * @param	dozent - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateDozent(Dozent dozent) {
		List<Dozent> dozentList = dozentDataProvider.getList();
		int i = 0;
		for (Dozent a : dozentList) {
			if (a.getId() == dozent.getId()) {
				dozentList.set(i, dozent);
				dozentDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die List des "dozentDataProviders" dahingehend aktualisiert,
	 * dass ein Dozent entfernt werden muss, da dieser aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt.
	 * 
	 * @param	dozent - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenDozent(Dozent dozent) {

		int i = 0;

		for (Dozent d : dozentDataProvider.getList()) {
			if (d.getId() == dozent.getId()) {

				dozentDataProvider.getList().remove(i);
				dozentDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}

	}

	/**
	 * Methode welche die List des "dozentDataProviders" dahingehend aktualisiert,
	 * dass ein Dozent hinzugefügt werden muss, da dieser neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss.
	 * 
	 * @param	dozent - Objekt, welcher neu hinzugefügt wird  
	 */
	public void addDozent(Dozent dozent) {
		if (dozentDataProvider != null) {
			dozentDataProvider.getList().add(
			dozentDataProvider.getList().size(), dozent);
			dozentDataProvider.refresh();
		}
	}

	/**
	 * Methode welche die Listen aller "lehrveranstaltungDataProvider" dahingehend aktualisiert,
	 * dass eine Lehrveranstaltung entfernt werden muss, da diese aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt. Eine LV wird 
	 * wenn sie mehreren Studiengängen zugeordnet ist, mehrfach im CellTree gelistet)
	 * 
	 * @param	lehrveranstaltung - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {

		Vector<Integer> vi = new Vector<Integer>();;
		
		//Ermitteln des entsprechenden ListDataProviders
		if (hVectorLV.size() != 0) {
			for (int g = 0; g < hVectorSGforLV.size(); g++) {
				for (int i = 0; i < lehrveranstaltung.getStudiengaenge().size(); i++) {
					if (lehrveranstaltung.getStudiengaenge().elementAt(i).getId() == hVectorSGforLV.elementAt(g).getId()) {
						vi.add(new Integer(g));
					}
				}
			}
		}
		
		// Aktualisieren der ListDataProvider
		for (int g = 0; g < vi.size(); g++) {
			
			int i = 0;

			for (Lehrveranstaltung d : hVectorLV.elementAt(vi.elementAt(g)).getList()) {
				if (d.getId() == lehrveranstaltung.getId()) {

					hVectorLV.elementAt(vi.elementAt(g)).getList().remove(i);
					hVectorLV.elementAt(vi.elementAt(g)).refresh();
					break;
				} else {
					i++;
				}
			}
		}
	}

	/**
	 * Methode welche die Listen aller "lehrveranstaltungDataProvider" hinsichtlich einer geänderten
	 * Lehrveranstaltung aktualisiert. Damit bspw. eine Änderung der Bezeichung einer Lehrveranstaltung 
	 * auch im CellTree ersichtlich wird. (Eine LV wird wenn sie mehreren Studiengängen zugeordnet ist,
	 * mehrfach im CellTree gelistet)
	 * 
	 * @param	lehrveranstaltung - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateLehrveranstaltung(Lehrveranstaltung lehrveranstaltung) {
		
		// Löschen der LV aus allen LV-ListDataProvider
		for (int g = 0; g < hVectorLV.size(); g++) {
			
			int i = 0;

			for (Lehrveranstaltung d : hVectorLV.elementAt(g).getList()) {
				if (d.getId() == lehrveranstaltung.getId()) {

					hVectorLV.elementAt(g).getList().remove(i);
					hVectorLV.elementAt(g).refresh();
					break;
				} else {
					i++;
				}
			}
		}
		
		Vector<Integer> vi = new Vector<Integer>();;
		
		//Ermitteln der entsprechenden ListDataProvider
		if (hVectorLV.size() != 0) {
			for (int g = 0; g < hVectorSGforLV.size(); g++) {
				for (int i = 0; i < lehrveranstaltung.getStudiengaenge().size(); i++) {
					if (lehrveranstaltung.getStudiengaenge().elementAt(i).getId() == hVectorSGforLV.elementAt(g).getId()) {
						vi.add(new Integer(g));
					}
				}
			}
		}
		
		// Aktualisieren der ListDataProvider
		for (int g = 0; g < vi.size(); g++) {
			
			if (hVectorLV.elementAt(vi.elementAt(g)) != null) {
				hVectorLV.elementAt(vi.elementAt(g)).getList().add(hVectorLV.elementAt(vi.elementAt(g)).getList().size(), lehrveranstaltung);
				hVectorLV.elementAt(vi.elementAt(g)).refresh();
			}
		}
	}

	/**
	 * Methode welche die Listen aller "lehrveranstaltungDataProvider" dahingehend aktualisiert,
	 * dass eine Lehrveranstaltung hinzugefügt werden muss, da diese neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss. (Eine LV wird wenn sie mehreren Studiengängen 
	 * zugeordnet ist, mehrfach im CellTree gelistet)
	 * 
	 * @param	lv - Lehrveranstaltung-Objekt, welcher neu hinzugefügt wird  
	 */
	public void addLehrveranstaltung(Lehrveranstaltung lv) {
		
		Vector<Integer> vi = new Vector<Integer>();;
		
		//Ermitteln der entsprechenden ListDataProvider
		if (hVectorLV.size() != 0) {
			for (int g = 0; g < hVectorSGforLV.size(); g++) {
				for (int i = 0; i < lv.getStudiengaenge().size(); i++) {
					if (lv.getStudiengaenge().elementAt(i).getId() == hVectorSGforLV.elementAt(g).getId()) {
						vi.add(new Integer(g));
					}
				}
			}
		}
		
		// Aktualisieren der ListDataProvider
		for (int g = 0; g < vi.size(); g++) {
			
			if (hVectorLV.elementAt(vi.elementAt(g)) != null) {
				hVectorLV.elementAt(vi.elementAt(g)).getList().add(hVectorLV.elementAt(vi.elementAt(g)).getList().size(), lv);
				hVectorLV.elementAt(vi.elementAt(g)).refresh();
			}
		}
	}

	/**
	 * Methode welche die Listen aller "studiengangDataProvider" dahingehend aktualisiert,
	 * dass ein Studiengang entfernt werden muss, da dieser aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt.
	 * 
	 * @param	studiengang - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenStudiengang(Studiengang studiengang) {

		int i = 0;

		for (Studiengang sg : studiengangDataProvider.getList()) {
			if (sg.getId() == studiengang.getId()) {

				if (studiengangForSVDataProvider != null) {
					studiengangForSVDataProvider.getList().remove(i);
					studiengangForSVDataProvider.refresh();
				}
				
				if (studiengangForLVDataProvider != null) {
					studiengangForLVDataProvider.getList().remove(i);
					studiengangForLVDataProvider.refresh();
				}
				
				studiengangDataProvider.getList().remove(i);
				studiengangDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die Listen aller "studiengangDataProvider" hinsichtlich eines geänderten
	 * Studiengangs aktualisiert. Damit bspw. eine Änderung einer Bezeichnung auch im
	 * CellTree ersichtlich wird
	 * 
	 * @param	studiengang - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateStudiengang(Studiengang studiengang) {
		
		List<Studiengang> studiengangForSVList = null;
		List<Studiengang> studiengangForLVList = null;
		
		if (studiengangForSVDataProvider != null) {
			studiengangForSVList = studiengangForSVDataProvider.getList();
		}
		
		if (studiengangForLVDataProvider != null) {
			studiengangForLVList = studiengangForLVDataProvider.getList();
		}
		
		List<Studiengang> studiengangList = studiengangDataProvider.getList();
		int i = 0;
		for (Studiengang sg : studiengangDataProvider.getList()) {
			if (sg.getId() == studiengang.getId()) {
				
				if (studiengangForSVList != null) {
					studiengangForSVList.set(i, studiengang);
					studiengangForSVDataProvider.refresh();
				}
				
				if (studiengangForLVList != null) {
					studiengangForLVList.set(i, studiengang);
					studiengangForLVDataProvider.refresh();
				}
				
				studiengangList.set(i, studiengang);
				studiengangDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die Listen aller "studiengangDataProvider" dahingehend aktualisiert,
	 * dass ein Studiengang hinzugefügt werden muss, da dieser neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss.
	 * 
	 * @param	sg - Studiengang-Objekt, welcher neu hinzugefügt wird  
	 */
	public void addStudiengang(Studiengang sg) {
		if (studiengangDataProvider != null) {
			studiengangDataProvider.getList().add(
			studiengangDataProvider.getList().size(), sg);
			studiengangDataProvider.refresh();			
		}
		
		if (studiengangForSVDataProvider != null) {
			studiengangForSVDataProvider.getList().add(
			studiengangForSVDataProvider.getList().size(), sg);
			studiengangForSVDataProvider.refresh();	
		}
		
		if (studiengangForLVDataProvider != null) {
			studiengangForLVDataProvider.getList().add(
			studiengangForLVDataProvider.getList().size(), sg);
			studiengangForLVDataProvider.refresh();	
		}
	}

	/**
	 * Methode welche die List des "semesterverbandDataProviders" dahingehend aktualisiert,
	 * dass ein Semesterverband entfernt werden muss, da dieser aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt.
	 * 
	 * @param	semesterverband - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenSemesterverband(Semesterverband semesterverband) {

		int g = 0;
		
		//Ermitteln des entsprechenden ListDataProviders
		if (hVectorSV.size() != 0) {
			for (int i = 0; i < hVectorSGforSV.size(); i++) {
				if (semesterverband.getStudiengang().getId() == hVectorSGforSV.elementAt(i).getId()) {
					g = i;
					break;
				}
			}
		}
		
		int i = 0;

		// Aktualisieren des ListDataProviders
		for (Semesterverband sv : hVectorSV.elementAt(g).getList()) {
			if (sv.getId() == semesterverband.getId()) {

				hVectorSV.elementAt(g).getList().remove(i);
				hVectorSV.elementAt(g).refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die List des "semesterverbandDataProviders" hinsichtlich eines geänderten
	 * Semesterverband aktualisiert. Damit bspw. eine Änderung einer Bezeichnung auch im
	 * CellTree ersichtlich wird
	 * 
	 * @param	semesterverband - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateSemesterverband(Semesterverband semesterverband) {
				
		int i = 0;

		// Aktualisieren des ListDataProviders
		if (hVectorSV.size() != 0) {
			for (int g = 0; g < hVectorSV.size(); g++) {
				i = 0;
				boolean deleted = false;
				for (Semesterverband sv : hVectorSV.elementAt(g).getList()) {
					if (sv.getId() == semesterverband.getId()) {

						hVectorSV.elementAt(g).getList().remove(i);
						hVectorSV.elementAt(g).refresh();
						deleted = true;
						break;
					} else {
						i++;
					}
				}
				if (deleted) {
					break;
				}
			}
		}
		
		int j = 0;
		
		//Ermitteln des entsprechenden ListDataProviders
		if (hVectorSV.size() != 0) {
			for (int k = 0; k < hVectorSGforSV.size(); k++) {
				if (semesterverband.getStudiengang().getId() == hVectorSGforSV.elementAt(k).getId()) {
					j = k;
					break;
				}
			}
		}
		
		// Aktualisieren des ListDataProviders
		if (hVectorSV.elementAt(j) != null) {
			hVectorSV.elementAt(j).getList().add(hVectorSV.elementAt(j).getList().size(), semesterverband);
			hVectorSV.elementAt(j).refresh();
		}
	}

	/**
	 * Methode welche die List des "semesterverbandDataProviders" dahingehend aktualisiert,
	 * dass ein Semesterverband hinzugefügt werden muss, da dieser neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss.
	 * 
	 * @param	sv - Semesterverband-Objekt, welcher neu hinzugefügt wird  
	 */
	public void addSemesterverband(Semesterverband sv) {
		
		int g = 0;
		
		//Ermitteln des entsprechenden ListDataProviders
		if (hVectorSV.size() != 0) {
			for (int i = 0; i < hVectorSGforSV.size(); i++) {
				if (sv.getStudiengang().getId() == hVectorSGforSV.elementAt(i).getId()) {
					g = i;
					break;
				}
			}
		}
		
		// Aktualisieren des ListDataProviders
		if (hVectorSV.elementAt(g) != null) {
			hVectorSV.elementAt(g).getList().add(hVectorSV.elementAt(g).getList().size(), sv);
			hVectorSV.elementAt(g).refresh();
		}
	}

	/**
	 * Methode welche die List des "raumDataProviders" dahingehend aktualisiert,
	 * dass ein Raum entfernt werden muss, da dieser aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt.
	 * 
	 * @param	raum - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenRaum(Raum raum) {

		int i = 0;

		for (Raum r : raumDataProvider.getList()) {
			if (r.getId() == raum.getId()) {

				raumDataProvider.getList().remove(i);
				raumDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die List des "raumDataProviders" hinsichtlich eines geänderten
	 * Raumes aktualisiert. Damit bspw. eine Änderung einer Bezeichnung auch im
	 * CellTree ersichtlich wird
	 * 
	 * @param	raum - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateRaum(Raum raum) {
		List<Raum> raumList = raumDataProvider.getList();
		int i = 0;
		for (Raum r : raumDataProvider.getList()) {
			if (r.getId() == raum.getId()) {
				raumList.set(i, raum);
				raumDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die List des "raumDataProviders" dahingehend aktualisiert,
	 * dass ein Raum hinzugefügt werden muss, da dieser neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss.
	 * 
	 * @param	raum - -Objekt, welches neu hinzugefügt wird  
	 */
	public void addRaum(Raum raum) {
		if (raumDataProvider != null) {
			raumDataProvider.getList().add(raumDataProvider.getList().size(), raum);
			raumDataProvider.refresh();
		}
	}

	/**
	 * Methode welche dem CellTree "Auskunft" darüber gibt, ob es sich bei einem Kind-Element-Typ
	 * um ein Blatt (leaf) handelt. Andernfalls wird ein Kind-Element als Knoten dargestellt
	 * 
	 * @param	value - Object-Instanz, da der CellTree unterschiedliche Objektdatentypen mittels seiner
	 * 			Zellen abbilden kann  
	 */
	public boolean isLeaf(Object value) {

		if (value instanceof String && (String) value == "Dozent") {
			return true;
		}

		else if (value instanceof String && (String) value == "Lehrveranstaltung") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Belegung") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Belegungen") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Raum") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Semesterverband") {
			return true;
		} 
		else if (value instanceof String && (String) value == "Studiengang") {
			return true;
		}
		else if (value instanceof String && (String) value == "Studentenplan") {
			return true;
		}
		else if (value instanceof String && (String) value == "Dozentenplan") {
			return true;
		} else if (value instanceof String && (String) value == "Raumplan") {
			return true;
		}
		else if (value instanceof Dozent) {
			return true;
		}
		else if (value instanceof Lehrveranstaltung) {
			return true;
		}
		else if (value instanceof Semesterverband) {
			return true;
		}
		else if (value instanceof Raum) {
			return true;
		}
		else if (value instanceof Studiengang && !unterMenuLVzuSG && !unterMenuSVzuSG) {
			return true;
		} 
		else {
			return false;
		}

	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Belegungen
	 * 
	 * @param	bF - Referenz auf eine BelegungForm-Instanz  
	 */
	public void setBelegungForm(BelegungForm bF) {
		this.bF = bF;
	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Dozenten
	 * 
	 * @param	dF - Referenz auf eine DozentForm-Instanz  
	 */
	public void setDozentForm(DozentForm dF) {
		this.dF = dF;
	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Lehrveransdtaltungen
	 * 
	 * @param	lF - Referenz auf eine LehrveranstaltungForm-Instanz  
	 */
	public void setLehrveranstaltungForm(LehrveranstaltungForm lF) {
		this.lF = lF;
	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Studiengängen
	 * 
	 * @param	sgF - Referenz auf eine StudiengangForm-Instanz  
	 */
	public void setStudiengangForm(StudiengangForm sgF) {
		this.sgF = sgF;
	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Semesterverbänden
	 * 
	 * @param	svF - Referenz auf eine SemesterverbandForm-Instanz  
	 */
	public void setSemesterverbandForm(SemesterverbandForm svF) {
		this.svF = svF;
	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Räumen
	 * 
	 * @param	rF - Referenz auf eine RaumForm-Instanz  
	 */
	public void setRaumForm(RaumForm rF) {
		this.rF = rF;
	}

	/**
	 * Setzen der Referenz auf einen Wurzelknoten, in diesem
	 * Fall von einem CellTree
	 * 
	 * @param	tn - Referenz auf eine TreeNode-Instanz  
	 */
	public void setRootNode(TreeNode tn) {
		this.rootNode = tn;
	}

	/**
	 * Setzen der Referenz auf einen CellTree
	 * 
	 * @param	ct - Referenz auf eine CellTree-Instanz  
	 */
	public void setCellTree(CellTree ct) {
		this.cellTree = ct;
	}

	/**
	 * Methode, welche bei Öffnen des Knoten "Lehrveranstaltungen" die Knoten
	 * "Semesterverbände" und "Studiengänge" schließt (auch wenn diese bereits
	 * geschlossen sein sollten). Bei Öffnen des Knoten "Semesterverbände" werden
	 * die Knoten "Lehrveranstaltungen" und "Studiengänge" geschlossen (auch wenn 
	 * diese bereits geschlossen sein sollten) und bei Öffnen des Knoten
	 * "Studiengänge" werden die Knoten "Lehrveranstaltungen" und "Semesterverbände" 
	 * geschlossen (auch wenn diese bereits geschlossen sein sollten).
	 * 
	 * Dies wird durch Hinzufügen und definieren eines OpenHandlers für den CellTree 
	 * erreicht. Bei einem eingetrenen OpenEvent wird mittels "if-Zweige" durch
	 * "herantasten" und Vergleichen (".equals") ermittelt, welcher Knoten das Event
	 * ausgelöst hat. Ist dies geschehen, werden die anderen beiden relevanten Knoten
	 * geschlossen. Ein "Herantasten" ist hier unumgänglich, da es nur mittels der
	 * Methode ".setChildOpen(int, boolean)" möglich ist, einen Kindknoten zurück
	 * zu erhalten. Dabei erzwingt die genannte Methode, dass man für den Elternknoten
	 * bestimmt, ob dieser geöffnet oder geschlossen sein soll. Da man dies zur 
	 * Entwicklungszeit jedoch nur bedingt voraussehen kann, wird hier mittels
	 * geschachtelter if-Verzweigungen und der Methode ".isChildOpen(int)", welche
	 * ein "true" für offen zurückgibt, an die Ebene des Event auslösenden Knotens
	 * "herangetastet".
	 */
	public void addOpenHandler() {
		cellTree.addOpenHandler(new OpenHandler<TreeNode>() {
			public void onOpen(OpenEvent<TreeNode> event) {

				if (rootNode.isChildOpen(0)) {
					if (rootNode.setChildOpen(0, true).isChildOpen(1)) {
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(0)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, true).equals(event.getTarget())) {
							unterMenuLVzuSG = true;
							unterMenuSVzuSG = false;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, false);
						}
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(4)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, true).equals(event.getTarget())) {
							unterMenuLVzuSG = false;
							unterMenuSVzuSG = true;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, false);
						}
						if (rootNode.setChildOpen(0, true).setChildOpen(1, true).isChildOpen(5)	&& 
								rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(5, true).equals(event.getTarget())) {
							unterMenuLVzuSG = false;
							unterMenuSVzuSG = false;

							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(0, false);
							rootNode.setChildOpen(0, true).setChildOpen(1, true).setChildOpen(4, false);
						}
					}
				}

			}
		});
	}

	/**
	 * Setzen der Referenz auf die Entry-Point-Klasse
	 * 
	 * @param	spt2 - Referenz auf eine Stundenplantool2-Klasse  
	 */
	public void setStundenplantool2(Clicker spt2) {
		this.spt2 = spt2;
	}

	/**
	 * Zurückgeben einer Referenz auf eine Entry-Point-Klasse
	 */
	public Clicker getStundenplantool2() {
		return this.spt2;
	}

}
