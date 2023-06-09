package com.rhmsoft.omnia.parser;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggPageHeader;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Read Ogg Opus File Tag and Encoding information
 */
class OpusFileReader extends AudioFileReader {

    private OpusInfoReader ir;
    private OpusVorbisTagReader vtr;

    public OpusFileReader() {
        ir = new OpusInfoReader();
        vtr = new OpusVorbisTagReader();
    }

    protected GenericAudioHeader getEncodingInfo(FileChannel raf) throws CannotReadException, IOException {
        return ir.read(raf);
    }

    protected Tag getTag(FileChannel raf) throws CannotReadException, IOException {
        return vtr.read(raf);
    }

    /**
     * Return count Ogg Page header, count starts from zero
     * <p>
     * count=0; should return PageHeader that contains Vorbis Identification Header
     * count=1; should return Pageheader that contains VorbisComment and possibly SetupHeader
     * count>=2; should return PageHeader containing remaining VorbisComment,SetupHeader and/or Audio
     *
     * @param raf
     * @param count
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public OggPageHeader readOggPageHeader(RandomAccessFile raf, int count) throws CannotReadException, IOException {
        OggPageHeader pageHeader = OggPageHeader.read(raf);
        while (count > 0) {
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            pageHeader = OggPageHeader.read(raf);
            count--;
        }
        return pageHeader;
    }

    /**
     * Summarize all the ogg headers in a file
     * <p>
     * A useful utility function
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void summarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        while (raf.getFilePointer() < raf.length()) {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }

    /**
     * Summarizes the first five pages, normally all we are interested in
     *
     * @param oggFile
     * @throws CannotReadException
     * @throws IOException
     */
    public void shortSummarizeOggPageHeaders(File oggFile) throws CannotReadException, IOException {
        RandomAccessFile raf = new RandomAccessFile(oggFile, "r");

        int i = 0;
        while (raf.getFilePointer() < raf.length()) {
            System.out.println("pageHeader starts at absolute file position:" + raf.getFilePointer());
            OggPageHeader pageHeader = OggPageHeader.read(raf);
            System.out.println("pageHeader finishes at absolute file position:" + raf.getFilePointer());
            System.out.println(pageHeader + "\n");
            raf.seek(raf.getFilePointer() + pageHeader.getPageLength());
            i++;
            if (i >= 5) {
                break;
            }
        }
        System.out.println("Raf File Pointer at:" + raf.getFilePointer() + "File Size is:" + raf.length());
        raf.close();
    }
}

