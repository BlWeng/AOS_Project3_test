import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;


public class com_requester {

    // Private variables
    private request_message msg;
    private Socket com_port = null;


    // Constructor
    public com_requester(request_message msg){

        this.msg = msg;
    }


    // Send message
    public void send(){

        try {

            com_port = new Socket(InetAddress.getLocalHost(), 5000+Character.getNumericValue(msg.getReceiver()));

            ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

            oos.writeObject(msg);
            System.out.println("Object: " + msg + " sent to Port: " + (5000+Character.getNumericValue(msg.getReceiver())));

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
