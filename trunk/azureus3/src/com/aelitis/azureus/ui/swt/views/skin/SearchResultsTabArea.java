/**
 * Copyright (C) 2006 Aelitis, All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * AELITIS, SAS au capital de 63.529,40 euros
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 *
 */

package com.aelitis.azureus.ui.swt.views.skin;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;

import org.gudy.azureus2.core3.util.AERunnable;
import org.gudy.azureus2.core3.util.SystemTime;
import org.gudy.azureus2.core3.util.UrlUtils;
import org.gudy.azureus2.ui.swt.Utils;

import com.aelitis.azureus.core.messenger.config.PlatformConfigMessenger;
import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoListener;
import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
import com.aelitis.azureus.ui.skin.SkinConstants;
import com.aelitis.azureus.ui.swt.browser.OpenCloseSearchDetailsListener;
import com.aelitis.azureus.ui.swt.browser.listener.BrowserRpcBuddyListener;
import com.aelitis.azureus.ui.swt.browser.listener.MetaSearchListener;
import com.aelitis.azureus.ui.swt.skin.*;
import com.aelitis.azureus.util.Constants;
import com.aelitis.azureus.util.MapUtils;

/**
 * @author TuxPaper
 * @created Sep 30, 2006
 *
 */
public class SearchResultsTabArea
	extends SkinView
	implements ViewTitleInfo, OpenCloseSearchDetailsListener
{
	private SWTSkinObjectBrowser browserSkinObject;

	private SWTSkin skin;

	private String searchText;
	
	/* (non-Javadoc)
	 * @see com.aelitis.azureus.ui.swt.views.SkinView#showSupport(com.aelitis.azureus.ui.swt.skin.SWTSkinObject, java.lang.Object)
	 */
	public Object skinObjectInitialShow(SWTSkinObject skinObject, Object params) {
		skin = skinObject.getSkin();
		browserSkinObject = (SWTSkinObjectBrowser) skin.getSkinObject(
				SkinConstants.VIEWID_BROWSER_SEARCHRESULTS, skinObject);

		createBrowseArea(browserSkinObject);

/**
		final SWTSkinTabSet tabSetMain = skin.getTabSet(SkinConstants.TABSET_MAIN);
		if (tabSetMain != null) {
			final SWTSkinObjectTab tab = tabSetMain.getTab(SkinConstants.VIEWID_SEARCHRESULTS_TAB);
			if (tab != null) {
				SWTSkinObjectListener l = new SWTSkinObjectListener() {
					public Object eventOccured(SWTSkinObject skinObject, int eventType,
							Object params) {
						if (eventType == SWTSkinObjectListener.EVENT_SELECT) {
							tab.setVisible(tabSetMain.getActiveTab() == tab);
						}
						return null;
					}
				};
				tab.addListener(l);
			}
		}
**/
		
		if (browserSkinObject != null) {
  		Object o = skinObject.getData("CreationParams");
  		
  		anotherSearch((String) o);
		}
		
		return null;
	}

	/**
	 * 
	 */
	private void createBrowseArea(SWTSkinObjectBrowser browserSkinObject) {
		this.browserSkinObject = browserSkinObject;		
		browserSkinObject.getContext().addMessageListener(new MetaSearchListener(this));
	}


	public void restart() {
		if (browserSkinObject != null) {
			browserSkinObject.restart();
		}
	}

	public void openSearchResults(final Map params) {
		Utils.execSWTThread(new AERunnable() {

			public void runSupport() {
				SWTSkinObject soSearchResults = getSkinObject("searchresults-search-results");
				if (soSearchResults == null) {
					return;
				}

				Control controlTop = browserSkinObject.getControl();
				Control controlBottom = soSearchResults.getControl();
				Browser search = ((SWTSkinObjectBrowser) soSearchResults).getBrowser();
				String url = MapUtils.getMapString(params, "url",
						"http://google.com/search?q=" + Math.random());
				if (PlatformConfigMessenger.urlCanRPC(url)) {
					url = Constants.appendURLSuffix(url);
				}
				
				//Gudy, Not Tux, Listener Added
				String listenerAdded = (String) search.getData("g.nt.la");
				if(listenerAdded == null) {
					search.setData("g.nt.la","");
					search.addProgressListener(new ProgressListener() {
						public void changed(ProgressEvent event) {}
						
						public void completed(ProgressEvent event) {
							Browser search = (Browser) event.widget;
							String execAfterLoad = (String) search.getData("execAfterLoad");
							//Erase it, so that it's only used once after the page loads
							search.setData("execAfterLoad",null);
							if(execAfterLoad != null && ! execAfterLoad.equals("")) {
								//String execAfterLoadDisplay = execAfterLoad.replaceAll("'","\\\\'");
								//search.execute("alert('injecting script : " + execAfterLoadDisplay + "');");
								boolean result = search.execute(execAfterLoad);
								//System.out.println("Injection : " + execAfterLoad + " (" + result + ")");
							}

						}
					});
				}
				
				
				//Store the "css" match string in the search cdp browser object
				String execAfterLoad = MapUtils.getMapString(params, "execAfterLoad", null);
				search.setData("execAfterLoad",execAfterLoad);
				
				search.setUrl(url);

				FormData gd = (FormData) controlBottom.getLayoutData();
				gd.top = new FormAttachment(controlTop, 0);
				gd.height = SWT.DEFAULT;
				controlBottom.setLayoutData(gd);
				soSearchResults.setVisible(true);
				controlBottom.setVisible(true);
				search.setVisible(true);

				gd = (FormData) controlTop.getLayoutData();
				gd.bottom = null;
				gd.height = MapUtils.getMapInt(params, "top-height", 120);
				controlTop.setLayoutData(gd);

				controlTop.getParent().layout(true);
			}
		});
	}

	public void closeSearchResults(final Map params) {
		Utils.execSWTThread(new AERunnable() {

			public void runSupport() {
				SWTSkinObject soSearchResults = skin.getSkinObject("searchresults-search-results");
				if (soSearchResults == null) {
					return;
				}

				Control controlTop = browserSkinObject.getControl();
				Control controlBottom = soSearchResults.getControl();
				Browser search = ((SWTSkinObjectBrowser) soSearchResults).getBrowser();

				FormData gd = (FormData) controlBottom.getLayoutData();
				gd.top = null;
				gd.height = 0;
				controlBottom.setLayoutData(gd);
				soSearchResults.setVisible(false);

				gd = (FormData) controlTop.getLayoutData();
				gd.bottom = new FormAttachment(controlBottom, 0);
				gd.height = SWT.DEFAULT;
				controlTop.setLayoutData(gd);

				controlBottom.getParent().layout(true);
				search.setUrl("about:blank");
			}
		});
	}
	
	public void anotherSearch(String searchText) {
		this.searchText = searchText;
		String url = Constants.URL_PREFIX + Constants.URL_ADD_SEARCH
				+ UrlUtils.encode(searchText) + "&" + Constants.URL_SUFFIX + "&rand="
				+ SystemTime.getCurrentTime();

		if (System.getProperty("metasearch", "1").equals("1")) {
			url = Constants.URL_PREFIX + "xsearch?q=" + UrlUtils.encode(searchText)
					+ "&" + Constants.URL_SUFFIX + "&rand=" + SystemTime.getCurrentTime();
		}

		closeSearchResults(null);
		browserSkinObject.setURL(url);
		ViewTitleInfoManager.refreshTitleInfo(this);
	}

	// @see com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo#getTitleInfoObjectProperty(int)
	public Object getTitleInfoObjectProperty(int propertyID) {
		if (propertyID == TITLE_SKINVIEW) {
			return this;
		}
		return null;
	}

	// @see com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo#getTitleInfoStringProperty(int)
	public String getTitleInfoStringProperty(int propertyID) {
		if (propertyID == TITLE_TEXT) {
			return "Search: " + searchText;
		}
		return null;
	}
}
