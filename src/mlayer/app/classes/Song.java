package mlayer.app.classes;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.media.Media;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class Song{

    private static final String unknown = "Unknown";
    private Mp3File mp3File = null;
    private ID3v1 id3v1 = null;
    private ID3v2 id3v2 = null;
    private Media media = null;
    private File file = null;

    public Song(File file){
        try{
            mp3File = new Mp3File(file.getPath());
            media = new Media(file.toURI().toASCIIString());
            this.file = file;

            if(mp3File.hasId3v1Tag()){
                id3v1 = mp3File.getId3v1Tag();
            }

            if(mp3File.hasId3v2Tag()){
                id3v2 = mp3File.getId3v2Tag();
            }

        } catch (Exception e){
            System.out.println(e.getMessage() + " " + e.getCause());
        }
    }

    public Media getMedia(){
        return media;
    }

    public Image getCoverArt(){
        try {
            if (id3v2 != null) {
                byte[] coverArtData = id3v2.getAlbumImage();
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(coverArtData));
                return SwingFXUtils.toFXImage(img, null);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getEsteticTitle(){
        String esteticTitle;

        String title = unknown;
        String artist = unknown;
        if(id3v1 != null && id3v1.getTitle() != null){
            title = id3v1.getTitle();
        }
        if(id3v2 != null && id3v2.getTitle() != null){
            title = id3v2.getTitle();
        }
        esteticTitle = title;

        if(id3v1 != null && id3v1.getArtist() != null){
            artist = id3v1.getArtist();
        }
        if(id3v2 != null && id3v2.getArtist() != null){
            artist = id3v2.getArtist();
        }

        if(!artist.equals(unknown)){
            esteticTitle = esteticTitle + " - " + artist;
        }

        return esteticTitle;
    }

    public Mp3File getMp3File(){
        return mp3File;
    }
}
