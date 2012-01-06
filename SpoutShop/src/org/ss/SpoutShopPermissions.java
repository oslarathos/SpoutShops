
package org.ss;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public enum SpoutShopPermissions {
	ADMIN( "spoutshops.admin" ),
	DESTROY( "spoutshops.destroy" ),
	CREATE( "spoutshops.create" ),
	EXTEND( "spoutshops.extend" ),
	INTERACT( "spotshops.interact" );

	public static boolean checkPermission( Player player, SpoutShopPermissions... permissions ) {
		for ( SpoutShopPermissions permission : permissions ) {
			if ( permission.hasNode( player ) )
				return true;
		}

		return false;
	}

	public final String node;

	private SpoutShopPermissions( String node ) {
		this.node = node;
	}

	public boolean hasNode( Player player ) {
		if ( SpoutShopPlugin.getInstance().getServer().getPluginManager().isPluginEnabled( "PermissionsEx" ) )
			return PermissionsEx.getPermissionManager().has( player, node );

		return player.hasPermission( node );
	}
}
