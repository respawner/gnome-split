/*
 * Action.java
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

import org.freedesktop.icons.Icon;
import org.gnome.gtk.Stock;

/**
 * Abstract class to define a action triggered by a GTK+ widget.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class Action extends org.gnome.gtk.Action implements org.gnome.gtk.Action.Activate
{
    /**
     * Create a new action using a name and a {@link Stock} icon.
     */
    protected Action(String name, Stock stock) {
        super(name, stock);

        this.connect((Action.Activate) this);
    }

    /**
     * Create a new action using a name and a label.
     */
    protected Action(String name, String label) {
        super(name, label);

        this.connect((Action.Activate) this);
    }

    /**
     * Create a new action using a name, a label, a tooltip and a
     * {@link Stock} icon.
     */
    protected Action(String name, String label, String tooltip, Stock stock) {
        super(name, label, tooltip, stock);

        this.connect((Action.Activate) this);
    }

    /**
     * Create a new action using a name, a label, a tooltip and a {@link Icon}
     * icon.
     */
    protected Action(String name, String label, String tooltip, Icon icon) {
        super(name, label, tooltip, icon);

        this.connect((Action.Activate) this);
    }
}
