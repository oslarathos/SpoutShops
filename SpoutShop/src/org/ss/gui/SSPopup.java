
package org.ss.gui;

import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.ss.SpoutShopPlugin;

public class SSPopup
		extends GenericPopup {
	public static final Color color_error = new Color( 255, 0, 0 );
	public static final int SCREEN_WIDTH = 427;
	public static final int SCREEN_HEIGHT = 240;

	protected GenericLabel label;
	private GenericLabel status;

	public SSPopup( String str_label ) {
		label = new GenericLabel( str_label );
		label.setAuto( true );
		label.setAnchor( WidgetAnchor.TOP_LEFT );
		label.setX( 10 );
		label.setY( 10 );
		label.setWidth( 100 );
		label.setHeight( 10 );

		status = new GenericLabel( "" );
		status.setAuto( true );
		status.setAnchor( WidgetAnchor.TOP_LEFT );
		status.setX( 10 );
		status.setY( SCREEN_HEIGHT - 10 );
		status.setWidth( SCREEN_WIDTH - 20 );
		status.setHeight( 10 );
		status.setTextColor( new Color( 255, 0, 0 ) );
		status.setDirty( true );

		attachWidgets( label, status );
	}

	public void setError( String message ) {
		setStatus( color_error, message );
	}

	public void setStatus( Color color, String message ) {
		if ( color != null )
			status.setTextColor( color );

		status.setText( message );
	}

	public void attachWidgets( Widget... widgets ) {
		for ( Widget widget : widgets )
			attachWidget( SpoutShopPlugin.getInstance(), widget );
	}

	public void onButtonClick( ButtonClickEvent bce ) {
	}
}
