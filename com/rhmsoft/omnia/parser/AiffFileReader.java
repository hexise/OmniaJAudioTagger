package com.rhmsoft.omnia.parser;

import com.rhmsoft.omnia.model.IFileEntry;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.tag.Tag;

import java.io.IOException;

/**
 * Reads Audio and Metadata information contained in Aiff file.
 */
class AiffFileReader extends AudioFileReader2 {
    private AiffInfoReader ir = new AiffInfoReader();
    private AiffTagReader im = new AiffTagReader();

    @Override
    protected GenericAudioHeader getEncodingInfo(IFileEntry file) throws CannotReadException, IOException {
        return ir.read(file);
    }

    @Override
    protected Tag getTag(IFileEntry file) throws CannotReadException, IOException {
        return im.read(file);
    }
}
