/*
 * Created on 2 juil. 2003
 *
 * Copyright (C) 2004, 2005, 2006 Aelitis SAS, All rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the LICENSE file ).
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * AELITIS, SAS au capital de 46,603.30 euros,
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.ui.swt.views;

import java.util.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerStats;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.util.DisplayFormatters;
import org.gudy.azureus2.plugins.ui.UIPluginViewToolBarListener;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.Utils;
import org.gudy.azureus2.ui.swt.components.Legend;
import org.gudy.azureus2.ui.swt.components.graphics.MultiPlotGraphic;
import org.gudy.azureus2.ui.swt.components.graphics.ValueFormater;
import org.gudy.azureus2.ui.swt.components.graphics.ValueSource;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
import org.gudy.azureus2.ui.swt.plugins.UISWTView;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventImpl;

import com.aelitis.azureus.ui.common.ToolBarItem;
import com.aelitis.azureus.ui.selectedcontent.SelectedContent;
import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;



public class 
DownloadActivityView 
	implements UISWTViewCoreEventListener, UIPluginViewToolBarListener
{
	public static final String MSGID_PREFIX = "DownloadActivityView";

	private static Color[]	colors = { Colors.fadedGreen, Colors.blues[Colors.BLUES_DARKEST], Colors.light_grey };
		
	private UISWTView 				swtView;
	private boolean					legend_at_bottom;
	private Composite				panel;
	private MultiPlotGraphic 		mpg;
	
	private DownloadManager 		manager;
	
	public 
	DownloadActivityView()
	{
	}
	
	private String 
	getFullTitle() 
	{
		return( MessageText.getString(MSGID_PREFIX + ".title.full" ));
	}
	  
	public void 
	initialize(
		Composite composite )
	{
	    panel = new Composite(composite,SWT.NULL);
	    panel.setLayout(new GridLayout(legend_at_bottom?1:2, false));
	    GridData gridData;

	    ValueFormater formatter =
	    	new ValueFormater() 
	    	{
	        	public String 
	        	format(
	        		int value) 
	        	{
	        		return DisplayFormatters.formatByteCountToKiBEtcPerSec( value );
	        	}
	    	};
	      
	    
	    final ValueSourceImpl[] sources = {
	    	new ValueSourceImpl( "Up", 0, colors, true, false )
	    	{
	    		public int
	    		getValue()
	    		{
	    			DownloadManager dm = manager;
	    			
	    			if ( dm == null ){
	    				
	    				return( 0 );
	    			}
	    			
	    			DownloadManagerStats stats = manager.getStats();
	    			
	    			return((int)(stats.getDataSendRate() + stats.getProtocolSendRate()));
	    		}
	    	},
	    	new ValueSourceImpl( "Down", 1, colors, false, false )
	    	{
	    		public int
	    		getValue()
	    		{
	    			DownloadManager dm = manager;
	    			
	    			if ( dm == null ){
	    				
	    				return( 0 );
	    			}
	    			
	    			DownloadManagerStats stats = manager.getStats();
	    			
	    			return((int)(stats.getDataReceiveRate() +stats.getProtocolReceiveRate()));
	    		}
	    	},
	    	new ValueSourceImpl( "Swarm Peer Average", 2, colors, false, true )
	    	{
	    		public int
	    		getValue()
	    		{
	    			DownloadManager dm = manager;
	    			
	    			if ( dm == null ){
	    				
	    				return( 0 );
	    			}
	    				    			
	    			return((int)(manager.getStats().getTotalAveragePerPeer()));
	    		}
	    	}
	    };
	    
		final MultiPlotGraphic f_mpg = mpg = MultiPlotGraphic.getInstance( sources, formatter );
	    
	    
		String[] color_configs = new String[] {
				"DownloadActivityView.legend.up",
				"DownloadActivityView.legend.down",
				"DownloadActivityView.legend.peeraverage",
			};

		Legend.LegendListener legend_listener = 
			new Legend.LegendListener()
			{
				private int	hover_index = -1;
				
				public void 
				hoverChange(
					boolean 	entry, 
					int 		index ) 
				{
					if ( hover_index != -1 ){
						
						sources[hover_index].setHover( false );
					}
					
					if ( entry ){
						
						hover_index = index;
						
						sources[index].setHover( true );
					}
										
					f_mpg.refresh( true );
				}
				
				public void
				visibilityChange(
					boolean	visible,
					int		index )
				{
					sources[index].setVisible( visible );

					f_mpg.refresh( true );
				}
			};
			
		
		if ( !legend_at_bottom ){
				
			gridData = new GridData( GridData.FILL_VERTICAL );
			gridData.verticalAlignment = SWT.CENTER;
			
			Legend.createLegendComposite(panel, colors, color_configs, null, gridData, false, legend_listener );
		}

	    Composite gSpeed = new Composite(panel,SWT.NULL);
	    gridData = new GridData(GridData.FILL_BOTH);
	    gSpeed.setLayoutData(gridData);    
	    gSpeed.setLayout(new GridLayout());
	     
	    if ( legend_at_bottom ){
	    	
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			
			Legend.createLegendComposite(panel, colors, color_configs, null, gridData, true, legend_listener );

	    }
	    
	    Canvas speedCanvas = new Canvas(gSpeed,SWT.NO_BACKGROUND);
	    gridData = new GridData(GridData.FILL_BOTH);
	    speedCanvas.setLayoutData(gridData);

		mpg.initialize( speedCanvas, false );
	}
	
	private void
	refresh(
		boolean	force )
	{
		mpg.refresh( force );
	}
	
	public Composite 
	getComposite() 
	{
		return( panel );
	}
	
	private boolean comp_focused;
	private Object focus_pending_ds;

	private void
	setFocused( boolean foc )
	{
		if ( foc ){

			comp_focused = true;

			dataSourceChanged( focus_pending_ds );

		}else{

			focus_pending_ds = manager;

			dataSourceChanged( null );

			comp_focused = false;
		}
	}
	
	public void 
	dataSourceChanged(
		Object newDataSource ) 
	{
		if ( !comp_focused ){
			focus_pending_ds = newDataSource;
			return;
		}
		  
	  	DownloadManager old_manager = manager;
		if (newDataSource == null){
			manager = null;
		}else if (newDataSource instanceof Object[]){
			Object temp = ((Object[])newDataSource)[0];
			if ( temp instanceof DownloadManager ){
				manager = (DownloadManager)temp;
			}else if ( temp instanceof DiskManagerFileInfo){
				manager = ((DiskManagerFileInfo)temp).getDownloadManager();
			}else{
				return;
			}
		}else{
			if ( newDataSource instanceof DownloadManager ){
				manager = (DownloadManager)newDataSource;
			}else if ( newDataSource instanceof DiskManagerFileInfo){
				manager = ((DiskManagerFileInfo)newDataSource).getDownloadManager();
			}else{
				return;
			}
		}
		
		if ( old_manager == manager ){
			
			return;
		}
		
		if ( manager == null ){
			
			mpg.setActive( false );
			
			mpg.reset( new int[3][0] );
		
		}else{
		
			DownloadManagerStats stats = manager.getStats();
			
			stats.setRecentHistoryRetention( true );
			
			int[][] history = stats.getRecentHistory();
			
			mpg.reset( history );
			
			mpg.setActive( true );
		}
	}
	
	public void 
	delete()
	{
		 Utils.disposeComposite( panel );
		 
		 if ( mpg != null ){
		 
			 mpg.dispose();
		 }
	}
	
	public boolean 
	eventOccurred(
		UISWTViewEvent event )
	{
	    switch( event.getType()){
		    case UISWTViewEvent.TYPE_CREATE:{
		    	swtView = event.getView();
		    	
		    	swtView.setTitle(getFullTitle());
		    	
		    	swtView.setToolBarListener(this);
		    	
		    	if ( event instanceof UISWTViewEventImpl ){

		    		String parent = ((UISWTViewEventImpl)event).getParentID();

		    		legend_at_bottom = parent != null && parent.equals( UISWTInstance.VIEW_TORRENT_DETAILS );
		    	}
		    	 
		    	break;
		    }
		    case UISWTViewEvent.TYPE_DESTROY:{
		    	
		    	delete();
		    	
		    	break;
		    }
		    case UISWTViewEvent.TYPE_INITIALIZE:{
		    	
		    	initialize((Composite)event.getData());
		    	
		    	break;
		    }
		    case UISWTViewEvent.TYPE_REFRESH:{
		     
		    	refresh( false );
		    	
		        break;
		    }
		    case UISWTViewEvent.TYPE_LANGUAGEUPDATE:{
		    	Messages.updateLanguageForControl(getComposite());
		    	
		    	swtView.setTitle(getFullTitle());
		    	
		    	break;
		    }
		    case UISWTViewEvent.TYPE_DATASOURCE_CHANGED:{
		    	
		    	dataSourceChanged(event.getData());
		    	
		    	break;
		    }
	    	case UISWTViewEvent.TYPE_FOCUSGAINED:{
	    		
	    		String id = "DMDetails_DownloadGraph";

			    setFocused( true );	// do this here to pick up corrent manager before rest of code

	    		if ( manager != null ){

	    			if ( manager.getTorrent() != null ){

	    				id += "." + manager.getInternalName();

	    			}else{

	    				id += ":" + manager.getSize();
	    			}
	    		}

	    		SelectedContentManager.changeCurrentlySelectedContent(id, new SelectedContent[]{ new SelectedContent(manager)});

	    		refresh( true );
	    					    
			    break;
	    	}
		    case UISWTViewEvent.TYPE_FOCUSLOST:{
		    	
		    	setFocused( false );
		    	
		    	break;
		    }
	    }
	    
	    return( true );
	}
	
	public boolean 
	toolBarItemActivated(
		ToolBarItem 	item, 
		long 			activationType,
		Object 			datasource) 
	{
		return( ViewUtils.toolBarItemActivated( manager, item, activationType, datasource ));
	}

	public void 
	refreshToolBarItems(
		Map<String, Long> list) 
	{	
		ViewUtils.refreshToolBarItems(manager, list);
	}
	
	private abstract class
	ValueSourceImpl
		implements ValueSource
	{	
		private String			name;
		private int				index;
		private Color[]			colours;
		private boolean			is_up;
		private boolean			trimmable;
		
		private boolean			is_hover;
		private boolean			is_invisible;
		
		private
		ValueSourceImpl(
			String					_name,
			int						_index,
			Color[]					_colours,
			boolean					_is_up,
			boolean					_trimmable )
		{
			name			= _name;
			index			= _index;
			colours			= _colours;
			is_up			= _is_up;
			trimmable		= _trimmable;
		}
			
		public String
		getName()
		{
			return( name );
		}
		
		public Color 
		getLineColor() 
		{
			return( colours[index] );
		}
		
		public boolean
		isTrimmable()
		{
			return( trimmable );
		}
		
		private void
		setHover(
			boolean	h )
		{
			is_hover = h;
		}
		
		private void
		setVisible(
			boolean	visible )
		{
			is_invisible = !visible;
		}
		
		public int 
		getStyle() 
		{
			if ( is_invisible ){
				
				return( STYLE_INVISIBLE );
			}
			
			int	style = is_up?STYLE_UP:STYLE_DOWN;
			
			if ( is_hover ){
				
				style |= STYLE_BOLD;
			}
			
			return( style );
		}
	}
}
