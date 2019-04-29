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
        try {
            running.set(true);
            //System.out.println("Local IP " + this.in_obj.getLocalSocketAddress() + " Remote IP: " + this.in_obj.getRemoteSocketAddress());

            while (running.get()) {

                //System.out.println("In handler, ID: " + Thread.currentThread().getId());
                //System.out.println("In server");
                request_message in = (request_message) ois.readObject();
                System.out.println("In ID: " + in.getSender() + " Host node: " + this.node.getNid());
                System.out.println("Action: " + in.getAct_selected() + " Calling option: " + in.getCalling_action());
                //System.out.println("Iteration in handler: " + in.getIteration());

                //this.node.setReceived_msg(in);
                this.node.setLogical_time_assign_value_pls_one(in.getReq_logical_time());

                request_message processing_msg = new request_message(in);
                processing_msg.setReq_logical_time(this.node.getLogical_time());

/*
                this.node.setBuffer(processing_msg);

                for (int i = 0; i < this.node.getBuffer().size(); i++) {
                    System.out.println("Index: " + i + " ID: " + this.node.getBuffer().get(i).getSender()
                            + " Iteration: " + this.node.getBuffer().get(i).getIteration()
                            + " Action: " + this.node.getBuffer().get(i).getAct_selected());
                }
*/
                if(in.getAct_selected().equals(request_message.action_options.VOTE_REQUEST)){
                    request_message reply_msg =
                            new request_message(node.getNid(), in.getSender(), node.getLogical_time(),
                            node.getVN(), node.getSC(), node.getDS(),
                            request_message.action_options.VOTE_REQUEST_REPLY, request_message.calling_option.single);

                    com_requester reply = new com_requester(reply_msg, node);
                    reply.send();

                }

                if(in.getAct_selected().equals(request_message.action_options.VOTE_REQUEST_REPLY)){
                    this.node.setBuffer(in);
                }

                if(in.getAct_selected().equals(request_message.action_options.CATCH_UP)){
                    this.node.setVN(in.getS_VN());
                }

                if(in.getAct_selected().equals(request_message.action_options.COMMIT)){
                    this.node.setVN(in.getS_VN());
                    this.node.setSC(in.getS_SC());
                    this.node.getDS().clear();
                    this.node.setDS(in.getS_DS());
                }

                if(in.getAct_selected().equals(request_message.action_options.ABORT)){
                    // Release resource
                }




            }

        }catch(Exception e){}

    }




}




