/*
 * AssistantAction.java
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

import org.gnome.gtk.Stock;
import org.gnome.split.gtk.dialog.AssistantDialog;

/**
 * Allow the user to use the assistant.
 * 
 * @author Guillaume Mazoyer
 */
final class AssistantAction extends Action
{
    protected AssistantAction() {
        super("assistant-action", _("_Assistant"), _("Create a split or a merge."), Stock.ADD);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        new AssistantDialog().present();
    }
}
