/*
 * GnomeSplit.java
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
package org.gnome.split.core.merger;

import static org.gnome.split.GnomeSplit.config;

import java.io.File;
import java.io.IOException;

import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.exception.MissingChunkException;
import org.gnome.split.core.io.GRandomAccessFile;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to merge files with the GNOME Split algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public final class GnomeSplit extends DefaultMergeEngine
{
    public GnomeSplit(File file, String filename) {
        super(file, filename);
    }

    @Override
    protected void loadHeaders() throws IOException {
        GRandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new GRandomAccessFile(file, "r");

            // Skip useless header
            access.skipBytes(5);

            // Read filename
            byte[] bytes = new byte[access.read()];
            access.read(bytes);
            access.skipBytes(50 - bytes.length);

            // Update the filename only if it is not specified by the user
            if (filename == null) {
                filename = file.getAbsolutePath().replace(file.getName(), "") + new String(bytes);
            }

            // Read if MD5 is used
            md5 = access.readBoolean();

            // Read file number
            parts = access.readInt();

            // Read file length
            fileLength = access.readLong();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                access.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getNextChunk(String part, int number) {
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
        return (part + current + ".gsp");
    }

    @Override
    public void merge() throws IOException, EngineException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        GRandomAccessFile out = null;
        File chunk = null;
        boolean run = true;
        boolean success = true;

        try {
            // Open the final file
            out = new GRandomAccessFile(filename, "rw");

            // Define the buffer size
            byte[] buffer;

            for (int i = 1; i <= parts; i++) {
                // Next chunk
                chunk = new File(this.getNextChunk(part, i));
                if (!chunk.exists()) {
                    // Check if the chunk really exists
                    throw new MissingChunkException();
                }

                // Open the chunk to read it
                GRandomAccessFile access = new GRandomAccessFile(chunk, "r");

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                long read = 0;
                long length = access.length();
                if (i == 1) {
                    // Skip headers if it is the first part
                    access.skipBytes(69);
                    read += 69;
                } else if (md5 && (i == parts)) {
                    // Skip the MD5 sum if it is the last part
                    length -= 32;
                }

                // Merge the file
                run = this.mergeChunk(out, access, read, length);

                // Reading stopped
                if (!run) {
                    return;
                }

                if (config.CHECK_FILE_HASH && md5 && (i == parts)) {
                    // Read the MD5 which was calculated during the split
                    buffer = new byte[32];
                    access.read(buffer);
                    md5sum = new String(buffer);

                    // Notify the view
                    this.fireMD5SumStarted();

                    // Calculate the MD5 of the new file
                    MD5Hasher hasher = new MD5Hasher();
                    String found = hasher.hashToString(new File(filename));

                    // MD5 are different
                    success = md5sum.equals(found);

                    // Notify the view again
                    this.fireMD5SumEnded();
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();
            }

            if (!success && md5) {
                // Notify the error. It's just a warning so we don't throw it.
                this.fireEngineError(new MD5Exception());
            } else if (success) {
                if (config.DELETE_PARTS && md5) {
                    // Delete all parts if and *only if* the MD5 sums are
                    // equals
                    for (String path : chunks) {
                        new File(path).delete();
                    }
                }

                // Notify the end
                this.fireEngineEnded();
            }
        } finally {
            try {
                // Close the final file
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
