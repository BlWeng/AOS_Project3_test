import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.*;


public class Node implements Serializable{
    //private int nid; // ID of Node
    private char nid; // ID of Node

    private int logical_time;

    private ServerSocket port;

    private ArrayList<request_message>  msg_list = new ArrayList<>();

    private Vector<Hashtable> aimed_quorum = new Vector<>();

    private boolean lock;

    private int iteration;

    private Vector<request_message> read_granted = new Vector<>();

    private Vector<request_message> buffer = new Vector<>();

    private int nm_msg_sent;

    private int nm_msg_received;

    private boolean cs_permission;

    private boolean checker;

    private boolean request_act_finished;

    private boolean open_port;

    private boolean key_for_next;

    private request_message.action_options status;

    private boolean initial_state;

    private int prvious_id;

    private int pid_iteration;

    private boolean unit_task_completed;

    private request_message previous_task;

    private boolean cs_in_use;

    private boolean reply;

    private boolean mutual_exclusion;

    private int VN;

    private int SC;

    private ArrayList<Character> DS;

    private int max;

    private ArrayList<Character> P;

    private ArrayList<Character> I;

    private boolean isDistinguished;

    // Constructor of Node
    public Node(char nid) {
        this.nid = nid; // assign node ID


        try {
/*
            if (this.nid == 0)
                this.port = new ServerSocket(13 + 5000);
            else
*/
                this.port = new ServerSocket( (int)this.nid + 5000);

        } catch (IOException e) {
            e.printStackTrace();
        }


        this.logical_time = 0;

        this.lock = false;

        this.iteration = 0;

        this.cs_permission = false;

        this.checker = false;

        this.open_port = true;

        this.initial_state = true;

        this.key_for_next = true;

        this.request_act_finished = false;

        //this.previous_task = new request_message(' ', ' ', 0, 0,
        //        request_message.action_options.initial, request_message.calling_option.broadcast_clique);

        this.cs_in_use = false;

        this.reply = false;


    }



    // Get Functions
    public char getNid() { return nid;}

    public int getLogical_time() {return logical_time;}

    public ServerSocket getPort() { return port; }

    public ArrayList<request_message> getMsg_list() {return msg_list;}

    public boolean getLock() {return lock;}

    public Hashtable getAimed_quorum(int index){return aimed_quorum.get(index);}

    public Vector<Hashtable> getWhole() {return aimed_quorum;}

    public int getIteration(){return iteration;}

    public Vector<request_message> getRead_granted() { return read_granted; }

    public Vector<request_message> getBuffer() {return buffer;}

    public int getNm_msg_sent() {return nm_msg_sent;}

    public int getNm_msg_received() {return nm_msg_received;}

    public boolean getCs_permission() {return cs_permission;}

    public boolean getChecker() {return  checker;}

    public boolean getRequest_act_finished() {return request_act_finished;}

    public boolean getOpen_port() {return open_port;}

    public boolean getKey_for_next() {return key_for_next;}

    public request_message.action_options getStatus() {return status;}

    public boolean getInitial_state() {return initial_state;}

    public int getPrvious_id() {return prvious_id;}

    public int getPid_iteration() {return pid_iteration;}

    public boolean getUnit_task_completed() {return unit_task_completed;}

    public request_message getPrevious_task() {return previous_task;}

    public boolean getCs_in_use() {return cs_in_use;}

    public boolean getReply() {return reply;}

    public boolean getMutual_exclusion() {return mutual_exclusion;}

    public int getVN() {return VN;}

    public int getSC() {return SC;}

    public ArrayList<Character> getDS() {return DS;}

    public int getMax() {return max;}

    public ArrayList<Character> getP() {return P;}

    public ArrayList<Character> getI() {return I;}

    public boolean getIsDistinguished() {return isDistinguished;}

    // Set Functions

    public void setLogical_time_unit_increase() {logical_time++;}
    public void setLogical_time_assign_value_pls_one( int a_t) { logical_time = a_t + 1; }



    public void setMsg_list(request_message dv) {
        msg_list.add(dv);
/*
        Collections.sort(msg_list, new Comparator<request_message>() {
            @Override
            public int compare(request_message o1, request_message o2) {
                return o1.getReq_logical_time() - o2.getReq_logical_time();
            }
        });
*/
    }

    public void setLock(boolean dv) {lock = dv;}

    public void setAimed_quorum(int d_index, Hashtable dv){ aimed_quorum.add(d_index,dv);}

    public void setIteration(int dv) {iteration=dv;}

    public void setRead_granted( request_message dv) {read_granted.add(dv);}

    public void UpdateRead_granted(int index, Boolean dv) {read_granted.get(index).setRead_granted(dv);}

    public void setBuffer(request_message dv) {

        buffer.add(dv);

/*
        Vector<request_message> pending = new Vector<>();

        if(dv.getAct_selected().equals(request_message.action_options.release))
            buffer.add(dv);
        else
            pending.add(dv);
*/


        Collections.sort(buffer, new Comparator<request_message>() {
            @Override
            public int compare(request_message o1, request_message o2) {
                    return o1.getSender() -  o2.getSender();
            }
        });


/*
        Collections.sort(pending, new Comparator<request_message>() {
            @Override
            public int compare(request_message o1, request_message o2) {
                if (o1.getReq_logical_time() == o2.getReq_logical_time())
                    return o1.getSender()-o2.getSender();
                else
                    return o1.getReq_logical_time() - o2.getReq_logical_time();
            }
        });

        buffer.addAll(pending);
*/
/*
        for(int i=0; i < buffer.size() ; i++){
            System.out.println("Index: " + i+ " ID: " + buffer.get(i).getSender()+" Iteration: " + buffer.get(i).getIteration());
        }
*/

    }


    public void setNm_msg_sent(){nm_msg_sent++;}

    public void resetNm_msg_sent(){nm_msg_sent=0;}

    public void setNm_msg_received() {nm_msg_received++;}

    public void resetsetNm_msg_received() {nm_msg_received=0;}

    public void setCs_permission(boolean dv) {cs_permission = dv;}

    public void setChecker(boolean dv) {checker =dv;}

    public void setRequest_act_finished(boolean dv) {request_act_finished = dv;}

    public void setKey_for_next(boolean dv) {key_for_next = dv;}

    public void setStatus(request_message.action_options dv) {status = dv;}

    public void setInitial_state(boolean dv) {initial_state = dv;}

    public void setPrvious_id(int dv) {prvious_id = dv;}

    public void setPid_iteration(int dv) {pid_iteration = dv;}

    public void setUnit_task_completed(boolean dv) {unit_task_completed =dv;}

    public void setPrevious_task(request_message dv) {previous_task = new request_message(dv);}

    public void setCs_in_use(boolean dv) {cs_in_use = dv;}

    public void setReply(boolean dv) {reply = dv;}

    public void setMutual_exclusion(boolean dv) {mutual_exclusion = dv;}

    public void setVN(int dv) { VN = dv;}

    public void setSC(int dv) { SC = dv;}

    public void setDS(ArrayList<Character> dv ) { DS = new ArrayList<>(dv);}

    public void setMax(int dv) {max = dv;}

    public void setP(ArrayList<Character> dv ) {P = new ArrayList<>(dv);}

    public void setI(ArrayList<Character> dv ) {I = new ArrayList<>(dv);}

    public void setIsDistinguished(boolean dv) {isDistinguished = dv;}
}

