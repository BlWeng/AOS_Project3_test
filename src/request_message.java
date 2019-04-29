import java.io.Serializable;
import java.util.ArrayList;

public class request_message implements Serializable {

    private char sender;

    private char receiver;

    public enum action_options{
        VOTE_REQUEST,
        VOTE_REQUEST_REPLY,
        CATCH_UP,
        COMMIT,
        ABORT,
        grant,
        release,
        complete,
        relinquish,
        close,
        initial,
        enter_cs
    }
    private action_options act_selected;


    private int req_logical_time;


    public enum calling_option{
        broadcast_clique,
        single,
        shut_down
    }

    private calling_option calling_act;

    //private int iteration;

    private boolean read_granted;

    private int s_VN;
    private int s_SC;
    private ArrayList<Character> s_DS;

    //public request_message(char in_sender, char in_receiver, int in_req_time,
    //                       int in_iteration, action_options in_act_s, calling_option in_opt){
    public request_message(char in_sender, char in_receiver, int in_req_time,
                           int in_s_VN, int in_s_SC, ArrayList<Character> in_s_DS,
                           action_options in_act_s, calling_option in_opt){
        this.sender = in_sender;
        this.receiver = in_receiver;
        this.req_logical_time = in_req_time;
        //this.iteration = in_iteration;
        this.act_selected = in_act_s;
        this.calling_act = in_opt;

        this.s_VN = in_s_VN;
        this.s_SC = in_s_SC;
        this.s_DS = new ArrayList<>(in_s_DS);
    }


    public request_message(request_message in_req_msg){
        this.sender = in_req_msg.getSender();
        this.receiver = in_req_msg.getReceiver();
        this.req_logical_time = in_req_msg.getReq_logical_time();
        //this.iteration = in_req_msg.getIteration();
        this.calling_act = in_req_msg.getCalling_action();
        this.act_selected = in_req_msg.getAct_selected();

        this.s_VN = in_req_msg.getS_VN();
        this.s_SC = in_req_msg.getS_SC();
        this.s_DS = new ArrayList<>(in_req_msg.getS_DS());
    }


/*
    public request_message(int p_id, int p_it, action_options p_at){
        this.sender = p_id;
        this.iteration = p_it;
        this.act_selected = p_at;
    }
*/


    public char getSender() { return this.sender; }

    public char getReceiver() { return  this.receiver;}

    public action_options getAct_selected() {return this.act_selected;}

    public int getReq_logical_time() {return this.req_logical_time;}

    public calling_option getCalling_action() {return this.calling_act;}

    //public int getIteration() {return  this.iteration;}

    public boolean getRead_granted() { return read_granted;}

    public int getS_VN() {return s_VN;}

    public int getS_SC() {return s_SC;}

    public ArrayList<Character> getS_DS() { return s_DS;}


    public void setSender(char dv) {this.sender = dv;}

    public void setReceiver(char dv) {this.receiver=dv;}

    public void setAct_selected(action_options dv) {this.act_selected = dv;}

    public void setReq_logical_time(int dv) {this.req_logical_time = dv;}

    public void setCalling_action(calling_option dv) {this.calling_act = dv;}

    //public void setIteration(int dv) {this.iteration = dv;}

    public void setRead_granted(boolean dv) {this.read_granted = dv;}

    public void setS_VN(int dv) {this.s_VN = dv;}

    public void setS_SC(int dv) {this.s_SC = dv;}

    public void setS_DS(ArrayList<Character> dv) {
        this.s_DS.clear();
        this.s_DS = new ArrayList<>(dv);
    }


}
