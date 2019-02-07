
public class Main {

    public static void main(String[] args) {

        NIOSelectorClient nioClient = NIOSelectorClient.start();

        nioClient.sendMessage("I am thread one");

    }
}
