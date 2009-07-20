/*
 * DbusInhibit.java
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
package org.gnome.split.dbus;

import org.freedesktop.PowerManagement.Inhibit;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.gnome.split.config.Constants;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Try to inhibit and uninhibit the computer hibernation using dbus and GNOME
 * Power Manager.
 * 
 * @author Guillaume Mazoyer
 */
public class DbusInhibit
{
    /**
     * Connection to dbus.
     */
    private DBusConnection connection;

    /**
     * Inhibit dbus object.
     */
    private Inhibit inhibit;

    /**
     * Inhibit cookie.
     */
    private UInt32 cookie;

    /**
     * Inhibit the computer hibernation.
     */
    public void inhibit() {
        try {
            // Get dbus connection
            connection = DBusConnection.getConnection(DBusConnection.SESSION);

            // Get inhibit object
            inhibit = connection.getRemoteObject("org.freedesktop.PowerManagement",
                    "/org/freedesktop/PowerManagement/Inhibit", Inhibit.class);

            // Inhibit hibernation and get inhibit cookie
            cookie = inhibit.Inhibit(Constants.PROGRAM_NAME, _("gSplit activity"));
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uninhibit the computer hibernation.
     */
    public void unInhibit() {
        // Uninhibit hibernation
        inhibit.UnInhibit(cookie);

        // Close dbus connection
        connection.disconnect();
    }
}