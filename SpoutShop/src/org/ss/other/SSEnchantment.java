
package org.ss.other;

import java.util.HashMap;

public enum SSEnchantment {
	PROTECTION( 0, "Protection" ),
	PROTECTION_FIRE( 1, "Fire Protection" ),
	PROTECTION_FALL( 2, "Fall Protection" ),
	PROTECTION_EXPLOSION( 3, "Explosion Protection" ),
	PROTECTION_PROJECTILE( 4, "Projectile Protection" ),
	PROTECTION_DROWN( 5, "Resist Drowning" ),
	AQUA_AFFINITY( 6, "Aqua Affinity" ),
	SHARPNESS( 16, "Increased Damage" ),
	ANTI_UNDEAD( 17, "Undead's Bane" ),
	ANTI_SPIDER( 18, "Spider's Bane" ),
	KNOCKBACK( 19, "Knockback" ),
	IGNITION( 20, "Ignition" ),
	LOOTING( 21, "Looting" ),
	EFFICIENCY( 32, "Efficiency" ),
	SILK_TOUCH( 33, "Silk Touch" ),
	DURABILITY( 34, "Durability" ),
	FORTUNE( 35, "Fortune" );

	private static HashMap< Integer, SSEnchantment > lookup = new HashMap< Integer, SSEnchantment >();

	static {
		for ( SSEnchantment enchant : values() )
			lookup.put( enchant.eid, enchant );
	}

	public static SSEnchantment lookup( Integer eid ) {
		return lookup.get( eid );
	}

	public final int eid;
	public final String display;

	private SSEnchantment( int eid, String display ) {
		this.eid = eid;
		this.display = display;
	}
}
