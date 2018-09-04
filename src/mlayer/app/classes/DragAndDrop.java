package mlayer.app.classes;

import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;
import java.util.List;

public class DragAndDrop {
    public static List<File> mouseDragDropped(final DragEvent event, TableView<Song> tableView){
        Dragboard dragboard = event.getDragboard();
        List<File> listOfFiles = dragboard.getFiles();
        for(File file : listOfFiles){
            if (!file.isFile() || !file.getName().toLowerCase().endsWith(".mp3")) {
                listOfFiles.remove(file);
            }
        }

        tableView.setStyle(null);

        return listOfFiles;
    }

    public static void mouseDragOver(DragEvent event, TableView<Song> tableView) {
        Dragboard dragboard = event.getDragboard();
        int mp3FileCount = 0;

        for(File file : dragboard.getFiles()){
            if(file.isFile() && file.getName().toLowerCase().endsWith(".mp3")){
                mp3FileCount++;
            }
        }

        if(mp3FileCount > 0){
            tableView.setStyle("-fx-border-color: lime;");
            event.acceptTransferModes(TransferMode.COPY);
        } else {
            tableView.setStyle("-fx-border-color: red;");
        }
    }

    public static void mouseDragExited(DragEvent event, TableView<Song> tableView) {
        tableView.setStyle(null);
    }
}
