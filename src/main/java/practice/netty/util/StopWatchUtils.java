package practice.netty.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.StopWatch;

@UtilityClass
public class StopWatchUtils {
    public static StopWatch start() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        return stopWatch;
    }

    public static void stop(StopWatch stopWatch) {
        stopWatch.stop();
    }

    // 스탑워치를 멈추고 경과 시간을 주어진 태그와 함께 초 단위로 출력합니다.
    public static void stopAndPrintSeconds(StopWatch stopWatch, String tag) {
        stopWatch.stop();
        System.out.printf("%s: %,f%n", tag, stopWatch.getTotalTimeSeconds());
    }
}
