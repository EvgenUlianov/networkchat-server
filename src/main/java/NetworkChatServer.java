import Chat.User;
import General.Decoder;
import General.WordDelimiter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class NetworkChatServer{

    private static class Settings{
        private static int PORT;

        static {
            NetworkChatServer chatServer = new NetworkChatServer();
            URL url = chatServer.getClass().getClassLoader().getResource("settings.txt");
            String fileName = null;
            if (url != null) {
                fileName = Decoder.DecodeURL(url);
            }
            try {
                url = new URL(fileName);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            File file = new File(url.getFile());

            Map<String, Consumer<String>> settingsCommands = new HashMap<>();
            settingsCommands.put("PORT", (stringNumber) -> {
                try {
                    PORT = Integer.parseInt(stringNumber);
                    log.debug(String.format("NetworkChatServer.Settings.PORT = %d", Settings.PORT));
                } catch (NumberFormatException ex) {
                    log.error(ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            });

            try(BufferedReader reader = new BufferedReader(new FileReader(file))){
                String line = reader.readLine();
                while (line != null) {
                    WordDelimiter wordDelimiter = new WordDelimiter(line);
                    Consumer<String> command = settingsCommands.get(wordDelimiter.getFirstWord());
                    if (command != null)
                        command.accept(wordDelimiter.getSecondWord());
                    line = reader.readLine();
                }
            } catch(IOException ex){
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("NetworkChatServer is run...");
        log.info("NetworkChatServer is run...");

        ServerSocketChannel serverChannel = null;
        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("localhost", Settings.PORT));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        //  Определяем буфер для получения данных
        final ByteBuffer buffer = ByteBuffer.allocate(2 << 10);
        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            //  Ждем подключения клиента и получаем потоки для дальнейшей работы
            try {
                SocketChannel socketChannel = serverChannel.accept();
                log.info(String.format("Подключился %s %n", socketChannel.getRemoteAddress()));
                String msg = "Здравствуй, пользователь, введи свое имя:";
                userSendMsg(socketChannel, msg);

                User user = null;

                while (user == null) {
                    int bytesCount = 0;
                    while (bytesCount == 0) {
                        bytesCount = socketChannel.read(buffer);
                        boolean needToBreak = false;
                        if (bytesCount == -1) {
                            //  если из потока читать нельзя, перестаем работать с этим клиентом
                            System.out.println("и закончили");
                            needToBreak = true;
                            break;
                        }
                        if (needToBreak) break;
                    }
                    //  получаем переданную от клиента строку в нужной кодировке и очищаем буфер
                    final String userName = new String(buffer.array(), 0, bytesCount, StandardCharsets.UTF_8);
                    buffer.clear();
                    user = User.addUser(userName);
                    if (user == null){
                        userSendMsg(socketChannel, "Недопустимое имя, попроуйте выбрать другое");
                    }

                }

                Thread channelOutput = new UserChannelOutput(socketChannel, user);
                channelOutput.start();
                Thread channelInput = new UserChannelInput(socketChannel, channelOutput, user);
                channelInput.start();

                channels.add(socketChannel);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    public static void userSendMsg(SocketChannel socketChannel, String msg){

        try {
            socketChannel.write(ByteBuffer.wrap((msg).getBytes(StandardCharsets.UTF_8)));
        }catch (IOException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }


}
