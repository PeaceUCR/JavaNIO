
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
//https://blog.csdn.net/Z0157/article/details/86720561
//https://www.baeldung.com/java-nio2-async-socket-channel
//https://www.baeldung.com/java-nio-selector
//http://tutorials.jenkov.com/java-nio/selectors.html
public class NIOSelectorServer {

    private static final String POISON_PILL = "POISON_PILL";

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        //Binds the channel's socket to a local address and configures the socket to listen for connections
        serverSocket.bind(new InetSocketAddress("localhost", 5454));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        while (true) {
            System.out.println("I am here");
            //select() method blocks until at least one channel is ready for an operation
            selector.select();
            //return ready keys
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();
            while (iter.hasNext()) {
                System.out.println("keys?");
                SelectionKey key = iter.next();
                //check key is ready, for accept vs connect?accept: a connection was accepted by a ServerSocketChannel;connect: a connection was established with a remote server.
                //during the test, first while(true) loop go accept, second while(true) loop go read
                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    answerWithEcho(buffer, key);
                }
                //Notice the remove() call at the end of each iteration. The Selector does not remove the SelectionKey instances from the selected key set itself. You have to do this, when you are done processing the channel.
                //The next time the channel becomes "ready" the Selector will add it to the selected key set again.
                iter.remove();
            }
        }
    }

    private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        if (new String(buffer.array()).trim().equals(POISON_PILL)) {
            client.close();
            System.out.println("Not accepting client messages anymore");
        }
        //flip?
        //https://stackoverflow.com/questions/16461284/difference-between-bytebuffer-flip-and-bytebuffer-rewind
        //A ByteBuffer is normally ready for read() (or for put()).
        // flip() makes it ready for write() (or for get()).
        //rewind() and compact() and clear() make it ready for read()/put() again after write() (or  get()).
        buffer.flip();
        client.write(buffer);
        buffer.clear();
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {
        //https://stackoverflow.com/questions/28441151/how-java-nio-serversocketchannel-accept-works
        //accept() Accepts a connection made to this channel's socket.
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }

}

