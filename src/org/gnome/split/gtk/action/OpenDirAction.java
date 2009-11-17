/*
 * OpenDirAction.java
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

import java.net.URI;
import java.net.URISyntaxException;

import org.gnome.gtk.Gtk;
import org.gnome.gtk.Stock;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.action.properties.SplitProperties;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to open the directory containing files.
 * 
 * @author Guillaume Mazoyer
 */
public final class OpenDirAction extends Action
{
    public OpenDirAction(final GnomeSplit app) {
        super(app, Stock.OPEN, _("_Open folder"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Get the current action properties.
        SplitProperties properties = (SplitProperties) this.getApplication()
                .getMainWindow()
                .getAction()
                .getActionProperties();

        if (properties != null) {
            try {
                // Open the folder with the right program.
                Gtk.showURI(new URI("file://" + properties.directory));
            } catch (URISyntaxException e) {
                // Should not be used.
                e.printStackTrace();
            }
        }
    }
}
