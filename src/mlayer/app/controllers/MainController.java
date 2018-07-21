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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import mlayer.app.Main;
import mlayer.app.classes.Song;

import java.io.*;
import java.util.Optional;


public class MainController {

    private AnchorPane rootPane;
    private Stage myStage;
    private ObservableList<Song> songOList;
    private MediaPlayer player;
    private ChangeListener<Duration> updateOfSongBar = new ChangeListener<Duration>() {
        @Override
        public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
            if(player == null || isSliderUsed) return;
            double current = player.getCurrentTime().toSeconds();
            double fullTime = player.getMedia().getDuration().toSeconds();
            timeBar.setValue(current / fullTime);
        }
    };
    private Runnable endRunnable = this::endOfSong;
    private Integer MAX_TITLE_SIZE = 35;
    private Double SKIP_VAL = 5.0;
    private Integer notASongFile = 0;
    private Image muteImg, unmuteImg, pauseImg, playImg, nextImg, prevImg, skipForwImg, skipBackImg, loopImg, loopWImg, loopNImg;
    private Integer loopStatus = 1; // 1 -> [1, N] with loop   2 -> [1, N]   3 -> [i, i]
    private Boolean isSliderUsed = false;

    @FXML
    private MenuItem loadMenu;

    @FXML
    private MenuItem loadDirectoryMenu;

    @FXML
    private MenuItem closeMenu;

    @FXML
    private MenuItem deleteMenu;

    @FXML
    private MenuItem aboutMenu;

    @FXML
    private MenuItem clearListMenu;

    @FXML
    private Text titleText;

    @FXML
    private ImageView coverArtImageView;

    @FXML
    private Slider timeBar;

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
    private TableColumn<Song, String> durationColumn;

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

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button loopButton;

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
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().getDurationProperty());

        playImg = loadNewPngImageFromImgFolder("play");
        pauseImg = loadNewPngImageFromImgFolder("pause");
        nextImg = loadNewPngImageFromImgFolder("next");
        prevImg = loadNewPngImageFromImgFolder("prev");
        muteImg = loadNewPngImageFromImgFolder("mute");
        unmuteImg = loadNewPngImageFromImgFolder("unmute");
        skipForwImg = loadNewPngImageFromImgFolder("skipForw");
        skipBackImg = loadNewPngImageFromImgFolder("skipBack");
        loopImg = loadNewPngImageFromImgFolder("loop");
        loopWImg = loadNewPngImageFromImgFolder("loopW");
        loopNImg = loadNewPngImageFromImgFolder("loopN");

        playButton.setText(null); playButton.setGraphic(new ImageView(playImg));
        nextButton.setGraphic(new ImageView(nextImg));
        prevButton.setGraphic(new ImageView(prevImg));
        muteButton.setGraphic(new ImageView(unmuteImg));
        skipNextButton.setGraphic(new ImageView(skipForwImg));
        skipPrevButton.setGraphic(new ImageView(skipBackImg));

        loopTo(loopStatus);

        myStage.setOnCloseRequest(event -> {
            if(!closeWindow()){
                event.consume();
            }
        });

        songOList = FXCollections.observableArrayList();

        songsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null){
                if(player != null){
                    player.stop();
                }
                return;
            }
            setNewSongToPlay(newValue);
        });

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(player != null){
                player.setVolume(volumeSlider.getValue());
            }
        });

        timeBar.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(player == null) return;
            if(isSliderUsed)
                player.seek(Duration.seconds(player.getMedia().getDuration().toSeconds() * newValue.doubleValue()));
        });

        myStage.getIcons().add(loadNewPngImageFromImgFolder("mainWindowIcon"));

        loadFilesFromIniFile();

        System.out.println("...setting done!");
    }

    private Image loadNewPngImageFromImgFolder(String src){
        return new Image(Main.class.getResource("img/" + src + ".png").toString());
    }

    private boolean closeWindow(){
        Alert closingAlert = new Alert(Alert.AlertType.CONFIRMATION);
        //closingAlert.initStyle(StageStyle.UTILITY);
        closingAlert.setTitle("Closing program");
        closingAlert.setHeaderText("You are about to close the program");
        closingAlert.setContentText("Are you sure?");

        Stage alertStage = (Stage)closingAlert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Main.class.getResource("img/warning-icon.png").toString()));

        Optional<ButtonType> result = closingAlert.showAndWait();

        if(result.isPresent() && result.get().equals(ButtonType.OK)){
            createIniFile();
            myStage.close();
            return true;
        } else {
            return false;
        }
    }

    private void createIniFile(){
        try {
            PrintWriter writer = new PrintWriter("mlayer-conf.ini", "UTF-8");
            writer.println("#Mlayer config");
            writer.println("#Songs");
            for (Song aSongOList : songOList) {
                String path = aSongOList.getFile().getPath();
                writer.println(path);
            }
            writer.println("#end");
            writer.println("#Selested");
            writer.println(songsList.getSelectionModel().getSelectedIndex());
            writer.println("#end");
            writer.println("#Loop_Type");
            writer.println(loopStatus);
            writer.println("#end");
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void loadFilesFromIniFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("mlayer-conf.ini"));
            String read;
            while((read = br.readLine()) != null){
                if(read.startsWith("#Mlayer config")) continue;
                if(read.startsWith("#Songs")) continue;
                if(read.startsWith("#end")) break;
                File file = new File(read);
                Song addedSong = new Song(file);
                songOList.add(addedSong);
                songsList.getItems().add(addedSong);
            }
            while((read = br.readLine()) != null){
                if(read.startsWith("#Mlayer config")) continue;
                if(read.startsWith("#Selected")) continue;
                if(read.startsWith("#end")) break;
                songsList.getSelectionModel().select(Integer.parseInt(br.readLine()));
            }
            while((read = br.readLine()) != null){
                if(read.startsWith("#Mlayer config")) continue;
                if(read.startsWith("#Loop_Type")) continue;
                if(read.startsWith("#end")) break;
                loopTo(Integer.parseInt(read));
            }
        } catch (IOException e){
            System.out.println("File mlayer-conf.ini not found!");
        }
    }

    @FXML
    void aboutMenuOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Mlayer is a simple music player made in JavaFX\n" +
                "by Alek \"Tudny\" Tudruj - 2018");
        alert.showAndWait();
    }

    @FXML
    void closeMenuAction(ActionEvent event) {
        closeWindow();
    }

    @FXML
    void deleteMenuAction(ActionEvent event) {
        if(songsList.getItems().size() < 1) return;
        Song songToDelete = songsList.getSelectionModel().getSelectedItem();
        songOList.removeAll(songToDelete);
        songsList.getItems().removeAll(songToDelete);
        if(songOList.size() == 0){
            clearList();
        }
    }

    @FXML
    void loadMenuOnAction(ActionEvent event) {
        loadNewSong();
    }

    @FXML
    void loadDirectoryMenuOnAction(ActionEvent event){
        loadNewSongsFromDirectory();
    }

    @FXML
    void muteButtonOnAction(ActionEvent event) {
        if(player == null) return;
        if(player.isMute()){
            player.setMute(false);
            muteButton.setGraphic(new ImageView(unmuteImg));
        }else{
            player.setMute(true);
            muteButton.setGraphic(new ImageView(muteImg));
        }
    }

    @FXML
    void clearListMenuOnAction(ActionEvent event){
        clearList();
    }

    @FXML
    void nextButtonOnAction(ActionEvent event) {
        if(player == null) return;
        if(songOList.size() < 2){
            setNewSongToPlay(songsList.getSelectionModel().getSelectedItem());
        }
        playNextSong();
    }

    @FXML
    void playButtonOnAction(ActionEvent event) {
        if(player == null){
            return;
        }

        if(player.getStatus().equals(MediaPlayer.Status.PLAYING)){
            player.pause();
            playButton.setGraphic(new ImageView(playImg));
        }else if(player.getStatus().equals(MediaPlayer.Status.PAUSED)){
            player.play();
            playButton.setGraphic(new ImageView(pauseImg));
        }
    }

    @FXML
    void prevButtonOnAction(ActionEvent event) {
        if(player == null) return;
        if(songOList.size() < 2){
            setNewSongToPlay(songsList.getSelectionModel().getSelectedItem());
        }
        playPrevSong();
    }

    private void loadNewSong(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().
                addAll(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));
        File file = fileChooser.showOpenDialog(null);
        if(file == null) return;

        Song addedSong = new Song(file);

        songOList.add(addedSong);
        songsList.getItems().add(addedSong);

        if(songOList.size() == 1){
            songsList.getSelectionModel().select(songOList.get(0));
        }
    }

    private void loadNewSongsFromDirectory(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File folder = directoryChooser.showDialog(null);
        if(folder == null) return;
        boolean notChoosen = false;
        if(songOList.size() == 0){
            notChoosen = true;
        }
        notASongFile = 0;
        addAllFromDir(folder);
        System.out.println("Number of not-music-files: " + notASongFile);
        if(notChoosen){
            songsList.getSelectionModel().select(songOList.get(0));
        }
    }

    private void addAllFromDir(File dir){
        File[] listOfFiles = dir.listFiles();
        if(listOfFiles == null) return;
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                if (listOfFile.getName().toLowerCase().endsWith(".mp3")) {
                    Song songToAdd = new Song(listOfFile);
                    songOList.add(songToAdd);
                    songsList.getItems().addAll(songToAdd);
                } else {
                    notASongFile++;
                }
            } else if (listOfFile.isDirectory()) {
                addAllFromDir(listOfFile);
            }
        }
    }

    private void clearList(){
        songsList.getItems().clear();
        songOList.clear();
        coverArtImageView.setImage(null);
        myStage.setTitle("Mlayer");
        titleText.setText("Title");
    }

    private void setNewSongToPlay(Song newSong){
        if(player != null) player.stop();
        player = new MediaPlayer(newSong.getMedia());
        player.currentTimeProperty().addListener(updateOfSongBar);
        player.setOnEndOfMedia(endRunnable);
        String title = newSong.getEstheticTitle(1);
        myStage.setTitle(newSong.getEstheticTitle(2));
        if(title.length() > MAX_TITLE_SIZE){
            title = title.substring(0, MAX_TITLE_SIZE) + "...";
        }
        titleText.setText(title);
        Image img = newSong.getCoverArt();
        coverArtImageView.setImage(img);
        coverArtImageView.setFitHeight(250);
        coverArtImageView.setFitWidth(250);
        playButton.setGraphic(new ImageView(pauseImg));
        player.play();
    }

    public void skipPrevButtonOnAction(ActionEvent actionEvent) {
        if(player == null) return;
        player.seek(player.getCurrentTime().subtract(Duration.seconds(SKIP_VAL)));
    }

    public void skipNextButtonOnAction(ActionEvent actionEvent) {
        if(player == null) return;
        player.seek(player.getCurrentTime().add(Duration.seconds(SKIP_VAL)));
    }

    private void endOfSong(){
        if(loopStatus == 1){
            playNextSong();
        }else if(loopStatus == 2){
            songsList.getSelectionModel().selectNext();
        }else if(loopStatus == 3){
            playTheSameSong();
        }
    }

    private void playNextSong(){
        if(songsList.getSelectionModel().getFocusedIndex() == songOList.size() - 1){
            songsList.getSelectionModel().select(0);
        }else{
            songsList.getSelectionModel().selectNext();
        }
    }

    private void playPrevSong(){
        if(songsList.getSelectionModel().getFocusedIndex() == 0){
            songsList.getSelectionModel().select(songOList.size() - 1);
        }else{
            songsList.getSelectionModel().selectPrevious();
        }
    }

    private void playTheSameSong(){
        setNewSongToPlay(songsList.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void loopButtonOnAction(ActionEvent event) {
        loopStatus %= 3;
        loopStatus++;
        loopTo(loopStatus);
    }

    private void loopTo(Integer nr){
        if(nr == 1){
            loopStatus = 1;
            loopButton.setGraphic(new ImageView(loopWImg));
        }else if(nr == 2){
            loopStatus = 2;
            loopButton.setGraphic(new ImageView(loopImg));
        }else if(nr == 3){
            loopStatus = 3;
            loopButton.setGraphic(new ImageView(loopNImg));
        }
    }

    @FXML
    private void sliderIsBeingUsed(){
        isSliderUsed = true;
    }

    @FXML
    private void sliderIsNotBeingUsed(){
        isSliderUsed = false;
    }
}
