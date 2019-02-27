package server.multusession;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Server server;
    private DataOutputStream out;
    DataOutputStream outputStream;


    private DataInputStream in;

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    private Socket clientSocket = null;
    private String name;
    private String pass;
    private String clientMessage;

    private static int clients_count = 0;

    public ClientHandler(Socket socket, Server server) throws ClassNotFoundException,SQLException {
        try {
            this.server = server;
            this.clientSocket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());

            //out.print("Good");
            if (in.available()!=-1) {
                name =in.readUTF();
            }
            if (in.available()!=-1){
                pass = in.readUTF();
            }

            if(DataBase.NameOK(name,pass).compareTo("Good")==0){
                clients_count++;
            }
            sendMsg(DataBase.NameOK(name,pass));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {


            server.sendMessageToAllClients("Новый участник вошёл в чат!");
            server.sendMessageToAllClients("Клиентов в чате = " + clients_count);

            while (true) {
                if (in.available()!=-1) {
                    clientMessage=in.readUTF();

                   if (clientMessage.equalsIgnoreCase("##session##end##")) {
                       break;
                     }
                        server.sendMessageToAllClients(name+" : "+clientMessage);
                     }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }
    // отправляем сообщение
    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // клиент выходит из чата
    public void close() {
        // удаляем клиента из списка
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Клиентов в чате = " + clients_count);
    }
}
