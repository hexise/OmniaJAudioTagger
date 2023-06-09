package com.rhmsoft.omnia.parser;

import org.jaudiotagger.StandardCharsets;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.ogg.OggVorbisTagReader;
import org.jaudiotagger.audio.ogg.util.OggPageHeader;
import org.jaudiotagger.audio.opus.OpusHeader;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentReader;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

class OpusVorbisTagReader extends OggVorbisTagReader {

    private VorbisCommentReader tagReader = new VorbisCommentReader();

    /**
     * Read the Logical VorbisComment Tag from the file
     *
     * <p>Read the CommenyTag, within an OggVorbis file the VorbisCommentTag is mandatory
     *
     * @param raf
     * @return
     * @throws CannotReadException
     * @throws IOException
     */
    public Tag read(FileChannel raf) throws CannotReadException, IOException {
        logger.config("Starting to read ogg vorbis tag from file:");
        byte[] rawVorbisCommentData = readRawPacketData(raf);

        //Begin tag reading
        VorbisCommentTag tag = tagReader.read(rawVorbisCommentData, false);
        logger.fine("CompletedReadCommentTag");
        return tag;
    }

    /**
     * Retrieve the raw VorbisComment packet data, does not include the OggVorbis header
     *
     * @param raf
     * @return
     * @throws CannotReadException if unable to find vorbiscomment header
     * @throws IOException
     */
    public byte[] readRawPacketData(FileChannel channel) throws CannotReadException, IOException {
        logger.fine("Read 1st page");
        ChannelRandomAccessAdapter raf = new ChannelRandomAccessAdapter(channel);
        //1st page = codec infos
        OggPageHeader pageHeader = OggPageHeader.read(raf);
        //Skip over data to end of page header 1
        raf.seek(raf.getFilePointer() + pageHeader.getPageLength());

        logger.fine("Read 2nd page");
        //2nd page = comment, may extend to additional pages or not , may also have setup header
        pageHeader = OggPageHeader.read(raf);

        //Now at start of packets on page 2 , check this is the OpusTags comment header
        byte[] b = raf.read(OpusHeader.TAGS_CAPTURE_PATTERN_LENGTH);
        if (!isVorbisCommentHeader(b)) {
            throw new CannotReadException("Cannot find comment block (no vorbiscomment header)");
        }

        //Convert the comment raw data which maybe over many pages back into raw packet
        return convertToVorbisCommentPacket(pageHeader, raf);
    }

    protected byte[] convertToVorbisCommentPacket(OggPageHeader startPage, ChannelRandomAccessAdapter raf) throws IOException, CannotReadException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // read the rest of the first page
        byte[] packet = raf.read(startPage.getPacketList().get(0).getLength() - OpusHeader.TAGS_CAPTURE_PATTERN_LENGTH);
        baos.write(packet);

        if (startPage.getPacketList().size() > 1 || !startPage.isLastPacketIncomplete()) {
            return baos.toByteArray();
        }

        //The VorbisComment can extend to the next page, so carry on reading pages until we get to the end of comment
        while (true) {
            logger.config("Reading comment page");
            OggPageHeader nextPageHeader = OggPageHeader.read(raf);
            packet = raf.read(nextPageHeader.getPacketList().get(0).getLength());

            //Because there is at least one other packet (SetupHeaderPacket) this means the Comment Packet has finished
            //on this page so that's all we need and we can return
            if (nextPageHeader.getPacketList().size() > 1) {
                logger.config("Comments finish on Page because there is another packet on this page");
                return baos.toByteArray();
            }

            //There is only the VorbisComment packet on page if it has completed on this page we can return
            if (!nextPageHeader.isLastPacketIncomplete()) {
                logger.config("Comments finish on Page because this packet is complete");
                return baos.toByteArray();
            }
        }
    }

    @Override
    public boolean isVorbisCommentHeader(byte[] headerData) {
        String opusTags = new String(headerData, OpusHeader.TAGS_CAPTURE_PATTERN_POS, OpusHeader.TAGS_CAPTURE_PATTERN_LENGTH, StandardCharsets.ISO_8859_1);
        return opusTags.equals(OpusHeader.TAGS_CAPTURE_PATTERN);
    }
}
