/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphaël Slinckx <raphael@slinckx.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.rhmsoft.omnia.parser;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.mp4.Mp4InfoReader;
import org.jaudiotagger.audio.mp4.Mp4TagReader;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Mp4 File Reader
 *
 * <p>This can read files containing either the .mp4 or .m4a suffixes
 */
class Mp4FileReader extends AudioFileReader
{
    private Mp4InfoReader ir = new Mp4InfoReader();
    private Mp4TagReader tr = new Mp4TagReader();

    protected GenericAudioHeader getEncodingInfo(FileChannel raf) throws CannotReadException, IOException
    {
        return ir.read(raf);
    }

    protected Tag getTag(FileChannel raf) throws CannotReadException, IOException
    {
        return tr.read(raf);
    }
}
