import Chat.Chat;
import Chat.Message;
import Chat.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

@Slf4j
@Data
public class UserChannelOutput
        extends  Thread{
    private final User user;
    private SocketChannel channel;

    public UserChannelOutput(SocketChannel channel, User user) {
        super(String.format("%s: input", user.getName()));
        this.channel = channel;
        this.user = user;
    }

    @Override
    public void run() {

        //  Определяем буфер для получения данных
//        final ByteBuffer buffer = ByteBuffer.allocate(2 << 10);


        Chat chat = Chat.get();
        int currentIndex = chat.getCurrentIndex();
        while (channel.isConnected() && user.isAlive()) {

            Chat.LastMessages lastMessages = chat.getMessages(currentIndex);
            if (currentIndex != lastMessages.getCurrentIndex()){

                final Queue<Message> messages= lastMessages.getMessages();
                if (!messages.isEmpty() )
                    currentIndex = lastMessages.getCurrentIndex();

                boolean needToBreak = false;

                while (!messages.isEmpty() && user.isAlive()) {

                    String result = messages.poll().toString();
                    try {
                        channel.write(ByteBuffer.wrap((result).getBytes(StandardCharsets.UTF_8)));
                    } catch (IOException e) {
                        // do not print stack trace, because of leaving user
                        if(!user.isAlive()){
                            needToBreak = true;
                            break;
                        } else {
                            log.error(e.getMessage(), e);
                            e.printStackTrace();
                        }
                    }

                }
                if (needToBreak)
                    break;

            }




        }


    }
}
