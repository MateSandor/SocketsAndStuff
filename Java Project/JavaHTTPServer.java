import java.io.*;
import java.net.*;
import java.util.*;

public class JavaHTTPServer implements Runnable {

    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";

    private Socket connect;

    public JavaHTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(8080);
            System.out.println("Server started.\nListening for connections on port : 8080 ...\n");

            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());

                Thread thread = new Thread(myServer);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;

        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            String input = in.readLine();

            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();

            fileRequested = parse.nextToken().toLowerCase();

            if (method.equals("GET") || method.equals("HEAD")) {
                if (fileRequested.endsWith("/")) {
                    fileRequested += DEFAULT_FILE;
                }

                File file = new File(WEB_ROOT, fileRequested);
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);

                if (file.isFile()) {
                    sendResponse(out, dataOut, "HTTP/1.1 200 OK", content, fileLength);
                    writeFileData(file, dataOut);
                } else {
                    fileNotFound(out, dataOut, fileRequested);
                }
            }

        } catch (IOException e) {
            System.err.println("Server error : " + e);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close();
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }

    }

    private void sendResponse(PrintWriter out, OutputStream dataOut, String response, String contentType, int fileLength) throws IOException {
        out.println(response);
        out.println("Server: Java HTTP Server v1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + contentType);
        out.println("Content-length: " + fileLength);
        out.println();
        out.flush();
    }

    private void writeFileData(File file, OutputStream dataOut) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[(int) file.length()];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
            dataOut.write(fileData, 0, (int) file.length());
            dataOut.flush();
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
    }

    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        sendResponse(out, dataOut, "HTTP/1.1 404 File Not Found", content, fileLength);
        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        byte[] fileData = new byte[fileLength];
        FileInputStream fileIn = null;

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }

        return fileData;
    }

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else if (fileRequested.endsWith(".png")) {
            return "image/png";
        } else if (fileRequested.endsWith(".jpg") || fileRequested.endsWith(".jpeg")) {
            return "image/jpeg";
        } else {
            return "text/plain";
        }
    }
}
