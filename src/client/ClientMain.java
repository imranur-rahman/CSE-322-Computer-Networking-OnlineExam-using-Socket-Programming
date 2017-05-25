package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Shimul on 3/4/2017.
 */
public class ClientMain extends Application {

        static ClientMain clientMain;
        static Stage stage;
        static Scene exam;
        ClientLoginController clientLoginController;
        ExamInfoController examInfoController;

        public ClientMain() { clientMain = this; }
        public static ClientMain getClientMain() { return clientMain; }

        @Override
        public void start(Stage primaryStage) throws Exception {

                stage = primaryStage;

                FXMLLoader loader1 = new FXMLLoader(getClass().getResource("ClientLogin.fxml"));
                Parent root1 =  loader1.load();
                stage.setTitle("ClientMain");
                stage.setScene(new Scene(root1));
                clientLoginController = loader1.getController();

                FXMLLoader loader2 = new FXMLLoader(getClass().getResource("ExamInfo.fxml"));
                Parent root2 = loader2.load();
                exam = new Scene(root2);
                examInfoController = loader2.getController();

                stage.show();
        }
        public void showExamInfo(){
                stage.setScene(exam);
        }
}
