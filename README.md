
# practice-netty
Netty 프레임워크를 능숙하게 활용하고 깊이있게 이해하기 위해 학습한 내용을 기록합니다.

### TCP server-client simple test
- [When client sends a command, Then server react with response and client receive it](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L123)
- [When Server sends 10 commands, Then client receive 10 responses](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L137)

### Blocking 발생하는 Handler의 영향 분석과 해결
- [Blocking Handler 영향](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L122)
- [독릭접인 Executor(EventLoop) 할당](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L144)
  
  
### Exception Handling in Channel
- [When you just throw exception, Then exceptionCaught() is not called](https://github.com/Jsing/practice-netty/blob/bf873e00cc233527a3c32eb307a01e954be55d49/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L17)
- [When you call fireExceptionCaught(), Then exceptionCaught() is called](https://github.com/Jsing/practice-netty/blob/bf873e00cc233527a3c32eb307a01e954be55d49/src/test/java/practice/netty/exception/ExceptionHandleTest.java#L37)
