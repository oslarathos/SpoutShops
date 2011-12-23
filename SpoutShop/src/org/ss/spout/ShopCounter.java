
package org.ss.spout;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.design.GenericCuboidBlockDesign;
import org.getspout.spoutapi.block.design.Texture;
import org.getspout.spoutapi.material.block.GenericCuboidCustomBlock;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPlugin;

public class ShopCounter
		extends GenericCuboidCustomBlock {

	public static final GenericCuboidBlockDesign design;

	static {
		Plugin plugin = SpoutShopPlugin.getInstance();

		// Creating the texture.
		Texture tex = new Texture( plugin, "http://www.langricr.ca/files/shop_3D.png", 48, 16, 12 );

		// Creating the design.
		design = new GenericCuboidBlockDesign( plugin, tex, new int[] { 0, 1, 1, 1, 1, 2 }, 0, 0, 0, 1, 1, 1 );
		design.setBrightness( 0.20f );
		design.setMaxBrightness( 0.40f );
	}

	public ShopCounter( Plugin plugin ) {
		super( plugin, "Shop Counter", false, design );

		setLightLevel( 0 );
		setItemDrop( new ItemStack( Material.BOOK, 1 ) );
	}

	@Override
	public void onBlockDestroyed( World world, int x, int y, int z ) {
	}

	@Override
	public void onBlockDestroyed( World world, int x, int y, int z, LivingEntity living ) {
	}

	@Override
	public boolean onBlockInteract( World world, int x, int y, int z, SpoutPlayer player ) {
		return false;
	}
}
