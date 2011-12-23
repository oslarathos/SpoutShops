
package org.ss.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.ss.SpoutShopPlugin;
import org.ss.serial.Coordinate;
import org.ss.shop.Shop;
import org.ss.shop.ShopEntry;

public class SSBlockListener
		extends BlockListener {
	private static SSBlockListener instance = new SSBlockListener();

	public static SSBlockListener getInstance() {
		return instance;
	}

	public static void registerEvents( SpoutShopPlugin plugin ) {
		PluginManager manager = plugin.getServer().getPluginManager();

		manager.registerEvent( Type.SIGN_CHANGE, instance, Priority.Normal, plugin );
		manager.registerEvent( Type.BLOCK_BREAK, instance, Priority.Normal, plugin );
	}

	private HashMap< Coordinate, Shop > block_based_shops = new HashMap< Coordinate, Shop >();
	private File folder = new File( SpoutShopPlugin.getInstance().getDataFolder(), "block_shops" );

	private SSBlockListener() {
		if ( !folder.exists() )
			folder.mkdir();
	}

	public void reloadAllShops() {
		block_based_shops.clear();

		for ( File file : folder.listFiles() ) {
			try {
				FileInputStream fis = new FileInputStream( file );
				ObjectInputStream ois = new ObjectInputStream( fis );

				Shop shop = ( Shop ) ois.readObject();
				Coordinate coord = ( Coordinate ) ois.readObject();

				ois.close();
				fis.close();

				block_based_shops.put( coord, shop );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public void saveAllShops() {
		int count = 0;
		for ( Coordinate coord : block_based_shops.keySet() ) {
			Shop shop = block_based_shops.get( coord );

			try {
				File file = new File( folder, shop.shop_uuid.toString() );

				if ( !file.exists() || file.delete() )
					file.createNewFile();

				FileOutputStream fos = new FileOutputStream( file );
				ObjectOutputStream oos = new ObjectOutputStream( fos );

				oos.writeObject( shop );
				oos.writeObject( coord );

				oos.flush();
				oos.close();

				fos.flush();
				fos.close();

				count++;
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}

		SpoutShopPlugin.log( Level.INFO, "Saved " + count + " of " + block_based_shops.size() + " block-based shops." );
	}

	public Shop getShop( Block block ) {
		return block_based_shops.get( block );
	}

	public Shop getShop( Location location ) {
		return block_based_shops.get( location );
	}

	public Shop getShop( Coordinate coord ) {
		return block_based_shops.get( coord );
	}

	public void onBlockBreak( BlockBreakEvent event ) {
		Shop shop = block_based_shops.get( event.getBlock() );

		if ( shop == null )
			return;

		Player player = event.getPlayer();

		if ( !player.hasPermission( "spoutshops.delete" ) || !player.hasPermission( "spoutshops.admin" ) ) {
			player.sendMessage( "You do not have permission to destroy shops." );
			event.setCancelled( true );
			return;
		}

		if ( !shop.isManager( player ) && !player.hasPermission( "spoutshops.admin" ) ) {
			player.sendMessage( "This is not your shop to destroy." );
			event.setCancelled( true );
			return;
		}

		// Deleting the shop's save file.
		File shop_file = new File( folder, shop.shop_uuid.toString() );
		shop_file.delete();

		// Removing the shop from the roster.
		block_based_shops.remove( event.getBlock() );

		// Dropping the shop's inventory.
		for ( ShopEntry entry : shop.shop_entries ) {
			if ( entry.hasInfiniteStock() )
				continue;

			ItemStack stack = entry.createItemStack();
			stack.setAmount( entry.units_in_stock );

			event.getBlock().getWorld().dropItem( event.getBlock().getLocation().add( 0.5, 0.5, 0.5 ), stack );
		}

		// Granting the funds to the shop destroyer.
		if ( !shop.hasInfiniteWealth() ) {
			Economy econ = SpoutShopPlugin.getInstance().getVaultEconomy();
			econ.depositPlayer( player.getName(), shop.shop_vault );
			player.sendMessage( ChatColor.GREEN + "[SpoutShops] " + ChatColor.WHITE + "$" + shop.shop_vault
					+ " has been desposited into your economy account" );
		}
	}

	public void onSignChange( SignChangeEvent event ) {
		String[] lines = event.getLines();

		if ( !lines[ 0 ].equalsIgnoreCase( "[SPOUTSHOP]" ) )
			return;

		Player player = event.getPlayer();

		if ( !player.hasPermission( "spoutshops.create.sign" ) ) {
			player.sendMessage( "You do not have permission to create a shop." );
			event.setCancelled( true );
			return;
		}

		Coordinate coord = new Coordinate( event.getBlock() );
		Shop shop = new Shop( event.getPlayer().getName() );
		block_based_shops.put( coord, shop );
		System.out.println( coord.toString() );
		player.sendMessage( "Your shop has been created, right click the sign to access it." );
	}
}
