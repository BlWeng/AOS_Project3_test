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
                System.out.println("In server");
                request_message in = (request_message) ois.readObject();
                //System.out.println("In ID: " + in.getSender() + " Host node: " + this.node.getNid());
                //System.out.println("Action: " + in.getAct_selected() + " Calling option: " + in.getCalling_action());
                //System.out.println("Iteration in handler: " + in.getIteration());

                //this.node.setReceived_msg(in);
                this.node.setLogical_time_assign_value_pls_one(in.getReq_logical_time());

                request_message processing_msg = new request_message(in);
                processing_msg.setReq_logical_time(this.node.getLogical_time());

                this.node.setBuffer(processing_msg);
/*
                for (int i = 0; i < this.node.getBuffer().size(); i++) {
                    System.out.println("Index: " + i + " ID: " + this.node.getBuffer().get(i).getSender()
                            + " Iteration: " + this.node.getBuffer().get(i).getIteration()
                            + " Action: " + this.node.getBuffer().get(i).getAct_selected());
                }


*/
                //quorum_algorithm_without_MutualExclusion(this.node);
                quorum_algorithm(this.node);

            }

        }catch(Exception e){}

    }

    public void quorum_algorithm(Node node) throws Exception{
        while(!node.getBuffer().isEmpty()){
            //System.out.println("Before fetching, Size of Buffer: " + node.getBuffer().size());
            request_message processing_msg = new request_message(node.getBuffer().get(0));
            node.getBuffer().remove(0);
            //node.setKey_for_next(false);


            //System.out.println("Size of Buffer: " + node.getBuffer().size());
            //System.out.println("First element action: " + processing_msg.getAct_selected() + " Process ID: " + processing_msg.getSender() + " Iteration: " + processing_msg.getIteration());

            if (processing_msg.getAct_selected().equals(request_message.action_options.request))
                node.setStatus(request_message.action_options.request);
            if (processing_msg.getAct_selected().equals(request_message.action_options.release))
                node.setStatus(request_message.action_options.release);
            if (processing_msg.getAct_selected().equals(request_message.action_options.complete))
                node.setStatus(request_message.action_options.complete);
            if (processing_msg.getAct_selected().equals(request_message.action_options.close))
                node.setStatus(request_message.action_options.close);
            if (processing_msg.getAct_selected().equals(request_message.action_options.grant))
                node.setStatus(request_message.action_options.grant);
            if (processing_msg.getAct_selected().equals(request_message.action_options.relinquish))
                node.setStatus(request_message.action_options.relinquish);
            if (processing_msg.getAct_selected().equals(request_message.action_options.enter_cs))
                node.setStatus(request_message.action_options.enter_cs);

            if(node.getNid() < 8 && node.getNid() > 0){
                request_message grant_msg = null;
                request_message release_msg = null;

                switch (node.getStatus()) {
                    //if (processing_msg.getAct_selected().equals(request_message.action_options.request)) {
                    case request:
                        //System.out.println("In QA Thread ID: " + Thread.currentThread().getId());
                        System.out.println("In request " + node.getLock() );
/*
                        System.out.println("In request" + " From " + this.node.getPrevious_task().getSender()
                                + " : " + this.node.getPrevious_task().getIteration()
                                + " : " + this.node.getPrevious_task().getAct_selected()
                                + " CS: " + this.node.getCs_in_use());
*/
/*
                        if(this.node.getInitial_state()){
                            this.node.setInitial_state(false);
                            this.node.setRequest_act_finished(true);
                        }

                        else {
                            if (this.node.getPrevious_task().getAct_selected().equals(request_message.action_options.task_complete))
                                this.node.setRequest_act_finished(true);

                            else this.node.setRequest_act_finished(false);
                        }

                        if(!this.node.getRequest_act_finished() && !this.node.getInitial_state()){
                            if(this.node.getPrevious_task().getSender() > processing_msg.getSender()
                                && this.node.getPrevious_task().getIteration() > processing_msg.getIteration())
                            {
                                this.node.setBuffer(this.node.getPrevious_task());
                                this.node.setRequest_act_finished(true);
                                this.node.setLock(false);
                            }
                        }


                        if(this.node.getRequest_act_finished())
                        {
*/
                            if (!node.getLock() && !node.getCs_in_use()) {
                                System.out.println("In non-locked request");
                                node.setLock(true);


                            /*
                            node.setStatus(request_message.action_options.release);
                            node.setPrvious_id(processing_msg.getSender());
                            node.setPid_iteration(processing_msg.getIteration());
                            */


                                node.setPrevious_task(processing_msg);
                                this.node.setRequest_act_finished(false);

                                //System.out.println("Sender ID in request: " + node.getNid() + " Receiver ID in request: " + processing_msg.getSender());
                                grant_msg = new request_message(node.getNid(), processing_msg.getSender(),
                                        node.getLogical_time(), processing_msg.getIteration(),
                                        request_message.action_options.grant, request_message.calling_option.single);

                                com_requester to_grant = new com_requester(grant_msg, node);
                                to_grant.send();

                            }

                            else if(node.getLock() && !node.getCs_in_use()){
                                System.out.println("In release resource for request");
                                node.setLock(false);
                                node.setBuffer(processing_msg);
                            }

                            else {
                                System.out.println("In locked request");
                                //this.node.setMsg_list(in);
                                //node.setMsg_list(processing_msg);
                                node.setBuffer(processing_msg);
                                /*
                                System.out.println("MSG_list: " + node.getMsg_list().get(0).getSender()
                                        + " Iteration: " + node.getMsg_list().get(0).getIteration()
                                        + " Action: " + node.getMsg_list().get(0).getAct_selected());
                                */

/*
                                node.setBuffer(node.getPrevious_task());
                                if (node.getBuffer().firstElement().equals(node.getPrevious_task())) {
                                    node.getBuffer().remove(0);
                                    node.setReply(false);
                                } else {
                                    processing_msg = new request_message(node.getBuffer().get(0));
                                    node.getBuffer().remove(1);
                                    node.setReply(true);
                                }
*/

                            }
/*
                        }

                        else
                        {
                            System.out.println("Waiting previous task complete!!");
                            this.node.setBuffer(processing_msg);
                        }
                        //this.node.setKey_for_next(true);
*/
                        break;
                    //}


                    //if (processing_msg.getAct_selected().equals(request_message.action_options.release)) {
                    case release:

                        //System.out.println("1st In Release" + " From " + processing_msg.getSender() + " : " + processing_msg.getIteration());
                        //System.out.println("2nd In Release" + " From " + this.node.getPrevious_task().getSender() + " : " +this.node.getPrevious_task().getIteration());
/*
                        if((this.node.getPrevious_task().getSender() == processing_msg.getSender()
                                && this.node.getPrevious_task().getIteration() == processing_msg.getIteration()
                                )
                        )
                        {

                            System.out.println("In previous task setting!!!");
                           this.node.getPrevious_task().setAct_selected(request_message.action_options.task_complete);
*/
                        if(node.getCs_in_use()) {
                            node.setLock(false);
                            node.setCs_in_use(false);
                        }
                        else
                            node.setBuffer(processing_msg);
/*
                            if (!node.getMsg_list().isEmpty()) {
                                System.out.println("In mag MSG list.");
                                //node.setBuffer(node.getMsg_list().get(0));
                                //node.getMsg_list().remove(0);
                                node.getBuffer().addAll(0, node.getMsg_list());
                                node.getMsg_list().clear();
                            }

                        }

                        else
                        {

                            this.node.setBuffer(processing_msg);
                        }
*/
                        break;
                    //}

                    case enter_cs:
                        //System.out.println("Resetting CS!!!!!!!");
                        //System.out.printl("Node of " + node.getNid() + ", number of message sent: " + node.getNm_msg_sent() + "\t");
                        //System.out.print("Node of " + node.getNid() + ", number of message received: " + node.getNm_msg_received() + "\r");
                        node.setCs_in_use(true);
                        break;

                    case close:
                        //this.node.getBuffer().remove(processing_msg);
                        //System.out.println("In relinquish");
                        System.out.println("Close node: " + node.getNid());
                        node.getPort().close();
                        node.setKey_for_next(true);
                        break;

                    default:
                        System.out.println("Wrong action in resource.");
                        break;

                }
            }


            else if(node.getNid()>=8){

                switch(node.getStatus()){

                    //if (processing_msg.getAct_selected().equals(request_message.action_options.grant)) {
                    case grant:
                        System.out.println("In Grant");
                        System.out.println("First Pop-out ID: " + processing_msg.getSender() + " Action: " + processing_msg.getAct_selected());
                        //this.node.getBuffer().remove(processing_msg);
                        //System.out.println("Remainder size of buffer: " + this.node.getBuffer().size() + " First Element: " + this.node.getBuffer().get(0).getSender());
                        node.getAimed_quorum(processing_msg.getIteration()).replace(Integer.toString(processing_msg.getSender()), true);
                        //node.setKey_for_next(true);
                        break;
                    //}

                    //if (processing_msg.getAct_selected().equals(request_message.action_options.relinquish)) {
                    case relinquish:
                        //this.node.getBuffer().remove(processing_msg);
                        //System.out.println("In relinquish");
                        node.getAimed_quorum(processing_msg.getIteration()).replace(Integer.toString(processing_msg.getSender()), false);
                        //node.setKey_for_next(true);
                        break;
                    //}
                    case close:
                        //this.node.getBuffer().remove(processing_msg);
                        //System.out.println("In relinquish");
                        System.out.println("Close node: " + node.getNid());
                        node.getPort().close();
                        node.setKey_for_next(true);
                        break;

                    default:
                        System.out.println("Wrong action in client.");

                }
            }

            else if(node.getNid() == 0){
                switch(node.getStatus()) {
                    //if (processing_msg.getAct_selected().equals(request_message.action_options.complete)) {
                    case complete:
                        //this.node.getBuffer().remove(processing_msg);
                        System.out.println("In Complete");
                        System.out.println("Completion of Node " + processing_msg.getSender());
                        System.out.println("Node of " + node.getNid() + ", number of message sent: " + node.getNm_msg_sent());
                        System.out.println("Node of " + node.getNid() + ", number of message received: " + node.getNm_msg_received());
                        if (node.getNm_msg_received() == 5) {
                            System.out.println("Receive all request from clients!");
                            request_message close_msg = new request_message(node.getNid(), -1, node.getLogical_time(), node.getNm_msg_received(),
                                    request_message.action_options.close, request_message.calling_option.shut_down);
                            com_requester close_server = new com_requester(close_msg, node);
                            close_server.send();

                        }
                        break;
                    //}

                    //if (processing_msg.getAct_selected().equals(request_message.action_options.close)) {
                    case close:
                        //this.node.getBuffer().remove(processing_msg);
                        //System.out.println("In relinquish");
                        System.out.println("Close node: " + node.getNid());
                        node.getPort().close();
                        node.setKey_for_next(true);
                        break;
                    //}

                    default:
                        System.out.println("Wrong action in client.");
                }
            }
        }
    }

    public void quorum_algorithm_without_MutualExclusion(Node node) throws Exception{
        while(!node.getBuffer().isEmpty()){
            System.out.println("Before fetching, Size of Buffer: " + node.getBuffer().size());
            request_message processing_msg = new request_message(node.getBuffer().get(0));
            node.getBuffer().remove(0);
            //node.setKey_for_next(false);


            System.out.println("Size of Buffer: " + node.getBuffer().size());
            System.out.println("First element action: " + processing_msg.getAct_selected() + " Process ID: " + processing_msg.getSender() + " Iteration: " + processing_msg.getIteration());


            if(node.getNid() < 8 && node.getNid() > 0){
                request_message grant_msg = null;
                request_message release_msg = null;

                switch (processing_msg.getAct_selected()) {
                    case request:
                        System.out.println("In request " + node.getLock() );

                        System.out.println("In request" + " From " + this.node.getPrevious_task().getSender()
                                + " : " + this.node.getPrevious_task().getIteration()
                                + " : " + this.node.getPrevious_task().getAct_selected()
                                + " CS: " + this.node.getCs_in_use());


                        if (!node.getLock()) {
                            System.out.println("In non-locked request");
                            node.setLock(true);

                            node.setPrevious_task(processing_msg);
                            this.node.setRequest_act_finished(false);

                            System.out.println("Sender ID in request: " + node.getNid() + " Receiver ID in request: " + processing_msg.getSender());
                            grant_msg = new request_message(node.getNid(), processing_msg.getSender(),
                                    node.getLogical_time(), processing_msg.getIteration(),
                                    request_message.action_options.grant, request_message.calling_option.single);

                            com_requester to_grant = new com_requester(grant_msg, node);
                            to_grant.send();

                        }

                        else {
                            System.out.println("In locked request");
                            node.setBuffer(processing_msg);

                        }

                        break;

                    case release:

                        System.out.println("1st In Release" + " From " + processing_msg.getSender() + " : " + processing_msg.getIteration());
                        System.out.println("2nd In Release" + " From " + this.node.getPrevious_task().getSender() + " : " +this.node.getPrevious_task().getIteration());

                        if(node.getCs_in_use()) {
                            node.setLock(false);
                        }
                        else
                            node.setBuffer(processing_msg);

                        break;

                    case enter_cs:
                        System.out.println("Resetting CS!!!!!!!");
                        node.setCs_in_use(true);
                        break;

                    default:
                        System.out.println("Wrong action in resource.");
                        break;

                }
            }


            else if(node.getNid()>=8){

                switch(processing_msg.getAct_selected()){

                    case grant:
                        System.out.println("In Grant");
                        System.out.println("First Pop-out ID: " + processing_msg.getSender() + " Action: " + processing_msg.getAct_selected());
                        node.getAimed_quorum(processing_msg.getIteration()).replace(Integer.toString(processing_msg.getSender()), true);
                        break;

                    case relinquish:
                        node.getAimed_quorum(processing_msg.getIteration()).replace(Integer.toString(processing_msg.getSender()), false);
                        break;
                    default:
                        System.out.println("Wrong action in client.");
                }
            }

            else if(node.getNid() == 0){
                switch(processing_msg.getAct_selected()) {
                    case complete:
                        System.out.println("In Complete");
                        System.out.println("Completion of Node " + processing_msg.getSender());
                        System.out.println("Node of " + node.getNid() + ", number of message sent: " + node.getNm_msg_sent());
                        System.out.println("Node of " + node.getNid() + ", number of message received: " + node.getNm_msg_received());
                        if (node.getNm_msg_received() == 5) {
                            System.out.println("Receive all request from clients!");
                            request_message close_msg = new request_message(node.getNid(), -1, node.getLogical_time(), node.getNm_msg_received(),
                                    request_message.action_options.close, request_message.calling_option.shut_down);
                            com_requester close_server = new com_requester(close_msg, node);
                            close_server.send();

                        }
                        break;
                    case close:

                        System.out.println("Close node: " + node.getNid());
                        node.getPort().close();
                        node.setKey_for_next(true);
                        break;

                    default:
                        System.out.println("Wrong action in client.");
                }
            }
        }
    }



}




