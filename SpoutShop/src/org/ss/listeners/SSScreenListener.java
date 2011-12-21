
package org.ss.listeners;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.getspout.spoutapi.event.screen.TextFieldChangeEvent;
import org.ss.gui.SSPopup;

public class SSScreenListener
		extends ScreenListener {

	public void onButtonClick( ButtonClickEvent event ) {
		if ( event.getScreen() instanceof SSPopup ) {
			( ( SSPopup ) event.getScreen() ).onButtonClick( event );
		}
	}

	public void onTextFieldChange( TextFieldChangeEvent event ) {

	}
}
