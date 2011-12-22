
package org.ss.listeners;

import org.bukkit.Material;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPlugin;
import org.ss.gui.ShopBuyPopup;
import org.ss.gui.ShopManagerPopup;
import org.ss.serial.Coordinate;
import org.ss.shop.Shop;

public class SSPlayerListener
		extends PlayerListener {
	private static SSPlayerListener instance = new SSPlayerListener();

	public static SSPlayerListener getInstance() {
		return instance;
	}

	public static void registerEvents( SpoutShopPlugin plugin ) {
		PluginManager manager = plugin.getServer().getPluginManager();

		manager.registerEvent( Type.PLAYER_INTERACT, instance, Priority.Normal, plugin );
	}

	public void onPlayerInteract( PlayerInteractEvent event ) {
		if ( event.getAction() != Action.RIGHT_CLICK_BLOCK )
			return;

		Material material = event.getClickedBlock().getType();

		if ( material != Material.WALL_SIGN && material != Material.SIGN_POST )
			return;

		SpoutPlayer player = SpoutManager.getPlayer( event.getPlayer() );

		Shop shop = SSBlockListener.getInstance().getShop( new Coordinate( event.getClickedBlock() ) );

		if ( shop == null )
			return;

		if ( !player.hasPermission( "spoutshops.interact.sign" ) ) {
			player.sendMessage( "You do not have permission to interact with this." );
			return;
		}

		if ( !player.isSpoutCraftEnabled() ) {
			player.sendMessage( "SpoutCraft must used to interact with this." );
			return;
		}

		if ( shop.isManager( player ) )
			new ShopManagerPopup( player, shop ).show();
		else
			new ShopBuyPopup( player, shop ).show();
	}
}
