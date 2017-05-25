package server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Shimul on 3/3/2017.
 */
public class ServerMainController implements Initializable {
        @FXML
        TextField examStartTime = new TextField();
        @FXML
        TextField duration = new TextField();
        @FXML
        TextField warningTime = new TextField();
        @FXML
        TextField rulesAndRegulations = new TextField();
        @FXML
        TextField nameOfQuestion = new TextField();
        @FXML
        TextArea locationOfFile = new TextArea("Choose location first");
        @FXML
        Button chooseLocationOfFile = new Button();
        @FXML
        TextField eligibleStudents = new TextField();
        @FXML
        TextField durationForSendingBackupFiles = new TextField();
        @FXML
        TextField corrections = new TextField();
        @FXML
        Button submit = new Button();
        @FXML
        Button sendCorrections = new Button();
        @FXML
        Text correctionsLabel = new Text();

        String _examStartTime;
        int _duration;
        int _warningTime;
        String _rulesAndRegulations;
        String _nameOfQuestion;
        int[] _eligibleStudents;
        int _durationForSendingBackupFiles;
        String _corrections;


        File question;
        Thread serverSocketThread;
        FileChooser fileChooser;
        TestServer testServer;


        @Override
        public void initialize(URL location, ResourceBundle resources) {

                examStartTime.setText("05:55:00");
                duration.setText("2");
                warningTime.setText("1");
                eligibleStudents.setText("1-20");
                durationForSendingBackupFiles.setText("1");


                chooseLocationOfFile.setOnAction((ActionEvent e) -> {
                        fileChooser = new FileChooser();
                        fileChooser.setTitle("Select File");
                        try
                        {
                                fileChooser.setInitialDirectory(new File("C:/Users/Shimul/Documents"));
                                question = fileChooser.showOpenDialog(new Stage());
                                locationOfFile.setText(question.getAbsolutePath());
                                //extension filter add korte hobe
                        }
                        catch(Exception e1)
                        {
                                System.out.println("No file choosen");
                        }
                });

                submit.setOnAction(e -> {
                        if(examStartTime.getText().trim().equals("") == false) _examStartTime = examStartTime.getText().toString();
                        if(duration.getText().trim().equals("") == false) _duration = Integer.parseInt(duration.getText().toString());
                        if(warningTime.getText().trim().equals("") == false) _warningTime = Integer.parseInt(warningTime.getText().toString());
                        if(rulesAndRegulations.getText().trim().equals("") == false) _rulesAndRegulations = rulesAndRegulations.getText().toString();
                        if(nameOfQuestion.getText().trim().equals("") == false) _nameOfQuestion = nameOfQuestion.getText().toString();
                        if(eligibleStudents.getText().trim().equals("") == false)
                        {
                                if(eligibleStudents.getText().contains("-"))
                                {
                                        String[] temp = eligibleStudents.getText().split("-");
                                        if(temp.length == 2)
                                        {
                                                _eligibleStudents = new int[Integer.parseInt(temp[1]) - Integer.parseInt(temp[0]) + 1];
                                                for(int i = Integer.parseInt(temp[0]), j = 0; i <= Integer.parseInt(temp[1]); ++i, ++j)
                                                        _eligibleStudents[j] = i;
                                        }
                                }
                                else
                                {
                                        String[] temp = eligibleStudents.getText().split(" ,");
                                        _eligibleStudents = new int[temp.length];
                                        for(int i = 0; i < temp.length; ++i)
                                                _eligibleStudents[i] = Integer.parseInt(temp[i]);
                                }
                                for(int i : _eligibleStudents)
                                        System.out.println(i);
                        }
                        if(durationForSendingBackupFiles.getText().trim().equals("") == false) _durationForSendingBackupFiles = Integer.parseInt(durationForSendingBackupFiles.getText().toString());
                        if(corrections.getText().trim().equals("") == false) _corrections = corrections.getText().toString();

                        System.out.println(_duration);

                        if(serverSocketThread == null) {
                                serverSocketThread = new Thread(() -> {
                                        System.out.println("Starting testServer Thread");
                                        testServer = new TestServer();
                                        testServer.main(this);

                                        /*corrections.setVisible(true);
                                        correctionsLabel.setVisible(true);
                                        sendCorrections.setVisible(true);*/
                                });
                                serverSocketThread.start();
                        }
                });

                sendCorrections.setOnAction(e -> {
                        if(serverSocketThread != null){
                                if(corrections.getText().trim().equals("") == false) _corrections = corrections.getText().toString();
                                testServer.sendCorrectionsToAllWorkerThreads();
                        }
                });
        }

        public boolean checkStudentInfo(String ipAddress, String portNumber, String studentID){
                for(int i = 0; i < _eligibleStudents.length; ++i)
                        if(Integer.parseInt(studentID) == _eligibleStudents[i])
                                return true;
                return false;
        }
}
