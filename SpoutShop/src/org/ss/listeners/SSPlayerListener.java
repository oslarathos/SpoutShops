
package org.ss.listeners;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.gui.ShopBuyPopup;
import org.ss.gui.ShopManagerPopup;
import org.ss.shop.Shop;
import org.ss.shop.ShopManager;

public class SSPlayerListener
		extends PlayerListener {
	public void onPlayerInteract( PlayerInteractEvent event ) {
		if ( event.getAction() != Action.RIGHT_CLICK_BLOCK )
			return;

		Material material = event.getClickedBlock().getType();

		if ( material != Material.WALL_SIGN && material != Material.SIGN_POST )
			return;

		SpoutPlayer player = SpoutManager.getPlayer( event.getPlayer() );
		Shop shop = ShopManager.getShop( event.getClickedBlock() );

		if ( shop == null )
			return;

		if ( !player.hasPermission( "spoutshops.interact" ) ) {
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
