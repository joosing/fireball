package practice.netty.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SleepUtils {
    /**
     * Thread.sleep() 메서드에서 발생하는 체크 예외를 런타임 예외로 바꾸어 줍니다.
     * @throws RuntimeException Thread.sleep() 메서드에서 발생하는 체크 예외를 런타임 예외로 바꾸어 줍니다.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
