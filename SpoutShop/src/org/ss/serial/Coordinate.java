
package org.ss.serial;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Coordinate
		implements Serializable {
	private static final long serialVersionUID = 1L;

	public final UUID world_uuid;
	public final int x;
	public final int y;
	public final int z;

	public Coordinate( Location location ) {
		this( location.getBlock() );
	}

	public Coordinate( Block block ) {
		this( block.getX(), block.getY(), block.getZ(), block.getWorld().getUID() );
	}

	public Coordinate( int x, int y, int z, UUID world_uuid ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world_uuid = world_uuid;
	}

	public int hashCode() {
		int hash = 3;

		hash = 97 * hash + x;
		hash = 97 * hash + y;
		hash = 97 * hash + z;
		hash = 97 * hash + world_uuid.hashCode();

		return hash;
	}

	public boolean equals( Object o ) {
		if ( o == null )
			return false;

		if ( o == this )
			return true;

		if ( o instanceof Block || o instanceof Location ) {
			Block block = o instanceof Block ? ( Block ) o : ( ( Location ) o ).getBlock();

			if ( x != block.getX() )
				return false;
			if ( y != block.getY() )
				return false;
			if ( z != block.getZ() )
				return false;
			if ( !world_uuid.equals( block.getWorld().getUID() ) )
				return false;

			return true;
		}

		if ( o instanceof Coordinate ) {
			return hashCode() == o.hashCode();
		}

		return false;
	}

	public String toString() {
		return "Coordinate[x:" + x + ",z:" + y + ",z:" + z + "]";
	}
}
