/*
 * OpenDirAction.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.openURI;
import static org.gnome.split.GnomeSplit.ui;

import org.gnome.gtk.Stock;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.SplitWidget;

/**
 * Action to open the directory containing files.
 * 
 * @author Guillaume Mazoyer
 */
final class OpenDirAction extends Action
{
    protected OpenDirAction() {
        super("opendir-action", _("_Open folder"), _("Open the folder where the action is performed."),
                Stock.OPEN);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        ActionWidget widget = ui.getActionWidget();
        String directory = "file://";

        if (widget instanceof SplitWidget) {
            directory += ((SplitWidget) widget).getDirectory().getAbsolutePath();
        } else {
            directory += ((MergeWidget) widget).getDirectory().getAbsolutePath();
        }

        // Open the directory with the default program
        openURI(directory);
    }
}
