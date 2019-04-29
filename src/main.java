import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class main {
    public static void main(String[] args) {

        boolean cnt = true;

        System.out.println("Node Number?");

        Scanner cn = new Scanner(System.in).useDelimiter("\\s");

        Node node = new Node(cn.next().charAt(0));

        node.setVN(1);
        node.setSC(8);
        node.setDS(new ArrayList<>(List.of('A')));

        node.setMutual_exclusion(false);
        try {

            Thread server = new Thread(new com_server(node));

            server.start();

                while (cnt) {
                    try {

                        System.out.println("Would you like to make request? (y/n)");
                        Scanner cmd = new Scanner(System.in).useDelimiter("\\s");


                        if (cmd.next().equals("y")) {
                            int iteration = 0;
                            node.setIteration(iteration);
                            int target_set;

/*
                            String set[];
                            Hashtable<String, Boolean> quorum_set ;
                            node.resetNm_msg_sent();
                            node.resetsetNm_msg_received();
*/

// Test for partition in network

                            System.out.println("Please enter desire level for partition testing. (0-3)");
                            Scanner p_cmd = new Scanner(System.in).useDelimiter("\\s");

                            iteration = p_cmd.nextInt();

                            String selected_set = partition_selected(node, iteration);

                            ArrayList<Character> connection_set = new ArrayList<>();

                            if(selected_set.equals("self"))
                                connection_set.add(node.getNid());
                            else
                                for(char it:selected_set.toCharArray())
                                    connection_set.add(it);

                            System.out.println("Selected iteration: " +iteration);
                            System.out.println("Selected partition: " + connection_set.toString());
// end of mimic in test

                            Hashtable quorum_set = new Hashtable();
                            for (char t_in : connection_set)
                                quorum_set.put(t_in, false);


                            //request_message req_msg = new request_message(node.getNid(), ' ', node.getLogical_time(), iteration,
                            //        request_message.action_options.VOTE_REQUEST, request_message.calling_option.broadcast_clique);

                            request_message req_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                                            node.getVN(), node.getSC(), node.getDS(),
                                            request_message.action_options.VOTE_REQUEST, request_message.calling_option.broadcast_clique);

                            //com_requester req_begin = new com_requester(req_msg, quorum_set, node);

                            com_requester req_begin = new com_requester(req_msg, connection_set, node);

                            req_begin.send();

                            while(node.getBuffer().size() != connection_set.size()-1)
                                System.out.print("Waiting reply message(s)..." + " Size of Buffer; " + node.getBuffer().size() + "\r");
                                //System.out.print("Waiting reply message(s)...\r");

                            System.out.println("Escape from replying.");

                            request_message self_msg = new request_message(node.getNid(), node.getNid(), node.getLogical_time(),
                                    node.getVN(), node.getSC(), node.getDS(),
                                    request_message.action_options.VOTE_REQUEST_REPLY, request_message.calling_option.broadcast_clique);
                            node.setBuffer(self_msg);

                            node.setIsDistinguished(false);

                            Is_Distinguished(node, connection_set);

                            if(node.getIsDistinguished()){
                                Catch_up(node);
                                Do_Updated(node, connection_set);
                            }

                            node.getBuffer().clear();

/*
                            while(node.getIteration()<20 ) {
                                long start= System.nanoTime();
                                int temp_msg_nm = node.getNm_msg_sent()+node.getNm_msg_received();
                                node.setRequest_act_finished(false);
                                target_set = (int) (Math.random() * 100) % 15;
                                set = quorum.get(target_set).toString().split(" ");
                                quorum_set = new Hashtable();
                                for (String t_in : set)
                                    quorum_set.put(t_in, false);

                                node.setAimed_quorum(iteration, quorum_set);

                                node.setLogical_time_unit_increase();
                                request_message req_msg = new request_message(node.getNid(), -1, node.getLogical_time(), iteration,
                                        request_message.action_options.request, request_message.calling_option.broadcast_clique);

                                com_requester req_begin = new com_requester(req_msg, quorum_set, node);

                                req_begin.send();


                                checker(node, iteration);

                                request_message cs_msg = new request_message(node.getNid(), -1, node.getLogical_time(), iteration,
                                        request_message.action_options.enter_cs, request_message.calling_option.broadcast_clique);
                                com_requester cs_inform = new com_requester(cs_msg, quorum_set, node);
                                cs_inform.send();

                                critical_section(node, start, temp_msg_nm);

                                release_resource(node, iteration, quorum_set);




                                iteration++;
                                node.setIteration(iteration);
                                Thread.sleep(5);
                            }

                        node.getWhole().clear();

                        request_message complete_msg = new request_message(node.getNid(), 0, node.getLogical_time(), iteration,
                                request_message.action_options.complete, request_message.calling_option.single);

                        com_requester cp_bk = new com_requester(complete_msg, node);
                        cp_bk.send();

                        System.out.println("Node of " + node.getNid()+ ", number of message sent: " + node.getNm_msg_sent());
                        System.out.println("Node of " + node.getNid()+ ", number of message received: " + node.getNm_msg_received());

                        //System.out.println("Size of Buffer: " + node.getBuffer().size());

    */
                    }
                    else {
                        System.out.println("Goodbye!!!");
                        cnt = false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }catch (Exception e) {e.printStackTrace();}


    }

    public static void checker(Node node, int iteration) throws Exception{
        int temp;
        while(!node.getChecker()) {
            //System.out.print("Iteration: "+ iteration + " Updated Quorum: " + node.getAimed_quorum(iteration)+"\r");

            Enumeration qs_checker = node.getAimed_quorum(iteration).elements();
            temp=0;
            while(qs_checker.hasMoreElements()){
                if(qs_checker.nextElement().equals(true)){
                    temp++;
                }
            }

            if (temp == node.getAimed_quorum(iteration).size()) {
                System.out.println("Receive permission for critical sections.");
                System.out.println("Iteration: " + iteration +
                        " Requirement no. permission: " + node.getAimed_quorum(iteration).size());
                System.out.println("Quorum set in CS: " + node.getAimed_quorum(iteration));
                node.setChecker(true);
            }

        }

        if(node.getChecker()) {
            node.setCs_permission(true);
        }
    }

    public static void critical_section(Node node, long start, int temp_msg_nm) throws Exception{
        while (node.getCs_permission()){
            node.setChecker(false);

            if ( !node.getRequest_act_finished() ) {
                //System.out.println("Enter Critical Section!!!!!!!!!!!");
                long elapsed_time = System.nanoTime()-start;
                int total_msg_nm = node.getNm_msg_sent()+node.getNm_msg_received()-temp_msg_nm;
                //System.out.println("In critical section of Critical Section.");
                //write_file(node, elapsed_time,total_msg_nm);
                node.setCs_permission(false);


            }
        }
    }

    public static void release_resource(Node node,int iteration, Hashtable for_release){

            //System.out.println("Releasing Def.");
    /*
            request_message release_msg = new request_message(node.getNid(),-1,node.getLogical_time(), iteration,
                    request_message.action_options.release, request_message.calling_option.broadcast_clique);
            com_requester release = new com_requester(release_msg, for_release, node);
            release.send();
    */
    }



    public static void write_file(Node node, long elpased_time, int total_msg_nm) throws Exception{
        //System.out.println("Enter write file.");
        //String home = System.getProperty("c:\\Users\\a62ba\\Desktop\\Spring_2019_CS6378_AOS_Undergoing");
        String cwd = System.getProperty("user.dir") + "\\record.txt";
        //String cwd = System.getProperty("user.home\\server01") + "\\record.txt";
        //String cwd = System.getProperty("\\home\\eng\\b\\bxw170030") + "\\record.txt";
        //String cwd = System.getProperty("user.home") + "\\record.txt";
        File f = new File(cwd);
        if (!f.exists()) { f.createNewFile(); }

        //String in = "entering node " + this.node.getNid() + " \t" + "Time Stamp " + this.node.getLogical_time()+ "\n";
        //String in = "Test_Read_8:  " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //String in = "< Client ID:  " + this.node.getNid() + ", " + " Timestamp: " + this.node.getLogical_time() + " >";
        //String in = "< Client:  " + ori_msg.getReq_initiator()+ ", " + " Timestamp: " + ori_msg.getReq_logical_time() + " >";


        String in = "Iteration: " + node.getIteration()
                    +" entering\n < Client:  " + node.getNid()+ ", " + "Physical time: " + System.currentTimeMillis() + " >\n"
                    +" elapsed time: " + elpased_time + ", total number of message: " + total_msg_nm;




        String serial_no=readFromLast(node);
        String old_no = "";
        String new_no = "";

        /*
        if(serial_no.equals(""))
        //if(f.length() == 1)
            serial_no = "--- 1 ---";

        else {
            old_no = serial_no.substring(serial_no.indexOf(" ")+1, serial_no.indexOf(" ",(serial_no.indexOf(" ")+1)));
            new_no = Integer.toString(Integer.parseInt(old_no) + 1);
            serial_no = "--- " + new_no + " ---";
        }
        String in = serial_no + "Iteration: " + node.getIteration()
                +" entering < Client:  " + node.getNid()+ ", " + "Physical time: " + System.currentTimeMillis() + " > "
                +" elapsed time: " + elpased_time + ", total number of message: " + total_msg_nm;

*/
        PrintWriter out = new PrintWriter(new FileWriter(f, true));
        out.println(in);

        Thread.sleep(3);
        out.close();

        System.out.println("Exit file write.");


    }


    public static String readFromLast( Node node ) throws Exception{
        String temp="";
        //File file = new File(System.getProperty(System.getProperty("user.dir"))+ "/record.txt");
        //File file = new File(System.getProperty("C:\\Users\\a62ba\\Desktop\\Spring_2019_CS6378_AOS_Undergoing\\AOS_Project1_ori\\record.txt"));
        //System.out.println("Enter Read file.");
        String cwd = System.getProperty("user.dir") + "\\record.txt";
        //String cwd = System.getProperty("\\home\\eng\\b\\bxw170030\\sever01") + "\\record.txt";
        //String cwd = System.getProperty("user.home") + "record.txt";
        File file = new File(cwd);
        if (!file.exists()) {file.createNewFile();}
        //int lines = 0;
        StringBuilder builder = new StringBuilder();
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            if(fileLength < 0) fileLength =0;
            // Set the pointer at the last of the file
            randomAccessFile.seek(fileLength);
            for(long pointer = fileLength; pointer >= 0; pointer--){
                randomAccessFile.seek(pointer);
                char c;
                // read from the last one char at the time
                c = (char)randomAccessFile.read();
                // break when end of the line
                if(c == '\n' && pointer != fileLength){
                    break;
                }
                builder.append(c);
            }
            // Since line is read from the last so it
            // is in reverse so use reverse method to make it right
            builder.reverse();
            temp = builder.toString();
            System.out.println("Result of request: " + temp);
            //node.setRead_string(temp);
            //node.getReceived_msg().setRequest_result(temp);
            //node.setCarrier_msg_w_Stg(node.getCarrier_msg(), temp);
            //node.getCarrier_msg().setAction(message.msg_options.task_finished);
            //System.out.println("Line - " + temp);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(randomAccessFile != null){
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return temp;
    }

    public static String partition_selected(Node node, int iteration){
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

    public static void Is_Distinguished(Node node, ArrayList<Character> subordinates){
        int max = node.getBuffer().get(0).getS_VN();
        int card_I = 0;
        int N = node.getBuffer().get(0).getS_SC();

        for(int i = 1 ; i < node.getBuffer().size(); i++){
            if ( node.getBuffer().get(i).getS_VN() > max)
                max = node.getBuffer().get(i).getS_VN();
                N = node.getBuffer().get(i).getS_SC();
        }

        node.setMax(max);
        System.out.println("Result of max: " + max);
        System.out.println("Result of N: " + N);

        for(int j = 0 ; j < node.getBuffer().size(); j++){
            if ( node.getBuffer().get(j).getS_VN() == max)
                card_I++;
        }

        System.out.println("Result of card_I: " + card_I);

        boolean found = false;

        ArrayList<Character> P = new ArrayList<>();
        for(int l = 0 ; l < node.getBuffer().size(); l++)
            P.add(node.getBuffer().get(l).getSender());
        node.setP(P);

        Vector<request_message> I_complete = new Vector<>();
        ArrayList<Character> I = new ArrayList<>();
        for(int q = 0 ; q < node.getBuffer().size(); q++) {
            if ( node.getBuffer().get(q).getS_VN() == max) {
                I_complete.add(node.getBuffer().get(q));
                I.add(node.getBuffer().get(q).getSender());
            }
        }

        node.setI(I);

        ArrayList<Character> temp = new ArrayList<>();

        if( N == 3 ){
            System.out.println("Pre-Distinguish partition in N == 3.");
            found = false;
            temp = new ArrayList<>(P);

            for(int n = 0 ; n < node.getBuffer().size(); n++){
                if ( node.getBuffer().get(n).getS_VN() == max)
                temp.removeAll(node.getBuffer().get(n).getS_DS());
            }

            if(temp.isEmpty() || temp.size() > 1){
                System.out.println("Distinguish partition in N == 3");
                node.setIsDistinguished(true);
                found = true;
            }

        }
        if(!found) {
            if (card_I > N / 2) {
                System.out.println("Distinguish partition in > N/2.");
                node.setIsDistinguished(true);
            }

            if (card_I == N / 2) {
                System.out.println("Pre-Distinguish partition in == N/2.");
                found = false;
                temp = new ArrayList<>(I);

                for (int k = 0; k < node.getBuffer().size(); k++) {
                    if (node.getBuffer().get(k).getS_VN() == max)
                        temp.removeAll(I_complete.get(k).getS_DS());
                }

                if (temp.isEmpty() || temp.size() != P.size()) {
                    System.out.println("Distinguish partition in == N/2");
                    node.setIsDistinguished(true);
                }

            }
        }
        else {
            System.out.println("Not in Distinguished Partition. ");
            node.setIsDistinguished(false);
            request_message dis_abort_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                    node.getVN(), node.getSC(), node.getDS(),
                    request_message.action_options.ABORT, request_message.calling_option.broadcast_clique);

            com_requester dis_abort = new com_requester(dis_abort_msg, subordinates, node);

            dis_abort.send();

        }

    }

    public static void Catch_up(Node node){
        for(int i= 0 ; i < node.getBuffer().size() ; i++){
            if ( node.getBuffer().get(i).getS_VN() != node.getMax()){
                request_message catchup_msg = new request_message(node.getNid(), node.getBuffer().get(i).getSender(), node.getLogical_time(),
                        node.getVN(), node.getSC(), node.getDS(),
                        request_message.action_options.CATCH_UP, request_message.calling_option.single);

                com_requester catchup = new com_requester(catchup_msg, node);

                catchup.send();
            }

        }

        if(!node.getI().contains(node.getNid())) node.setVN(node.getMax());
    }

    public static void Do_Updated(Node node, ArrayList<Character> subordinates){

        node.setVN(node.getVN()+1);
        node.setSC(node.getBuffer().size());
        node.getDS().clear();
        if(node.getDS().size() == 3)
            node.setDS(node.getP());
        else
            node.setDS( new ArrayList<>( List.of( node.getBuffer().get(0).getSender() ) ) );

        request_message update_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                node.getVN(), node.getSC(), node.getDS(),
                request_message.action_options.COMMIT, request_message.calling_option.broadcast_clique);

        com_requester update = new com_requester(update_msg, subordinates, node);

        update.send();

    }

    public static void Make_Current(Node node, ArrayList<Character> subordinates){
        if(!node.getIsDistinguished()){
            request_message abort_msg = new request_message(node.getNid(), ' ', node.getLogical_time(),
                    node.getVN(), node.getSC(), node.getDS(),
                    request_message.action_options.ABORT, request_message.calling_option.broadcast_clique);

            com_requester abort = new com_requester(abort_msg, subordinates, node);

            abort.send();
        }

        else{
            if(node.getVN() != node.getMax()) node.setVN(node.getMax());
            Do_Updated(node, subordinates);

        }
    }




}

