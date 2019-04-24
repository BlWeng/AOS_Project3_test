import java.io.Serializable;

public class request_message implements Serializable {

    private int sender;

    private int receiver;

    public enum action_options{
        request,
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

    private int iteration;

    private boolean read_granted;

    public request_message(int in_sender, int in_receiver, int in_req_time,
                           int in_iteration, action_options in_act_s, calling_option in_opt){
        this.sender = in_sender;
        this.receiver = in_receiver;
        this.req_logical_time = in_req_time;
        this.iteration = in_iteration;
        this.act_selected = in_act_s;
        this.calling_act = in_opt;
    }


    public request_message(request_message in_req_msg){
        this.sender = in_req_msg.getSender();
        this.receiver = in_req_msg.getReceiver();
        this.req_logical_time = in_req_msg.getReq_logical_time();
        this.iteration = in_req_msg.getIteration();
        this.calling_act = in_req_msg.getCalling_action();
        this.act_selected = in_req_msg.getAct_selected();
    }

/*
    public request_message(int p_id, int p_it, action_options p_at){
        this.sender = p_id;
        this.iteration = p_it;
        this.act_selected = p_at;
    }
*/


    public int getSender() { return this.sender; }

    public int getReceiver() { return  this.receiver;}

    public action_options getAct_selected() {return this.act_selected;}

    public int getReq_logical_time() {return this.req_logical_time;}

    public calling_option getCalling_action() {return this.calling_act;}

    public int getIteration() {return  this.iteration;}

    public boolean getRead_granted() { return read_granted;}


    public void setSender(int dv) {this.sender = dv;}

    public void setAct_selected(action_options dv) {this.act_selected = dv;}

    public void setReq_logical_time(int dv) {this.req_logical_time = dv;}

    public void setCalling_action(calling_option dv) {this.calling_act = dv;}

    public void setIteration(int dv) {this.iteration = dv;}

    public void setReceiver(int dv) {this.receiver=dv;}

    public void setRead_granted(boolean dv) {this.read_granted = dv;}


}
