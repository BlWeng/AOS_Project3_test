import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class InetPort {

    private static HashMap<Character, InetSocketAddress> hash_table;

    static {
        init_hash();
    }

    public InetPort(){ }

    private static void init_hash() {

        hash_table = new HashMap<Character, InetSocketAddress>();

        for (int i = 0; i < 8; i++) {

            char cur_c = (char)('A' + i);

            try {
                hash_table.put(cur_c,
                        new InetSocketAddress(InetAddress.getByName("10.176.69." + (32 + i)), 5000 + (int)cur_c));
                //hash_table.put(cur_c,
                       //new InetSocketAddress(InetAddress.getLocalHost(), 5000 + (int)cur_c));

            } catch (Exception e){
                e.printStackTrace();
            }
        }

        System.out.println(hash_table);
    }

    public static InetSocketAddress get_ip_port(char c){

        return hash_table.get(c);
    }

    public static InetAddress get_addr(char c){

        return hash_table.get(c).getAddress();
    }

    public static int get_port(char c){

        return hash_table.get(c).getPort();
    }
}
