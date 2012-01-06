
package org.ss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;
import org.ss.listeners.SSBlockListener;
import org.ss.listeners.SSPlayerListener;
import org.ss.listeners.SSScreenListener;
import org.ss.spout.ShopCounter;

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
		try {
			SSBlockListener.getInstance().saveAllShops();
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		log( Level.INFO, "Version " + getDescription().getVersion() + " disabled." );
	}

	@Override
	public void onEnable() {
		instance = this;

		if ( !getDataFolder().exists() )
			getDataFolder().mkdir();

		// Reading the configuration
		try {
			File config_file = new File( getDataFolder(), "config.ini" );

			if ( !config_file.exists() ) {
				log( "Extracting config.ini" );
				config_file.createNewFile();

				byte[] buffer = new byte[ 128 ];
				InputStream is = getResource( "config.ini" );
				FileOutputStream fos = new FileOutputStream( config_file );

				int read = 0;
				while ( ( read = is.read( buffer ) ) > 0 ) {
					fos.write( buffer, 0, read );
					fos.flush();
				}

				fos.close();
				is.close();
			}

			getConfig().load( config_file );
		} catch ( Exception e ) {
			e.printStackTrace();
			getPluginLoader().disablePlugin( this );
			return;
		}

		// Loading up shops.
		try {
			SSBlockListener.getInstance().reloadAllShops();
		} catch ( Exception e ) {
			e.printStackTrace();
		}

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

		// registering recipes

		if ( getConfig().getBoolean( "shop-counter-recipe" ) ) {
			ShopCounter shop_block = new ShopCounter( this );
			ItemStack shop_block_drop = new ItemStack( Material.BOOK, 1 );
			shop_block.setItemDrop( shop_block_drop );

			SpoutShapedRecipe recipe = new SpoutShapedRecipe( new SpoutItemStack( shop_block, 1 ) );
			recipe.shape( "wpw", "www", "sss" );
			recipe.setIngredient( 'w', MaterialData.wood );
			recipe.setIngredient( 'p', MaterialData.paper );
			recipe.setIngredient( 's', MaterialData.stone );
			SpoutManager.getMaterialManager().registerSpoutRecipe( recipe );
		}

		log( "Version " + getDescription().getVersion() + " loaded." );
	}

	public Economy getVaultEconomy() {
		return vault_economy;
	}

}
