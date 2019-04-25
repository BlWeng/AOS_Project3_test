import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class com_server implements Runnable{


    private Node node;
    private Socket com_port = null;
    com_handler msg_thread = null;


    public com_server(Node node){

        this.node = node;
    }


    @Override
    public void run(){
        try{

            while (node.getOpen_port()){

                com_port = this.node.getPort().accept();
                ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                msg_thread = new com_handler(node, com_port, oos, ois);
                Thread p = new Thread(msg_thread);
                p.start();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
