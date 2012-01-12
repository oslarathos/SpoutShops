
package org.ss.gui;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutPlayerInventory;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.other.SSLang;
import org.ss.shop.Shop;

public class ShopAddItemPopup
		extends ShopPopup {

	private final GenericTextField txt_itemname = new GenericTextField();
	private final GenericTextField txt_amount = new GenericTextField();

	private final GenericButton btn_add = new GenericButton( SSLang.lookup( player, "btn_saip_add" ) );
	private final GenericButton btn_dump = new GenericButton( SSLang.lookup( player, "btn_saip_dump" ) );

	public ShopAddItemPopup( SpoutPlayer player, Shop shop ) {
		super( SSLang.lookup( player, "lbl_saip_title" ), player, shop );

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
		txt_itemname.setPlaceholder( SSLang.lookup( player, "txt_saip_itemname_placeholder" ) );

		txt_amount.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_amount.setX( 100 );
		txt_amount.setY( 70 );
		txt_amount.setWidth( SCREEN_WIDTH - 260 );
		txt_amount.setHeight( 20 );
		txt_amount.setPlaceholder( SSLang.lookup( player, "txt_saip_amount_placeholder" ) );

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

				SpoutPlayerInventory inventory = ( SpoutPlayerInventory ) player.getInventory();

				int count = 0;
				for ( ItemStack stack : inventory.getContents() ) {
					if ( stack == null || stack.getAmount() == 0 )
						continue;

					SpoutItemStack sis = new SpoutItemStack( stack );
					String compare_term = sis.getMaterial().getNotchianName();

					if ( compare_term.toUpperCase().replaceAll( "_", " " ).contains( search.toUpperCase() ) ) {
						if ( stack.getAmount() <= amount ) {
							inventory.removeItem( stack );
							amount -= stack.getAmount();
							shop.add( stack );
							count += stack.getAmount();
						}
					}

					if ( amount == 0 )
						break;
				}

				String msg = SSLang.lookup( player, "suc_saip_add" );
				msg = SSLang.format( msg, "amount", Integer.toString( count ) );
				setStatus( color_green, msg );
			} catch ( NumberFormatException nfe ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}
		}

		if ( button.equals( btn_dump ) ) {
			SpoutPlayerInventory inventory = ( SpoutPlayerInventory ) player.getInventory();

			if ( inventory.getSize() == 0 )
				return;

			for ( ItemStack stack : inventory.getContents() ) {
				if ( stack == null || stack.getAmount() == 0 )
					continue;

				SpoutItemStack sis = new SpoutItemStack( stack );

				inventory.remove( sis );
				shop.add( stack );
			}

			ShopManagerPopup screen = new ShopManagerPopup( player, shop );
			setStatus( color_green, SSLang.lookup( player, "suc_saip_dump" ) );
			screen.show();

			return;
		}

		super.onButtonClick( bce );
	}

}
