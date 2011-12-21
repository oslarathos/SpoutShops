
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

}
