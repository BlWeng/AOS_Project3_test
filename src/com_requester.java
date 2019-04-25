import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class com_requester {

    private Node node;

    private int destination;

    private request_message rv;

    com_handler msg_thread = null;

    Socket com_port = null;

    static Vector<com_handler> server_list_client_end = new Vector<>();

    private Hashtable quorum_set_inReq;


    public com_requester(request_message in_rv, Hashtable in_set, Node in_node)
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

            Enumeration enu_qs = this.quorum_set_inReq.keys();

            while(enu_qs.hasMoreElements()){
                //if(!this.rv.getAct_selected().equals(request_message.action_options.enter_cs))
                //    this.node.setNm_msg_sent();
                //String temp = enu_qs.nextElement().toString();
                char temp = enu_qs.nextElement().toString().charAt(0);
                if (temp != this.node.getNid()) {
                    try {
/*
                    String server_id;
                    if(temp == "0")
                        server_id = "dc13.utdallas.edu";
                    else {
                        server_id = "dc0" + temp + ".utdallas.edu";
                        if (this.node.getNid() < 10)
                            server_id = "dc0" + temp + ".utdallas.edu";
                        else
                            server_id = "dc" + temp + ".utdallas.edu";

                    }
                    com_port = new Socket(server_id, 5000+Integer.parseInt(temp));
*/

                        com_port = new Socket(InetAddress.getLocalHost(), 5000 + (int) temp);

                        this.rv.setReceiver(temp);
                        //System.out.println("Client handler back!!!!!");
                        ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                        oos.writeObject(this.rv);


                        //System.out.println("Server " + temp + " established at client ");

                    } catch (Exception e) {
                        System.out.println("Port of Node " + temp + " is not ready.");
                        e.printStackTrace();
                    }
                }
            }

        }


        if (this.rv.getCalling_action() == request_message.calling_option.single) {
            this.node.setNm_msg_sent();
            try {
                System.out.println("Sender: " + this.rv.getSender() + " Receiver: " + this.rv.getReceiver());
                com_port = new Socket(InetAddress.getLocalHost(), 5000 + (int)this.rv.getReceiver());
                //com_port = new Socket(InetAddress.getLocalHost(), 5000 + this.rv.getNode_number());

/*
                String server_id;
                if(this.rv.getReceiver() == 0)
                    server_id = "dc13.utdallas.edu";
                else {
                    server_id = "dc0" + this.rv.getReceiver() + ".utdallas.edu";
                    if (this.node.getNid() < 10)
                        server_id = "dc0" + this.rv.getReceiver() + ".utdallas.edu";
                    else
                        server_id = "dc" + this.rv.getReceiver() + ".utdallas.edu";

                }

                com_port = new Socket(server_id, 5000+this.rv.getReceiver());
*/
                System.out.println("Client handler back in single option!!!!!");

                ObjectOutputStream oos = new ObjectOutputStream(com_port.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(com_port.getInputStream());

                oos.writeObject(this.rv);


            } catch (Exception e) {
                //i--;
                System.out.println("Port of Node " + this.rv.getSender() + " is not ready.");
            }
        }



    }



}
