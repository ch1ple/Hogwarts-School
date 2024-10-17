package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Stream;

@RestController
@Tag(name = "Контроллер по работе с портом")
public class InfoController {

    private final int port;

    public InfoController(@Value("${server.port}") int port) {
        this.port = port;
    }

    @GetMapping(path = "getPort")
    public int getPort() {
        return port;
    }

    @GetMapping
    public String checkStreamIterator() {
        long before = System.currentTimeMillis();
        int sum = calcSum();
        long after = System.currentTimeMillis();

        long beforeImpr = System.currentTimeMillis();
        int sumImpr = calcSumImpr();
        long afterImpr = System.currentTimeMillis();

        return "Sum: " + sum + "; Time: " + (after - before) + "ms | " +
                "SumImpr: " + sumImpr + "; Time: " + (afterImpr - beforeImpr) + "ms";
    }

    private int calcSum() {
        return Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, Integer::sum);
    }

    private int calcSumImpr() {
        return Stream.iterate(1, a -> a + 1)
                .parallel()
                .limit(1_000_000)
                .reduce(0, Integer::sum);
    }

}
