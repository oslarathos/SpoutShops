
package org.ss.gui;

import net.milkbowl.vault.economy.Economy;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPlugin;
import org.ss.shop.Shop;

public class ShopPopup
		extends PlayerPopup {
	protected static final Economy economy = SpoutShopPlugin.getInstance().getVaultEconomy();

	protected static final Color color_green = new Color( 100, 255, 100 );
	protected static final Color color_white = new Color( 255, 255, 255 );
	protected static final Color color_red = new Color( 255, 100, 100 );

	public final Shop shop;

	protected final GenericLabel lbl_player_funds = new GenericLabel();
	protected final GenericLabel lbl_shop_funds = new GenericLabel();
	protected final GenericButton btn_menu_manager = new GenericButton( "Manager" );
	protected final GenericButton btn_menu_buy = new GenericButton( "Buy" );
	protected final GenericButton btn_menu_sell = new GenericButton( "Sell" );

	public ShopPopup( String label, SpoutPlayer player, Shop shop ) {
		super( label, player );

		this.shop = shop;

		lbl_player_funds.setDirty( true );
		lbl_player_funds.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_player_funds.setTextColor( new Color( 128, 128, 128 ) );
		lbl_player_funds.setX( 10 );
		lbl_player_funds.setY( 20 );
		lbl_player_funds.setWidth( lbl_player_funds.getText().length() * 5 );
		lbl_player_funds.setHeight( 10 );

		lbl_shop_funds.setDirty( true );
		lbl_shop_funds.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_shop_funds.setTextColor( new Color( 128, 128, 128 ) );
		lbl_shop_funds.setX( 10 );
		lbl_shop_funds.setY( 30 );
		lbl_shop_funds.setWidth( lbl_player_funds.getText().length() * 5 );
		lbl_shop_funds.setHeight( 10 );

		updateFunds();

		btn_menu_manager.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_menu_manager.setX( SCREEN_WIDTH - 90 );
		btn_menu_manager.setY( 10 );
		btn_menu_manager.setWidth( 80 );
		btn_menu_manager.setHeight( 20 );
		if ( !shop.isManager( player ) )
			btn_menu_manager.setEnabled( false );

		btn_menu_buy.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_menu_buy.setX( SCREEN_WIDTH - 90 );
		btn_menu_buy.setY( 40 );
		btn_menu_buy.setWidth( 80 );
		btn_menu_buy.setHeight( 20 );

		btn_menu_sell.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_menu_sell.setX( SCREEN_WIDTH - 90 );
		btn_menu_sell.setY( 70 );
		btn_menu_sell.setWidth( 80 );
		btn_menu_sell.setHeight( 20 );

		attachWidgets( lbl_player_funds, lbl_shop_funds, btn_menu_manager, btn_menu_buy, btn_menu_sell );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( button.equals( btn_menu_manager ) ) {
			new ShopManagerPopup( player, shop ).show();
			return;
		}

		if ( button.equals( btn_menu_buy ) ) {
			new ShopBuyPopup( player, shop ).show();
			return;
		}

		if ( button.equals( btn_menu_sell ) ) {
			new ShopSellPopup( player, shop ).show();
			return;
		}
	}

	protected void updateFunds() {
		lbl_player_funds.setText( "Funds: $" + economy.getBalance( player.getName() ) );

		if ( shop.hasInfiniteWealth() )
			lbl_shop_funds.setText( "" );
		else
			lbl_shop_funds.setText( "Shop: $" + shop.shop_vault );
	}
}
