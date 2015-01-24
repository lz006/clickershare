package de.hdm.clicker.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResultsForm extends VerticalPanel {
	
	Clicker clicker = null;

	public ResultsForm(int wrongs, int corrects) {
		this.add(new Label("Richtige Antworten: " + corrects));
		this.add(new Label("Falsche Antworten: " + wrongs));
		Button button = new Button("zurück zur Quizübersicht");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				clicker.setChooseFormToMain();
				
			}
			
		});
		this.add(button);
	}

	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	
}
