import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.*;


public class Node implements Serializable{


    // Private variables
    private char nid; // ID of Node
    private int logical_time;
    private ServerSocket port;
    private boolean lock;
    private Vector<request_message> buffer = new Vector<>();

    private boolean isCurrent;
    private int VN;
    private int SC;
    private ArrayList<Character> DS;
    private ArrayList<Character> P;
    private ArrayList<Character> I;
    private boolean isDistinguished;
    private int N;
    private int Max;
    private char newestNode;
    private int request_time;


    // Constructor of Node
    public Node(char nid) {
        this.nid = nid; // assign node ID

        try {
            this.port = new ServerSocket(InetPort.get_port(nid), 0, InetPort.get_addr(nid));
            System.out.println("[*] Server Port: " + ((int)this.nid + 5000) + " created.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        VN = 1;
        SC = 8;
        DS = new ArrayList<Character>('A');
        isDistinguished = false;

        this.logical_time = 0;
        this.lock = false;
    }


    // Get Functions
    public char getNid() { return nid;}
    public int getLogical_time() {return logical_time;}
    public ServerSocket getPort() { return port; }
    public boolean getLock() {return lock;}
    public Vector<request_message> getBuffer() {return buffer;}
    public int getVN() {return VN;}
    public int getSC() {return SC;}
    public ArrayList<Character> getDS() {return DS;}
    public int getMax() {return Max;}
    public ArrayList<Character> getP() {

        ArrayList<Character> P = new ArrayList<Character>();
        P.add(this.nid);

        for (int i=0; i < getBuffer().size(); i++){

            P.add(getBuffer().get(i).getSender());
        }
        System.out.println(">> P set: " + P);
        return P;
    }
    public char getLargestInP (ArrayList<Character> P){

        char c = nid;

        for (int i=0; i < P.size(); i++){
            if ((char)P.get(i) < c){
                c = (char)P.get(i);
            }
        }

        System.out.println(">> Largest node: " + c);
        return c;
    }
    public ArrayList<Character> getI() {return I;}
    public boolean getIsDistinguished() {return isDistinguished;}
    public boolean getIsCurrent() {return isCurrent;}
    public int getN() {return N;}
    public char getNewestNode() {return newestNode;}
    public int getRequest_time() {return request_time;}

    // Set Functions
    public void setLogical_time_unit_increase() {logical_time++;}
    public void setLogical_time_assign_value_pls_one( int a_t) { logical_time = a_t + 1; }
    public void setLock(boolean dv) {lock = dv;}
    public void setBuffer(request_message dv) {

        buffer.add(dv);

        Collections.sort(buffer, new Comparator<request_message>() {
            @Override
            public int compare(request_message o1, request_message o2) {
                    return o1.getSender() -  o2.getSender();
            }
        });
    }
    public void setVN(int dv) { VN = dv;}
    public void setSC(int dv) { SC = dv;}
    public void setDS(ArrayList<Character> dv ) { DS = new ArrayList<>(dv);}
    public void setMax(int dv) {Max = dv;}
    public void setP(ArrayList<Character> dv ) {P = new ArrayList<>(dv);}
    public void setI(ArrayList<Character> dv ) {I = new ArrayList<>(dv);}
    public void setIsDistinguished(boolean dv) {isDistinguished = dv;}
    public void setIsCurrent(boolean dv) {isCurrent=dv;}
    public void setN(int dv) {N = dv;}
    public void setNewestNode(char dv) {newestNode = dv;}
    public void setRequest_time(int dv) { request_time = dv; }
}