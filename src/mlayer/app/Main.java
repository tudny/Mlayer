package mlayer.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mlayer.app.classes.Song;
import mlayer.app.controllers.MainController;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/MainWindow.fxml"));
        AnchorPane root = loader.load();

        ((MainController)loader.getController()).setAll(root);

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        Song song = new Song(file);

        MainController cnt = loader.getController();
        Image art = song.getCoverArt();
        cnt.setImage(art);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, art.getWidth(), art.getHeight()));
        primaryStage.show();

        MediaPlayer mp = new MediaPlayer(song.getMedia());

        mp.play();

        System.out.println(song.getEstheticTitle());

        Text lenTxt = cnt.getText();
        lenTxt.toFront();

        mp.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
            String toPass;
            Long sec = (long)(mp.getCurrentTime().toSeconds());
            Long min = sec / 60; sec %= 60;
            String secc = sec.toString(); if(secc.length() == 1) secc = "0" + secc;
            String minn = min.toString(); if(minn.length() == 1) minn = "0" + minn;
            toPass = "" + minn + ":" + secc + "";

            lenTxt.setText(toPass);
        }));

        Button bt = cnt.getButton();
        bt.setOnAction(event -> {

            if(mp.getStatus() == MediaPlayer.Status.PLAYING){
                mp.pause();
            }else{
                mp.play();
            }
        });


    }

    public static void main(String[] args) {
        launch(args);
    }
}
