/*
 * SplitWidget.java
 * 
 * Copyright (c) 2009-2011 Guillaume Mazoyer
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
package org.gnome.split.gtk.widget;

import java.io.File;

import org.gnome.gtk.Entry;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileChooserWidget;
import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.model.SplitModel;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.widget.base.AlgorithmsBox;
import org.gnome.split.gtk.widget.base.UnitsBox;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A widget derived from {@link Frame} to allow the user to start a split.
 * 
 * @author Guillaume Mazoyer
 */
public class SplitWidget extends VBox implements ActionWidget, SplitModel
{
    /**
     * The GNOME Split application.
     */
    private GnomeSplit app;

    /**
     * Define if the widget is visible or not.
     */
    private boolean visible;

    /**
     * File to split.
     */
    private Entry fileEntry;

    /**
     * Select a file using a {@link FileChooserWidget}.
     */
    private FileChooserButton fileChooser;

    /**
     * Name of the files to create.
     */
    private Entry destinationEntry;

    /**
     * Directory where the chunks will be created.
     */
    private FileChooserButton dirChooser;

    /**
     * Size value of the chunks.
     */
    private SpinButton sizeButton;

    /**
     * Size units of the chunks.
     */
    private UnitsBox sizeUnits;

    /**
     * Algorithm to use to split the file.
     */
    private AlgorithmsBox algoList;

