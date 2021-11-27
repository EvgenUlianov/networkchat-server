import Chat.Chat;
import Chat.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


@Slf4j
@Data
public class UserChannelInput extends  Thread{
    private final User user;
    private SocketChannel channel;
    private static final String EXIT_COMMAND = "/exit";
    private Thread threadOutput;

    public UserChannelInput(SocketChannel channel, Thread threadOutput, User user) {
        super(String.format("%s: input", user.getName()));
        this.channel = channel;
        this.threadOutput = threadOutput;
        this.user = user;
    }

    @Override
    public void run() {
        //  Определяем буфер для получения данных
        final ByteBuffer buffer = ByteBuffer.allocate(2 << 10);

        Chat chat = Chat.get();

        while (channel.isConnected()) {

            //  читаем данные из канала в буфер
            try {

                int bytesCount = 0;
                while (bytesCount <= 0) {
                    bytesCount = channel.read(buffer);
                }
                //  получаем переданную от клиента строку в нужной кодировке и очищаем буфер
                final String text = new String(buffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                buffer.clear();
                if (EXIT_COMMAND.equals(text)){
                    chat.addMessage(User.ADMIN, String.format("Нас покидает %s",user.getName()));
                    break;
                }
                chat.addMessage(user, text);
            } catch (SocketException e) {
                chat.addMessage(User.ADMIN, String.format("%s has a trouble: %s",user.getName(),  e.getMessage()));
                log.error(e.getMessage(), e);
                break;
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
                break;
            }
        }
        closeChannel();
    }

    private void closeChannel(){
        user.setAlive(false);
        if (threadOutput != null)
            threadOutput.interrupt();
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
