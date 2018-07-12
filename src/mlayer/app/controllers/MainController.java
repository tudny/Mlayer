package mlayer.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class MainController {

    private AnchorPane thisRoot;

    @FXML
    private ImageView imageView;

    public MainController(){

    }

    public void initialize(){

    }

    public void setImage(Image coverArt) {
        imageView.setImage(coverArt);
        imageView.fitWidthProperty().bind(thisRoot.widthProperty());
        imageView.fitHeightProperty().bind(thisRoot.heightProperty());
    }

    public void setAll(AnchorPane root){
        thisRoot = root;
    }
}
