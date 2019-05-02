import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class main {
    public static void main(String[] args) {

        // Init Node
        Node node = new Node(args[0].charAt(0));
        int iteration;

        try {

            // Start to listen
            Thread server = new Thread(new com_server(node));
            server.start();

                while (true) {
                    try {
                        // Read from user: Request or not?
                        System.out.println("[*] Would you like to make request? (y/n)");
                        Scanner cmd = new Scanner(System.in).useDelimiter("\\s");

                        // Send Request
                        if (cmd.next().equals("y")) {
/*
                            // Get Set of nodes to send
                            System.out.println("Please enter desire level for partition testing. (0-3)");
                            Scanner p_cmd = new Scanner(System.in).useDelimiter("\\s");

                            int iteration = p_cmd.nextInt();
*/
                            iteration = node.getRequest_time() / 2 % 4;

                            node.setRequest_time(node.getRequest_time()+1);

                            String selected_set = partition_selected(node, iteration);
                            ArrayList<Character> connection_set = new ArrayList<>();

                            if(selected_set.equals("self"))
                                connection_set.add(node.getNid());
                            else
                                for(char it:selected_set.toCharArray())
                                    connection_set.add(it);

                            System.out.println("Selected iteration: " +iteration);
                            System.out.println("Selected partition: " + connection_set.toString());

                            Hashtable quorum_set = new Hashtable<>();
                            for (char t_in : connection_set)
                                quorum_set.put(t_in, false);

                            // Lock local manager
                            while (true){
                                if (!node.getLock()){
                                    node.setLock(true);
                                    break;
                                }
                            }
                            System.out.println(">> Lock local manager succeed");

                            // Send Request messages to subordinates
                            request_message req_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                                            node.getVN(), node.getSC(), node.getDS(),
                                            request_message.action_options.VOTE_REQUEST, request_message.calling_option.broadcast_clique);

                            com_requester req_begin = new com_requester(req_msg, connection_set, node);
                            req_begin.send();

                            // Wait for responses
                            while(true){
                                if (node.getBuffer().size() == connection_set.size()-1){
                                    System.out.println(">> Has received " + node.getBuffer().size() + "messages");
                                    break;
                                }
                            }

                            Is_Distinguished(node, connection_set);

                            if(node.getIsDistinguished()){
                                // Get the most current copy
                                Catch_up(node);

                                while (!node.getIsCurrent()){
                                    Thread.sleep(500);
                                    System.out.print(node.getIsCurrent());
                                }

                                // Update files
                                Do_Updated(node, connection_set);

                            } else {

                                System.out.println(">> Aborted. (Current status: [VN: " + node.getVN() + " SC: " + node.getSC() + " DS: " + node.getDS() + " )");

                                // Release local manager lock
                                node.setLock(false);

                                // Send Abort to participants
                                request_message abort_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                                        node.getVN(), node.getSC(), node.getDS(),
                                        request_message.action_options.ABORT, request_message.calling_option.broadcast_clique);
                                com_requester abort_begin = new com_requester(abort_msg, connection_set, node);
                                abort_begin.send();
                            }
                            System.out.println(">> Updated variables: [VN: " + node.getVN() +" SC: " + node.getSC() + " DS: " + node.getDS() + "]");
                            //node.setRequest_time(node.getRequest_time()+1);
                            node.getBuffer().clear();

                    }
                    else {
                        System.out.println("Goodbye!!!");
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e) {e.printStackTrace();}

    }

    private static String partition_selected(Node node, int iteration){
        String target= "";
        switch(iteration){
            case 0:
                target = "ABCDEFGH";
                break;
            case 1:
                if(node.getNid() - 'D' <= 0)
                   target = "ABCD";
                else
                    target = "EFGH";
                break;
            case 2:
                if(node.getNid()- 'A' == 0 || node.getNid() - 'H' ==0)
                    target = "self";
                else if(node.getNid() - 'B' >=0 && node.getNid() - 'D' <=0)
                    target = "BCD";
                else if(node.getNid() - 'E' >=0 && node.getNid() - 'G' <=0)
                    target = "EFG";
                break;
            case 3:
                if(node.getNid() == 'A'  || node.getNid() == 'H')
                    target = "self";
                else if(node.getNid() - 'B' >=0 && node.getNid() - 'G' <=0)
                target = "BCDEFG";
                break;
        }

        return target;
    }

    private static void Is_Distinguished(Node node, ArrayList<Character> subordinates){


        // Compute:
        //   MaxVN: VN of the newest updated node
        //   N: Number of nodes having MaxVN
        //   node_to_catch_up: Node id of the newest updated node
        int MaxVN = node.getVN();
        char node_to_catch_up = node.getNid();
        int N=node.getSC();

        for (int i=0; i<node.getBuffer().size(); i++){

            int temp_vn = node.getBuffer().get(i).getS_VN();

            if (temp_vn > MaxVN){

                node_to_catch_up = node.getBuffer().get(i).getSender();
                MaxVN = temp_vn;
                N = node.getBuffer().get(i).getS_SC();
            }
        }

        node.setNewestNode(node_to_catch_up);
        node.setN(N);
        node.setMax(MaxVN);

        System.out.println(">> Result of max: " + MaxVN);
        System.out.println(">> Result of N: " + N);


        // Compute:
        //   P: P set, list of 'char's, nodes that current node can connect to
        //   I_complete: Nodes of I set, list of 'Node's
        //   I: I set, list of 'char's
        //   card_I: Number of nodes in I set
        int card_I=0;
        ArrayList<Character> P = new ArrayList<>();
        ArrayList<request_message> I_complete = new ArrayList<>();
        ArrayList<Character> I = new ArrayList<>();

        P.add(node.getNid());

        for(int i = 0 ; i < node.getBuffer().size(); i++) {

            P.add(node.getBuffer().get(i).getSender());
            if ( node.getBuffer().get(i).getS_VN() == MaxVN) {

                card_I++;
                I_complete.add(node.getBuffer().get(i));
                I.add(node.getBuffer().get(i).getSender());
            }
        }

        if (node.getVN() == MaxVN) {

            card_I++;
            I.add(node.getNid());
        }

        node.setP(P);
        node.setI(I);

        System.out.println(">> Result of P: " + P);
        System.out.println(">> Result of card_I: " + card_I);
        System.out.println(">> Result of I: " + I);

        // Decide current node is in Distinguished Partition or not

        boolean found = false;
        if (card_I > N / 2){

            System.out.println(">> Card(I) > N/2");
            found = true;
        }
        else if (card_I == N / 2){

            System.out.println(">> Card(I) = N/2");
            found = true;
            for (request_message s_i: I_complete) {
                if (!I.contains(s_i.getS_DS().get(0))){
                    System.out.println(">> I doesn't contain: " + s_i.getS_DS().get(0));
                    found = false;
                }
            }
        }
        else if (N == 3){

            System.out.println(">> N = 3");
            int count_contain_ds = 0;
            for (char c : I){

                if (I_complete.get(0).getS_DS().contains(c)){
                    count_contain_ds ++;
                }
            }

            if (count_contain_ds >= 2){
                found =true;
            }
        }
        else {

            System.out.println(">> Abort updates");
            request_message abort_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                    node.getVN(), node.getSC(), node.getDS(),
                    request_message.action_options.ABORT, request_message.calling_option.broadcast_clique);
            com_requester abort_requester = new com_requester(abort_msg, subordinates, node);
            abort_requester.send();
        }

        if (found)
            System.out.println(">> Current node is in DS");
        else
            System.out.println(">> Current node is not in DS");

        node.setIsDistinguished(found);
    }

    private static void Catch_up(Node node){

        int MaxVN = node.getMax();
        char node_to_catch_up = node.getNewestNode();

        if (MaxVN == node.getVN()){

            System.out.println(">> Current node: " + node.getNid() + " has the highest VN: " + node.getVN());
            node.setIsCurrent(true);
        } else {

            node.setIsCurrent(false);
            request_message catchup_msg = new request_message(node.getNid(), node_to_catch_up, node.getLogical_time(),
                    node.getVN(), node.getSC(), node.getDS(),
                    request_message.action_options.CATCH_UP, request_message.calling_option.single);
            com_requester catchup = new com_requester(catchup_msg, node);
            catchup.send();
            System.out.println(">> Sent CATCH_UP to node: " + node_to_catch_up);
        }

    }


    private static void Do_Updated(Node node, ArrayList<Character> subordinates){

        System.out.println(">> Current variables: [VN: " + node.getVN() + " SC: " + node.getSC() + " DS: " + node.getDS() + " ]");

        // Update VN
        node.setVN(node.getVN()+1);

        // Update SC and DS
        if (!(node.getBuffer().size()+1 == 2 && node.getN() == 3)){

            node.setSC(node.getBuffer().size()+1);

            System.out.println("TEST: " + node.getP());

            if(node.getSC() == 3)
                node.setDS(node.getP());
            else {
                ArrayList<Character> ds = new ArrayList<>();
                ds.add(node.getLargestInP(node.getP()));
                node.setDS(ds);
            }
        }
        System.out.println(">> Updated variables: [VN: " + node.getVN() + " SC: " + node.getSC() + " DS: " + node.getDS() + " ]");

        request_message update_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                node.getVN(), node.getSC(), node.getDS(),
                request_message.action_options.COMMIT, request_message.calling_option.broadcast_clique);

        com_requester update = new com_requester(update_msg, subordinates, node);

        update.send();
        node.setLock(false);
        System.out.println(">> Sent COMMIT to node: " + subordinates);
    }

//    public static void Make_Current(Node node, ArrayList<Character> subordinates){
//        if(!node.getIsDistinguished()){
//            request_message abort_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
//                    node.getVN(), node.getSC(), node.getDS(),
//                    request_message.action_options.ABORT, request_message.calling_option.broadcast_clique);
//
//            com_requester abort = new com_requester(abort_msg, subordinates, node);
//
//            abort.send();
//        }
//
//        else{
//            if(node.getVN() != node.getMax()) node.setVN(node.getMax());
//            Do_Updated(node, subordinates);
//
//        }
//    }

}