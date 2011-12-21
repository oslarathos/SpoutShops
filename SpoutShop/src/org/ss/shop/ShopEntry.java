
package org.ss.shop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class ShopEntry
		implements Serializable {
	private static final long serialVersionUID = 1L;

	public Material unit_material;
	public byte unit_data;
	public HashMap< Integer, Integer > unit_enchantments = new HashMap<>();;
	public double unit_durability;

	public int units_in_stock;
	public int units_wanted;
	public double cost_to_buy_unit = -1;
	public double cost_to_sell_unit = -1;

	public ShopEntry( ItemStack stack ) {
		unit_material = stack.getType();
		unit_durability = stack.getDurability();

		MaterialData material_data = stack.getData();
		if ( material_data != null )
			unit_data = material_data.getData();

		Map< Enchantment, Integer > enchantments = stack.getEnchantments();
		for ( Enchantment encha : enchantments.keySet() ) {
			unit_enchantments.put( encha.getId(), enchantments.get( encha ) );
		}

		units_in_stock = stack.getAmount();
	}

	public ItemStack createItemStack() {
		ItemStack stack = new ItemStack( unit_material, 1 );
		stack.setData( new MaterialData( unit_material, unit_data ) );

		for ( Integer eid : unit_enchantments.keySet() ) {
			stack.addEnchantment( Enchantment.getById( eid ), unit_enchantments.get( eid ) );
		}

		return stack;
	}

	public boolean matchesString( String criteria ) {
		if ( criteria == null )
			return false;

		return unit_material.name().toUpperCase().replaceAll( "_", " " ).contains( criteria.toUpperCase() );
	}

	public boolean hasInfiniteStock() {
		return units_in_stock == -1;
	}

	public boolean hasInfiniteDemand() {
		return units_wanted == -1;
	}

	public boolean equals( Object o ) {
		if ( o == null )
			return false;

		if ( o == this )
			return true;

		if ( o instanceof ItemStack ) {
			ItemStack stack = ( ItemStack ) o;

			if ( unit_material != stack.getType() )
				return false;

			if ( unit_durability != stack.getDurability() )
				return false;

			if ( unit_data != stack.getData().getData() )
				return false;

			return true;
		}

		return false;
	}
}
