package fileTransfer;

import java.io.*;
import java.net.Socket;

/**
 * Created by Shimul on 3/11/2017.
 */
public class FileTransferProcessor3 {
        private Socket socket;
        private BufferedInputStream bis = null;
        private DataInputStream dis = null;
        private BufferedOutputStream bos = null;
        private DataOutputStream dos = null;


        public FileTransferProcessor3(Socket socket)  {
                this.socket = socket;
        }

        public void sendFile(File file) {
                try {
                        this.bos = new BufferedOutputStream(socket.getOutputStream());
                        this.dos = new DataOutputStream(this.bos);

                        long length = file.length();
                        dos.writeLong(length);

                        String name = file.getName();
                        dos.writeUTF(name);

                        //dos.flush();

                        FileInputStream fis = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        System.out.println("file sending starting");

                        int theByte = 0;
                        while ((theByte = bis.read()) != -1) {
                                bos.write(theByte);
                        }
                        bos.flush();
                        bis.close();
                        fis.close();
                        System.out.println("file sent by 3");
                }
                catch (IOException e){
                        e.printStackTrace();
                }

        }

        public void receiveFile(File file) {
                try{
                        this.bis = new BufferedInputStream(socket.getInputStream());
                        this.dis = new DataInputStream(this.bis);

                        long fileLength = dis.readLong();
                        String fileName = dis.readUTF();

                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);

                        System.out.println("file receiving starting");

                        for(int j = 0; j < fileLength; j++) {
                                bos.write(bis.read());
                        }

                        bos.close();
                        fos.close();

                        System.out.println("file received by 3");
                }
                catch (IOException e){
                        e.printStackTrace();
                }
        }

}
