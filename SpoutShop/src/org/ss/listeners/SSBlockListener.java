
package org.ss.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.ss.SpoutShopPlugin;
import org.ss.shop.Shop;
import org.ss.shop.ShopManager;

public class SSBlockListener
		extends BlockListener {
	public void onBlockBreak( BlockBreakEvent event ) {
		Shop shop = ShopManager.getShop( event.getBlock() );

		if ( shop == null )
			return;

		Player player = event.getPlayer();

		if ( !player.hasPermission( "spoutshops.delete" ) || !player.hasPermission( "spoutshops.admin" ) ) {
			player.sendMessage( "You do not have permission to destroy shops." );
			return;
		}

		if ( !shop.isManager( player ) && !player.hasPermission( "spoutshops.admin" ) ) {
			player.sendMessage( "This is not your shop to destroy." );
			return;
		}

		ShopManager.destroyShop( event.getBlock() );
	}

	public void onSignChange( SignChangeEvent event ) {
		if ( SpoutShopPlugin.getInstance().getConfig().getInt( "mode" ) != SpoutShopPlugin.MODE_SIGN )
			return;

		String[] lines = event.getLines();

		if ( !lines[ 0 ].equalsIgnoreCase( "[SPOUTSHOP]" ) )
			return;

		Player player = event.getPlayer();

		if ( !player.hasPermission( "spoutshops.create" ) ) {
			player.sendMessage( "You do not have permission to create a shop." );
			return;
		}

		Shop shop = ShopManager.createShop( event.getBlock(), player.getName() );
		player.sendMessage( "Your shop has been created, right click the sign to access it." );

		for ( int i = 1; i < 3; i++ ) {
			if ( lines[ i ] != null && lines[ i ].length() != 0 )
				shop.owners.add( lines[ i ] );
		}
	}
}
