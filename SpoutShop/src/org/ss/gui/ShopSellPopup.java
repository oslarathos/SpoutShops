
package org.ss.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.SpoutShopPlugin;
import org.ss.other.SSEnchantment;
import org.ss.shop.Shop;
import org.ss.shop.ShopEntry;

public class ShopSellPopup
		extends ShopPopup {
	private int range_start = -1;
	private int range_end = -1;
	private boolean forward_scan;
	private int scan_index;
	private String search;

	private GenericButton btn_prev = new GenericButton( "Previous" );
	private GenericButton btn_next = new GenericButton( "Next" );
	private GenericTextField txt_search = new GenericTextField();
	private GenericButton btn_search = new GenericButton( "Search" );

	private ArrayList< ShopEntry > shop_entries = new ArrayList< ShopEntry >();
	private ArrayList< GenericTextField > sell_amounts = new ArrayList< GenericTextField >();
	private ArrayList< GenericButton > sell_buttons = new ArrayList< GenericButton >();
	private ArrayList< GenericLabel > sell_avail = new ArrayList< GenericLabel >();

	public ShopSellPopup( SpoutPlayer player, Shop shop ) {
		this( player, shop, 0, null );
	}

	public ShopSellPopup( SpoutPlayer player, Shop shop, int p_scan_index, String p_search ) {
		this( player, shop, p_scan_index, true, p_search );
	}

	public ShopSellPopup( SpoutPlayer p, Shop s, int p_scan_index, boolean p_forward_scan, String p_search ) {
		super( "Shop: Sell Items", p, s );

		this.forward_scan = p_forward_scan;
		this.scan_index = p_scan_index;
		this.search = p_search;

		btn_menu_sell.setEnabled( false );

		btn_prev.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_prev.setX( SCREEN_WIDTH - 90 );
		btn_prev.setY( 100 );
		btn_prev.setWidth( 80 );
		btn_prev.setHeight( 20 );
		btn_prev.setEnabled( false );

		btn_next.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_next.setX( SCREEN_WIDTH - 90 );
		btn_next.setY( 130 );
		btn_next.setWidth( 80 );
		btn_next.setHeight( 20 );
		btn_next.setEnabled( false );

		txt_search.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_search.setX( 80 );
		txt_search.setY( SCREEN_HEIGHT - 45 );
		txt_search.setWidth( 200 );
		txt_search.setHeight( 20 );
		if ( p_search != null )
			txt_search.setText( p_search );
		else
			txt_search.setPlaceholder( "Quick Search" );

		btn_search.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_search.setX( SCREEN_WIDTH - 90 );
		btn_search.setY( SCREEN_HEIGHT - 45 );
		btn_search.setWidth( 80 );
		btn_search.setHeight( 20 );

		attachWidgets( btn_prev, btn_next, txt_search, btn_search );

		if ( s.shop_entries.size() == 0 ) {
			setStatus( color_red, "It seems this shop is empty." );
			return;
		}

		int mod = forward_scan ? 1 : -1;
		int shown = 0;
		for ( ; shown < 5; ) {
			if ( p_forward_scan ) {
				if ( scan_index == s.shop_entries.size() )
					break;
			} else {
				if ( scan_index < 0 )
					break;
			}

			ShopEntry entry = s.shop_entries.get( scan_index );

			if ( entry.units_wanted == 0 || entry.cost_to_sell_unit < 0 ) {
				scan_index += mod;
				continue;
			}

			if ( p_search != null && !entry.matchesString( p_search ) ) {
				scan_index += mod;
				continue;
			}

			shown++;
			shop_entries.add( entry );
			scan_index += mod;
		}

		if ( !forward_scan )
			Collections.reverse( shop_entries );

		int y_start = 10;
		int count = 0;
		for ( ShopEntry entry : shop_entries ) {
			ItemStack stack = entry.createItemStack();

			GenericLabel lbl_num = new GenericLabel( ( ++count ) + ") " );
			lbl_num.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_num.setX( 80 );
			lbl_num.setY( y_start += 30 );
			lbl_num.setWidth( lbl_num.getText().length() * 5 );
			lbl_num.setHeight( 10 );

			GenericItemWidget display = new GenericItemWidget( stack );
			display.setAnchor( WidgetAnchor.TOP_LEFT );
			display.setX( 100 );
			display.setY( y_start );
			display.setWidth( 10 );
			display.setHeight( 10 );

			if ( entry.unit_enchantments.size() != 0 ) {
				StringBuilder builder = new StringBuilder();
				HashMap< Integer, Integer > enchantments = new HashMap< Integer, Integer >();

				for ( Integer eid : enchantments.keySet() ) {
					if ( builder.length() != 0 )
						builder.append( "\n" );

					builder.append( SSEnchantment.lookup( eid ) + ": Level " + enchantments.get( eid ) );
				}

				display.setTooltip( builder.toString() );
			}

			GenericLabel lbl_qty = new GenericLabel();
			if ( entry.hasInfiniteDemand() )
				lbl_qty.setText( "Always Buying" );
			else {
				if ( entry.units_wanted < 10000 )
					lbl_qty.setText( Integer.toString( entry.units_wanted ) + " Units" );
				else
					lbl_qty.setText( "<9999 Units" );
			}

			lbl_qty.setDirty( true );
			lbl_qty.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_qty.setX( 130 );
			lbl_qty.setY( y_start );
			lbl_qty.setWidth( lbl_num.getText().length() * 5 );
			lbl_qty.setHeight( 10 );
			sell_avail.add( lbl_qty );

			GenericLabel lbl_cost = new GenericLabel( "$" + entry.cost_to_sell_unit + "/unit" );
			lbl_cost.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_cost.setX( 130 );
			lbl_cost.setY( y_start + 10 );
			lbl_cost.setWidth( lbl_num.getText().length() * 5 );
			lbl_cost.setHeight( 10 );
			lbl_cost.setTextColor( color_green );

			GenericTextField txt_amount = new GenericTextField();
			txt_amount.setAnchor( WidgetAnchor.TOP_LEFT );
			txt_amount.setX( SCREEN_WIDTH - 210 );
			txt_amount.setY( y_start );
			txt_amount.setWidth( 60 );
			txt_amount.setHeight( 20 );
			txt_amount.setPlaceholder( "# to sell" );
			sell_amounts.add( txt_amount );

			GenericButton btn_sell = new GenericButton( "Sell" );
			btn_sell.setAnchor( WidgetAnchor.TOP_LEFT );
			btn_sell.setX( SCREEN_WIDTH - 140 );
			btn_sell.setY( y_start );
			btn_sell.setWidth( 40 );
			btn_sell.setHeight( 20 );
			sell_buttons.add( btn_sell );

			attachWidgets( lbl_num, display, lbl_qty, lbl_cost, txt_amount, btn_sell );
		}

		if ( shown == 0 ) {
			if ( p_search != null )
				setStatus( color_red, "Nothing matched your search terms." );
			else
				setStatus( color_red, "Nothing is being bought here." );
		}

		if ( forward_scan ) {
			if ( p_scan_index != 0 && scan_index != 0 )
				btn_prev.setEnabled( true );
		} else {
			for ( int start = ( forward_scan ? range_start : range_end ) + 1; start > 0; start-- ) {
				ShopEntry entry = s.shop_entries.get( start );

				if ( p_search != null && !entry.matchesString( p_search ) )
					continue;

				if ( entry.units_wanted == 0 || entry.cost_to_sell_unit < 0 )
					continue;

				btn_prev.setEnabled( true );
				return;
			}
		}

		if ( shown == 5 ) {
			for ( int start = ( forward_scan ? range_end : range_start ) + 1; start < s.shop_entries.size(); start++ ) {
				ShopEntry entry = s.shop_entries.get( start );

				if ( p_search != null && !entry.matchesString( p_search ) )
					continue;

				if ( entry.units_wanted == 0 || entry.cost_to_sell_unit < 0 )
					continue;

				btn_next.setEnabled( true );
				return;
			}
		}

	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( button.equals( btn_next ) ) {
			new ShopSellPopup( player, shop, ( forward_scan ? range_end : range_start ) + 1, true, search ).show();
			return;
		}

		if ( button.equals( btn_prev ) ) {
			new ShopSellPopup( player, shop, ( forward_scan ? range_start : range_end ) - 1, false, search ).show();
			return;
		}

		if ( button.equals( btn_search ) ) {
			new ShopSellPopup( player, shop, 0, true, txt_search.getText() ).show();
			return;
		}

		for ( int index = 0; index < sell_buttons.size(); index++ ) {
			if ( !button.equals( sell_buttons.get( index ) ) )
				continue;

			int amount = -1;
			try {
				amount = Integer.parseInt( sell_amounts.get( index ).getText() );

				if ( amount < 1 )
					throw new NumberFormatException();
			} catch ( NumberFormatException e ) {
				setError( "Please enter only a positive number." );
				return;
			}

			ShopEntry entry = shop_entries.get( index );
			ItemStack stack = entry.createItemStack();
			stack.setAmount( amount );

			if ( !entry.hasInfiniteDemand() && amount > entry.units_wanted ) {
				setError( "The shop is not buying that many units." );
				return;
			}

			if ( !shop.hasInfiniteWealth() && amount * entry.cost_to_sell_unit > shop.shop_vault ) {
				setError( "The shop does not have enough wealth to complete that transaction." );
				return;
			}

			if ( player.getInventory().removeItem( stack ).size() != 0 ) {
				setError( "You do not have that many to sell." );
				return;
			}

			if ( !entry.hasInfiniteDemand() ) {
				entry.units_wanted -= amount;

				String avail;

				if ( entry.units_wanted < 10000 )
					avail = Integer.toString( entry.units_wanted ) + " Units";
				else
					avail = "<9999 Units";

				sell_avail.get( index ).setText( avail );
			}

			if ( !entry.hasInfiniteStock() )
				entry.units_in_stock += amount;

			if ( !shop.hasInfiniteWealth() )
				shop.shop_vault -= amount * entry.cost_to_sell_unit;

			double earnings = entry.cost_to_sell_unit * amount;

			Economy economy = SpoutShopPlugin.getInstance().getVaultEconomy();
			economy.depositPlayer( player.getName(), earnings );

			setStatus( color_green, "You earned $" + earnings + " in that transaction." );
			updateFunds();

			return;
		}

		super.onButtonClick( bce );
	}
}