    public SplitWidget(final GnomeSplit app) {
        super(false, 12);

        // Save instance
        this.app = app;

        // At first, it is invisible
        visible = false;

        // Set the border of the widget
        this.setBorderWidth(5);

        final HBox firstRow = new HBox(false, 5);
        this.packStart(firstRow, true, true, 0);

        // Pack all Labels in the same box
        final VBox labelColumn = new VBox(false, 5);
        firstRow.packStart(labelColumn, false, false, 0);

        final Label fileLabel = new Label(_("File:"));
        fileLabel.setAlignment(0.0f, 0.5f);
        labelColumn.packStart(fileLabel, true, true, 0);

        final Label destinationLabel = new Label(_("Destination:"));
        destinationLabel.setAlignment(0.0f, 0.5f);
        labelColumn.packStart(destinationLabel, true, true, 0);

        // Pack all Entrys in the same box
        final VBox entryColumn = new VBox(false, 5);
        firstRow.packStart(entryColumn, true, true, 0);

        fileEntry = new Entry();
        entryColumn.packStart(fileEntry, true, true, 0);

        destinationEntry = new Entry();
        entryColumn.packStart(destinationEntry, true, true, 0);

        // Pack all choosers in the same box
        final VBox chooserColumn = new VBox(false, 5);
        firstRow.packStart(chooserColumn, false, false, 0);

        fileChooser = new FileChooserButton(_("Choose a file."), FileChooserAction.OPEN);
        fileChooser.setCurrentFolder(app.getConfig().SPLIT_DIRECTORY);
        fileChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                setFile(source.getFilename());
            }
        });
        chooserColumn.packStart(fileChooser, true, true, 0);

        dirChooser = new FileChooserButton(_("Choose a directory."), FileChooserAction.SELECT_FOLDER);
        dirChooser.setCurrentFolder(app.getConfig().SPLIT_DIRECTORY);
        chooserColumn.packStart(dirChooser, true, true, 0);

        final HBox thirdRow = new HBox(false, 5);
        this.packStart(thirdRow, true, true, 0);

        // Pack size related widgets
        final VBox firstColumn = new VBox(false, 5);
        thirdRow.packStart(firstColumn, true, true, 0);

        final Label sizeLabel = new Label(_("Split in:"));
        firstColumn.packStart(sizeLabel, true, true, 0);

        final HBox splitSize = new HBox(false, 3);
        firstColumn.packStart(splitSize, true, true, 0);

        sizeButton = new SpinButton(1, 4096, 1);
        splitSize.packStart(sizeButton, true, true, 0);

        sizeUnits = new UnitsBox(app);
        splitSize.packStart(sizeUnits, true, true, 0);

        // Pack algorithm related widgets
        final VBox secondColumn = new VBox(false, 5);
        thirdRow.packStart(secondColumn, true, true, 0);

        final Label algoLabel = new Label(_("Algorithm:"));
        secondColumn.packStart(algoLabel, true, true, 0);

        algoList = new AlgorithmsBox(app);
        secondColumn.packStart(algoList, true, true, 0);

        // Make the sizes of size and algorithm boxes equal
        final SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        group.add(firstColumn);
        group.add(secondColumn);
    }

    /**
     * Set the size and update the widget.
     */
    private void setSize(double size) {
        sizeButton.setValue(size);
    }

    /**
     * Set the unit and update the widget.
     */
    private void setUnit(int unit) {
        sizeUnits.setActive(unit);
    }

    /**
     * Set the algorithm and update the widget.
     */
    private void setAlgorithm(int algorithm) {
        algoList.setActive(algorithm);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean setting) {
        visible = setting;

        if (visible) {
            this.showAll();
        } else {
            this.hide();
        }
    }

    @Override
    public boolean isFullyFilled() {
        return (!fileEntry.getText().isEmpty() && !destinationEntry.getText().isEmpty());
    }

    @Override
    public long checkFreeSpace() {
        File file = new File(fileEntry.getText());
        long free = new File(dirChooser.getCurrentFolder()).getFreeSpace();

        return ((free >= file.length()) ? -1 : free);
    }

    @Override
    public byte checkFileSystemPermission() {
        // Check permission
        boolean read = new File(fileEntry.getText()).canRead();
        boolean write = new File(dirChooser.getCurrentFolder()).canWrite();

        // Consider we can do everything
        byte result = 0;

        if (!read) {
            // Can't read
            result += 1;
        }

        if (!write) {
            // Can't write
            result += 2;
        }

        return result;
    }

    @Override
    public void disable() {
        // Get all widgets
        Widget[] widgets = this.getChildren();
        for (Widget widget : widgets) {
            // Make them non-sensitive
            widget.setSensitive(false);
        }
    }

    @Override
    public void enable() {
        // Get all widgets
        Widget[] widgets = this.getChildren();
        for (Widget widget : widgets) {
            // Make them sensitive
            widget.setSensitive(true);
        }
    }

    @Override
    public void reset() {
        fileEntry.setText("");
        fileChooser.setCurrentFolder(app.getConfig().SPLIT_DIRECTORY);
        destinationEntry.setText("");
        dirChooser.setCurrentFolder(app.getConfig().SPLIT_DIRECTORY);
        sizeButton.setValue(1);
        sizeUnits.setActive(0);
        algoList.setActive(app.getConfig().DEFAULT_ALGORITHM);
        app.getMainWindow().getProgressBar().reset();
    }

    @Override
    public void updateProgress(double progress, String text, boolean sure) {
        if (!sure) {
            // Unknown progress
            app.getMainWindow().getProgressBar().pulse();
        } else {
            // Known progress
            app.getMainWindow().getProgressBar().setFraction(progress);

            if (!text.isEmpty()) {
                app.getMainWindow().getProgressBar().setText(text);
            }
        }
    }

    @Override
    public File getFile() {
        return new File(fileEntry.getText());
    }

    @Override
    public File getDirectory() {
        return new File(dirChooser.getCurrentFolder());
    }

    @Override
    public String getDestination() {
        StringBuilder builder = new StringBuilder();

        // Add directory + name
        builder.append(dirChooser.getCurrentFolder());
        builder.append(File.separator);
        builder.append(destinationEntry.getText());

        return builder.toString();
    }

    @Override
    public long getMaxSize() {
        int unit = sizeUnits.getActive();
        long input = this.getFile().length();
        long result;

        if (unit == 0) {
            // Split by chunk number
            result = (long) (input / sizeButton.getValue());
        } else {
            // Split by size
            unit -= 2;
            double multiplicator = (unit == -1) ? 1 : SizeUnit.values()[unit];
            result = (long) (sizeButton.getValue() * multiplicator);
        }

        // If size is not valid (bigger than the length of the file to split),
        // just return -1, else return the result
        return ((result >= input) ? -1 : result);
    }

    @Override
    public int getAlgorithm() {
        return algoList.getActive();
    }

    /**
     * Update the {@link ProgressBar} to display the right
     * <code>progress</code>.
     */
    public void setProgress(double progress) {
        app.getMainWindow().getProgressBar().setFraction(progress);
    }

    /**
     * Set the file to split and update the widget.
     */
    public void setFile(String filename) {
        int separator = filename.lastIndexOf(File.separator) + 1;
        String file = filename.substring(separator, filename.length());

        // Update entries
        fileChooser.setFilename(filename);
        fileEntry.setText(filename);
        destinationEntry.setText(file);
    }

    /**
     * Update the widget with the given values.
     */
    public void setSplit(String filename, double size, int unit, int algorithm) {
        this.setFile(filename);
        this.setSize(size);
        this.setUnit(unit);
        this.setAlgorithm(algorithm);
    }
}
