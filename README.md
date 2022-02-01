# practice-netty
Netty 프레임워크를 능숙하게 활용하고 깊이있게 이해하기 위해 학습한 내용을 기록합니다.

### Simple TCP 서버-클라이언트
- [TCP 서버와 클라이언트를 구성하고 간단한 메시지를 전송하고, 응답을 수신하는 기능을 테스트합니다.](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L123)
- [서버에서 N개의 메시지를 전송하고, 클라이언트에서 N개의 메시지를 수신하는 기능을 테스트합니다.](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/SimpleTcpTest.java#L137)

### Blocking 발생하는 Handler의 영향을 테스트하고, 별도의 쓰레드로 분리하여 문제를 해결합니다.
- [Blocking 발생하는 Handler에 의해 전체 채널 파이프라인이 멈추는 것을 확인합니다.](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L122)
- [Blocking 발생하는 Handler의 Executor(EventLoop)를 독립적 할당하여 문제가 해결됨을 확인합니다.](https://github.com/Jsing/practice-netty/blob/edc7b364776d7f40b27de5cbd21fcdc3f014f156/src/test/java/practice/netty/tcp/BlockingHandlerTest.java#L144)
