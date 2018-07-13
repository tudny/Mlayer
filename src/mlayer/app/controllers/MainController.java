package mlayer.app.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import mlayer.app.Main;
import mlayer.app.classes.Song;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Optional;


public class MainController {

    private AnchorPane rootPane;
    private Stage myStage;
    private ObservableList<Song> songOList;
    private MediaPlayer player;
    private ChangeListener<Duration> updateOfSongBar = new ChangeListener<Duration>() {
        @Override
        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
            if(player == null) return;
            double current = player.getCurrentTime().toSeconds();
            double fullTime = player.getMedia().getDuration().toSeconds();
            timeBar.setProgress(current / fullTime);
        }
    };
    private Integer MAX_TITLE_SIZE = 35;

    @FXML
    private MenuItem loadMenu;

    @FXML
    private MenuItem closeMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private MenuItem aboutMenu;

    @FXML
    private Text titleText;

    @FXML
    private ImageView coverArtImageView;

    @FXML
    private ProgressBar timeBar;

    @FXML
    private TableView<Song> songsList;

    @FXML
    private TableColumn<Song, String> nrColumn;

    @FXML
    private TableColumn<Song, String> nameColumn;

    @FXML
    private TableColumn<Song, String> artistColumn;

    @FXML
    private TableColumn<Song, String> albumColumn;

    @FXML
    private Button muteButton;

    @FXML
    private Button prevButton;

    @FXML
    private Button skipPrevButton;

    @FXML
    private Button playButton;

    @FXML
    private Button skipNextButton;

    @FXML
    private Button nextButton;

    public MainController(){
        System.out.println("Controller created");
    }

    @FXML
    public void initialize(){
        System.out.println("Initializing started...");
        System.out.println("...initializing done!");
    }

    public void setAll(AnchorPane root){
        System.out.println("Setting things up...");
        rootPane = root;
        myStage = (Stage) root.getScene().getWindow();

        nrColumn.setCellValueFactory(cellData -> cellData.getValue().getNrProperty());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().getArtistProperty());
        albumColumn.setCellValueFactory(cellData -> cellData.getValue().getAlbumProperty());

        myStage.setOnCloseRequest(event -> {
            if(!closeWindow()){
                event.consume();
            }
        });

        songOList = FXCollections.observableArrayList();

        System.out.println("...setting done!");
    }

    boolean closeWindow(){
        Alert closingAlert = new Alert(Alert.AlertType.CONFIRMATION);
        //closingAlert.initStyle(StageStyle.UTILITY);
        closingAlert.setTitle("Closing program");
        closingAlert.setHeaderText("You are about to close the program");
        closingAlert.setContentText("Are you sure?");

        Stage alertStage = (Stage)closingAlert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Main.class.getResource("img/warning-icon.png").toString()));

        Optional<ButtonType> result = closingAlert.showAndWait();

        if(result.isPresent() && result.get().equals(ButtonType.OK)){
            myStage.close();
            return true;
        } else {
            return false;
        }
    }

    public void test(){
        /*player = new MediaPlayer(songOList.get(0).getMedia());
        player.currentTimeProperty().addListener(updateOfSongBar);
        player.play();*/
        //createIniFile();
        setNewSongToPlay(songOList.get(0));
    }

    public void createIniFile(){
        try {
            PrintWriter writer = new PrintWriter("mlayer-conf.ini", "UTF-8");
            writer.println("<Mlayer configuration file>");
            for(int i = 0; i < songOList.size(); i++){
                String path = songOList.get(i).getMedia().getSource();
                writer.println(path);
            }
            writer.println("</Mlayer configuration file>");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void aboutMenuOnAction(ActionEvent event) {
        test();
    }

    @FXML
    void closeMenuAction(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void deleteMenuAction(ActionEvent event) {

    }

    @FXML
    void loadMenuOnAction(ActionEvent event) {
        loadNewSong();
    }

    @FXML
    void muteButtonOnAction(ActionEvent event) {

    }

    @FXML
    void nextButtonOnAction(ActionEvent event) {

    }

    @FXML
    void playButtonOnAction(ActionEvent event) {

    }

    @FXML
    void prevButtonOnAction(ActionEvent event) {

    }

    private void loadNewSong(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
        File file = fileChooser.showOpenDialog(null);

        Song addedSong = new Song(file);

        System.out.println(addedSong.getEstheticTitle());

        songOList.add(addedSong);
        if(addedSong != null) {
            songsList.getItems().add(addedSong);
        }
    }

    private void setNewSongToPlay(Song newSong){
        player = new MediaPlayer(newSong.getMedia());
        player.currentTimeProperty().addListener(updateOfSongBar);
        String title = newSong.getEstheticTitle();
        if(title.length() > MAX_TITLE_SIZE){
            title = title.substring(0, MAX_TITLE_SIZE) + "...";
        }
        titleText.setText(title);
        Image img = newSong.getCoverArt();
        coverArtImageView.setImage(img);
        coverArtImageView.setFitHeight(250);
        coverArtImageView.setFitWidth(250);
        player.play();
    }

    public void skipPrevButtonOnAction(ActionEvent actionEvent) {
        
    }

    public void skipNextButtonOnAction(ActionEvent actionEvent) {

    }
}
