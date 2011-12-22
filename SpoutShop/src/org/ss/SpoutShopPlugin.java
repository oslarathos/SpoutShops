
package org.ss;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ss.listeners.SSBlockListener;
import org.ss.listeners.SSPlayerListener;
import org.ss.listeners.SSScreenListener;

public class SpoutShopPlugin
		extends JavaPlugin {

	private static final Logger logger = Logger.getLogger( "SpoutShops" );

	public static final void log( String message ) {
		log( Level.INFO, message );
	}

	public static final void log( Level level, String message ) {
		logger.log( level, "[SpoutShops] " + message );
	}

	private static SpoutShopPlugin instance;

	public static SpoutShopPlugin getInstance() {
		return instance;
	}

	private Economy vault_economy;

	@Override
	public void onDisable() {
		SSBlockListener.getInstance().saveAllShops();

		log( Level.INFO, "Version " + getDescription().getVersion() + " disabled." );
	}

	@Override
	public void onEnable() {
		instance = this;

		if ( !getDataFolder().exists() )
			getDataFolder().mkdir();

		// Reading the configuration
		try {

		} catch ( Exception e ) {
			e.printStackTrace();
			getPluginLoader().disablePlugin( this );
			return;
		}

		// Loading up shops.
		SSBlockListener.getInstance().reloadAllShops();

		// Registering events
		SSBlockListener.registerEvents( this );
		SSPlayerListener.registerEvents( this );
		SSScreenListener.registerEvents( this );

		// setting up the economy
		RegisteredServiceProvider< Economy > economy = getServer().getServicesManager().getRegistration( Economy.class );
		if ( economy != null ) {
			vault_economy = economy.getProvider();

			log( "Vault economy linked: " + vault_economy.getName() );
		} else {
			log( Level.SEVERE, "Vault economy not found, aborting." );
			getPluginLoader().disablePlugin( this );
			return;
		}

		log( "Version " + getDescription().getVersion() + " loaded." );
	}

	public Economy getVaultEconomy() {
		return vault_economy;
	}
}
