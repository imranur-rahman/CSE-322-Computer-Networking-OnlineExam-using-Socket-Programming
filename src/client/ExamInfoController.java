package client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Shimul on 3/4/2017.
 */
public class ExamInfoController implements Initializable {
        @FXML
        TextArea examStartTime = new TextArea();
        @FXML
        TextArea examDuration = new TextArea();
        @FXML
        TextArea currentServerTime = new TextArea();
        @FXML
        TextArea rulesAndRegulation = new TextArea();
        @FXML
        TextArea nameOfQuestion = new TextArea();
        @FXML
        TextArea backupInterval = new TextArea();
        @FXML
        TextArea examEndTime = new TextArea();

        String _currentServerTime = null;

        @Override
        public void initialize(URL location, ResourceBundle resources) {
                examStartTime.setEditable(false);
                examDuration.setEditable(false);
                currentServerTime.setEditable(false);
                rulesAndRegulation.setEditable(false);
                nameOfQuestion.setEditable(false);
                backupInterval.setEditable(false);
                examEndTime.setEditable(false);
        }




        public void setExamInfos(String examStartTime, String examDuration, String currentServerTime, String rulesAndRegulations, String nameOfQuestion, String backupInterval){

                _currentServerTime = currentServerTime;

                this.examStartTime.setText(examStartTime);
                this.examDuration.setText(examDuration);
                this.currentServerTime.setText(currentServerTime);
                this.rulesAndRegulation.setText(rulesAndRegulations);
                this.nameOfQuestion.setText(nameOfQuestion);
                this.backupInterval.setText(backupInterval);
                this.examEndTime.setText(calculateExamEndTime(examStartTime, examDuration));

                System.out.println("exam info set to UI");
                main(examStartTime, examDuration, backupInterval);
        }

        public void main(String examStartTime, String examDuration, String backupInterval) {
                System.out.println("ExamInfoController : main");
                //ekta thread chalu kore dite hobe, currentServerTime update korar jonno
                new Thread(() -> {
                        while(true) {

                                try {
                                        Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                        e.printStackTrace();
                                }

                                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                                Date d = null;
                                try {
                                        d = df.parse(_currentServerTime);

                                } catch (ParseException e) {
                                        e.printStackTrace();
                                }
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(d);
                                cal.add(Calendar.SECOND, 1);
                                String newTime = df.format(cal.getTime());
                                _currentServerTime = newTime;
                                this.currentServerTime.setText(_currentServerTime);

                                if(examEndTime.getText().toString().equals(_currentServerTime))
                                        break;
                        }
                }).start();


                //exam time hoye gele kothay file rakhbo user theke input nibe
                //new Thread(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date currentServerTimeInDateFormat = null, examStartTimeInDateFormat = null;
                while(true) {
                        try {
                                currentServerTimeInDateFormat = sdf.parse(_currentServerTime);
                                examStartTimeInDateFormat = sdf.parse(examStartTime);//eta change hobe na, so ekhane na dileo hobe
                        } catch (ParseException e) {
                                e.printStackTrace();
                        }
                        if (currentServerTimeInDateFormat.getTime() - examStartTimeInDateFormat.getTime() >= 0)
                                break;
                }

                //showing dialogbox about exam starting

                Platform.runLater(() -> {

                        DirectoryChooser directoryChooser = new DirectoryChooser();
                        directoryChooser.setTitle("Choose where to save question");
                        directoryChooser.setInitialDirectory(new File("C:/Users/Shimul/Documents"));
                        File selectedDirectory = directoryChooser.showDialog(new Stage());
                        System.out.println(selectedDirectory.getAbsolutePath());
                        //ClientMain.getClientMain().clientLoginController.simpleClient.requestToSendQuestions();
                        ClientMain.getClientMain().clientLoginController.simpleClient.receiveQuestion(selectedDirectory.getAbsolutePath());

                });

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("Got it!", ButtonBar.ButtonData.CANCEL_CLOSE));
                dialog.setContentText("Exam has been started");
                dialog.showAndWait();

                        //ClientMain.getClientMain().clientLoginController.simpleClient.receiveQuestion("fdas");

                //}).start();

                System.out.println("dui thread er majhe achi");
                /*new Thread(() -> {
                        ClientMain.getClientMain().clientLoginController.simpleClient.receiveQuestion("asdf");
                }).start();*/
                //ClientMain.getClientMain().clientLoginController.simpleClient.receiveQuestion("asdf");

                //backup file pathabe exam time shesh na howa porjonto
                //new Thread(() -> {



                System.out.println("In Backup File Sending");
                //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                ArrayList<Date> timesToSendBackups = new ArrayList<>();
                Date d = null, examEndTimeInDateFormat = null;

                try {
                        d = sdf.parse(examStartTime);
                        examEndTimeInDateFormat = sdf.parse(calculateExamEndTime(examStartTime, examDuration));
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                while(true) {

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(d);
                        cal.add(Calendar.MINUTE, Integer.parseInt(backupInterval));
                        Date nextTimeToSendBackup = cal.getTime();

                        if (nextTimeToSendBackup.getTime() - examEndTimeInDateFormat.getTime() > 0)
                                break;
                        timesToSendBackups.add(nextTimeToSendBackup);
                        d = nextTimeToSendBackup;
                }

                for(int i = 0; i < timesToSendBackups.size(); ++i){
                        System.out.println(timesToSendBackups.get(i).toString());
                }

                /*new Thread(() -> {
                        ClientMain.getClientMain().clientLoginController.simpleClient.takingCorrections();
                }).start();*/
                Platform.runLater(() -> {
                        ClientMain.getClientMain().clientLoginController.simpleClient.takingCorrections();
                });

                //exam time hoye geche
                new Thread(() -> {
                        Date currentServerTime = null;
                        for(int i = 0; i < timesToSendBackups.size(); ++i){
                                try {
                                        currentServerTime = sdf.parse(_currentServerTime);
                                } catch (ParseException e) {
                                        e.printStackTrace();
                                }
                                if(currentServerTime.getTime() - timesToSendBackups.get(i).getTime() >= 0){
                                        ClientMain.getClientMain().clientLoginController.simpleClient.sendBackupToServer();
                                }
                                else {
                                        --i;
                                }
                        }
                        //exam shesh
                        ClientMain.getClientMain().clientLoginController.simpleClient.sendExamEndingInfoToServer();
                }).start();
                //}).start();*/
        }

        private String calculateExamEndTime(String examStartTime, String examDuration) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                Date d = null;
                try {
                        d = sdf.parse(examStartTime);
                } catch (ParseException e) {
                        e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.add(Calendar.MINUTE, Integer.parseInt(examDuration));
                String examEndTime = sdf.format(cal.getTime());
                return examEndTime;
        }

        public void showCorrections(String corrections){
                System.out.println("showing corrections to UI");
                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("Got it!", ButtonBar.ButtonData.CANCEL_CLOSE));
                dialog.setContentText("New Correction : " + corrections);
                dialog.setTitle("Correction");
                dialog.showAndWait();
        }
}
