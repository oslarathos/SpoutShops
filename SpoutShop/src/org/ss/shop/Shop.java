
package org.ss.shop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.ss.SpoutShopPermissions;

public class Shop
		implements Serializable {
	private static final long serialVersionUID = 1L;

	public final UUID shop_uuid = UUID.randomUUID();
	public final ArrayList< ShopEntry > shop_entries = new ArrayList< ShopEntry >();
	public final ArrayList< String > owners = new ArrayList< String >();
	public String shop_name = "Shop";
	public String shop_motd = "A shop";
	public double shop_vault = 0;

	public void add( ItemStack stack ) {
		SpoutItemStack sis = new SpoutItemStack( stack );

		for ( ShopEntry entry : shop_entries ) {
			if ( entry.equals( stack ) ) {
				if ( !entry.hasInfiniteStock() )
					entry.units_in_stock += sis.getAmount();

				return;
			}
		}

		ShopEntry entry = new ShopEntry( sis );
		shop_entries.add( entry );
	}

	public ItemStack remove( ItemStack stack ) {
		int shop_index = shop_entries.indexOf( stack );

		if ( shop_index < 0 )
			return null;

		ShopEntry entry = shop_entries.get( shop_index );

		if ( entry.units_in_stock < stack.getAmount() ) {
			stack.setAmount( entry.units_in_stock );
			shop_entries.remove( entry );
			return stack;
		}

		entry.units_in_stock -= stack.getAmount();

		if ( entry.units_in_stock == 0 && entry.units_wanted == 0 )
			shop_entries.remove( entry );

		return stack;
	}

	public void addOwner( String name ) {
		if ( !owners.contains( name.toLowerCase() ) )
			owners.add( name.toLowerCase() );
	}

	public boolean isManager( Player player ) {
		if ( SpoutShopPermissions.ADMIN.hasNode( player ) )
			return true;

		return owners.contains( player.getName().toLowerCase() );
	}

	public boolean hasInfiniteWealth() {
		return shop_vault == -1;
	}

}
