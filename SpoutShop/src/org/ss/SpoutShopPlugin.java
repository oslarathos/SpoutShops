
package org.ss;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.material.MaterialData;
import org.ss.extend.MinecraftCurrencyEconomy;
import org.ss.gui.ShopPopup;
import org.ss.listeners.SSBlockListener;
import org.ss.listeners.SSPlayerListener;
import org.ss.listeners.SSScreenListener;
import org.ss.other.SSLang;
import org.ss.spout.ShopCounter;

public class SpoutShopPlugin
		extends JavaPlugin
		implements Runnable {

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
	private Thread shutdown_thread = new Thread( this );

	@Override
	public void onDisable() {
		try {
			SSBlockListener.getInstance().saveAllShops();
		} catch ( IOException e ) {
			e.printStackTrace();
		}

		Runtime.getRuntime().removeShutdownHook( shutdown_thread );

		log( Level.INFO, "Version " + getDescription().getVersion() + " disabled." );
	}

	@Override
	public void onEnable() {
		instance = this;

		if ( !getDataFolder().exists() )
			getDataFolder().mkdir();

		// Registering the shutdown thread
		Runtime.getRuntime().addShutdownHook( shutdown_thread );

		// Setting up languages
		SSLang.startup();

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

		// Checking for desired texture
		File block_texture_file = new File( getDataFolder(), "shop-block.png" );
		if ( block_texture_file.exists() ) {
			try {
				SpoutManager.getFileManager().addToCache( this, block_texture_file );

				BufferedImage block_texture = ImageIO.read( block_texture_file );

				ShopCounter.setTexture( block_texture_file.getName(), block_texture.getWidth(),
						block_texture.getHeight(), block_texture.getWidth() / 4 );

				log( "Now using shop-block.png" );
			} catch ( Exception e ) {
				log( Level.SEVERE, "Failed to read shop-block.png" );
			}
		} else if ( getConfig().isSet( "texture-path" ) ) {
			int texwidth = getConfig().getInt( "texture-width" );
			int texheight = getConfig().getInt( "texture-height" );
			int texspan = getConfig().getInt( "texture-span" );

			ShopCounter.setTexture( getConfig().getString( "texture-path" ), texwidth, texheight, texspan );

			log( "Now using remote image at " + texwidth + "x" + texheight + "@" + texspan );
		} else
			log( "Using default texture" );

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
		} else if ( getServer().getPluginManager().isPluginEnabled( "MinecraftCurrency" ) ) {
			vault_economy = new MinecraftCurrencyEconomy();
			ShopPopup.format.applyPattern( "#.#" );

			log( "Using custom provider for MinecraftCurrency by Perdog" );
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

	public void run() {
		log( "Emergency shutdown thread started." );

		try {
			SSBlockListener.getInstance().saveAllShops();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
