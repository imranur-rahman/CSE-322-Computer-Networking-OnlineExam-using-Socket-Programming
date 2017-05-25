package fileTransfer;

import java.io.*;
import java.net.Socket;

/**
 * Created by Shimul on 3/10/2017.
 */
public class FileTransferProcessor2 {
        private final Socket socket;

        public FileTransferProcessor2(Socket socket){
                this.socket = socket;
        }

        public void sendFile(File file) {
                try {
                        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                        FileInputStream fis = new FileInputStream(file);
                        byte[] buffer = new byte[4096];

                        while (fis.read(buffer) > 0) {
                                dos.write(buffer);
                        }

                        fis.close();
                        dos.flush();
                        //dos.close();
                        System.out.println("file sent");
                }
                catch(IOException e){
                        e.printStackTrace();
                }
        }

        public void receiveFile(File file)  {
                try {
                        file.createNewFile();
                        InputStream in = socket.getInputStream();
                        DataInputStream dis = new DataInputStream(in);
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] buffer = new byte[4096];

                        int filesize = 15123; // Send file size in separate msg
                        int read = 0;
                        int totalRead = 0;
                        int remaining = filesize;
                        while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                                totalRead += read;
                                remaining -= read;
                                System.out.println("read " + totalRead + " bytes.");
                                fos.write(buffer, 0, read);
                        }

                        fos.close();
                        dis.close();
                        System.out.println("file received");
                }
                catch(IOException e){
                        e.printStackTrace();
                }
        }

}
