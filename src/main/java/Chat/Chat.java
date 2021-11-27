package Chat;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class Chat {

    @Getter
    public static class LastMessages{
        private Queue<Message> messages;
        private int currentIndex;
        private LastMessages(){}
    }

    private final List<Message> messages;

    public void addMessage(User user, String text){
        Message message = new Message(user, text);
        messages.add(message);
        String messageText = message.toString();
        log.info(messageText);
        System.out.println(messageText);
    }

    public LastMessages getMessages(int clientIndex){

        Queue<Message> result = new ArrayDeque();
        int index = messages.size();
        for (int i = index; i > clientIndex; i--) {
            try {
                Message message = messages.get(i - 1);
                if (message != null)
                    result.add(message);
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        LastMessages lastMessages = new LastMessages();
        lastMessages.messages = result;
        lastMessages.currentIndex = index;
        return lastMessages;
    }

    public int getCurrentIndex(){
        return messages.size() - 1;
    }


    // SingleTone ++

    private Chat(){
        messages = new CopyOnWriteArrayList();
    }

    private static class Holder {
        public static final Chat CHAT = new Chat();
    }

    public static Chat get()  {
        return Chat.Holder.CHAT;
    }

    // SingleTone --
}
