
package org.ss.extend;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomItem;

import dev.mCraft.RealMoney.Main;

public enum MinecraftCurrencyCoins {
	Copper_Coin( 0.1, Main.instance.copperCoin ),
	HalfBronze_Coin( 0.5, Main.instance.halfbronzeCoin ),
	Bronze_Coin( 1.0, Main.instance.bronzeCoin ),
	HalfSilver_Coin( 5.0, Main.instance.halfsilverCoin ),
	Silver_Coin( 10.0, Main.instance.silverCoin ),
	HalfGold_Coin( 50.0, Main.instance.halfgoldCoin ),
	Gold_Coin( 100.0, Main.instance.goldCoin ),
	HalfPlatinum_Coin( 500.0, Main.instance.halfplatinumCoin ),
	Platinum_Coin( 1000.0, Main.instance.platinumCoin );

	private static final HashMap< String, MinecraftCurrencyCoins > name_map = new HashMap< String, MinecraftCurrencyCoins >();

	static {
		for ( MinecraftCurrencyCoins coin : values() ) {
			name_map.put( coin.item.getName(), coin );
		}
	}

	public static final MinecraftCurrencyCoins lookupCoinName( String name ) {
		return name_map.get( name );
	}

	public static double has( Player player ) {
		double total = 0.0;

		for ( ItemStack s : player.getInventory().getContents() ) {
			if ( s == null || s.getAmount() == 0 )
				continue;

			SpoutItemStack stack = new SpoutItemStack( s );

			if ( !stack.isCustomItem() )
				continue;

			MinecraftCurrencyCoins coin = lookupCoinName( stack.getMaterial().getName() );

			if ( coin == null )
				continue;

			total += ( coin.value * stack.getAmount() );
		}

		return total;
	}

	public static void givePlayer( Player player, double amount ) {
		double still_to_add = amount;

		for ( int index = values().length - 1; index >= 0; index-- ) {
			MinecraftCurrencyCoins coin = values()[ index ];

			int to_give = ( int ) ( still_to_add / coin.value );
			if ( to_give == 0 )
				continue;

			still_to_add -= to_give * coin.value;

			SpoutItemStack stack = new SpoutItemStack( coin.item );
			stack.setAmount( to_give );

			player.getInventory().addItem( stack );
		}
	}

	public static boolean takePlayer( Player player, double amount ) {
		if ( has( player ) < amount )
			return false;

		ArrayList< SpoutItemStack > to_take = new ArrayList< SpoutItemStack >();
		double to_return = 0.0;

		for ( ItemStack s : player.getInventory().getContents() ) {
			if ( s == null || s.getAmount() == 0 )
				continue;

			SpoutItemStack stack = new SpoutItemStack( s );

			if ( !stack.isCustomItem() )
				continue;

			MinecraftCurrencyCoins coin = lookupCoinName( stack.getMaterial().getName() );

			if ( coin == null )
				continue;

			if ( coin.value * stack.getAmount() > amount ) {
				to_take.add( stack );

				to_return = Math.abs( amount - ( coin.value * stack.getAmount() ) );
				amount = 0;
				break;
			} else {
				to_take.add( stack );
				amount -= ( coin.value * stack.getAmount() );
			}
		}

		if ( amount == 0 && to_take.size() != 0 ) {
			SpoutItemStack[] take_array = new SpoutItemStack[ to_take.size() ];
			to_take.toArray( take_array );
			player.getInventory().removeItem( take_array );
		}

		if ( to_return != 0.0 )
			givePlayer( player, to_return );

		return amount == 0;
	}

	public final double value;
	public final CustomItem item;

	private MinecraftCurrencyCoins( double value, CustomItem item ) {
		this.value = value;
		this.item = item;
	}
}
