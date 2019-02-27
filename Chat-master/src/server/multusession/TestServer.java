package server.multusession;


import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestServer {
    public static void main(String[] args) throws InterruptedException {
        ConcurrentLinkedQueue<Client> clients = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 100; i++) {
            clients.add(new Client());
        }
        Thread t = new Thread(()-> {
            for (Client client : clients) {
                for (int j = 0; j < 30; j++) {
                    client.putMessage("msg from " + client.getName() + " - " + j);
                }
                client.showDialog();
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
