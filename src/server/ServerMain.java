package server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Shimul on 3/3/2017.
 */

public class ServerMain extends Application {

        static ServerMain serverMain;
        static Stage stage;

        public ServerMain(){
                serverMain = this;
        }
        public static ServerMain getServerMain(){
                return serverMain;
        }
        @Override
        public void start(Stage primaryStage) throws Exception {
                stage = primaryStage;
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/server/ServerMain.fxml"));
                Parent root = fxmlLoader.load();
                stage.setTitle("ServerMain");
                stage.setScene(new Scene(root));
                stage.show();
        }

        public static void main(String[] args) {
                launch(args);
        }
}
