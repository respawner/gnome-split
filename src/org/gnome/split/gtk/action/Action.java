/*
 * Action.java
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

import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToolButton;
import org.gnome.gtk.ToolItem;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;

/**
 * Abstract class to define a action triggered by a GTK+ widget.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class Action
{
    private GnomeSplit app;

    private String label;

    private String tooltip;

    private Stock stock;

    public Action(GnomeSplit app, String label, String tooltip, Stock stock) {
        this.app = app;
        this.label = label;
        this.tooltip = tooltip;
        this.stock = stock;
    }

    public Action(GnomeSplit app, Stock stock, String label) {
        this(app, label, null, stock);
    }

    public Action(GnomeSplit app, Stock stock) {
        this(app, null, null, stock);
    }

    public Action(GnomeSplit app, String label) {
        this(app, label, null, null);
    }

    public abstract void actionPerformed(ActionEvent event);

    public MenuItem createMenuItem() {
        MenuItem item = null;

        if ((stock != null) && (label != null)) {
            // Create a menu item with a label and an image
            final Image image = new Image(stock, IconSize.MENU);
            item = new ImageMenuItem(image, label);
        } else if (stock != null)
            // Create a menu item with an image
            item = new ImageMenuItem(stock);
        else if (label != null)
            // Create a menu item with a label
            item = new MenuItem(label);

        // Set menu item tooltip
        if (tooltip != null)
            item.setTooltipText(tooltip);

        item.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                ActionEvent event = new ActionEvent(source);
                actionPerformed(event);
            }
        });

        return item;
    }

    public ToolItem createToolItem() {
        // Cannot build a tool button without stock item
        if (stock == null)
            throw new NullPointerException(
                    "It is not possible to create a GtkToolButton without any GtkStock item.");

        // Create a tool item with a Stock
        ToolItem item = new ToolButton(stock);

        // Set a label to the item
        if (label != null)
            ((ToolButton) item).setLabel(label);

        // Set menu item tooltip
        if (tooltip != null)
            item.setTooltipText(tooltip);

        ((ToolButton) item).connect(new ToolButton.Clicked() {
            @Override
            public void onClicked(ToolButton source) {
                ActionEvent event = new ActionEvent(source);
                actionPerformed(event);
            }
        });

        return item;
    }

    protected GnomeSplit getApplication() {
        return app;
    }

    /**
     * Event generated when the associated action is triggered.
     * 
     * @author Guillaume Mazoyer
     */
    public class ActionEvent
    {
        private Widget item;

        public ActionEvent(Widget item) {
            this.item = item;
        }

        public Widget getWidget() {
            return item;
        }
    }
}