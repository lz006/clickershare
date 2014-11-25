package de.hdm.clicker.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import de.hdm.clicker.shared.bo.*;

/**
 * Diese Klasse ist eine konkrete Ausprägung der abstrakten Klasse
 * AbstractCell<T>. Sie definiert den sichtbaren Inhalt einer Zelle
 * des CellTrees für einen spezifischen Kind-Element-Typ, in diesem 
 * Fall einem Kind-Element vom Typ Category {@link Category}. Eine
 * Instanz einer solchen Klasse wird vom Konstruktor der 
 * DefaultNodeInfo<T>-Klasse verlangt und wird diesem konkret in
 * der Methode "getNodeInfo(T value)" in der Klasse "LecturerTreeViewModel"
 * {@link LecturerTreeViewModel} als Argument übergeben.
 * 
 * @author Roth, Zimmermann, Zanella
 * @version 1.0
 * 
 */
public class CategoryCell extends AbstractCell<Category> {

	/**
	 * Abstrakte Methode der Klasse AbstractCell<T> welche nicht-abstrakt überschrieben
	 * werden muss. Ihr wird u.a. ein Objekt eines definierten Kind-Element-Typs als
	 * Argument übergeben und gewünschte Attributwerte dessen, werden innerhalb eines
	 * <div>-Tags gesetzt. Diese Attributwerte ergeben jenen Text, welcher in den Zellen
	 * des CellTrees schlißlich sichtbar ist.
	 * 
	 *  @param	context - Objekt, welches Kontextinformationen enthält
	 *  		lecturer - Objekt, welches den definierten Kind-Element-Typ enthält
	 *  		sb - SafeHtmlBuilder-Objekt, welches zuständig für die Erzeugung von
	 *  		anzeigbaren HTML-Tags benötigt wird
	 */
	public void render(Context context, Category value, SafeHtmlBuilder sb) {

		if (value == null) {
			return;
		}

		sb.appendHtmlConstant("<div>");
		sb.appendEscaped(value.getDescription());
		sb.appendHtmlConstant("</div>");

	}

}
