package server.multusession;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;
    private Scanner in;
    private BufferedWriter out;
    private static int cnt = 0;
    private int number;
    private Server server;

    public int getName() {
        return number;
    }

    public Client() {
        cnt++;
        number = cnt;
        try {
            clientSocket = new Socket("localhost", 8080);
            in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            while (in.hasNext()){
                String line = in.nextLine();
                System.out.println("Msg from server: " + line);
                //String command = new Scanner(System.in).nextLine();
                //out.write(command + '\n');
                //out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client was created, " + cnt);

    }

    public void close() throws IOException {
        clientSocket.close();
        in.close();
        out.close();
    }

    public void putMessage(String message){
        try {
            out.write(message + '\n');
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDialog(){
        System.out.println(in.nextLine());
    }

    public static void main(String[] args) {
        new Client();
    }
}
