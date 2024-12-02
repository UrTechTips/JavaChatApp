import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private User sender;
    private User reciver;
    private String content;
    private Date timestamp;

    public Message(User sender, User reciver, String content, Date timestamp) {
        this.sender = sender;
        this.reciver = reciver;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(timestamp);
    }

    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] " + sender + ": " + content;
    }
}
