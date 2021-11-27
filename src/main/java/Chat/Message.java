package Chat;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Message {
    private Date date;
    private User user;
    private String text;
    static final private SimpleDateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }

    protected Message(User user, String text) {
        this.date = new Date();
        this.user = user;
        this.text = text;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(user.getName());
        builder.append(" [");
        builder.append(DATE_FORMAT.format(date));
        builder.append("]: ");
        builder.append(text);
        return builder.toString();
    }
}
