package de.hdm.clicker.client;

import java.util.Vector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.clicker.shared.*;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse stellt die fürs Quiz-Reporting notwendige
 * grafische Benutzeroberfläche bereit
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class ReportForm extends VerticalPanel {

	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;
	
	/**
	 * ListBox und Container zur Auswahl des Startdatums
	 */
	Label startdateLabel = new Label("Startdatum: ");
	ListBox startdateListBox = new ListBox();
	Vector<Integer> startdatumVectorLB = null;
	
	/**
	 * ListBox und Container zur Auswahl eines Quizzes
	 */
	Label quizLabel = new Label("Quiz (Titel, ID, Vers.): ");
	ListBox quizBox = new ListBox();
	Vector<Quiz> quizVectorLB = null;
	
	/**
	 * Button zur Pie-Chart-Anzeige
	 */
	Button piechartButton = new Button("Diagramme generieren");
	
	/**
	 * Button für CSV-Export
	 */
	Button csvButton = new Button("CSV-Export");
	
	/**
	 * Grid zur Anordnung der Widgets
	 */
	Grid auswahlGrid = null;
	
	/**
	 * FlexTable um die PieCharts anzuordnen
	 */
	FlexTable piechartFlexTable = null;
	
	/**
	 * ScrollPanel um über die PieCharts scrollen zu können
	 */
	ScrollPanel scrollPanel = null;
	
	/**
	 * PieChart-Container
	 */
	Vector<PieChart> piechartVector = null;

	/**
	 * Referenz auf des TreeViewModel um Zugriff auf Methoden dieser Klasse 
	 * zu haben {@link CustomTreeViewModel}
	 */
	LecturerTreeViewModel ltvm = null;

	
	/**
	 * Komstruktor der alle notwendigen Widgets initialisiert und anordnet,
	 * so dass das Objekt für weitere Konfigurationen bereit ist
	 * 
	 * @param	verwaltungA - Referenz auf ein Proxy-Objekt. 
	 */	
	public ReportForm(VerwaltungAsync verwaltungA) {

		this.verwaltung = verwaltungA;
		
		auswahlGrid = new Grid(3,2);
		
		auswahlGrid.setWidget(0, 0, startdateLabel);
		auswahlGrid.setWidget(0, 1, startdateListBox);
		auswahlGrid.setWidget(1, 0, quizLabel);
		auswahlGrid.setWidget(1, 1, quizBox);
		auswahlGrid.setWidget(2, 0, piechartButton);
		auswahlGrid.setWidget(2, 1, csvButton);
		
		this.add(auswahlGrid);
		
		scrollPanel = new ScrollPanel();
		piechartFlexTable = new FlexTable();
		scrollPanel.add(piechartFlexTable);
		this.add(scrollPanel);

		startdateListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ladenQuizze();
				
			}
			
		});
		
		piechartButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				piechartButton.setEnabled(false);
				generatePieCharts();
				
			}
			
		});
		
		csvButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				csvButton.setEnabled(false);
				loadCSVData();
				
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

	
	public void ladenStartdaten() {
		verwaltung.auslesenAlleQuizStartdatenByLecturer(new AsyncCallback<Vector<Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(Vector<Integer> result) {
				startdatumVectorLB = result;
				
				if (result != null && result.size() > 0) {
					for (Integer i : result) {
						String tempDate = i.toString();
						if (tempDate.length() == 8) {
							startdateListBox.addItem(tempDate.substring(6) + "." + tempDate.substring(4,6) + "." + tempDate.substring(0,4));
						}
						
					}
					ladenQuizze();
				}
				
			}
			
		});
	}
	
	public void ladenQuizze() {
		startdateListBox.setEnabled(false);
		quizBox.clear();
		
		if (startdatumVectorLB.elementAt(startdateListBox.getSelectedIndex()) != null && 
				startdatumVectorLB.elementAt(startdateListBox.getSelectedIndex()) != 0) {
			verwaltung.auslesenAlleQuizByLecturerAndStartdate(startdatumVectorLB.elementAt(startdateListBox.getSelectedIndex()), 
					new AsyncCallback<Vector<Quiz>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							startdateListBox.setEnabled(true);
							
						}

						@Override
						public void onSuccess(Vector<Quiz> result) {
							quizVectorLB = result;
							
							if (result != null && result.size() > 0) {
								for (Quiz q : result) {
									quizBox.addItem(q.getDescription() + " -- " + q.getId() + " -- " + q.getVersion());
								}
							}
							
							startdateListBox.setEnabled(true);
							
						}
				
			});
		}
	}
	
	public void generatePieCharts() {
		piechartFlexTable.removeAllRows();
		
		verwaltung.auslesenChartInfoByQuiz(quizVectorLB.elementAt(quizBox.getSelectedIndex()), new AsyncCallback<Vector<ChartInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				piechartButton.setEnabled(true);
				
			}

			@Override
			public void onSuccess(Vector<ChartInfo> result) {
				
				piechartVector = new Vector<PieChart>();
				
				if (result != null && result.size() != 0) {
					
					int row = 0;
					int col = 0;
					for (int i = 0; i < result.size(); i++) {
						piechartVector.add(new PieChart(result.elementAt(i)));
						piechartFlexTable.setWidget(row, col, piechartVector.elementAt(i));
						piechartVector.elementAt(i).update();
						col++;
						if(col >= 3) {
							col = 0;
							row++;
						}
					}
				}
				piechartButton.setEnabled(true);
			}
			
		});
	}
	
	public void loadCSVData() {
		verwaltung.auslesenCSVDataByQuiz(quizVectorLB.elementAt(quizBox.getSelectedIndex()), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				csvButton.setEnabled(true);
				
			}

			@Override
			public void onSuccess(String result) {
				createCSV(result);
				csvButton.setEnabled(true);
				
			}
			
		});
	}
	
	public static native void createCSV(String csvData) /*-{
		
		var pom = document.createElement('a');
		pom.setAttribute('href', 'data:text/csv;charset=utf-8,' + encodeURIComponent(csvData));
		pom.setAttribute('download', 'test.csv');
		document.body.appendChild(pom);
		pom.click();
		
	}-*/;

}
