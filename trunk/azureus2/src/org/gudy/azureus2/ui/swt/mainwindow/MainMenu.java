/*
 * Created on 4 mai 2004
 * Created by Olivier Chalouhi
 * 
 * Copyright (C) 2004 Aelitis SARL, All rights Reserved
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
 * AELITIS, SARL au capital de 30,000 euros,
 * 8 Alle Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
package org.gudy.azureus2.ui.swt.mainwindow;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.gudy.azureus2.core3.internat.MessageText;
import org.gudy.azureus2.core3.logging.LGLogger;
import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.Constants;
import org.gudy.azureus2.core3.util.Debug;
import org.gudy.azureus2.core3.util.SystemProperties;
import org.gudy.azureus2.plugins.PluginView;
import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.Tab;
import org.gudy.azureus2.ui.swt.config.wizard.ConfigureWizard;
import org.gudy.azureus2.ui.swt.donations.DonationWindow2;
import org.gudy.azureus2.ui.swt.exporttorrent.wizard.ExportTorrentWizard;
import org.gudy.azureus2.ui.swt.help.AboutWindow;
import org.gudy.azureus2.ui.swt.help.HealthHelpWindow;
import org.gudy.azureus2.ui.swt.importtorrent.wizard.ImportTorrentWizard;
import org.gudy.azureus2.ui.swt.maketorrent.NewTorrentWizard;
import org.gudy.azureus2.ui.swt.sharing.ShareUtils;
import org.gudy.azureus2.ui.swt.update.UpdateMonitor;

/**
 * @author Olivier Chalouhi
 *
 */
public class MainMenu {

  private Display display;
  private MainWindow mainWindow;
    
  private Menu menuBar;
  
  private MenuItem menu_tools;
  private Menu toolsMenu;
  
  private MenuItem menu_plugin;
  private Menu pluginMenu;
  
  
  public MainMenu(MainWindow mainWindow) {
    this.mainWindow = mainWindow;
    this.display = SWTThread.getInstance().getDisplay();
  }

  /**
   * @param locales
   */
  public void buildMenu(Locale[] locales) {
    try {
      
      //The Main Menu
      menuBar = new Menu(mainWindow.getShell(), SWT.BAR);
      mainWindow.getShell().setMenuBar(menuBar);
      
      
      //The File Menu
      MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
      Messages.setLanguageText(fileItem, "MainWindow.menu.file"); //$NON-NLS-1$
      Menu fileMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      fileItem.setMenu(fileMenu);
      
      MenuItem file_new = new MenuItem(fileMenu, SWT.CASCADE);
      Messages.setLanguageText(file_new, "MainWindow.menu.file.open"); //$NON-NLS-1$
      
      MenuItem file_share= new MenuItem(fileMenu, SWT.CASCADE);
      Messages.setLanguageText(file_share, "MainWindow.menu.file.share"); //$NON-NLS-1$
      
      MenuItem file_create = new MenuItem(fileMenu, SWT.NULL);
      Messages.setLanguageText(file_create, "MainWindow.menu.file.create"); //$NON-NLS-1$
  
      new MenuItem(fileMenu, SWT.SEPARATOR);
  
      MenuItem file_export = new MenuItem(fileMenu, SWT.NULL);
      Messages.setLanguageText(file_export, "MainWindow.menu.file.export"); //$NON-NLS-1$
  
      MenuItem file_import = new MenuItem(fileMenu, SWT.NULL);
      Messages.setLanguageText(file_import, "MainWindow.menu.file.import"); //$NON-NLS-1$
       
      //No need for exit on OS X
      if(! Constants.isOSX) {

        
        new MenuItem(fileMenu, SWT.SEPARATOR);
  
        MenuItem file_exit = new MenuItem(fileMenu, SWT.NULL);
        Messages.setLanguageText(file_exit, "MainWindow.menu.file.exit"); //$NON-NLS-1$
        
        file_exit.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            mainWindow.dispose();
          }
        });
      }
      	// file->open submenus
      
      Menu newMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      file_new.setMenu(newMenu);
  
      MenuItem file_new_torrent = new MenuItem(newMenu, SWT.NULL);
      Messages.setLanguageText(file_new_torrent, "MainWindow.menu.file.open.torrent"); //$NON-NLS-1$
      file_new_torrent.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openTorrent();
        }
      });
      
