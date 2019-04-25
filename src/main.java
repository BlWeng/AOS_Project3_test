import java.io.*;
import java.util.*;
import java.lang.Math;

/////////test for github

public class main {
    public static void main(String[] args){

        Hashtable<Character, Integer[]> char_component = new Hashtable<Character, Integer[]>();
        char_component.put('A', new Integer[] {1, 2, 4});
        char_component.put('B', new Integer[] {1, 2, 5, 8});
        char_component.put('C', new Integer[] {1, 2, 5, 8});
        char_component.put('D', new Integer[] {1, 2, 5, 8});
        char_component.put('E', new Integer[] {1, 3, 6, 8});
        char_component.put('F', new Integer[] {1, 3, 6, 8});
        char_component.put('G', new Integer[] {1, 3, 6, 8});
        char_component.put('H', new Integer[] {1, 3, 7});

        Node node = new Node(args[0].charAt(0));
//        System.out.println("Node:");
//        Scanner cn = new Scanner(System.in).useDelimiter("\\s");
//        Node node = new Node(cn.next().charAt(0));

        try{

            // Create server port
            Thread server = new Thread(new com_server(node));
            server.start();

            Scanner cn;
            // Continuous check sending
            while (node.getTime() < 8){

                System.out.println("Request WRITE? (y/n)");
                cn = new Scanner(System.in);

                // Send WRITE
                if (cn.next().equals("y")){

                    node.setUpdateTime();

                    ArrayList<Character> nodes_to_send = new ArrayList<Character>();
                    nodes_to_send = get_nodes_of_component(char_component, node.getComponent());

                    System.out.println("Nodes to send are:" + nodes_to_send);
                    // Send message to each node
                    for (Character c: nodes_to_send) {
                        if (c != node.getNid()){

                            request_message msg = new request_message(node.getNid(), c);
                            com_requester reqtr = new com_requester(msg);
                            reqtr.send();
                            System.out.println("Message:" + msg +" (sent)");
                        }
                    }

                    node.setComponent(get_updated_component(char_component, node));
                }
                else {
                    continue;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Character> get_nodes_of_component(Hashtable<Character, Integer[]> hash, int component){

        ArrayList<Character> nodes = new ArrayList<Character>();

        Enumeration enu = hash.keys();

        while (enu.hasMoreElements()){

            Character temp_node = new Character(enu.nextElement().toString().charAt(0));
            for (Integer c: hash.get(temp_node)) {
                if (c == component){
                    nodes.add(temp_node);
                }
            }
        }

        return nodes;
    }

    public static int get_updated_component(Hashtable<Character, Integer[]> hash, Node node){

        int candidate_component = candidate_component = node.getComponent();
        int min_component = min_component = (int) Math.pow(2, node.getTime() / 2);
        int max_component = max_component = (int) Math.pow(2, node.getTime() / 2 + 1);

        if (node.getComponent() < min_component ){

            for (int c: hash.get(node.getNid())) {
                if (c >= min_component && c < max_component){
                    candidate_component = c;
                    break;
                }
            }
        }

        return candidate_component;
    }
}
