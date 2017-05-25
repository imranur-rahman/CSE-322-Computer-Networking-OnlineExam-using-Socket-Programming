package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Shimul on 3/4/2017.
 */
public class ClientLoginController implements Initializable {
        @FXML
        TextField ipAddress = new TextField();
        @FXML
        TextField portNumber = new TextField();
        @FXML
        TextField studentID = new TextField();
        @FXML
        Button login = new Button();
        @FXML
        Label loginDenied = new Label();

        String _ipAddress;
        String _portNumber;
        String _studentID;
        SimpleClient simpleClient;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                loginDenied.setVisible(false);
                login.setOnAction(e -> {

                        if(ipAddress.getText().trim().equals("") == false) _ipAddress = ipAddress.getText().toString();
                        if(portNumber.getText().trim().equals("") == false) _portNumber = portNumber.getText().toString();
                        if(studentID.getText().trim().equals("") == false) _studentID = studentID.getText().toString();

                        System.out.println("InLoginSetOnAction");
                        Platform.runLater(() -> {
                                if(simpleClient == null)
                                        simpleClient = new SimpleClient(this);
                                simpleClient.sendLoginInfoToServer(_ipAddress, _portNumber, _studentID);
                                System.out.println("login");
                                //simpleClient.getExamInfo();
                                //ClientMain.getClientMain().showExamInfo();
                                if(simpleClient.checkIfLoginApproved() == true) {//login succeeded
                                        simpleClient.getExamInfo();
                                        ClientMain.getClientMain().showExamInfo();
                                }
                                else{//login failed
                                        loginDenied.setVisible(true);
                                        loginDenied.setTextFill(Color.RED);
                                        try {
                                                Thread.sleep(5000);
                                        } catch (InterruptedException e1) {
                                                e1.printStackTrace();
                                        }
                                        loginDenied.setVisible(false);
                                }
                        });
                });
        }
}
