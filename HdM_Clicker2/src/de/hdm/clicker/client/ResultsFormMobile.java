package de.hdm.clicker.client;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;

public class ResultsFormMobile extends FlexPanel {
	
	Clicker clicker = null;

	public ResultsFormMobile(int wrongs, int corrects) {
		this.add(new Label("Richtige Antworten: " + corrects));
		this.add(new Label("Falsche Antworten: " + wrongs));
		Button button = new Button("zurück zur Quizübersicht");
		button.addTapHandler(new TapHandler() {

			@Override
			public void onTap(TapEvent event) {
				clicker.setChooseQuizFormMobileToMain();
				
			}
			
		});
		this.add(button);
	}

	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
	
	
}
