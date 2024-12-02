
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private User sender;
    private String content;
    private Date timestamp;

    public Message(User sender, String content) {
        this.sender = sender;
        this.content = content;
        this.timestamp = new Date();
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(timestamp);
    }

    @Override
    public String toString() {
        return "[" + getFormattedTimestamp() + "] @|" + sender.color + " " + sender.displayname + ":|@ " + content;
    }
}
