
package org.ss.gui;

import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerPopup
		extends SSPopup {
	public final SpoutPlayer player;

	public PlayerPopup( String label, SpoutPlayer player ) {
		super( label );

		this.player = player;
	}

	public final void show() {
		PopupScreen popup = player.getMainScreen().getActivePopup();
		if ( popup != null )
			popup.close();

		player.getMainScreen().attachPopupScreen( this );
	}
}
