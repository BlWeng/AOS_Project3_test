import java.io.Serializable;


public class request_message implements Serializable{

    private char sender;
    private char receiver;


    public request_message(char sender, char receiver){

        this.sender = sender;
        this.receiver = receiver;
    }

    public char getSender() { return sender; }
    public char getReceiver() { return receiver;}
}
