/*
 * File    : Tracker.java
 * Created : 30 nov. 2003
 * By      : Olivier
 *
 * Azureus - a Java Bittorrent client
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
 */
 
package org.gudy.azureus2.plugins.tracker;

/**
 * @author Olivier
 *
 */

import org.gudy.azureus2.plugins.tracker.web.*;

public interface Tracker {
    /**
     * adds an identificator to the tracker
     * @param indentificator the Identificator
     */
    public void addTrackerIdentificator(Identificator identificator);
    
    /**
     * adds a stats listener to the tracker
     * @param listener
     */
    public void addTrackerStatsListener(StatsListener listener);
    
    public void
    addPageGenerator(
    	TrackerWebPageGenerator	generator );
}
