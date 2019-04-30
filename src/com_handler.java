import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

//public class com_handler  implements Runnable {
public class com_handler implements Runnable, Serializable {


    private int id;
    private Socket in_obj = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private Node node;
    private Thread worker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private int interval = 1000;
    public enum duty{
        server,
        client
    }
    private duty ply_role;


    // constructor
    public com_handler(int id, Socket in_obj, Node node, duty in_role, ObjectInputStream ois, ObjectOutputStream oos) {
        this.id = id;
        this.in_obj = in_obj;
        this.node = node;
        this.worker = Thread.currentThread();
        this.ply_role = in_role;
        this.ois = ois;
        this.oos = oos;
    }


    @Override
    public void run() {

        System.out.println("[*] COM_HANDLER thread is created, from " + in_obj.getInetAddress() + ".");

        try {

            running.set(true);

            while (running.get()) {

                request_message in = (request_message) ois.readObject();
                System.out.println(">> In ID: " + in.getSender() + " Host node: " + this.node.getNid());
                System.out.println(">> Action: " + in.getAct_selected() + " Calling option: " + in.getCalling_action());

                this.node.setLogical_time_assign_value_pls_one(in.getReq_logical_time());

                request_message processing_msg = new request_message(in);
                processing_msg.setReq_logical_time(this.node.getLogical_time());

                // On receiving VOTE_REQUEST
                if(in.getAct_selected().equals(request_message.action_options.VOTE_REQUEST)){

                    // Wait to lock local manager
                    while (true){
                        if (node.getLock() == false){
                            node.setLock(true);
                            break;
                        }
                    }

                    request_message reply_msg =
                            new request_message(node.getNid(), in.getSender(), node.getLogical_time(),
                            node.getVN(), node.getSC(), node.getDS(),
                            request_message.action_options.VOTE_REQUEST_REPLY, request_message.calling_option.single);

                    com_requester reply = new com_requester(reply_msg, node);
                    reply.send();
                    System.out.println(">> Sent VOTE_REQUEST_REPLY to node: " + node.getNid());
                }

                // On receiving VOTE_REQUEST_REPLY
                if(in.getAct_selected().equals(request_message.action_options.VOTE_REQUEST_REPLY)){
                    this.node.setBuffer(in);
                }

                // On receiving CATCH_UP_REPLY
                if (in.getAct_selected().equals(request_message.action_options.CATCH_UP_REPLY)){

                    node.setVN(in.getS_VN());
                    node.setIsCurrent(true);
                    System.out.println(">> Current file is the newest copy. (isCurrent: " + node.getIsCurrent() + ")");
                }

                // On receiving CATCH_UP
                if(in.getAct_selected().equals(request_message.action_options.CATCH_UP)){

                    request_message reply_catchup_msg = new request_message(node.getNid(), in.getSender(), node.getLogical_time(),
                            node.getVN(), node.getSC(), node.getDS(),
                            request_message.action_options.CATCH_UP_REPLY, request_message.calling_option.single);
                    com_requester reply_catchup_er = new com_requester(reply_catchup_msg, node);
                    reply_catchup_er.send();
                    System.out.println(">> Sent CATCH_UP_REPLY to node: " + node.getNid());
                }

                // On receiving COMMIT
                if(in.getAct_selected().equals(request_message.action_options.COMMIT)){
                    this.node.setVN(in.getS_VN());
                    this.node.setSC(in.getS_SC());
                    this.node.setDS(in.getS_DS());
                    node.setLock(false);
                    System.out.println(">> Updated variables: [VN: " + node.getVN() +" SC: " + node.getSC() + " DS: " + node.getDS() + "]");
                }

                // On receiving ABORT
                if(in.getAct_selected().equals(request_message.action_options.ABORT)){
                    // Release resource
                    this.node.setLock(false);
                }
            }
        }catch(Exception e){}
    }
}