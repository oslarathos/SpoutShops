
package org.ss.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.ss.SpoutShopPlugin;

public class SSLang {
	public static final File folder = new File( SpoutShopPlugin.getInstance().getDataFolder(), "lang" );

	private static HashMap< String, HashMap< String, String > > languages = new HashMap< String, HashMap< String, String > >();

	public static void startup() {
		if ( !folder.exists() )
			folder.mkdir();

		checkBaseFile();

		// Loading up languages
		for ( File file : folder.listFiles() ) {
			try {
				FileReader fr = new FileReader( file );
				BufferedReader reader = new BufferedReader( fr );

				String version_line = reader.readLine();
				if ( !version_line.startsWith( "#version" ) ) {
					SpoutShopPlugin.log( Level.WARNING, "Language file is missing '#version' tag: " + file.getName() );
					continue;
				}

				double version = Double.parseDouble( version_line.substring( version_line.indexOf( ":" ) + 1 ).trim() );

				if ( version < Double.parseDouble( SpoutShopPlugin.getInstance().getDescription().getVersion() ) ) {
					SpoutShopPlugin.log( Level.WARNING, "Language file " + file.getName() + " is out of date." );
					continue;
				}

				String name_line = reader.readLine();

				if ( !name_line.startsWith( "#lang" ) ) {
					SpoutShopPlugin.log( Level.WARNING, "Language file is missing '#lang' tag: " + file.getName() );
					continue;
				}

				String lang = name_line.substring( name_line.indexOf( ":" ) + 1 ).trim();

				if ( languages.containsKey( lang.toLowerCase() ) ) {
					SpoutShopPlugin.log( Level.SEVERE, "Duplicate language entry for '" + lang + "'" );
					continue;
				}

				HashMap< String, String > key_map = new HashMap< String, String >();

				String line = null;
				while ( ( line = reader.readLine() ) != null ) {
					if ( line.startsWith( "#" ) || line.length() == 0 )
						continue;

					String key = line.substring( 0, line.indexOf( ":" ) ).trim();
					String val = line.substring( line.indexOf( ":" ) + 1 ).replaceAll( "\\$n", "\n" ).trim();

					key_map.put( key, val );
				}

				SpoutShopPlugin.log( "Loaded language: " + lang );

				languages.put( lang, key_map );
			} catch ( Exception e ) {
				SpoutShopPlugin.log( Level.SEVERE, "Failed to read language file: " + file.getName() );
			}
		}
	}

	private static void checkBaseFile() {
		try {
			File enUS = new File( folder, "en-US.txt" );

			if ( enUS.exists() ) {
				FileReader fr = new FileReader( enUS );
				BufferedReader reader = new BufferedReader( fr );

				String version_line = reader.readLine();

				reader.close();
				fr.close();

				if ( !version_line.startsWith( "#version" ) )
					throw new Exception( "Version line is missing from " + enUS.getName() );

				double version = Double.parseDouble( version_line.substring( version_line.indexOf( ":" ) + 1 ).trim() );

				if ( version == Double.parseDouble( SpoutShopPlugin.getInstance().getDescription().getVersion() ) )
					return;
				else {
					SpoutShopPlugin.log( "Language file en-US is out-of-date, copying over." );
					enUS.delete();
				}
			} else {
				SpoutShopPlugin.log( "Language file en-US does not exist, copying over." );
			}

			enUS.createNewFile();

			InputStream is = SpoutShopPlugin.getInstance().getResource( "en-US.txt" );
			FileOutputStream fos = new FileOutputStream( enUS );
			byte[] buffer = new byte[ 1024 ];
			int read = 0;

			while ( ( read = is.read( buffer ) ) > 0 ) {
				fos.write( buffer, 0, read );
				fos.flush();
			}

			is.close();
			fos.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public static String lookup( Player player, String key ) {
		String lang = SpoutShopPlugin.getInstance().getConfig().getString( "default-lang", "en-US" );

		return lookup( lang, key );
	}

	public static String lookup( String lang, String key ) {
		if ( !languages.containsKey( lang ) )
			return null;

		if ( !languages.get( lang ).containsKey( key ) ) {
			SpoutShopPlugin.log( Level.SEVERE, "Language " + lang + " is missing key '" + key + "'" );
			return languages.get( "en-US" ).get( key );
		}

		return languages.get( lang ).get( key );
	}

	public static String format( String str, String key, String value ) {
		return str.replaceAll( "%" + key + "%", value );
	}
}
