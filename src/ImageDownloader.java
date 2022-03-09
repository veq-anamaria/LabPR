import java.io.*;
import java.net.Socket;
import java.net.URL;

public class ImageDownloader extends  Thread{

    public ImageDownloader(String urlString, File path) throws IOException{

        String toWriteTo = path.toPath().toString() + System.getProperty("file.separator");
        System.out.println(toWriteTo);

        URL url = new URL(urlString);

        Socket socket = new Socket(url.getHost(), 80);
        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println("GET " + url.getPath() + " HTTP/1.1");
        pw.println("Host: " + url.getHost());
        pw.println();
        pw.flush();

        final FileOutputStream fileOutputStream = new FileOutputStream(toWriteTo + url.getPath().replaceAll(".*/", ""));
        final InputStream inputStream = socket.getInputStream();


        boolean headerEnded = false;
        byte[] bytes = new byte[2048];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            if (headerEnded)
                fileOutputStream.write(bytes, 0, length);
            else {
                for (int i = 0; i < 2048; i++) {
                    if (bytes[i] == 13 && bytes[i + 1] == 10 && bytes[i + 2] == 13 && bytes[i + 3] == 10) {
                        headerEnded = true;
                        fileOutputStream.write(bytes, i+4, 2048-i-4);
                        break;
                    }
                }
            }
        }
        inputStream.close();
        fileOutputStream.close();
    }
}