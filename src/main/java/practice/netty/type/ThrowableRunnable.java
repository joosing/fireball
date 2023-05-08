package practice.netty.type;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Exception;
}
