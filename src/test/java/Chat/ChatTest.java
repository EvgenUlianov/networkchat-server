package Chat;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class ChatTest {

    @Test
    void addGetMessage() {
        Chat chat = Chat.get();
        chat.addMessage(User.ADMIN, "test");

        Chat.LastMessages messages = chat.getMessages(0);
        Message message = messages.getMessages().poll();

        assertTrue(message.toString().contains("test"));
    }

    @Test
    void get() {

        Chat chat1 = null, chat2 = null;

        Callable callable1 = () -> Chat.get();
        Callable callable2 = () -> Chat.get();

        ExecutorService threadPool = Executors.newFixedThreadPool(2);

        Future<Chat> chatFuture1 = threadPool.submit(callable1);
        Future<Chat> chatFuture2 = threadPool.submit(callable2);

        final int timeOut500 = 500;
        try {
            Thread.sleep(timeOut500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            chat1 = chatFuture1.get();
            chat2 = chatFuture2.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(chat1, chat2);
     }
}