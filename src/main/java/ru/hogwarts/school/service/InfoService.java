package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class InfoService {
    private static final Logger logger = LoggerFactory.getLogger(InfoService.class);
    @Value("${server.port}")
    private String port;

    public String getPort() {
        logger.info("Was invoked method for \"getPort\"");
        logger.debug("The port={} number was transmitted",port);
        return port;
    }

    public void step4() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Stream");
        long sum = Stream.iterate(1L, a -> a +1)
                .limit(1_000_000)
                .reduce(0L, Long::sum);
        stopWatch.stop();
        logger.info(stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
    }
}
