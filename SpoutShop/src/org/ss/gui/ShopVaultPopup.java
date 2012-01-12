
package org.ss.gui;

import net.milkbowl.vault.economy.EconomyResponse;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPermissions;
import org.ss.other.SSLang;
import org.ss.shop.Shop;

public class ShopVaultPopup
		extends ShopPopup {

	GenericTextField txt_setfunds;
	GenericButton btn_setfunds_update;
	GenericTextField txt_withdraw = new GenericTextField();
	GenericButton btn_withdraw = new GenericButton();
	GenericTextField txt_deposit = new GenericTextField();
	GenericButton btn_deposit = new GenericButton();

	public ShopVaultPopup( SpoutPlayer player, Shop shop ) {
		super( SSLang.lookup( player, "lbl_svp_title" ), player, shop );

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		if ( SpoutShopPermissions.ADMIN.hasNode( player ) ) {
			GenericLabel lbl_setfunds = new GenericLabel( SSLang.lookup( player, "lbl_svp_setfunds" ) );
			lbl_setfunds.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_setfunds.setX( 70 );
			lbl_setfunds.setY( 40 );
			lbl_setfunds.setWidth( 20 );
			lbl_setfunds.setHeight( 20 );

			txt_setfunds = new GenericTextField();
			txt_setfunds.setAnchor( WidgetAnchor.TOP_LEFT );
			txt_setfunds.setX( 140 );
			txt_setfunds.setY( 40 );
			txt_setfunds.setWidth( 100 );
			txt_setfunds.setHeight( 20 );
			txt_setfunds.setPlaceholder( Double.toString( shop.shop_vault ) );

			btn_setfunds_update = new GenericButton( SSLang.lookup( player, "btn_svp_setfunds" ) );
			btn_setfunds_update.setAnchor( WidgetAnchor.TOP_LEFT );
			btn_setfunds_update.setX( SCREEN_WIDTH - 180 );
			btn_setfunds_update.setY( 40 );
			btn_setfunds_update.setWidth( 80 );
			btn_setfunds_update.setHeight( 20 );

			attachWidgets( lbl_setfunds, txt_setfunds, btn_setfunds_update );
		}

		GenericLabel lbl_withdraw = new GenericLabel( SSLang.lookup( player, "btn_svp_withdraw" ) );
		lbl_withdraw.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_withdraw.setX( 70 );
		lbl_withdraw.setY( 70 );
		lbl_withdraw.setWidth( lbl_withdraw.getWidth() * 5 );
		lbl_withdraw.setHeight( 10 );

		txt_withdraw.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_withdraw.setX( 140 );
		txt_withdraw.setY( 70 );
		txt_withdraw.setWidth( 100 );
		txt_withdraw.setHeight( 20 );
		txt_withdraw.setPlaceholder( "0.00" );

		btn_withdraw.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_withdraw.setX( SCREEN_WIDTH - 180 );
		btn_withdraw.setY( 70 );
		btn_withdraw.setWidth( 80 );
		btn_withdraw.setHeight( 20 );
		btn_withdraw.setText( SSLang.lookup( player, "btn_svp_withdraw" ) );

		GenericLabel lbl_deposit = new GenericLabel( SSLang.lookup( player, "btn_svp_deposit" ) );
		lbl_deposit.setAnchor( WidgetAnchor.TOP_LEFT );
		lbl_deposit.setX( 70 );
		lbl_deposit.setY( 100 );
		lbl_deposit.setWidth( lbl_deposit.getWidth() * 5 );
		lbl_deposit.setHeight( 10 );

		txt_deposit.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_deposit.setX( 140 );
		txt_deposit.setY( 100 );
		txt_deposit.setWidth( 100 );
		txt_deposit.setHeight( 20 );
		txt_deposit.setPlaceholder( "$0.0000" );

		btn_deposit.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_deposit.setX( SCREEN_WIDTH - 180 );
		btn_deposit.setY( 100 );
		btn_deposit.setWidth( 80 );
		btn_deposit.setHeight( 20 );
		btn_deposit.setText( SSLang.lookup( player, "btn_svp_deposit" ) );

		attachWidgets( lbl_withdraw, txt_withdraw, btn_withdraw, lbl_deposit, txt_deposit, btn_deposit );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( !shop.isManager( player ) ) {
			close();
			return;
		}

		if ( button.equals( btn_setfunds_update ) ) {
			try {
				double amount = Double.parseDouble( txt_setfunds.getText() );
				amount = Double.parseDouble( format.format( amount ) );

				if ( amount < 0 ) {
					shop.shop_vault = -1;
					setStatus( color_green, SSLang.lookup( player, "suc_svp_infinite_funds" ) );
					return;
				}

				shop.shop_vault = amount;

				String msg = SSLang.lookup( player, "suc_svp_setfunds" );
				msg = SSLang.format( msg, "amount", Double.toString( amount ) );
				setStatus( color_green, msg );

				updateFunds();
			} catch ( NumberFormatException nfe ) {
				setError( "Please enter only a number." );
			}
		}

		if ( button.equals( btn_withdraw ) ) {
			try {
				double amount = Double.parseDouble( txt_withdraw.getText() );
				amount = Double.parseDouble( format.format( amount ) );

				if ( amount < 1 )
					throw new NumberFormatException();

				if ( shop.hasInfiniteWealth() ) {
					setError( SSLang.lookup( player, "err_svp_infinitecash" ) );
					return;
				}

				if ( shop.shop_vault < amount ) {
					setError( SSLang.lookup( player, "err_svp_notenoughvault" ) );
					return;
				}

				EconomyResponse response = economy.depositPlayer( player.getName(), amount );

				if ( !response.transactionSuccess() ) {
					setError( response.errorMessage );
					return;
				}

				shop.shop_vault -= amount;

				String msg = SSLang.lookup( player, "suc_svp_withdraw" );
				msg = SSLang.format( msg, "amount", Double.toString( amount ) );
				setStatus( color_green, msg );

				updateFunds();
			} catch ( NumberFormatException nfe ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}

			return;
		}

		if ( button.equals( btn_deposit ) ) {
			try {
				double amount = Double.parseDouble( txt_deposit.getText() );
				amount = Double.parseDouble( format.format( amount ) );

				if ( amount < 1 )
					throw new NumberFormatException();

				if ( shop.hasInfiniteWealth() ) {
					setError( SSLang.lookup( player, "err_svp_infinitecash" ) );
					return;
				}

				EconomyResponse response = economy.withdrawPlayer( player.getName(), amount );

				if ( !response.transactionSuccess() ) {
					setError( response.errorMessage );
					return;
				}

				shop.shop_vault += amount;

				String msg = SSLang.lookup( player, "suc_svp_deposit" );
				msg = SSLang.format( msg, "amount", Double.toString( amount ) );
				setStatus( color_green, msg );

				updateFunds();
			} catch ( NumberFormatException nfe ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
			}

			return;
		}

		super.onButtonClick( bce );
	}
}
