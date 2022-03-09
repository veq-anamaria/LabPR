import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetRequest {
    public static void main(String[] args) throws IOException, InterruptedException {
        InetAddress addr;
        addr=InetAddress.getByName("me.utm.md");
        Socket socket = new Socket(addr, 80);
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println("GET / HTTP/1.1");
        out.println("Host: me.utm.md:80");
        out.println("Connection: Close");
        out.println("Accept-Language: ro");
        out.println("Content-Language: en, ru");
        out.println("Save-Data: <sd-token");
        out.println();
        out.flush();

        StringBuilder sb = new StringBuilder(8096);

        String responseLine = in.readLine();
        while (responseLine != null) {
            responseLine = in.readLine();
            sb.append(responseLine);
            sb.append('\n');

        }
        System.out.println(sb.toString());
        socket.close();

        Pattern pattern = Pattern.compile("[^\"']*\\.(?:png|jpg|gif)");

        List<String> allPhotos = new ArrayList<>();

        Matcher m = pattern.matcher(sb.toString());

        while (m.find()) {
            allPhotos.add(m.group());
        }

        for(int i=0; i<allPhotos.size(); i++){
            String photo=allPhotos.get(i);
            if (!photo.startsWith("http://")) {
                allPhotos.set(i,"http://me.utm.md/" + photo);
            }
        }

        File downloads = new File("D:\\Video_cursuriOnline\\img");

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        final Semaphore semaphore = new Semaphore(2);

        for (String link : allPhotos) {

            ImageDownloader imageDownloader = null;


            semaphore.acquire();

            imageDownloader = new ImageDownloader(link, downloads);
            executor.execute(imageDownloader);
            semaphore.release();

        }
        executor.shutdown();
    }


}