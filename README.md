
# practice-netty
Netty 프레임워크를 능숙하게 활용하고 깊이있게 이해하기 위해 학습한 내용을 기록합니다.

### TCP server-client simple test
- [When client sends a command, Then server react with response and client receive it](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L122)
- [When Server sends 10 commands, Then client receive 10 responses](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L137)

### Influence and resolution of Blocking Handlers
- [Blocking Handler 영향](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L122)
- [독릭접인 Executor(EventLoop) 할당](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L144)
  
  
### Exception Handling in Channel
- [When you throw exception from outbound, Then exceptionCaught() is not called](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L16)
- [When you throw exception from inbound, Then exceptionCaught() is not called](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L36)
- [When you call fireExceptionCaught() from outbound, Then exceptionCaught() is called](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L53)
- [When you call fireExceptionCaught() from inbound, Then exceptionCaught() is called](https://github.com/Jsing/practice-netty/blob/fb9b33ee2c53c6d8f9bbc3fa1f2c6a148973cc92/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L70)
