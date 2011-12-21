
package org.ss.gui;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.shop.Shop;

public class ShopManagerPopup
		extends ShopPopup {

	private GenericButton btn_lookup = new GenericButton( "Lookup Items" );
	private GenericButton btn_add = new GenericButton( "Add Items" );
	private GenericButton btn_funds = new GenericButton( "Access Funds" );
	private GenericButton btn_owners = new GenericButton( "Manage Owners" );

	public ShopManagerPopup( SpoutPlayer player, Shop shop ) {
		super( "Shop: Manager", player, shop );

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		btn_menu_manager.setEnabled( false );

		btn_lookup.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_lookup.setX( SCREEN_WIDTH / 4 );
		btn_lookup.setY( 10 );
		btn_lookup.setWidth( SCREEN_WIDTH / 2 );
		btn_lookup.setHeight( 20 );

		btn_add.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_add.setX( SCREEN_WIDTH / 4 );
		btn_add.setY( 40 );
		btn_add.setWidth( SCREEN_WIDTH / 2 );
		btn_add.setHeight( 20 );

		btn_funds.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_funds.setX( SCREEN_WIDTH / 4 );
		btn_funds.setY( 70 );
		btn_funds.setWidth( SCREEN_WIDTH / 2 );
		btn_funds.setHeight( 20 );

		btn_owners.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_owners.setX( SCREEN_WIDTH / 4 );
		btn_owners.setY( 100 );
		btn_owners.setWidth( SCREEN_WIDTH / 2 );
		btn_owners.setHeight( 20 );

		attachWidgets( btn_lookup, btn_add, btn_funds, btn_owners );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		if ( button.equals( btn_lookup ) ) {
			new ShopLookupItemPopup( player, shop ).show();
			return;
		}

		if ( button.equals( btn_add ) ) {
			new ShopAddItemPopup( player, shop ).show();
			return;
		}

		if ( button.equals( btn_funds ) ) {
			new ShopVaultPopup( player, shop ).show();
			return;
		}

		if ( button.equals( btn_owners ) ) {
			new ShopManageOwnersPopup( player, shop ).show();
			return;
		}

		super.onButtonClick( bce );
	}
}
