
package org.ss.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.ss.SpoutShopPermissions;
import org.ss.SpoutShopPlugin;
import org.ss.serial.Coordinate;
import org.ss.shop.Shop;
import org.ss.shop.ShopEntry;
import org.ss.spout.ShopCounter;

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
		manager.registerEvent( Type.BLOCK_PLACE, instance, Priority.Normal, plugin );
	}

	private HashMap< Coordinate, Shop > shop_coord_roster = new HashMap< Coordinate, Shop >();
	private HashMap< UUID, Shop > shop_uuid_roster = new HashMap< UUID, Shop >();

	private File folder = new File( SpoutShopPlugin.getInstance().getDataFolder(), "block_shops" );

	private SSBlockListener() {
		if ( !folder.exists() )
			folder.mkdir();
	}

	public void reloadAllShops() throws Exception {
		shop_coord_roster.clear();

		for ( File file : folder.listFiles() ) {
			if ( file.getName().equalsIgnoreCase( "shop.index" ) )
				continue;

			FileInputStream fis = new FileInputStream( file );
			ObjectInputStream ois = new ObjectInputStream( fis );

			Shop shop = ( Shop ) ois.readObject();

			ois.close();
			fis.close();

			shop_uuid_roster.put( shop.shop_uuid, shop );
		}

		File file = new File( folder, "shop.index" );

		if ( !file.exists() )
			return;

		FileInputStream fis = new FileInputStream( file );
		ObjectInputStream ois = new ObjectInputStream( fis );

		Integer index_size = ( Integer ) ois.readObject();
		for ( int index = 0; index < index_size; index++ ) {
			Coordinate coord = ( Coordinate ) ois.readObject();
			UUID shop_uuid = ( UUID ) ois.readObject();

			if ( !shop_uuid_roster.containsKey( shop_uuid ) )
				throw new Exception( "UUID in shop index does not match a shop." );

			shop_coord_roster.put( coord, shop_uuid_roster.get( shop_uuid ) );
		}

		SpoutShopPlugin.log( Level.INFO,
				"Loaded " + shop_uuid_roster.size() + " shops across " + shop_coord_roster.size() + " blocks." );
	}

	public void saveAllShops() throws IOException {
		ArrayList< Shop > saved_shops = new ArrayList< Shop >();

		File index = new File( folder, "shop.index" );

		if ( !index.exists() || index.delete() )
			index.createNewFile();

		FileOutputStream index_fos = new FileOutputStream( index );
		ObjectOutputStream index_oos = new ObjectOutputStream( index_fos );

		index_oos.writeObject( shop_coord_roster.size() );

		int count = 0;
		for ( Coordinate coord : shop_coord_roster.keySet() ) {
			index_oos.writeObject( coord );

			Shop shop = shop_coord_roster.get( coord );
			index_oos.writeObject( shop.shop_uuid );

			if ( saved_shops.contains( shop ) )
				continue;
			else
				saved_shops.add( shop );

			File file = new File( folder, shop.shop_uuid.toString() );

			if ( !file.exists() || file.delete() )
				file.createNewFile();

			FileOutputStream fos = new FileOutputStream( file );
			ObjectOutputStream oos = new ObjectOutputStream( fos );

			oos.writeObject( shop );

			oos.flush();
			oos.close();

			fos.flush();
			fos.close();

			count++;
		}

		index_oos.flush();
		index_oos.close();

		index_fos.flush();
		index_fos.close();

		SpoutShopPlugin.log( Level.INFO, "Saved " + count + " shop(s) in " + shop_coord_roster.size() + " blocks." );
	}

	public Shop getShop( Coordinate coord ) {
		return shop_coord_roster.get( coord );
	}

	public void onBlockBreak( BlockBreakEvent event ) {
		Coordinate coord = new Coordinate( event.getBlock() );

		Shop shop = shop_coord_roster.get( coord );

		if ( shop == null )
			return;

		Player player = event.getPlayer();

		if ( !SpoutShopPermissions.checkPermission( player, SpoutShopPermissions.DESTROY, SpoutShopPermissions.ADMIN ) ) {
			player.sendMessage( "You do not have permission to destroy shops." );
			event.setCancelled( true );
		}

		if ( !shop.isManager( player ) && !SpoutShopPermissions.ADMIN.hasNode( player ) ) {
			player.sendMessage( "This is not your shop to destroy." );
			event.setCancelled( true );
			return;
		}

		// Removing the shop from the roster.
		shop_coord_roster.remove( coord );

		if ( !shop_coord_roster.containsValue( shop ) ) {
			shop_uuid_roster.remove( shop.shop_uuid );

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
				if ( shop.shop_vault > 0 ) {
					Economy econ = SpoutShopPlugin.getInstance().getVaultEconomy();

					econ.depositPlayer( player.getName(), shop.shop_vault );
					player.sendMessage( ChatColor.GREEN + "[SpoutShops] " + ChatColor.WHITE + "$" + shop.shop_vault
							+ " deposited." );
				}
			}

			// Deleting the shop's save file.
			File shop_file = new File( folder, shop.shop_uuid.toString() );
			shop_file.delete();

		}
	}

	public void onBlockPlace( BlockPlaceEvent event ) {
		if ( event.getBlock() instanceof SpoutBlock ) {
			SpoutBlock block = ( SpoutBlock ) event.getBlock();

			if ( block.isCustomBlock() ) {
				if ( block.getCustomBlock() instanceof ShopCounter ) {
					Player player = event.getPlayer();
					ArrayList< Shop > nearby_shops = new ArrayList< Shop >();

					for ( int x = -1; x <= 1; x++ ) {
						for ( int z = -1; z <= 1; z++ ) {
							for ( int y = -1; y <= 1; y++ ) {
								if ( x == 0 && y == 0 && z == 0 )
									continue;

								Block relative = block.getRelative( x, y, z );

								SpoutBlock sb = ( SpoutBlock ) relative;

								if ( !sb.isCustomBlock() )
									continue;
								else {
									if ( !( sb.getCustomBlock() instanceof ShopCounter ) )
										continue;
								}

								Coordinate coord = new Coordinate( relative );

								if ( shop_coord_roster.containsKey( coord ) ) {
									Shop shop = shop_coord_roster.get( coord );

									if ( !nearby_shops.contains( shop ) )
										nearby_shops.add( shop );
								}
							}
						}
					}

					if ( nearby_shops.size() > 1 ) {
						player.sendMessage( ChatColor.GREEN + "[SpoutShops] " + ChatColor.WHITE
								+ "Conflicting shops surrounding this block." );
						event.setCancelled( true );
						return;
					}

					Shop shop;

					if ( nearby_shops.size() == 1 ) {
						shop = nearby_shops.get( 0 );

						if ( !shop.isManager( player ) ) {
							player.sendMessage( "You are not an owner and cannot extend that shop." );
							event.setCancelled( true );
							return;
						}
					} else {
						if ( !SpoutShopPermissions.CREATE.hasNode( player ) ) {
							player.sendMessage( "You do not have permission to create a shop." );
							event.setCancelled( true );
							return;
						}

						shop = new Shop( event.getPlayer().getName() );
					}

					Coordinate coord = new Coordinate( event.getBlock() );
					shop_coord_roster.put( coord, shop );
				}
			}
		}
	}

	public void onSignChange( SignChangeEvent event ) {
		String[] lines = event.getLines();

		if ( !lines[ 0 ].equalsIgnoreCase( "[SPOUTSHOP]" ) )
			return;

		Player player = event.getPlayer();

		if ( !SpoutShopPermissions.CREATE.hasNode( player ) ) {
			player.sendMessage( "You do not have permission to create a shop." );
			event.setCancelled( true );
			return;
		}

		Coordinate coord = new Coordinate( event.getBlock() );
		Shop shop = new Shop( event.getPlayer().getName() );
		shop_coord_roster.put( coord, shop );
		System.out.println( coord.toString() );
		player.sendMessage( "Your shop has been created, right click the sign to access it." );
	}
}
