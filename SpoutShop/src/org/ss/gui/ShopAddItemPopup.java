
package org.ss.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.shop.Shop;

public class ShopAddItemPopup
		extends ShopPopup {

	private final GenericTextField txt_itemname = new GenericTextField();
	private final GenericTextField txt_amount = new GenericTextField();

	private final GenericButton btn_add = new GenericButton( "Add" );
	private final GenericButton btn_dump = new GenericButton( "Add All" );

	public ShopAddItemPopup( SpoutPlayer player, Shop shop ) {
		super( "Shop: Add Items", player, shop );

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		btn_dump.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_dump.setX( SCREEN_WIDTH - 90 );
		btn_dump.setY( 100 );
		btn_dump.setWidth( 80 );
		btn_dump.setHeight( 20 );

		txt_itemname.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_itemname.setX( 100 );
		txt_itemname.setY( 40 );
		txt_itemname.setWidth( SCREEN_WIDTH - 200 );
		txt_itemname.setHeight( 20 );
		txt_itemname.setPlaceholder( "Enter item name here." );

		txt_amount.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_amount.setX( 100 );
		txt_amount.setY( 70 );
		txt_amount.setWidth( SCREEN_WIDTH - 260 );
		txt_amount.setHeight( 20 );
		txt_amount.setPlaceholder( "Enter item amount here." );

		btn_add.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_add.setX( SCREEN_WIDTH - 140 );
		btn_add.setY( 70 );
		btn_add.setWidth( 40 );
		btn_add.setHeight( 20 );

		attachWidgets( btn_dump, txt_itemname, txt_amount, btn_add );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		if ( button.equals( btn_add ) ) {
			try {
				Integer amount = Integer.parseInt( txt_amount.getText() );
				String search = txt_itemname.getText();

				PlayerInventory inventory = player.getInventory();

				int count = 0;
				for ( ItemStack stack : inventory.getContents() ) {
					if ( stack == null || stack.getAmount() == 0 )
						continue;

					if ( stack.getType().name().toUpperCase().replaceAll( "_", " " ).contains( search.toUpperCase() ) ) {
						if ( stack.getAmount() <= amount ) {
							inventory.remove( stack );
							amount -= stack.getAmount();
							shop.add( stack );
							count += stack.getAmount();
						}
					}

					if ( amount == 0 )
						break;
				}

				setStatus( color_green, "A total of " + count + " items have been added." );
			} catch ( NumberFormatException nfe ) {
				setError( "Please enter only a number in the quantity field." );
			}
		}

		if ( button.equals( btn_dump ) ) {
			PlayerInventory inventory = player.getInventory();

			if ( inventory.getSize() == 0 )
				return;

			for ( ItemStack stack : inventory.getContents() ) {
				if ( stack == null || stack.getAmount() == 0 )
					continue;

				if ( stack instanceof SpoutItemStack ) {
					if ( ( ( SpoutItemStack ) stack ).isCustomItem() )
						continue;
				}

				inventory.remove( stack );
				shop.add( stack );
			}

			ShopManagerPopup screen = new ShopManagerPopup( player, shop );
			setStatus( color_green, "Your inventory has been dumped into the store." );
			screen.show();

			return;
		}

		super.onButtonClick( bce );
	}

}
