
package org.ss.gui;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPermissions;
import org.ss.other.SSLang;
import org.ss.shop.Shop;
import org.ss.shop.ShopEntry;

public class ShopModifyItemPopup
		extends ShopPopup {

	public final ShopEntry entry;

	private GenericLabel lbl_amount = new GenericLabel();
	private GenericTextField txt_setamount;
	private GenericButton btn_setamount_update;
	private GenericTextField txt_buying = new GenericTextField();
	private GenericButton btn_buying_update = new GenericButton();
	private GenericTextField txt_store_price = new GenericTextField();
	private GenericButton btn_store_price_update = new GenericButton();
	private GenericTextField txt_purchase_price = new GenericTextField();
	private GenericButton btn_purchase_price_update = new GenericButton();
	private GenericTextField txt_remove = new GenericTextField();
	private GenericButton btn_remove = new GenericButton();
	private GenericButton btn_delete = new GenericButton();

	public ShopModifyItemPopup( SpoutPlayer player, Shop shop, ShopEntry entry ) {
		super( SSLang.lookup( player, "lbl_smip_title" ), player, shop );

		this.entry = entry;

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		btn_delete.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_delete.setX( SCREEN_WIDTH - 90 );
		btn_delete.setY( 100 );
		btn_delete.setWidth( 80 );
		btn_delete.setHeight( 20 );
		btn_delete.setText( SSLang.lookup( player, "btn_smip_delete" ) );

		GenericItemWidget display = new GenericItemWidget( entry.createItemStack() );
		display.setAnchor( WidgetAnchor.TOP_LEFT );
		display.setWidth( 10 );
		display.setHeight( 10 );
		display.setX( 100 );
		display.setY( 10 );

		lbl_amount.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_amount.setX( 100 );
		lbl_amount.setY( 40 );

		if ( SpoutShopPermissions.ADMIN.hasNode( player ) ) {
			lbl_amount.setText( SSLang.lookup( player, "lbl_smip_modstock" ) );
			lbl_amount.setTooltip( SSLang.lookup( player, "lbl_smip_setamount_tt" ) );

			txt_setamount = new GenericTextField();
			txt_setamount.setAnchor( WidgetAnchor.TOP_LEFT );
			txt_setamount.setX( SCREEN_WIDTH - 270 );
			txt_setamount.setY( 40 );
			txt_setamount.setWidth( 80 );
			txt_setamount.setHeight( 20 );
			txt_setamount.setPlaceholder( Integer.toString( entry.units_in_stock )
					+ ( entry.hasInfiniteStock() ? " (" + SSLang.lookup( player, "term_infinite" ) + ")" : "" ) );

			btn_setamount_update = new GenericButton( SSLang.lookup( player, "btn_smip_updatestock" ) );
			btn_setamount_update.setAnchor( WidgetAnchor.TOP_LEFT );
			btn_setamount_update.setX( SCREEN_WIDTH - 180 );
			btn_setamount_update.setY( 40 );
			btn_setamount_update.setWidth( 80 );
			btn_setamount_update.setHeight( 20 );

			attachWidgets( txt_setamount, btn_setamount_update );
		} else {
			String msg = SSLang.lookup( player, "lbl_smip_instock" );
			lbl_amount.setText( SSLang.format( msg, "amount", Integer.toString( entry.units_in_stock ) ) );
		}
		lbl_amount.setWidth( lbl_amount.getText().length() * 5 );
		lbl_amount.setHeight( 10 );

		GenericLabel lbl_wanted = new GenericLabel( SSLang.lookup( player, "lbl_smip_buying" ) );
		lbl_wanted.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_wanted.setX( 100 );
		lbl_wanted.setY( 70 );
		lbl_wanted.setWidth( lbl_wanted.getText().length() * 5 );
		lbl_wanted.setHeight( 10 );
		lbl_wanted.setTooltip( SSLang.lookup( player, "lbl_smip_buying_tt" ) );

		txt_buying.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_buying.setX( SCREEN_WIDTH - 270 );
		txt_buying.setY( 70 );
		txt_buying.setWidth( 80 );
		txt_buying.setHeight( 20 );
		txt_buying.setPlaceholder( Integer.toString( entry.units_wanted )
				+ ( entry.hasInfiniteDemand() ? " (" + SSLang.lookup( player, "term_infinite" ) + ")" : "" ) );

		btn_buying_update.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_buying_update.setX( SCREEN_WIDTH - 180 );
		btn_buying_update.setY( 70 );
		btn_buying_update.setWidth( 80 );
		btn_buying_update.setHeight( 20 );
		btn_buying_update.setText( SSLang.lookup( player, "btn_smip_updatedemand" ) );

		GenericLabel lbl_store_price = new GenericLabel( SSLang.lookup( player, "lbl_smip_buyprice" ) );
		lbl_store_price.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_store_price.setX( 100 );
		lbl_store_price.setY( 100 );
		lbl_store_price.setWidth( lbl_wanted.getText().length() * 5 );
		lbl_store_price.setHeight( 10 );
		lbl_store_price.setTooltip( SSLang.lookup( player, "lbl_smip_buyprice_tt" ) );

		txt_store_price.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_store_price.setX( SCREEN_WIDTH - 270 );
		txt_store_price.setY( 100 );
		txt_store_price.setWidth( 80 );
		txt_store_price.setHeight( 20 );
		if ( entry.cost_to_buy_unit < 0 )
			txt_store_price.setPlaceholder( SSLang.lookup( player, "lbl_nfs" ) );
		else
			txt_store_price.setPlaceholder( Double.toString( entry.cost_to_buy_unit ) );

		btn_store_price_update.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_store_price_update.setX( SCREEN_WIDTH - 180 );
		btn_store_price_update.setY( 100 );
		btn_store_price_update.setWidth( 80 );
		btn_store_price_update.setHeight( 20 );
		btn_store_price_update.setText( SSLang.lookup( player, "btn_smip_updatebuyprice" ) );

		GenericLabel lbl_purchase = new GenericLabel( SSLang.lookup( player, "lbl_smip_sellprice" ) );
		lbl_purchase.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_purchase.setX( 100 );
		lbl_purchase.setY( 130 );
		lbl_purchase.setWidth( lbl_wanted.getText().length() * 5 );
		lbl_purchase.setHeight( 10 );
		lbl_purchase.setTooltip( SSLang.lookup( player, "lbl_smip_sellprice_tt" ) );

		txt_purchase_price.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_purchase_price.setX( SCREEN_WIDTH - 270 );
		txt_purchase_price.setY( 130 );
		txt_purchase_price.setWidth( 80 );
		txt_purchase_price.setHeight( 20 );
		if ( entry.cost_to_sell_unit < 0 )
			txt_purchase_price.setPlaceholder( SSLang.lookup( player, "lbl_nfs" ) );
		else
			txt_purchase_price.setPlaceholder( Double.toString( entry.cost_to_sell_unit ) );

		btn_purchase_price_update.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_purchase_price_update.setX( SCREEN_WIDTH - 180 );
		btn_purchase_price_update.setY( 130 );
		btn_purchase_price_update.setWidth( 80 );
		btn_purchase_price_update.setHeight( 20 );
		btn_purchase_price_update.setText( SSLang.lookup( player, "btn_smip_updatesellprice" ) );

		GenericLabel lbl_remove = new GenericLabel( SSLang.lookup( player, "lbl_remove" ) );
		lbl_remove.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_remove.setX( 100 );
		lbl_remove.setY( 160 );
		lbl_remove.setWidth( lbl_wanted.getText().length() * 5 );
		lbl_remove.setHeight( 10 );

		txt_remove.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_remove.setX( SCREEN_WIDTH - 270 );
		txt_remove.setY( 160 );
		txt_remove.setWidth( 80 );
		txt_remove.setHeight( 20 );
		txt_remove.setPlaceholder( "0" );

		btn_remove.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_remove.setX( SCREEN_WIDTH - 180 );
		btn_remove.setY( 160 );
		btn_remove.setWidth( 80 );
		btn_remove.setHeight( 20 );
		btn_remove.setText( SSLang.lookup( player, "btn_smip_remove" ) );

		attachWidgets( btn_delete, display, lbl_amount, lbl_wanted, txt_buying, btn_buying_update, lbl_store_price,
				txt_store_price, btn_store_price_update, lbl_purchase, txt_purchase_price, btn_purchase_price_update,
				lbl_remove, txt_remove, btn_remove );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		if ( button.equals( btn_delete ) ) {
			if ( entry.units_in_stock > 0 ) {
				ItemStack stack = entry.createItemStack();
				stack.setAmount( entry.units_in_stock );

				player.getInventory().addItem( stack );
			}

			shop.shop_entries.remove( entry );

			ShopLookupItemPopup popup = new ShopLookupItemPopup( player, shop );
			popup.setStatus( color_green, SSLang.lookup( player, "suc_smip_delete" ) );
			popup.show();
			return;
		}

		if ( button.equals( btn_setamount_update ) ) {
			try {
				Integer amount = Integer.parseInt( txt_setamount.getText() );

				if ( amount < 0 ) {
					setStatus( color_green, SSLang.lookup( player, "suc_smip_infinitestock" ) );
					entry.units_in_stock = -1;
					return;
				}

				entry.units_in_stock = amount;

				String msg = SSLang.lookup( player, "suc_smip_setamount" );
				setStatus( color_green, SSLang.format( msg, "amount", Integer.toString( amount ) ) );
			} catch ( NumberFormatException e ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}

			return;
		}

		if ( button.equals( btn_buying_update ) ) {
			try {
				Integer amount = Integer.parseInt( txt_buying.getText() );

				if ( amount < 0 ) {
					setStatus( color_green, SSLang.lookup( player, "suc_smip_infinitedemand" ) );
				} else {
					if ( amount == 0 )
						setStatus( color_green, SSLang.lookup( player, "suc_smip_stopbuying" ) );
					else {
						String msg = SSLang.lookup( player, "suc_smip_updatebuying" );
						setStatus( color_green, SSLang.format( msg, "amount", Integer.toString( amount ) ) );

						entry.units_wanted = amount;
					}
				}
			} catch ( NumberFormatException e ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}

			return;
		}

		if ( button.equals( btn_store_price_update ) ) {
			try {
				double amount = Double.parseDouble( txt_store_price.getText() );
				amount = Double.parseDouble( format.format( amount ) );

				if ( amount < 0 ) {
					entry.cost_to_buy_unit = -1;
					setStatus( color_error, SSLang.lookup( player, "suc_smip_stopselling" ) );
					return;
				}

				entry.cost_to_buy_unit = amount;

				String msg = SSLang.lookup( player, "suc_smip_updatesellprice" );
				setStatus( color_green, SSLang.format( msg, "amount", Double.toString( amount ) ) );
			} catch ( NumberFormatException e ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}
		}

		if ( button.equals( btn_purchase_price_update ) ) {
			try {
				double amount = Double.parseDouble( txt_purchase_price.getText() );
				amount = Double.parseDouble( format.format( amount ) );

				if ( amount < 0 ) {
					entry.cost_to_sell_unit = -1;
					setStatus( color_red, SSLang.lookup( player, "suc_smip_stopbuying" ) );
					return;
				}

				entry.cost_to_sell_unit = amount;

				String msg = SSLang.lookup( player, "suc_smip_updatebuyprice" );
				setStatus( color_green, SSLang.format( msg, "amount", Double.toString( amount ) ) );
			} catch ( Exception e ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}
		}

		if ( button.equals( btn_remove ) ) {
			try {
				Integer amount = Integer.parseInt( txt_remove.getText() );

				if ( amount < 1 )
					throw new NumberFormatException();

				if ( entry.hasInfiniteStock() ) {
					setError( SSLang.lookup( player, "err_smip_infinitestock" ) );
					return;
				}

				if ( entry.units_in_stock < amount ) {
					setError( SSLang.lookup( player, "err_smip_notenoughtoremove" ) );
					return;
				}

				ItemStack stack = entry.createItemStack();
				stack.setAmount( amount );
				entry.units_in_stock -= amount;
				player.getInventory().addItem( stack );

				String msg = SSLang.lookup( player, "suc_smip_removed" );
				setStatus( color_green, SSLang.format( msg, "amount", Integer.toString( amount ) ) );

				if ( !player.hasPermission( "spoutshops.admin" ) ) {
					msg = SSLang.lookup( player, "lbl_smip_instock" );
					lbl_amount.setText( SSLang.format( msg, "amount", Integer.toString( entry.units_in_stock ) ) );
				} else
					txt_setamount.setPlaceholder( Integer.toString( entry.units_in_stock ) );

			} catch ( NumberFormatException nfe ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}
		}

		super.onButtonClick( bce );
	}
}
