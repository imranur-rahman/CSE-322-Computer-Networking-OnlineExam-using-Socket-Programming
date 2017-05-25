package fileTransfer;

import java.io.*;
import java.net.Socket;

/**
 * Created by Shimul on 3/4/2017.
 */

public class FileTransferProcessor {
        Socket socket;
        InputStream is;
        FileOutputStream fos;
        BufferedOutputStream bos;
        int bufferSize;


        public FileTransferProcessor(Socket client) {
                socket = client;
                is = null;
                fos = null;
                bos = null;
                bufferSize = 0;

        }

        public void receiveFile(String fileName) {
                try {
                        is = socket.getInputStream();
                        bufferSize = socket.getReceiveBufferSize();
                        System.out.println("Buffer size: " + bufferSize);
                        fos = new FileOutputStream(fileName);
                        bos = new BufferedOutputStream(fos);
                        byte[] bytes = new byte[bufferSize];
                        int count;
                        while ((count = is.read(bytes)) >= 0) {
                                bos.write(bytes, 0, count);
                        }
                        bos.close();
                        is.close();
                        System.out.println("file received");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void receiveFile(File file) {
                try {

                        if(file.exists() == false){
                                file.createNewFile();
                                System.out.println(file.exists());
                        }
                        is = socket.getInputStream();
                        bufferSize = socket.getReceiveBufferSize();
                        System.out.println("Buffer size: " + bufferSize);
                        fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos);
                        byte[] bytes = new byte[bufferSize];
                        int count;
                        while ((count = is.read(bytes)) >= 0) {
                                bos.write(bytes, 0, count);
                                System.out.println(bytes.toString());
                        }
                        System.out.println("before bos closed");
                        bos.close();
                        fos.close();
                        //is.close();//ei line comment out korechi
                        System.out.println("file received");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }

        public void sendFile(File file) {

                FileInputStream fis;
                BufferedInputStream bis;
                BufferedOutputStream out;
                byte[] buffer = new byte[8192];
                try {
                        OutputStream os = socket.getOutputStream();
                        out = new BufferedOutputStream(os);
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        int count;
                        while ((count = bis.read(buffer)) > 0) {
                                out.write(buffer, 0, count);
                                System.out.println(buffer.toString());
                        }
                        //out.flush();
                        out.close();
                        fis.close();
                        bis.close();
                        System.out.println("file sent");
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
