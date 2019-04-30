import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class com_requester {


    private Node node;
    private request_message rv;
    com_handler msg_thread = null;
    Socket com_port = null;
    private ArrayList<Character> quorum_set_inReq;


    public com_requester(request_message in_rv, ArrayList in_set, Node in_node)
    {
        this.rv = new request_message(in_rv);
        this.quorum_set_inReq = in_set;
        this.node = in_node;
    }


    public com_requester(request_message in_rv, Node in_node)
    {
        this.rv = new request_message(in_rv);
        this.node = in_node;
    }


    public void send() {

        if (this.rv.getCalling_action() == request_message.calling_option.broadcast_clique) {

            for( char pt : quorum_set_inReq){

                char temp = pt;
                if (temp != this.node.getNid()) {
                    try {

                        com_port = new Socket(InetPort.get_addr(pt), InetPort.get_port(pt));
//                        com_port = new Socket(InetAddress.getLocalHost(), 5000 + (int) temp);
                        this.rv.setReceiver(temp);

                        ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                        oos.writeObject(this.rv);

                        System.out.println(">> Write message:" + rv.getAct_selected());

                    } catch (Exception e) {
                        System.out.println("Port of Node " + temp + " is not ready.");
                        e.printStackTrace();
                    }
                }
            }

        }


        if (this.rv.getCalling_action() == request_message.calling_option.single) {

            try {

                System.out.println("Sender: " + this.rv.getSender() + " Receiver: " + this.rv.getReceiver());
                com_port = new Socket(InetPort.get_addr(rv.getReceiver()), InetPort.get_port(rv.getReceiver()));
//                com_port = new Socket(InetAddress.getLocalHost(), 5000 + (int)this.rv.getReceiver());
                System.out.println("Client handler back in single option!!!!!");

                ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                oos.writeObject(this.rv);
                System.out.println(">> Write message:" + rv.getAct_selected());


            } catch (Exception e) {

                System.out.println("Port of Node " + this.rv.getSender() + " is not ready.");
            }
        }
    }
}
