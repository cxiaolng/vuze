/*
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

package org.gudy.azureus2.ui.swt.views.configsections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;

import org.gudy.azureus2.plugins.ui.config.ConfigSection;
import org.gudy.azureus2.plugins.ui.config.ConfigSectionSWT;

import org.gudy.azureus2.ui.swt.Messages;
import org.gudy.azureus2.ui.swt.config.*;

/** General Queueing options
 * @author TuxPaper
 * @created Jan 12, 2004
 */
public class ConfigSectionQueue implements ConfigSectionSWT {
  public String configSectionGetParentSection() {
    return ConfigSection.SECTION_ROOT;
  }

  /**
   * Create the "Queue" Tab in the Configuration view
   */
  public Composite configSectionCreate(Composite parent) {
    GridData gridData;
    GridLayout layout;
    Label label;

    // main tab set up

    Composite gMainTab = new Composite(parent, SWT.NULL);

    gridData = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
    gMainTab.setLayoutData(gridData);
    layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginHeight = 0;
    gMainTab.setLayout(layout);

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxdownloads"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gMainTab, "max downloads").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.maxactivetorrents"); //$NON-NLS-1$
    gridData = new GridData();
    gridData.widthHint = 40;
    new IntParameter(gMainTab, "max active torrents").setLayoutData(gridData); //$NON-NLS-1$

    label = new Label(gMainTab, SWT.NULL);
    Messages.setLanguageText(label, "ConfigView.label.minSpeedForActiveDL"); //$NON-NLS-1$
    final String activeDLLabels[] = new String[54];
    final int activeDLValues[] = new int[54];
    int pos = 0;
    for (int i = 0; i < 1024; i += 256) {
      activeDLLabels[pos] = "" + i + " B/s";
      activeDLValues[pos] = i;
      pos++;
    }
    for (int i = 1; pos < activeDLLabels.length; i++) {
      activeDLLabels[pos] = "" + i + " KB/s";
      activeDLValues[pos] = i * 1024;
      pos++;
    }
    new IntListParameter(gMainTab, "StartStopManager_iMinSpeedForActiveDL", activeDLLabels, activeDLValues);

    gridData = new GridData();
    gridData.horizontalSpan = 2;
    new BooleanParameter(gMainTab, "Alert on close", true,
                         "ConfigView.label.showpopuponclose").setLayoutData(gridData);

    gridData = new GridData();
    gridData.horizontalSpan = 2;
    new BooleanParameter(gMainTab, "StartStopManager_bDebugLog",
                         "ConfigView.label.queue.debuglog").setLayoutData(gridData);

    return gMainTab;
  }

  public String configSectionGetName() {
    return "queue";
  }

  public void configSectionSave() {
  }

  public void configSectionDelete() {
  }
}
