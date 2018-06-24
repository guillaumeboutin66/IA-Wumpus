/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wumpus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author azuron
 */
public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // getting loader and a pane for the first scene.
        // loader will then give a possibility to get related controller
        FXMLLoader firstPaneLoader = new FXMLLoader(getClass().getResource("resources/FXML/sample.fxml"));
        Parent firstPane = firstPaneLoader.load();
        Scene firstScene = new Scene(firstPane, 1200, 900);

        // getting loader and a pane for the second scene
        FXMLLoader secondPageLoader = new FXMLLoader(getClass().getResource("resources/FXML/settings.fxml"));
        Parent secondPane = secondPageLoader.load();
        Scene secondScene = new Scene(secondPane, 1200, 900);

        // injecting second scene into the controller of the first scene
        Controller firstPaneController = firstPaneLoader.getController();
        System.out.println(firstPaneController.toString());
        firstPaneController.setSecondScene(secondScene);
        firstPaneController.setsecondController(secondPageLoader.getController());

        // injecting first scene into the controller of the second scene
        Controller2 secondPaneController = secondPageLoader.getController();
        System.out.println(secondPaneController.toString());
        secondPaneController.setFirstScene(firstScene);

        primaryStage.setTitle("Smart agent for wumpus game");
        primaryStage.setScene(firstScene);
        primaryStage.show();

    }
}
