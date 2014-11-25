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
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class AdminTreeViewModel implements TreeViewModel {

	/**
	 * Referenzen auf alle Instanzen der "Formklassen" um Zugriff auf deren Methoden
	 * zu haben
	 */
	private LecturerForm lF;
	
	/**
	 * Referenz auf die Entry-Point-Klasse um Zugriff auf deren Methoden zu haben
	 */
	Clicker clicker;

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	private VerwaltungAsync verwaltung = null;

	/**
	 * Container welche alle Objekte enthalten, welche im Tree als Blätter oder
	 * Knoten abgebildet werden
	 */
	private ListDataProvider<Lecturer> lecturerDataProvider;
	private ListDataProvider<String> dummyDataProvider;
	
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
			else if (object instanceof Object) {
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
	 * Konstruktor welcher den "SelectionChangeHandler" definiert und dem 
	 * "selectionModel" hinzufügt, so dass der CellTree-Funktionsbereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */
	public AdminTreeViewModel(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;

		// Initialisieren, definieren und "adden" eines "SelectionChangeHandlers"
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			public void onSelectionChange(SelectionChangeEvent event) {
				Object selection = selectionModel.getSelectedObject();

				/*
				 *  Bei Klick auf ein "Lecturer-Objekt", wird dieses erneut geladen und
				 *  mittelbar die LecturerForm in den Arbeitsbereich der GUI geladen
				 */
				if (selection instanceof Lecturer) {

					setSelectedLecturer((Lecturer)selection);
				} 
								
				/*
				 *  Bei Klick auf ein "String-Objekt" wird die entsprechende Formklasse in der
				 *  "Anlegen-Variante" in die in den Arbeitsbereich GUI geladen
				 */
				else if (selection instanceof String && (String) selection == "Lehrender") {
					lecturerAnlegenMaske();
				} 
				
			}
		});

	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Lecturer zu bearbeiten
	 * 
	 * @param	lecturer - Referenz auf ein Lecturer-Objekt, welches Gegenstand der Bearbeitung ist 
	 */
	public void setSelectedLecturer(Lecturer lecturer) {
		
		clicker.setLecturerFormToMain();
		this.lF.setAtvm(this);
		this.lF.setShownLecturer(lecturer);
		this.lF.fillForm();
		this.lF.aendernMaske();
	}

	/**
	 * Methode welche wiederum alle notwendigen Methoden aufruft, die es dem
	 * User ermöglichen einen Lecturer anzulegen
	 */
	public void lecturerAnlegenMaske() {
		clicker.setLecturerFormToMain();
		this.lF.setAtvm(this);
		this.lF.anlegenMaske();
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

			dummyDataProvider.getList().add(editor);

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

			String lecturer = "Lehrender";

			dummyDataProvider.getList().add(lecturer);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Anlegen" enthält die editierbaren BusinessObjects in String-Repräsentation als Kind-Elemente
		if (value instanceof String && (String) value == "Verwalten") {

			dummyDataProvider = new ListDataProvider<String>();

			String lecturer = "Lehrende";

			dummyDataProvider.getList().add(lecturer);

			return new DefaultNodeInfo<String>(dummyDataProvider, new DummyCell(), selectionModel, null);
		}

		// "Lehrende" enthält Lehrende-Objekte als Kind-Elemente
		if (value instanceof String && (String) value == "Lehrende") {

			lecturerDataProvider = new ListDataProvider<Lecturer>();

			verwaltung.auslesenAlleLecturer(new AsyncCallback<Vector<Lecturer>>() {
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				public void onSuccess(Vector<Lecturer> lecturers) {
					for (Lecturer lecturer : lecturers) {
						lecturerDataProvider.getList().add(lecturer);
					}
				}
			});

			return new DefaultNodeInfo<Lecturer>(lecturerDataProvider, new LecturerCell(), selectionModel, null);
		}

		return null;

	}

	/**
	 * Methode welche die List des "lecturerDataProviders" hinsichtlich eines geänderten
	 * Dozenten aktualisiert. Damit bspw. eine Namensänderung eines Lecturers auch im
	 * CellTree ersichtlich wird
	 * 
	 * @param	lecturer - Objekt, welches "seine alte Version" ersetzt  
	 */
	public void updateLecturer(Lecturer lecturer) {
		List<Lecturer> lecturerList = lecturerDataProvider.getList();
		int i = 0;
		for (Lecturer a : lecturerList) {
			if (a.getId() == lecturer.getId()) {
				lecturerList.set(i, lecturer);
				lecturerDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}
	}

	/**
	 * Methode welche die List des "lecturerDataProviders" dahingehend aktualisiert,
	 * dass ein Lecturer entfernt werden muss, da dieser aus Systemsicht nicht mehr
	 * existent ist. In Folge wird dieser auch nicht mehr im CellTree angezeigt.
	 * 
	 * @param	lecturer - Objekt, welches gelöscht werden soll  
	 */
	public void loeschenLecturer(Lecturer lecturer) {

		int i = 0;

		for (Lecturer l : lecturerDataProvider.getList()) {
			if (l.getId() == lecturer.getId()) {

				lecturerDataProvider.getList().remove(i);
				lecturerDataProvider.refresh();
				break;
			} else {
				i++;
			}
		}

	}

	/**
	 * Methode welche die List des "lecturerDataProviders" dahingehend aktualisiert,
	 * dass ein Lecturer hinzugefügt werden muss, da dieser neu angelegt wurde und
	 * folglich im CellTree angezeigt werden muss.
	 * 
	 * @param	lecturer - Objekt, welcher neu hinzugefügt wird  
	 */
	public void addLecturer(Lecturer lecturer) {
		if (lecturerDataProvider != null) {
			lecturerDataProvider.getList().add(
			lecturerDataProvider.getList().size(), lecturer);
			lecturerDataProvider.refresh();
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

		if (value instanceof String && (String) value == "Lehrender") {
			return true;
		}
		else if (value instanceof Lecturer) {
			return true;
		}
		else {
			return false;
		}

	}

	/**
	 * Setzen der Referenz auf die Benutzeroberfläche für das
	 * Anlegen bzw. Editieren von Lecturer
	 * 
	 * @param	lF - Referenz auf eine LecturerForm-Instanz  
	 */
	public void setLecturerForm(LecturerForm lF) {
		this.lF = lF;
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
	 * Setzen der Referenz auf die Entry-Point-Klasse
	 * 
	 * @param	spt2 - Referenz auf eine Stundenplantool2-Klasse  
	 */
	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}

	/**
	 * Zurückgeben einer Referenz auf eine Entry-Point-Klasse
	 */
	public Clicker getClicker() {
		return this.clicker;
	}

}
