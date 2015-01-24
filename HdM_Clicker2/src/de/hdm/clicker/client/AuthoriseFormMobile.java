package de.hdm.clicker.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.googlecode.mgwt.dom.client.event.tap.TapEvent;
import com.googlecode.mgwt.dom.client.event.tap.TapHandler;
import com.googlecode.mgwt.ui.client.widget.button.Button;
import com.googlecode.mgwt.ui.client.widget.dialog.AlertDialog;
import com.googlecode.mgwt.ui.client.widget.input.MPasswordTextBox;
import com.googlecode.mgwt.ui.client.widget.input.MTextBox;
import com.googlecode.mgwt.ui.client.widget.panel.flex.FlexPanel;

import de.hdm.clicker.shared.VerwaltungAsync;

public class AuthoriseFormMobile extends FlexPanel {
	
	/**
	 * Referenz auf das Proxy-Objekt um mit dem Server kommunizieren zu können
	 */
	VerwaltungAsync verwaltung = null;
	
	/**
	 * Referenz auf die Clicker-Start-Seite
	 */
	Clicker clicker = null;
	
	MTextBox kuerzelLBL = null;
	MTextBox kuerzelTB = null;
	MTextBox pwLBL = null;
	MPasswordTextBox pwTB = null;
	Button ldapButton = null;
	
	AlertDialog ad = null;
	
	public AuthoriseFormMobile (VerwaltungAsync verwaltungA) {
		
		this.verwaltung = verwaltungA;
		
		kuerzelLBL = new MTextBox();
		kuerzelLBL.setText("HdM-Kürzel:");
		kuerzelLBL.setEnabled(false);
		kuerzelTB = new MTextBox();
		pwLBL = new MTextBox();
		pwLBL.setText("Password:");
		pwLBL.setEnabled(false);
		pwTB =  new MPasswordTextBox();
		ldapButton = new Button("Anmelden");
		
		this.add(kuerzelLBL);
		this.add(kuerzelTB);
		this.add(pwLBL);
		this.add(pwTB);
		this.add(ldapButton);
		
		
		ldapButton.addTapHandler(new TapHandler() {

			
			
			@Override
			public void onTap(TapEvent event) {
				
				ad = new AlertDialog("Meldung:", "bla");
				ad.addTapHandler(new TapHandler() {

					@Override
					public void onTap(TapEvent event) {
						ad.hide();
						
					}
					
				});
				//ad.show();
				//LDAP-Authorisierung
				
				verwaltung.signInParticipant("lz006", new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AlertDialog ad = new AlertDialog("Meldung:", caught.getMessage());
						ad.show();
						
					}

					@Override
					public void onSuccess(Void result) {
						clicker.setChooseQuizFormMobileToMain();
						
					}
					
				});
				
				
			}
			
		});
		
		
	}
	
	public void setClicker(Clicker clicker) {
		this.clicker = clicker;
	}
}
