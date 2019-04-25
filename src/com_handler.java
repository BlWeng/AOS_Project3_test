import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;


public class com_handler implements Runnable, Serializable{


    private Node node;
    private Socket in_obj = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;


    public com_handler(Node node, Socket in_obj, ObjectOutputStream oos, ObjectInputStream ois){

        this.node = node;
        this.in_obj = in_obj;
        this.oos = oos;
        this.ois = ois;
    }


    @Override
    public synchronized void run(){

        try {

            System.out.println("Local IP: " + this.in_obj.getLocalSocketAddress() + "\nRemote IP:" + this.in_obj.getRemoteSocketAddress());

            while (true){

                request_message in = (request_message) ois.readObject();
                while (in != null){

                    System.out.println("Sender:" + in.getSender() + "\nReceiver:" + in.getReceiver());

                    in = null;
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