/* Not working.. Hide for release
      MenuItem file_new_torrentwindow = new MenuItem(newMenu, SWT.NULL);
      file_new_torrentwindow.setText(MessageText.getString("MainWindow.menu.file.open.torrent") + " (Experimental)");
      file_new_torrentwindow.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openTorrentWindow();
        }
      });
*/
  
      MenuItem file_new_torrent_no_default = new MenuItem(newMenu, SWT.NULL);
      Messages.setLanguageText(file_new_torrent_no_default, "MainWindow.menu.file.open.torrentnodefault"); //$NON-NLS-1$
      file_new_torrent_no_default.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openTorrentNoDefaultSave(false);
        }      
      });
  
      MenuItem file_new_torrent_for_seeding = new MenuItem(newMenu, SWT.NULL);
      Messages.setLanguageText(file_new_torrent_for_seeding, "MainWindow.menu.file.open.torrentforseeding"); //$NON-NLS-1$
      file_new_torrent_for_seeding.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openTorrentNoDefaultSave(true);
        }      
      });
  
      MenuItem file_new_url = new MenuItem(newMenu,SWT.NULL);
      Messages.setLanguageText(file_new_url, "MainWindow.menu.file.open.url"); //$NON-NLS-1$
      file_new_url.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openUrl(mainWindow.getAzureusCore());
        }
      });
      MenuItem file_new_folder = new MenuItem(newMenu, SWT.NULL);
      Messages.setLanguageText(file_new_folder, "MainWindow.menu.file.folder"); //$NON-NLS-1$
      file_new_folder.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          TorrentOpener.openDirectory();
        }
      });
  
      	// file->share submenus
      
      Menu shareMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      file_share.setMenu(shareMenu);
  
      MenuItem file_share_file = new MenuItem(shareMenu, SWT.NULL);
      Messages.setLanguageText(file_share_file, "MainWindow.menu.file.share.file");
      file_share_file.addListener(SWT.Selection, new Listener() {
      	public void handleEvent(Event e) {
      		ShareUtils.shareFile( mainWindow.getAzureusCore(),mainWindow.getShell() );
      	}
      });
      
      MenuItem file_share_dir = new MenuItem(shareMenu, SWT.NULL);
      Messages.setLanguageText(file_share_dir, "MainWindow.menu.file.share.dir");
      file_share_dir.addListener(SWT.Selection, new Listener() {
      	public void handleEvent(Event e) {
      		ShareUtils.shareDir( mainWindow.getAzureusCore(), mainWindow.getShell() );
      	}
      });
      
      MenuItem file_share_dircontents = new MenuItem(shareMenu, SWT.NULL);
      Messages.setLanguageText(file_share_dircontents, "MainWindow.menu.file.share.dircontents");
      file_share_dircontents.addListener(SWT.Selection, new Listener() {
      	public void handleEvent(Event e) {
      		ShareUtils.shareDirContents( mainWindow.getAzureusCore(),mainWindow.getShell(), false );
      	}
      });
      MenuItem file_share_dircontents_rec = new MenuItem(shareMenu, SWT.NULL);
      Messages.setLanguageText(file_share_dircontents_rec, "MainWindow.menu.file.share.dircontentsrecursive");
      file_share_dircontents_rec.addListener(SWT.Selection, new Listener() {
      	public void handleEvent(Event e) {
      		ShareUtils.shareDirContents( mainWindow.getAzureusCore(),mainWindow.getShell(), true );
      	}
      });
         	// file->create
      
      file_create.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          new NewTorrentWizard(mainWindow.getAzureusCore(), display);
        }
      });
      
      file_export.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          new ExportTorrentWizard(mainWindow.getAzureusCore(), display);
        }
      });
  
      file_import.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          new ImportTorrentWizard(mainWindow.getAzureusCore(),display);
        }
      });
  
     
      	// ******** The Download Menu
      
      MenuItem downloadItem = new MenuItem(menuBar, SWT.CASCADE);
      Messages.setLanguageText(downloadItem, "MainWindow.menu.download"); //$NON-NLS-1$
      Menu downloadMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      downloadItem.setMenu(downloadMenu);

  
      
      // new MenuItem(fileMenu,SWT.SEPARATOR);
      
      final MenuItem itemStartAll = new MenuItem(downloadMenu,SWT.NULL);
      Messages.setLanguageText(itemStartAll,"MainWindow.menu.download.startalldownloads");
      
      final MenuItem itemStopAll = new MenuItem(downloadMenu,SWT.NULL); 
      Messages.setLanguageText(itemStopAll,"MainWindow.menu.download.stopalldownloads");

      final MenuItem itemPause = new MenuItem(downloadMenu,SWT.NULL);
      Messages.setLanguageText(itemPause,"MainWindow.menu.download.pausedownloads");
      
      final MenuItem itemResume = new MenuItem(downloadMenu,SWT.NULL); 
      Messages.setLanguageText(itemResume,"MainWindow.menu.download.resumedownloads");

      itemStartAll.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event arg0) {
        	mainWindow.getGlobalManager().startAllDownloads();
        }
      });
      
      itemStopAll.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event arg0) {
        	mainWindow.getGlobalManager().stopAllDownloads();
        }
      });

      itemPause.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event arg0) 
        {
        	mainWindow.getGlobalManager().pauseDownloads();
        }
      });
      
      itemResume.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event arg0) 
        {
          mainWindow.getGlobalManager().resumeDownloads();
        }
      });
      
      downloadMenu.addMenuListener(
          	new MenuListener()
    		{
          		public void
    			menuShown(
    				MenuEvent	menu )
          		{
          		  itemPause.setEnabled( mainWindow.getGlobalManager().canPauseDownloads() );
         			          			
          		  itemResume.setEnabled( mainWindow.getGlobalManager().canResumeDownloads() );
          		}
          		
        		public void
    			menuHidden(
    				MenuEvent	menu )
          		{
          			
          		}
    		});
      
      	// ******** The View Menu
      
      MenuItem viewItem = new MenuItem(menuBar, SWT.CASCADE);
      Messages.setLanguageText(viewItem, "MainWindow.menu.view"); //$NON-NLS-1$
      Menu viewMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      viewItem.setMenu(viewMenu);
  
      MenuItem view_torrents = new MenuItem(viewMenu, SWT.NULL);
      Messages.setLanguageText(view_torrents, "MainWindow.menu.view.mytorrents"); //$NON-NLS-1$
      view_torrents.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
    	mainWindow.showMyTorrents();
        }
      });
  
    MenuItem view_tracker = new MenuItem(viewMenu, SWT.NULL);
    Messages.setLanguageText(view_tracker, "MainWindow.menu.view.mytracker"); //$NON-NLS-1$
    view_tracker.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        mainWindow.showMyTracker();
      }
    });
    
    MenuItem view_shares = new MenuItem(viewMenu, SWT.NULL);
    Messages.setLanguageText(view_shares, "MainWindow.menu.view.myshares"); //$NON-NLS-1$
    view_shares.addListener(SWT.Selection, new Listener() {
    	public void handleEvent(Event e) {
        mainWindow.showMyShares();
    	}
    });

  

      new MenuItem(viewMenu, SWT.SEPARATOR);
  
      MenuItem view_closeDetails = new MenuItem(viewMenu, SWT.NULL);
      Messages.setLanguageText(view_closeDetails, "MainWindow.menu.closealldetails"); //$NON-NLS-1$
      view_closeDetails.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          Tab.closeAllDetails();
        }
      });
  
      addCloseDownloadBarsToMenu(viewMenu);
      
      
      //the Tools menu
      menu_tools = new MenuItem(menuBar,SWT.CASCADE);
      Messages.setLanguageText(menu_tools, "MainWindow.menu.tools"); //$NON-NLS-1$
      toolsMenu = new Menu(mainWindow.getShell(),SWT.DROP_DOWN);
      menu_tools.setMenu(toolsMenu);
      
      MenuItem view_console = new MenuItem(toolsMenu, SWT.NULL);
      Messages.setLanguageText(view_console, "MainWindow.menu.view.console"); //$NON-NLS-1$
      view_console.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          mainWindow.showConsole();                    
        }
      });
  
      MenuItem view_stats = new MenuItem(toolsMenu, SWT.NULL);
      Messages.setLanguageText(view_stats, "MainWindow.menu.view.stats"); //$NON-NLS-1$
      view_stats.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          mainWindow.showStats();
        }
      });
      
      
      new MenuItem(toolsMenu, SWT.SEPARATOR);
      
      MenuItem file_configure = new MenuItem(toolsMenu, SWT.NULL);
      Messages.setLanguageText(file_configure, "MainWindow.menu.file.configure"); //$NON-NLS-1$
      file_configure.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          new ConfigureWizard(mainWindow.getAzureusCore(), display);
        }
      });
      
      
      //No need for configuration on OS X
      if(! Constants.isOSX) {                
        MenuItem view_config = new MenuItem(toolsMenu, SWT.NULL);
        Messages.setLanguageText(view_config, "MainWindow.menu.view.configuration"); //$NON-NLS-1$
        view_config.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            mainWindow.showConfig();
          }
        });
      }
      
      
      //the Plugins menu
      menu_plugin = new MenuItem(menuBar, SWT.CASCADE);
      Messages.setLanguageText(menu_plugin, "MainWindow.menu.view.plugins"); //$NON-NLS-1$
      pluginMenu = new Menu(mainWindow.getShell(),SWT.DROP_DOWN);
      menu_plugin.setEnabled(false);
      menu_plugin.setMenu(pluginMenu);
      
      
      //The Help Menu
      MenuItem helpItem = new MenuItem(menuBar, SWT.CASCADE);
      Messages.setLanguageText(helpItem, "MainWindow.menu.help"); //$NON-NLS-1$
      Menu helpMenu = new Menu(mainWindow.getShell(), SWT.DROP_DOWN);
      helpItem.setMenu(helpMenu);
  
      MenuItem help_about = new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_about, "MainWindow.menu.help.about"); //$NON-NLS-1$
      help_about.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          AboutWindow.show(display);
        }
      });
      
      if ( !SystemProperties.isJavaWebStartInstance()){
        
        MenuItem help_checkupdate = new MenuItem(helpMenu, SWT.NULL);
        Messages.setLanguageText(help_checkupdate, "MainWindow.menu.help.checkupdate"); //$NON-NLS-1$
        help_checkupdate.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {          
            UpdateMonitor.getSingleton( mainWindow.getAzureusCore()).performCheck();
          }
        });
      }
      
      
      new MenuItem(helpMenu,SWT.SEPARATOR);
      
      MenuItem help_health = new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_health, "MyTorrentsView.menu.health");
      help_health.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          HealthHelpWindow.show( display );
        }
      });
      
      
      MenuItem help_faq = new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_faq, "MainWindow.menu.help.faq"); //$NON-NLS-1$
      help_faq.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          String faqString = "http://azureus.sourceforge.net/wiki/";
          Program.launch(faqString);
        }
      });
      
      new MenuItem(helpMenu,SWT.SEPARATOR);
      
      
      MenuItem help_new = new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_new, "MainWindow.menu.help.whatsnew"); //$NON-NLS-1$
      help_new.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          Program.launch("http://azureus.sourceforge.net/changelog.php?version=" + Constants.AZUREUS_VERSION);
        }
      });
      
      
      MenuItem help_plugin= new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_plugin, "MainWindow.menu.help.plugins"); //$NON-NLS-1$
      help_plugin.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            String pluginString = "http://azureus.sourceforge.net/plugin_list.php";
            Program.launch(pluginString);
          }
        });
    
  
      new MenuItem(helpMenu,SWT.SEPARATOR);
      
      MenuItem help_donate = new MenuItem(helpMenu, SWT.NULL);
      Messages.setLanguageText(help_donate, "MainWindow.menu.help.donate"); //$NON-NLS-1$
      help_donate.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e) {
          new DonationWindow2(display).show();
          //String donationString = "https://www.paypal.com/xclick/business=olivier%40gudy.org&item_name=Azureus&no_note=1&tax=0&currency_code=EUR";
          //Program.launch(donationString);
        }
      });
    } catch (Exception e) {
      LGLogger.log(LGLogger.ERROR, "Error while creating menu items");
      Debug.printStackTrace( e );
    }
  }

  public void addCloseDownloadBarsToMenu(Menu menu) {
    MenuItem view_closeAll = new MenuItem(menu, SWT.NULL);
    Messages.setLanguageText(view_closeAll, "MainWindow.menu.closealldownloadbars"); //$NON-NLS-1$
    view_closeAll.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        mainWindow.closeDownloadBars();
      }
    });
  }

  
  public void updateMenuText(Object menu) {
    if (menu == null)
      return;
    if (menu instanceof Menu) {
      MenuItem[] menus = ((Menu) menu).getItems();
      for (int i = 0; i < menus.length; i++) {
        updateMenuText(menus[i]);
      }
    }
    else if (menu instanceof MenuItem) {
      MenuItem item = (MenuItem) menu;
      if (item.getData() != null) {
        if (item.getData() instanceof String) {
          item.setText(MessageText.getString((String) item.getData()));
          updateMenuText(item.getMenu());
        }
      }
    }
  }
  
  
  public void refreshLanguage() {
    if (display == null || display.isDisposed())
      return;

    display.asyncExec(new AERunnable() {
      public void runSupport() {
        updateMenuText(menuBar);
        mainWindow.setSelectedLanguageItem(); 
      }
    });
  }
  
  public void addPluginView(final PluginView view) {
    display.asyncExec(new AERunnable() {
      public void runSupport() {
        MenuItem item = new MenuItem(pluginMenu,SWT.NULL);
        item.setText(view.getPluginViewName());
        item.addListener(SWT.Selection,new Listener() {
          public void handleEvent(Event e) {
            mainWindow.openPluginView(view);
          }
        });
        menu_plugin.setEnabled(true);
      }
    }); 
  }
}


