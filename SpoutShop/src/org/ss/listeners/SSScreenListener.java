
package org.ss.listeners;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenListener;
import org.ss.SpoutShopPlugin;
import org.ss.gui.SSPopup;

public class SSScreenListener
		extends ScreenListener {

	private static SSScreenListener instance = new SSScreenListener();

	public static SSScreenListener getInstance() {
		return instance;
	}

	public static void registerEvents( SpoutShopPlugin plugin ) {
		PluginManager manager = plugin.getServer().getPluginManager();

		manager.registerEvent( Type.CUSTOM_EVENT, instance, Priority.Normal, plugin );
	}

	public void onButtonClick( ButtonClickEvent event ) {
		if ( event.getScreen() instanceof SSPopup ) {
			( ( SSPopup ) event.getScreen() ).onButtonClick( event );
		}
	}
}
