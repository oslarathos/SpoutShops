
package org.ss.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.ss.other.SSEnchantment;
import org.ss.other.SSLang;
import org.ss.other.SSPotion;
import org.ss.shop.Shop;
import org.ss.shop.ShopEntry;

public class ShopBuyPopup
		extends ShopPopup {

	private int range_start = -1;
	private int range_end = -1;
	private boolean forward_scan;
	private int scan_index;
	private String search;

	private GenericButton btn_prev = new GenericButton();
	private GenericButton btn_next = new GenericButton();
	private GenericTextField txt_search = new GenericTextField();
	private GenericButton btn_search = new GenericButton();

	private ArrayList< ShopEntry > shop_entries = new ArrayList< ShopEntry >();
	private ArrayList< GenericTextField > buy_amounts = new ArrayList< GenericTextField >();
	private ArrayList< GenericButton > buy_buttons = new ArrayList< GenericButton >();
	private ArrayList< GenericLabel > buy_avail = new ArrayList< GenericLabel >();

	public ShopBuyPopup( SpoutPlayer player, Shop shop ) {
		this( player, shop, 0 );
	}

	public ShopBuyPopup( SpoutPlayer player, Shop shop, int p_scan_index ) {
		this( player, shop, p_scan_index, true );
	}

	public ShopBuyPopup( SpoutPlayer player, Shop shop, int p_scan_index, boolean p_forward_scan ) {
		this( player, shop, p_scan_index, p_forward_scan, null );
	}

	public ShopBuyPopup( SpoutPlayer p, Shop s, int p_scan_index, boolean p_forward_scan, String p_search ) {
		super( SSLang.lookup( p, "lbl_sbp_title" ), p, s );

		this.forward_scan = p_forward_scan;
		this.scan_index = p_scan_index;
		this.search = p_search;

		btn_menu_buy.setEnabled( false );

		btn_prev.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_prev.setX( SCREEN_WIDTH - 90 );
		btn_prev.setY( 100 );
		btn_prev.setWidth( 80 );
		btn_prev.setHeight( 20 );
		btn_prev.setEnabled( false );
		btn_prev.setText( SSLang.lookup( player, "btn_prev" ) );

		btn_next.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_next.setX( SCREEN_WIDTH - 90 );
		btn_next.setY( 130 );
		btn_next.setWidth( 80 );
		btn_next.setHeight( 20 );
		btn_next.setEnabled( false );
		btn_next.setText( SSLang.lookup( player, "btn_next" ) );

		txt_search.setAnchor( WidgetAnchor.TOP_LEFT );
		txt_search.setX( 80 );
		txt_search.setY( SCREEN_HEIGHT - 45 );
		txt_search.setWidth( 200 );
		txt_search.setHeight( 20 );
		if ( p_search != null )
			txt_search.setText( p_search );
		else
			txt_search.setPlaceholder( SSLang.lookup( player, "txt_search" ) );

		btn_search.setAnchor( WidgetAnchor.TOP_LEFT );
		btn_search.setX( SCREEN_WIDTH - 90 );
		btn_search.setY( SCREEN_HEIGHT - 45 );
		btn_search.setWidth( 80 );
		btn_search.setHeight( 20 );
		btn_search.setText( SSLang.lookup( player, "btn_search" ) );

		attachWidgets( btn_prev, btn_next, txt_search, btn_search );

		if ( s.shop_entries.size() == 0 ) {
			setStatus( color_red, SSLang.lookup( player, "err_shop_empty" ) );
			return;
		}

		int mod = forward_scan ? 1 : -1;
		int found_entries = 0;
		while ( found_entries < 5 ) {
			if ( p_forward_scan ) {
				if ( scan_index == s.shop_entries.size() )
					break;
			} else {
				if ( scan_index < 0 )
					break;
			}

			ShopEntry entry = s.shop_entries.get( scan_index );

			if ( entry.units_in_stock == 0 || entry.cost_to_buy_unit < 0 ) {
				scan_index += mod;
				continue;
			}

			if ( p_search != null && !entry.matchesString( p_search ) ) {
				scan_index += mod;
				continue;
			}

			if ( range_start == -1 )
				range_start = scan_index;
			else
				range_end = scan_index;

			found_entries++;
			shop_entries.add( entry );
			scan_index += mod;
		}

		if ( !forward_scan )
			Collections.reverse( shop_entries );

		int count = 0;
		int y_start = 10;
		for ( ShopEntry entry : shop_entries ) {
			SpoutItemStack stack = entry.createItemStack();

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

			StringBuilder builder = new StringBuilder();

			if ( stack.getType() == Material.POTION ) {
				builder.append( SSPotion.format( stack ) );
			} else
				builder.append( stack.getMaterial().getNotchianName() );

			if ( stack.getEnchantments().size() != 0 ) {
				Map< Enchantment, Integer > enchantments = stack.getEnchantments();

				if ( enchantments.size() != 0 ) {
					for ( Enchantment enc : enchantments.keySet() ) {
						builder.append( "\n" + SSEnchantment.lookup( enc.getId() ) + ": LvL " + enchantments.get( enc ) );
					}
				}
			}

			display.setTooltip( builder.toString() );

			GenericLabel lbl_qty = new GenericLabel();
			if ( entry.hasInfiniteStock() )
				lbl_qty.setText( SSLang.lookup( player, "term_infinite_stock" ) );
			else {
				String msg;

				if ( entry.units_in_stock < 10000 ) {
					msg = SSLang.lookup( player, "term_units" );
					msg = SSLang.format( msg, "amount", Integer.toString( entry.units_in_stock ) );
				} else
					msg = SSLang.lookup( player, "term_units_ex" );

				lbl_qty.setText( msg );
			}

			lbl_qty.setDirty( true );
			lbl_qty.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_qty.setX( 130 );
			lbl_qty.setY( y_start );
			lbl_qty.setWidth( lbl_num.getText().length() * 5 );
			lbl_qty.setHeight( 10 );
			buy_avail.add( lbl_qty );

			GenericLabel lbl_cost = new GenericLabel( "$" + entry.cost_to_buy_unit + " "
					+ SSLang.lookup( player, "term_per_unit" ) );
			lbl_cost.setAnchor( WidgetAnchor.TOP_LEFT );
			lbl_cost.setX( 130 );
			lbl_cost.setY( y_start + 10 );
			lbl_cost.setWidth( lbl_num.getText().length() * 5 );
			lbl_cost.setHeight( 10 );
			lbl_cost.setTextColor( color_red );

			GenericTextField txt_amount = new GenericTextField();
			txt_amount.setAnchor( WidgetAnchor.TOP_LEFT );
			txt_amount.setX( SCREEN_WIDTH - 210 );
			txt_amount.setY( y_start );
			txt_amount.setWidth( 60 );
			txt_amount.setHeight( 20 );
			txt_amount.setPlaceholder( "# to buy" );
			buy_amounts.add( txt_amount );

			GenericButton btn_buy = new GenericButton( SSLang.lookup( player, "btn_sbp_buy" ) );
			btn_buy.setAnchor( WidgetAnchor.TOP_LEFT );
			btn_buy.setX( SCREEN_WIDTH - 140 );
			btn_buy.setY( y_start );
			btn_buy.setWidth( 40 );
			btn_buy.setHeight( 20 );
			buy_buttons.add( btn_buy );

			attachWidgets( lbl_num, display, lbl_qty, lbl_cost, txt_amount, btn_buy );
		}

		if ( found_entries == 0 ) {
			if ( p_search != null )
				setStatus( color_red, SSLang.lookup( player, "err_search_none" ) );
			else
				setStatus( color_red, SSLang.lookup( player, "err_shop_buy_empty" ) );
		}

		if ( forward_scan ) {
			if ( p_scan_index != 0 && scan_index != 0 )
				btn_prev.setEnabled( true );
		} else if ( range_end > 0 ) {
			for ( int start = range_end - 1; start >= 0; start-- ) {
				ShopEntry entry = s.shop_entries.get( start );

				if ( p_search != null && !entry.matchesString( p_search ) )
					continue;

				if ( entry.units_in_stock == 0 || entry.cost_to_buy_unit < 0 )
					continue;

				btn_prev.setEnabled( true );
				return;
			}
		}

		if ( found_entries == 5 ) {
			for ( int start = ( forward_scan ? range_end : range_start ) + 1; start < s.shop_entries.size(); start++ ) {
				ShopEntry entry = s.shop_entries.get( start );

				if ( p_search != null && !entry.matchesString( p_search ) )
					continue;

				if ( entry.units_in_stock == 0 || entry.cost_to_buy_unit < 0 )
					continue;

				btn_next.setEnabled( true );
				return;
			}
		}
	}

	public void onButtonClick( ButtonClickEvent bce ) {
		Button button = bce.getButton();

		if ( button.equals( btn_search ) ) {
			new ShopBuyPopup( player, shop, 0, true, txt_search.getText() ).show();
			return;
		}

		if ( button.equals( btn_next ) ) {
			new ShopBuyPopup( player, shop, ( forward_scan ? range_end : range_start ) + 1, true, search ).show();
			return;
		}

		if ( button.equals( btn_prev ) ) {
			new ShopBuyPopup( player, shop, ( forward_scan ? range_start : range_end ) - 1, false, search ).show();
			return;
		}

		for ( int index = 0; index < buy_buttons.size(); index++ ) {
			if ( !button.equals( buy_buttons.get( index ) ) )
				continue;

			try {
				Integer amount_to_buy = Integer.parseInt( buy_amounts.get( index ).getText() );

				if ( amount_to_buy < 1 )
					throw new NumberFormatException();

				ShopEntry entry = shop_entries.get( index );

				if ( !entry.hasInfiniteStock() && amount_to_buy > entry.units_in_stock ) {
					setError( SSLang.lookup( player, "err_sbp_notenoughavail" ) );
					return;
				}

				double cost_to_buy = amount_to_buy * entry.cost_to_buy_unit;

				EconomyResponse response = economy.withdrawPlayer( player.getName(), cost_to_buy );

				if ( !response.transactionSuccess() ) {
					setError( response.errorMessage );
					return;
				}

				ItemStack stack = entry.createItemStack();
				stack.setAmount( amount_to_buy );
				player.getInventory().addItem( stack );

				if ( !entry.hasInfiniteStock() )
					entry.units_in_stock -= amount_to_buy;

				if ( !shop.hasInfiniteWealth() )
					shop.shop_vault += cost_to_buy;

				updateFunds();

				if ( entry.units_in_stock < 10000 ) {
					String msg = SSLang.lookup( player, "term_units" );
					msg = SSLang.format( msg, "amount", Integer.toString( entry.units_in_stock ) );
					buy_avail.get( index ).setText( msg );
				} else {
					buy_avail.get( index ).setText( SSLang.lookup( player, "term_units_ex" ) );
				}

				String msg = SSLang.lookup( player, "suc_sbp_spent" );
				msg = SSLang.format( msg, "cost", Double.toString( cost_to_buy ) );
				setStatus( color_green, msg );

				return;
			} catch ( NumberFormatException nfee ) {
				setError( SSLang.lookup( player, "err_not_number" ) );
				return;
			}
		}

		super.onButtonClick( bce );
	}
}
