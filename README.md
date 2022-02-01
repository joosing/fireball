# practice-netty
Netty 프레임워크를 능숙하게 활용하고 깊이있게 이해하기 위해 학습한 내용을 기록합니다.

### Simple TCP 서버-클라이언트
- [간단 메시지 교환](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L123)
- [N개 메시지 수신](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L137)

### Blocking 발생하는 Handler의 영향 분석과 해결
- [Blocking Handler 영향](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L122)
- [독릭접인 Executor(EventLoop) 할당](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L144)
