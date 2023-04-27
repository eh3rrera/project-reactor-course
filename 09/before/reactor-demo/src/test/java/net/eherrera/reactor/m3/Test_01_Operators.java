package net.eherrera.reactor.m3;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class Test_01_Operators {
    @Test
    void example_01_ImperativeMapReduce() {
        List<Integer> originalValues = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> newValues = new ArrayList();
        int total = 0;
        for(Integer val : originalValues) { // The map operation
            newValues.add(val * 2);
        }
        for(Integer val : newValues) {      // The reduce operation
            total += val;
        }
        System.out.println(total);
    }

    @Test
    void example_02_ImperativeMapFilterReduce() {
        List<Integer> originalValues = Arrays.asList(1, 2, 3, 4, 5, 6);
        List<Integer> newValues = new ArrayList();
        int total = 0;
        for(Integer val : originalValues) { // The map operation
            if(val % 2 == 0) {              // The filter operation
                newValues.add(val * 2);
            }
        }
        for(Integer val : newValues) {      // The reduce operation
            total += val;
        }
        System.out.println(total);
    }

    @Test
    void example_03_StreamMapFilterReduce() {
        List<Integer> originalValues = Arrays.asList(1, 2, 3, 4, 5, 6);
        int total = originalValues.stream()
                .map(val -> val * 2)
                .filter(val -> val % 2 == 0)
                .reduce(0, Integer::sum);
        System.out.println(total);
    }
}
