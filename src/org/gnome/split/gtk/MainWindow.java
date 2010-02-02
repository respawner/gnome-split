/*
 * MainWindow.java
 * 
 * Copyright (c) 2009-2010 Guillaume Mazoyer
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
package org.gnome.split.gtk;

import org.gnome.gdk.Event;
import org.gnome.gtk.Frame;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Toolbar;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowPosition;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.AboutSoftDialog;
import org.gnome.split.gtk.dialog.PreferencesDialog;
import org.gnome.split.gtk.dialog.PropertiesDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.AreaStatusIcon;
import org.gnome.split.gtk.widget.MainToolbar;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.SelectView;
import org.gnome.split.gtk.widget.SplitWidget;
import org.gnome.split.gtk.widget.StatusWidget;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Main window of the interface. It will be used to do everything GNOME Split
 * can thanks to the {@link MenuBar}, the {@link Toolbar} or the
 * {@link ActionWidget}.
 * 
 * @author Guillaume Mazoyer
 */
public class MainWindow extends Window implements Window.DeleteEvent
{
    /**
     * Current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Icon in the notification area associated to this window.
     */
    private AreaStatusIcon statusIcon;

    /**
     * Views selector.
     */
    private SelectView views;

    /**
     * Main container of the {@link Window}.
     */
    private VBox mainContainer;

    /**
     * Widget to display when the split view is selected.
     */
    private SplitWidget split;

    /**
     * Widget to display when the merge view is selected.
     */
    private MergeWidget merge;

    /**
     * Widget derived from {@link Frame} to display the status.
     */
    private StatusWidget status;

    /**
     * Properties dialog associated to this window to display detailed
     * information about the current action.
     */
    private PropertiesDialog properties;

    /**
     * Classic preferences dialog associated to this window.
     */
    private PreferencesDialog preferences;

    /**
     * Classic about dialog associated to this window.
     */
    private AboutSoftDialog about;

    /**
     * Build the main window of GNOME Split.
     */
    public MainWindow(final GnomeSplit app) {
        super();

        // Save program instance
        this.app = app;

        // Place the window in the middle of the screen
        this.setPosition(WindowPosition.CENTER);

        // Create the notification zone icon
        this.statusIcon = new AreaStatusIcon(app);
        this.statusIcon.setVisible(app.getConfig().SHOW_STATUS_ICON);

        // Main container
        this.mainContainer = new VBox(false, 0);
        this.mainContainer.showAll();
        this.add(this.mainContainer);

        // Add the menu bar
        final MenuBar menubar = this.createMenu();
        menubar.showAll();
        this.mainContainer.packStart(menubar, false, false, 0);

        // Add the tool bar
        final MainToolbar toolbar = new MainToolbar(app);
        toolbar.showAll();
        this.mainContainer.packStart(toolbar, false, false, 0);

        // Create the two main widgets
        this.split = new SplitWidget(app);
        this.merge = new MergeWidget(app);

        // Make sure they have the same size
        final SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        group.add(this.split);
        group.add(this.merge);

        // Add the views selector
        this.views = new SelectView(app);
        this.views.showAll();
        this.mainContainer.packStart(views, false, false, 0);

        // Add the main widgets
        this.mainContainer.packStart(this.split);
        this.mainContainer.packStart(this.merge);

        // Show the right one
        switch (app.getConfig().DEFAULT_VIEW) {
        case 0:
            // Add the split widget
            this.split.setVisible(true);
            this.split.showAll();
            break;
        case 1:
            // Add the merge widget
            this.merge.setVisible(true);
            this.merge.showAll();
            break;
        }

        // Add status widget
        this.status = new StatusWidget();
        this.status.showAll();
        this.mainContainer.packStart(this.status, false, false, 0);

        // Set the state of the interface
        this.app.getActionManager().setReadyState();

        // Connect delete event handler
        this.connect((Window.DeleteEvent) this);

        // Restore the window size
        if (app.getConfig().CUSTOM_WINDOW_SIZE) {
            this.setDefaultSize(app.getConfig().WINDOW_SIZE_X, app.getConfig().WINDOW_SIZE_Y);
        }
    }

    /**
     * Create the menubar to use.
     */
    private MenuBar createMenu() {
        final MenuBar menubar = new MenuBar();
        final ActionManager actions = app.getActionManager();

        // File menu item
        final MenuItem fileItem = new MenuItem(_("_File"));
        final Menu fileMenu = new Menu();

        // Create a special case for the assistants
        final Menu assistants = new Menu();

        // Add the assistants to the menu
        assistants.append(actions.getAction(ActionId.SPLIT_ASSISTANT).createMenuItem());
        assistants.append(actions.getAction(ActionId.MERGE_ASSISTANT).createMenuItem());

        // Pack it in a menu item
        final MenuItem assistant = actions.getAction(ActionId.ASSISTANT).createMenuItem();
        assistant.setSubmenu(assistants);

        fileItem.setSubmenu(fileMenu);
        fileMenu.append(assistant);
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.OPEN_DIR).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.PROPERTIES).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.START).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.PAUSE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.CANCEL).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.DELETE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.CLEAR).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.EXIT).createMenuItem());
        menubar.append(fileItem);

        // Edit menu item
        final MenuItem editItem = new MenuItem(_("_Edit"));
        final Menu editMenu = new Menu();

        editItem.setSubmenu(editMenu);
        editMenu.append(actions.getAction(ActionId.PREFERENCES).createMenuItem());
        menubar.append(editItem);

        // Help menu item
        final MenuItem helpItem = new MenuItem(_("_Help"));
        final Menu helpMenu = new Menu();

        helpItem.setSubmenu(helpMenu);
        helpMenu.append(actions.getAction(ActionId.HELP).createMenuItem());
        helpMenu.append(actions.getAction(ActionId.ABOUT).createMenuItem());
        menubar.append(helpItem);

        return menubar;
    }

    /**
     * Setup all the dialogs attached to the main window.
     */
    public void setupDialogs() {
        // Create the properties dialog
        this.properties = new PropertiesDialog(app);

        // Create classic preferences dialog
        this.preferences = new PreferencesDialog(app);

        // Create classic about dialog
        this.about = new AboutSoftDialog();
    }

    /**
     * Switch between widget to display.
     */
    public void switchView() {
        if (split.isVisible()) {
            // Hide the split widget
            split.hide();
            split.setVisible(false);

            // Show the merge widget
            merge.showAll();
            merge.setVisible(true);
        } else {
            // Hide the merge widget
            merge.hide();
            merge.setVisible(false);

            // Show the split widget
            split.showAll();
            split.setVisible(true);
        }
    }

    /**
     * Get the select view widget.
     */
    public SelectView getViewSwitcher() {
        return views;
    }

    /**
     * Get the current displayed widget.
     */
    public ActionWidget getActionWidget() {
        if (split.isVisible()) {
            return split;
        } else {
            return merge;
        }
    }

    /**
     * Get the widget that displays the status.
     */
    public StatusWidget getStatusWidget() {
        return status;
    }

    /**
     * Get the properties dialog.
     */
    public PropertiesDialog getPropertiesDialog() {
        return properties;
    }

    /**
     * Get the preferences dialog.
     */
    public PreferencesDialog getPreferencesDialog() {
        return preferences;
    }

    /**
     * Get the about dialog.
     */
    public AboutSoftDialog getAboutDialog() {
        return about;
    }

    /**
     * Get the notification area icon associated to this window.
     */
    public AreaStatusIcon getAreaStatusIcon() {
        return statusIcon;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        app.quit();
        return false;
    }
}
