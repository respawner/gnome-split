/*
 * Generic.java
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
package org.gnome.split.core.splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gnome.split.core.io.GRandomAccessFile;

/**
 * Algorithm to split files with an algorithm which does not use any headers
 * in the files.
 * 
 * @author Guillaume Mazoyer
 */
public final class Generic extends DefaultSplitEngine
{
    private int parts;

    public Generic(File file, long size, String destination) {
        super(file, size, destination);
        parts = (int) Math.ceil((float) file.length() / (float) size);
    }

    @Override
    protected String getChunkName(String destination, int number) {
        // Get the current extension
        String current;
        if (number >= 100) {
            current = String.valueOf(number);
        } else if (number >= 10) {
            current = "0" + number;
        } else {
            current = "00" + number;
        }

        // Finally
        return (destination + "." + current);
    }

    @Override
    public void split() throws IOException, FileNotFoundException {
        GRandomAccessFile toSplit = null;
        boolean run = true;
        try {
            // Open a new file
            toSplit = new GRandomAccessFile(file, "r");

            for (int i = 1; i <= parts; i++) {
                GRandomAccessFile access = null;
                File chunk = null;
                try {
                    // Open the part
                    chunk = new File(this.getChunkName(destination, i));
                    access = new GRandomAccessFile(chunk, "rw");

                    // Notify the view from a new part
                    chunks.add(chunk.getAbsolutePath());
                    this.fireEnginePartCreated(chunk.getName());

                    if (i == parts) {
                        // Update size to stop the split correctly
                        size = file.length() - total;
                    }

                    // Write the chunk
                    run = this.writeChunk(toSplit, access);

                    // Writing stopped
                    if (!run) {
                        return;
                    }

                    // Notify the view from a written part
                    this.fireEnginePartWritten(chunk.getName());
                } finally {
                    try {
                        // Close the part file
                        access.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Notify the end of the split
            this.fireEngineEnded();
        } finally {
            try {
                // Close the part file
                toSplit.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
