
package org.ss.other;

import org.bukkit.Material;

public class SSUtils {
	public static Material lookupMaterial( String material_name ) {
		for ( Material material : Material.values() ) {
			if ( material.name().replaceAll( "_", " " ).equalsIgnoreCase( material_name ) )
				return material;
		}

		return null;
	}

	public static String formatMaterialName( Material material ) {
		return material.name().replaceAll( "_", " " );
	}

	public static boolean isBitSet( byte value, int index ) {
		return ( value & ( 1 << index ) ) != 0;
	}

	public static byte subsetByte( byte value, int start, int end ) {
		byte subset = ( byte ) 0;

		for ( int index = start; index < end; index++ ) {
			if ( isBitSet( value, index ) )
				subset |= ( 1 << index );
		}

		return subset;
	}

}
