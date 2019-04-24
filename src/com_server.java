import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class com_server implements Runnable{

    private Node node;

    com_handler msg_thread = null;

    Socket com_port = null;

    static Vector<com_handler> client_list_server_end = new Vector<>();

    private int client_id;

    //public com_server( Node node, request_message in_rv)
    public com_server( Node node)
    {
        this.node = node;
        //this.vb = new requirement(in_vb);
        //this.rv = new request_message(in_rv);
    }


    //public void operation() throws Exception{
    @Override
    public void run(){
        try {
            while (this.node.getOpen_port()) {

               //System.out.println("Server of Node " + this.node.getNid() + " is ready.");
                com_port = this.node.getPort().accept();
                //client_id = this.rv.getSender();

                node.setNm_msg_received();

                ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                msg_thread = new com_handler(client_id, com_port, this.node, com_handler.duty.server, ois, oos);
                Thread p = new Thread(msg_thread);
                client_list_server_end.add(msg_thread);

                p.start();

            }
        }catch (Exception e) {e.printStackTrace();}
    }
}
