package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("GA_背包问题");
        primaryStage.setScene(new Scene(root, 1000, 500));
        Image image = new Image(getClass().getResourceAsStream("/logo.png"));
        primaryStage.getIcons().add(image);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
