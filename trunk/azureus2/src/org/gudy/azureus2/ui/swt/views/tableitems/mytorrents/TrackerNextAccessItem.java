/*
 * File    : TrackerStatusItem.java
 * Created : 24 nov. 2003
 * By      : Olivier
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
 * 8 Allee Lenotre, La Grille Royale, 78600 Le Mesnil le Roi, France.
 */
 
package org.gudy.azureus2.ui.swt.views.tableitems.mytorrents;

import java.util.HashMap;

import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.tracker.client.TRTrackerScraperResponse;
import org.gudy.azureus2.core3.tracker.client.impl.bt.TRTrackerBTScraperResponseImpl;
import org.gudy.azureus2.core3.util.TimeFormatter;
import org.gudy.azureus2.plugins.ui.tables.*;
import org.gudy.azureus2.ui.swt.mainwindow.Colors;
import org.gudy.azureus2.ui.swt.views.table.TableCellCore;
import org.gudy.azureus2.ui.swt.views.table.utils.CoreTableColumn;

/**
 * @author Olivier
 *
 */
public class TrackerNextAccessItem
       extends CoreTableColumn 
       implements TableCellRefreshListener, TableCellDisposeListener,
                  TableCellToolTipListener
{
	HashMap map = new HashMap();
	
  public TrackerNextAccessItem(String sTableID) {
    super("trackernextaccess", ALIGN_TRAIL, POSITION_INVISIBLE, 70, sTableID);
    setRefreshInterval(INTERVAL_LIVE);
  }

  public void refresh(TableCell cell) {
    DownloadManager dm = (DownloadManager)cell.getDataSource();
    if (cell.isValid() && map.containsKey(dm)) {
    	long lNextUpdate = ((Long)map.get(dm)).longValue();
    	if (System.currentTimeMillis() < lNextUpdate)
    		return;
    }
    long value = (dm == null) ? 0 : dm.getTrackerTime();
    
    if (value < -1)
      value = -1;

    long lNextUpdate = System.currentTimeMillis()
				+ (((value > 60) ? (value % 60) : 1) * 1000);
		map.put(dm, new Long(lNextUpdate));

    if (!cell.setSortValue(value) && cell.isValid())
      return;

    String sText = TimeFormatter.formatColon(value);
    
    if (value > 60)
    	sText = "< " + sText;
    
  	TrackerCellUtils.updateColor(cell, dm);
    cell.setText(sText);
  }

	public void cellHover(TableCell cell) {
		DownloadManager dm = (DownloadManager) cell.getDataSource();
		cell.setToolTip(TrackerCellUtils.getTooltipText(cell, dm));
	}

	public void cellHoverComplete(TableCell cell) {
		cell.setToolTip(null);
	}

	public void dispose(TableCell cell) {
		map.remove(cell.getDataSource());
	}
}
