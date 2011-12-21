
package org.ss;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.ss.listeners.SSBlockListener;
import org.ss.listeners.SSPlayerListener;
import org.ss.listeners.SSScreenListener;
import org.ss.shop.ShopManager;

public class SpoutShopPlugin
		extends JavaPlugin {
	public static final int MODE_SIGN = 0;
	public static final int MODE_BLOCK = 1;

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
		ShopManager.saveShops();

		log( Level.INFO, "All shops have been saved." );
	}

	@Override
	public void onEnable() {
		instance = this;

		if ( !getDataFolder().exists() )
			getDataFolder().mkdir();

		// Getting the configuration
		FileConfiguration config = getConfig();
		File config_file = new File( getDataFolder(), "config.ini" );

		try {
			if ( !config_file.exists() ) {
				log( "First time launch, setting up config.ini" );

				config.set( "mode", MODE_SIGN );
				config.save( config_file );
			} else
				config.load( config_file );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		ShopManager.reloadShops();

		PluginManager manager = getServer().getPluginManager();

		SSBlockListener block_listener = new SSBlockListener();
		manager.registerEvent( Type.SIGN_CHANGE, block_listener, Priority.Normal, this );
		manager.registerEvent( Type.BLOCK_BREAK, block_listener, Priority.Normal, this );

		SSPlayerListener player_listener = new SSPlayerListener();
		manager.registerEvent( Type.PLAYER_INTERACT, player_listener, Priority.Normal, this );

		SSScreenListener screen_listener = new SSScreenListener();
		manager.registerEvent( Type.CUSTOM_EVENT, screen_listener, Priority.Normal, this );

		// setting up the economy
		RegisteredServiceProvider< Economy > economy = getServer().getServicesManager().getRegistration( Economy.class );
		if ( economy != null ) {
			vault_economy = economy.getProvider();
			log( "Vault economy linked." );
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
