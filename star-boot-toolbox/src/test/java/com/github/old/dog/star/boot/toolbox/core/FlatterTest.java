package com.github.old.dog.star.boot.toolbox.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class FlatterTest {

    @Test
    public void test_1__flatterWithoutArguments() {
        // given
        FlatMe flatMe = new FlatMe();
        // when
        Map<String, Object> flatten = Flatter.flatten(flatMe);
        // then
        for (String key : flatten.keySet()) {
            System.out.printf("%s:=%s%n", key, flatten.get(key));
        }
    }


    @Data
    public static class FlatMe {

        // primes
        private String string = "value";
        private int integer = 5;
        private boolean bool = true;

        // arrays
        private String[] array_string = {"a", "b", "c"};
        private int[] array_integer = {1, 2, 3};
        private boolean[] array_bool = {true, false};
        private Object[] array_object = {new PrimalMe(), new BoxMe()};

        // collections
        private List<String> list_string = List.of("d", "e", "f");
        private List<Object> list_object = List.of(new PrimalMe(), new BoxMe());
        private Set<Object> set_object = Set.of(new PrimalMe(), new BoxMe());

        // tree
        private Map<String, Object> map_object = Map.of("primal", new PrimalMe(), "box", new BoxMe());

        // objects
        private PrimalMe primal_me = new PrimalMe();
        private BoxMe box_me = new BoxMe();
    }

    @Data
    public static class PrimalMe {

        // other primes
        private double a_double = 10.5;
        private float a_floater = 5.5f;
        private char character = '+';

    }

    @Data
    public static class BoxMe {

        // other primes
        private Double a_box_double = 100.1;
        private Float a_box_floater = 50.1f;
        private Character character = '-';

    }
}
