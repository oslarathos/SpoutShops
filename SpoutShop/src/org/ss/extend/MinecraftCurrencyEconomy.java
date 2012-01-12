
package org.ss.extend;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.ss.SpoutShopPlugin;

public class MinecraftCurrencyEconomy
		implements Economy {

	@Override
	public EconomyResponse bankBalance( String arg0 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public EconomyResponse bankDeposit( String arg0, double arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public EconomyResponse bankHas( String arg0, double arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public EconomyResponse bankWithdraw( String arg0, double arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public EconomyResponse createBank( String arg0, String arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public String format( double arg0 ) {
		return null;
	}

	@Override
	public double getBalance( String player_name ) {
		Player player = SpoutShopPlugin.getInstance().getServer().getPlayer( player_name );

		if ( player == null || !player.isOnline() )
			return 0.0;

		return MinecraftCurrencyCoins.has( player );
	}

	@Override
	public EconomyResponse withdrawPlayer( String player_name, double amount ) {
		Player player = SpoutShopPlugin.getInstance().getServer().getPlayer( player_name );

		if ( player == null || !player.isOnline() )
			return new EconomyResponse( 0, 0, ResponseType.FAILURE, "Player is not online" );

		boolean success = MinecraftCurrencyCoins.takePlayer( player, amount );
		double balance = MinecraftCurrencyCoins.has( player );

		if ( success )
			return new EconomyResponse( amount, balance, ResponseType.SUCCESS, null );
		else
			return new EconomyResponse( amount, balance, ResponseType.FAILURE, "Insufficient funds" );
	}

	@Override
	public EconomyResponse depositPlayer( String player_name, double amount ) {
		Player player = SpoutShopPlugin.getInstance().getServer().getPlayer( player_name );

		if ( player == null || !player.isOnline() )
			return new EconomyResponse( 0, 0, ResponseType.FAILURE, "Player is not online" );

		MinecraftCurrencyCoins.givePlayer( player, amount );

		return new EconomyResponse( amount, MinecraftCurrencyCoins.has( player ), ResponseType.SUCCESS, null );
	}

	@Override
	public boolean has( String player_name, double amount ) {
		Player player = SpoutShopPlugin.getInstance().getServer().getPlayer( player_name );

		if ( player == null || !player.isOnline() )
			return false;

		return MinecraftCurrencyCoins.has( player ) >= amount;
	}

	@Override
	public String getName() {
		return "MinecraftCurrency Economy Provider by Langricr";
	}

	@Override
	public EconomyResponse isBankMember( String arg0, String arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public EconomyResponse isBankOwner( String arg0, String arg1 ) {
		return new EconomyResponse( 0, 0, ResponseType.NOT_IMPLEMENTED, "Not yet Implemented" );
	}

	@Override
	public boolean isEnabled() {
		Plugin plugin = SpoutShopPlugin.getInstance().getServer().getPluginManager().getPlugin( "MinecraftCurrency" );

		if ( plugin == null )
			return false;

		return plugin.isEnabled();
	}

}
