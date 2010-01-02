/*
 * StartAction.java
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

import java.io.File;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.split.core.Engine;
import org.gnome.split.core.merger.DefaultMergeEngine;
import org.gnome.split.core.splitter.GnomeSplit;
import org.gnome.split.core.splitter.Simple;
import org.gnome.split.core.splitter.Xtremsplit;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.gtk.dialog.ErrorDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.SplitWidget;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to start a split/merge.
 * 
 * @author Guillaume Mazoyer
 */
public final class StartAction extends Action
{
    public StartAction(final org.gnome.split.GnomeSplit app) {
        super(app, Stock.MEDIA_PLAY, _("Start"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Get current instance and current widget
        org.gnome.split.GnomeSplit app = this.getApplication();
        Engine engine = app.getEngineListener().getEngine();

        // Action already started and paused
        if ((engine != null) && engine.paused()) {
            // Then resume it
            engine.resume();
            app.getActionManager().setRunningState();
        } else {
            // Get current widget
            ActionWidget widget = app.getMainWindow().getActionWidget();
            Engine run = null;

            // A split is performed
            if (widget instanceof SplitWidget) {
                if (!widget.isFullyFilled()) {
                    // The user did not fill all the fields
                    Dialog dialog = new ErrorDialog(app.getMainWindow(), _("Incompleted fields."),
                            _("You must fill all fields to start a split."));
                    dialog.run();
                    dialog.hide();
                    return;
                }

                // Widget related info
                SplitWidget split = (SplitWidget) widget;
                int algorithm = split.getAlgorithm();

                // Split related info
                File file = new File(split.getFilename());
                long size = split.getMaxSize();
                String dest = split.getDestination();

                switch (algorithm) {
                case Algorithm.XTREMSPLIT:
                    // Create the new process and start it
                    run = new Xtremsplit(app, file, size, dest);
                    new Thread(run, "Split - " + file.getName()).start();
                    break;
                case Algorithm.GNOME_SPLIT:
                    // Create the new process and start it
                    run = new GnomeSplit(app, file, size, dest);
                    new Thread(run, "Split - " + file.getName()).start();
                    break;
                case Algorithm.SIMPLE:
                    // Create the new process and start it
                    run = new Simple(app, file, size, dest);
                    new Thread(run, "Split - " + file.getName()).start();
                    break;
                default:
                    break;
                }
            } else if (widget instanceof MergeWidget) {
                if (!widget.isFullyFilled()) {
                    // The user did not fill all the fields
                    Dialog dialog = new ErrorDialog(app.getMainWindow(), _("Incompleted fields."),
                            _("You must fill all fields to start a merge."));
                    dialog.run();
                    dialog.hide();
                    return;
                }

                // Widget related info
                MergeWidget merge = (MergeWidget) widget;

                // Split related info
                File file = merge.getFirstFile();
                String dest = merge.getDestination();

                // Create the new process and start it
                run = DefaultMergeEngine.getInstance(app, file, dest);
                new Thread(run, "Merge - " + file.getName()).start();
            }

            // Update the interface state
            if (run != null) {
                app.getEngineListener().setEngine(run);
                app.getActionManager().setRunningState();
            }
        }
    }
}
