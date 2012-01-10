
package org.ss.other;

import java.text.DecimalFormat;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SSPotion {
	public enum SSPotionEffect {
		Regeneration( 1, 0.45 ),
		Speed( 2, 3.0 ),
		Fire_Resistance( 3, 3.0 ),
		Poison( 4, 0.45 ),
		Instant_Health( 5, 0.0 ),
		Weakness( 8, 1.30 ),
		Strength( 9, 3 ),
		Slowness( 10, 1.30 ),
		Instant_Damage( 12, 0.0 );

		public static final HashMap< Integer, SSPotionEffect > lookup_map = new HashMap< Integer, SSPotion.SSPotionEffect >();

		public static SSPotionEffect lookup( int id ) {
			return lookup_map.get( id );
		}

		static {
			for ( SSPotionEffect potion : SSPotionEffect.values() ) {
				lookup_map.put( potion.potion_id, potion );
			}
		}

		public final int potion_id;
		public final double duration;

		private SSPotionEffect( int potion_id, double duration ) {
			this.potion_id = potion_id;
			this.duration = duration;
		}
	}

	public static final DecimalFormat duration_format = new DecimalFormat( "#.##" );

	public static final int POTIONS_TIER_BIT = 5;
	public static final int POTIONS_SPLASH_BIT = 14;
	public static final int POTIONS_DURATION_BIT = 6;

	public static String format( ItemStack potion_stack ) {
		if ( potion_stack.getType() != Material.POTION )
			return null;

		byte data = ( byte ) potion_stack.getDurability();

		// Determining potion effect
		SSPotionEffect effect = SSPotionEffect.lookup( SSUtils.subsetByte( data, 0, 3 ) );

		if ( effect == null ) {
			switch ( data ) {
				case 0:
					return "Water Bottle";
				case 16:
					return "Awkward Potion";
				case 32:
					return "Thick Potion";
				case 64:
					return "Mundane Potion";
			}

			return "Unknown Potion";
		}

		StringBuilder builder = new StringBuilder();

		if ( SSUtils.isBitSet( data, POTIONS_SPLASH_BIT ) )
			builder.append( "Splash " );

		builder.append( "Potion of " + effect.name().replaceAll( "_", " " ) );

		if ( SSUtils.isBitSet( data, POTIONS_TIER_BIT ) )
			builder.append( " II" );
		else
			builder.append( " I" );

		if ( effect.duration != 0.0 ) {
			double duration = effect.duration;

			if ( SSUtils.isBitSet( data, POTIONS_TIER_BIT ) )
				duration *= 0.5;
			else if ( SSUtils.isBitSet( data, POTIONS_DURATION_BIT ) )
				duration *= ( 8 / 3 );

			builder.append( "\nDuration: " + Double.parseDouble( duration_format.format( duration ) ) + " seconds" );
		}

		return builder.toString();
	}
}
