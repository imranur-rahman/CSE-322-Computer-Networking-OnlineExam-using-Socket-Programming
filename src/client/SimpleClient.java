package client;

import fileTransfer.FileTransferProcessor;
import fileTransfer.FileTransferProcessor2;
import fileTransfer.FileTransferProcessor3;

import java.io.*;
import java.net.Socket;

/**
 * Created by Shimul on 3/4/2017.
 */

public class SimpleClient
{
        private Socket s = null;
        private BufferedReader br = null;
        private PrintWriter pr = null;
        private int instances = 1;
        String studentID;
        ClientLoginController clientLoginController;
        ExamInfoController examInfoController;
        String examStartTime = null, examDuration = null, currentServerTime = null, rulesAndRegulations = null, nameOfQuestion = null, backupInterval = null;
        File answerScript;
        FileTransferProcessor3 fileTransferProcessor;

        public SimpleClient(ClientLoginController clientLoginController){//, ExamInfoController examInfoController){

                this.clientLoginController = clientLoginController;
                //this.examInfoController = examInfoController;
                System.out.println("ConstructorStart");

                try
                {
                        //s = new Socket("localhost", 5555);//5555 chilo
                        s = new Socket(clientLoginController._ipAddress.toString(), Integer.parseInt(clientLoginController._portNumber));
                        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        pr = new PrintWriter(s.getOutputStream());
                        fileTransferProcessor = new FileTransferProcessor3(s);
                }
                catch(Exception e)
                {
                        System.err.println("Problem in connecting with the server. Exiting main.");
                        System.exit(1);
                }
                System.out.println("ConstructorEnd");
        }

        public void sendLoginInfoToServer(String ipAddress, String portNumber, String studentID){
                this.studentID = studentID;
                pr.println(ipAddress);
                pr.println(portNumber);
                pr.println(studentID);
                pr.flush();
                System.out.println("sendLoginToServer");
        }

        public boolean checkIfLoginApproved() {
                String temp = null;
                try {
                        temp = br.readLine();
                        System.out.println("Client found " + temp);

                } catch (IOException e) {
                        e.printStackTrace();
                }
                if(temp.equals("true"))
                        return true;
                else
                        return false;
        }

        public void getExamInfo() {
                try {
                        examStartTime = br.readLine();
                        examDuration = br.readLine();
                        currentServerTime = br.readLine();
                        rulesAndRegulations = br.readLine();
                        nameOfQuestion = br.readLine();
                        backupInterval = br.readLine();
                        System.out.println("exam info got");
                } catch (IOException e) {
                        e.printStackTrace();
                }
                ClientMain.getClientMain().examInfoController.setExamInfos(examStartTime, examDuration, currentServerTime, rulesAndRegulations, nameOfQuestion, backupInterval);
        }


        public void func(String studentID)
        {

                new File(studentID).mkdirs();


                /*FileTransferProcessor fileTransferProcessor = new FileTransferProcessor(s);
                fileTransferProcessor.receiveFile(new File(studentID + "/" + studentID + ".txt"));*/


        }

        public void requestToSendQuestions() {
                pr.println("send question");
                pr.flush();
        }

        public void receiveQuestion(String absolutePath) {

                /*func(studentID);

                answerScript = new File(studentID + "/" + studentID + ".txt");//change kochechi
                try {
                        answerScript.createNewFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }*/
                answerScript = new File(absolutePath + "/" + studentID + ".txt");
                try {
                        answerScript.createNewFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                System.out.println(answerScript.exists());

                fileTransferProcessor.receiveFile(answerScript);


                System.out.println("answerscript : " + answerScript);
                //fileTransferProcessor.receiveFile(new File(studentID + "/" + studentID + "999.txt"));
        }

        public void sendBackupToServer() {
                pr.println("sending backup");
                pr.flush();

                fileTransferProcessor.sendFile(answerScript);
        }

        public void sendExamEndingInfoToServer() {
                pr.println("exam ended");
                pr.flush();
        }

        public void takingCorrections(){
                try {
                        Thread.sleep(10000);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
                String temp = null;
                try {
                        while((temp = br.readLine()) != null){
                                if(temp.equals("sending corrections")){
                                        temp = br.readLine();
                                        System.out.println("get corrections in client");
                                        ClientMain.getClientMain().examInfoController.showCorrections(temp);
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}

