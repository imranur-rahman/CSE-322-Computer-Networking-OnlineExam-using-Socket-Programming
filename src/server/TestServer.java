package server;

import fileTransfer.FileTransferProcessor;
import fileTransfer.FileTransferProcessor2;
import fileTransfer.FileTransferProcessor3;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Shimul on 3/4/2017.
 */

public class TestServer
{
        public static int workerThreadCount = 0;
        //public static ServerMainController serverMainController;
        static int id = 1;

        public static HashMap<String, String> onIPAddress = new HashMap<String, String>();
        public static HashMap<String, String> onStudentID = new HashMap<String, String>();

        ArrayList<WorkerThread>workerThreadArrayList = new ArrayList<>();

        public void main(ServerMainController serverMainController)
        {
                try
                {
                        ServerSocket ss = new ServerSocket(5555);//5555 chilo
                        System.out.println("Server has been started successfully.");

                        while(true)
                        {
                                Socket s = ss.accept();		//TCP Connection
                                WorkerThread wt = new WorkerThread(s, id, serverMainController);
                                workerThreadArrayList.add(wt);
                                Thread t = new Thread(wt);
                                t.start();
                                workerThreadCount++;
                                System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
                                id++;
                        }
                }
                catch(Exception e)
                {
                        System.err.println("Problem in ServerSocket operation. Exiting main.");
                        e.printStackTrace();
                }
        }

        public void sendCorrectionsToAllWorkerThreads(){
                for(int i = 0; i < workerThreadArrayList.size(); ++i){
                        workerThreadArrayList.get(i).sendingCorrections();
                }
        }
}

class WorkerThread implements Runnable
{
        private Socket socket;
        private InputStream is;
        private OutputStream os;
        private ServerMainController serverMainController;
        private BufferedReader br;
        private PrintWriter pr;
        File serverCopy;

        String ipAddress = null, portNumber = null, studentID = null;

        private int id = 0;
        private boolean backup;

        public WorkerThread(Socket s, int id, ServerMainController serverMainController)
        {
                this.socket = s;
                this.serverMainController = serverMainController;

                try
                {
                        this.is = this.socket.getInputStream();
                        this.os = this.socket.getOutputStream();
                        br = new BufferedReader(new InputStreamReader(this.is));
                        pr = new PrintWriter(this.os);
                }
                catch(Exception e)
                {
                        System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
                }

                this.id = id;
        }

        public void run()
        {
                /*BufferedReader br = new BufferedReader(new InputStreamReader(this.is));
                PrintWriter pr = new PrintWriter(this.os);


                pr.println("Your id is: " + this.id);
                pr.flush();
                pr.close();*/

                /*FileTransferProcessor fileTransferProcessor = new FileTransferProcessor(socket);
                fileTransferProcessor.sendFile(serverMainController.question);
                System.out.println("sent to : " + TestServer.id);*/


                loginProcess();//er moddhe pore check korte hobe age login chilo kina habijabi

                sendExamInfos();

                updateHashMap();

                createDirectory();

                FileTransferProcessor3 fileTransferProcessor = new FileTransferProcessor3(socket);
                fileTransferProcessor.sendFile(serverMainController.question);

                //FileTransferProcessor3 nw = new FileTransferProcessor3(socket);
                //fileTransferProcessor.receiveFile(serverMainController.question);vul hoiche, ekhane save korbo na

                //fileTransferProcessor.sendFile(serverMainController.question);
                String temp = null;
                serverCopy = new File(studentID + "/" + studentID + ".txt");
                try {
                        serverCopy.createNewFile();
                } catch (IOException e) {
                        e.printStackTrace();
                }
                try {
                        while((temp = br.readLine()) != null){
                                if(temp.equals("sending backup")){
                                        fileTransferProcessor.receiveFile(serverCopy);
                                }
                                else if(temp.equals("exam ended")){
                                        break;
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }


                System.out.println("sent to : " + TestServer.id);


                //fileTransferProcessor.sendFile(serverMainController.question);

                try {
                        Thread.sleep(120000);
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                TestServer.workerThreadCount--;
                System.out.println("Client [" + id + "] is now terminating. No. of worker threads = "
                        + TestServer.workerThreadCount);
        }

        public void sendingCorrections(){
                pr.println("sending corrections");
                pr.println(serverMainController._corrections);
                pr.flush();
                System.out.println("corrections sent to client");
        }


        private void sendExamInfos() {
                pr.println(serverMainController._examStartTime); //exam start time
                pr.println(serverMainController._duration);
                pr.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));//current server time
                pr.println(serverMainController._rulesAndRegulations);
                pr.println(serverMainController._nameOfQuestion);
                pr.println(serverMainController._durationForSendingBackupFiles);
                pr.flush();
                System.out.println("server sent exam infos");
        }

        private void createDirectory() {
                new File(studentID).mkdirs();
        }

        public void loginProcess(){
                while(true) {
                        System.out.println("server");

                        try {
                                ipAddress = br.readLine();
                                portNumber = br.readLine();
                                studentID = br.readLine();
                                //break;
                        } catch (IOException e) {
                                e.printStackTrace();
                        }

                        if (serverMainController.checkStudentInfo(ipAddress, portNumber, studentID) == false)
                        {
                                pr.println("false");
                                pr.flush();
                                System.out.println("server says login denied");
                        }
                        else
                        {
                                pr.println("true");
                                pr.flush();
                                System.out.println("server says login approved");
                                break;
                        }
                }
        }
        public void updateHashMap(){
                TestServer.onIPAddress.put(ipAddress, studentID);
                TestServer.onStudentID.put(studentID, ipAddress);
                System.out.println(TestServer.onIPAddress.size());
                System.out.println(TestServer.onStudentID.size());
        }
}

