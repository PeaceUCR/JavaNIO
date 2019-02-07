Demo test with NIO

Why NIO?
https://dzone.com/articles/java-nio-vs-io
IO: Stream oriented, Blocking IO	
NIO: Buffer oriented, Non blocking IO, Selectors
	

Great Practice: A Thread uses a Selector to handle multiple channel


How to use:
open by IntelliJ,run NIOSelectorServer.java fist, then Main.java

Workflow:
1.start socket server and bind server with selector&accept_event(key)
2.then loop forever while (not sure also in prod?)  

    in loop, call select(), this blocks until at least one channel is ready for the events you registered for.
    in Set<SelectionKey>, each key represents a registered channel which is ready for an operation.
    (PS: The only operation a ServerSocketChannel can handle is an ACCEPT operation)
    check ready then do something.
    Not debug in multiple client envriments....

Ref:
//https://blog.csdn.net/Z0157/article/details/86720561
//https://www.baeldung.com/java-nio2-async-socket-channel
//https://www.baeldung.com/java-nio-selector
//http://tutorials.jenkov.com/java-nio/selectors.html

https://stackoverflow.com/questions/28441151/how-java-nio-serversocketchannel-accept-works