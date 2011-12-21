
package org.ss.shop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.ss.SpoutShopPlugin;
import org.ss.serial.Coordinate;

public class ShopManager {
	private static File folder = new File( SpoutShopPlugin.getInstance().getDataFolder(), "shops" );
	private static HashMap< Coordinate, Shop > shop_roster = new HashMap<>();

	static {
		if ( !folder.exists() )
			folder.mkdir();
	}

	public static Shop createShop( Block block, String owner ) {
		Shop shop = new Shop( owner );

		shop_roster.put( new Coordinate( block ), shop );

		return shop;
	}

	public static void saveShops() {
		for ( Coordinate coord : shop_roster.keySet() ) {
			try {
				Shop shop = shop_roster.get( coord );

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
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static void reloadShops() {
		shop_roster.clear();

		for ( File file : folder.listFiles() ) {
			try {
				FileInputStream fis = new FileInputStream( file );
				ObjectInputStream ois = new ObjectInputStream( fis );

				Shop shop = ( Shop ) ois.readObject();

				Coordinate coord = ( Coordinate ) ois.readObject();

				ois.close();
				fis.close();

				shop_roster.put( coord, shop );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}

	public static Shop getShop( Block block ) {
		return getShop( new Coordinate( block ) );
	}

	public static Shop getShop( Coordinate coord ) {
		return shop_roster.get( coord );
	}

	public static void destroyShop( Block block ) {
		Shop shop = shop_roster.remove( new Coordinate( block ) );

		if ( shop == null )
			return;

		for ( ShopEntry entry : shop.shop_entries ) {
			if ( entry.hasInfiniteStock() )
				continue;

			ItemStack stack = entry.createItemStack();
			stack.setAmount( entry.units_in_stock );

			block.getWorld().dropItemNaturally( block.getLocation().add( 0.5, 0.5, 0.5 ), stack );
		}

		File file = new File( folder, shop.shop_uuid.toString() );
		if ( file.exists() )
			file.delete();
	}
}
