/*
 * SelectSplitAction.java
 * 
 * Copyright (c) 2009 Guillaume Mazoyer
 * 
 * This file is part of GNOME Split.
 * 
 * GNOME Split is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GNOME Split is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GNOME Split.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gnome.split.gtk.action;

import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.MainWindow;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to display the split view.
 * 
 * @author Guillaume Mazoyer
 */
public final class SelectSplitAction extends ToggleAction
{
    public SelectSplitAction(final GnomeSplit app) {
        super(app, _("Split"), (app.getConfig().DEFAULT_VIEW == 0));
    }

    @Override
    public void actionPerformed(ToggleActionEvent event, boolean active) {
        this.setActive(active, false);
        if (active) {
            MainWindow window = this.getApplication().getMainWindow();

            if (window != null) {
                // Update the interface
                this.getApplication().getMainWindow().switchView();
            }
        }
    }
}
