import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.*;

// Server node
public class Node {

    // Private vairables
    private char nid;
    private ServerSocket port;
    private int component;
    private boolean open_port;
    private int time;

    // Constructor
    public Node(char nid){

        this.nid = nid;
        this.component = 1;

        try {
            int port_num = Character.getNumericValue(nid) + 5000;
            this.port = new ServerSocket(port_num);
            System.out.println("Local port: " + port.getLocalPort() + " created");
        } catch (IOException e){
            e.printStackTrace();
        }

        this.open_port = true;
        this.time=0;
    }

    // Get methods
    public char getNid() { return nid; }
    public ServerSocket getPort() { return port; }
    public int getComponent() { return component; }
    public boolean getOpen_port() { return open_port;}
    public int getTime() { return time;}

    // Set methods
    public void setUpdateTime() { time = time + 1;}
    public void setComponent(int component) { this.component = component;}
}

