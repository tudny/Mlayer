package mlayer.app.classes;

import javafx.collections.ObservableList;

import java.io.*;
import java.util.Vector;

public class ConfigurationFile {

    private Vector<Song> songs;
    private Integer selectedSong;
    private Integer loopType;

    public ConfigurationFile(){
        songs = new Vector<>();
    }

    public boolean load(){
        try (BufferedReader br = new BufferedReader(new FileReader("mlayer-config.mconf"))) {
            String read;
            Integer notFiles = 0;

            if(!br.readLine().startsWith("#Mlayer config"))
                return false;

            while((read = br.readLine()) != null){
                if(read.startsWith("#Songs")){
                    Integer id = -1, howManySub = 0;
                    while(!(read = br.readLine()).startsWith("#end")){
                        id++;
                        File file = new File(read);
                        if(file.isFile()) {
                            Song songToAdd = new Song(file);
                            songs.add(songToAdd);
                        } else {
                            if(id.compareTo(selectedSong) < 1) howManySub++;
                            notFiles++;
                        }
                    }
                    selectedSong -= howManySub;
                }

                if(read.equals("#Selected")){
                    while(!(read = br.readLine()).startsWith("#end")){
                        selectedSong = Integer.parseInt(read);
                    }
                }

                if(read.equals("#Loop_type")){
                    while(!((read = br.readLine())).startsWith("#end")){
                        loopType = Integer.parseInt(read);
                    }
                }
            }

        }catch (Exception e){
            System.out.println("Config file error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Integer getSelectedSong(){
        return selectedSong;
    }

    public Integer getLoopType(){
        return loopType;
    }

    public Vector<Song> getSongs(){
        return songs;
    }

    public void setSelectedSong(Integer newSelectedSong){
        selectedSong = newSelectedSong;
    }

    public void setLoopType(Integer newLoopType){
        loopType = newLoopType;
    }

    public void setSongs(ObservableList<Song> oList){
        songs.clear();
        songs.addAll(oList);
    }

    public void generateNewIni(){
        if(!songs.isEmpty()) songs.clear();
        selectedSong = -1;
        loopType = 1;
        generateIniFile();
    }

    public void generateIniFile(){
        try {
            PrintWriter writer = new PrintWriter("mlayer-config.mconf", "UTF-8");

            writer.println("#Mlayer config");

            writer.println("#Selected");
            writer.println(selectedSong);
            writer.println("#end");

            writer.println("#Songs");
            for(Song song : songs){
                String path = song.getFile().getPath();
                writer.println(path);
            }
            writer.println("#end");

            writer.println("#Loop_type");
            writer.println(loopType);
            writer.println("#end");

            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
