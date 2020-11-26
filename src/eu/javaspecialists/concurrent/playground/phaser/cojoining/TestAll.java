/*
 * This class forms part of the Thread Safety with Phaser,
 * StampedLock and VarHandle Talk by Dr Heinz Kabutz from
 * JavaSpecialists.eu and may not be distributed without written
 * consent.
 *
 * (C)opyright 2020 Heinz Max Kabutz, All rights reserved.
 */

package eu.javaspecialists.concurrent.playground.phaser.cojoining;

import eu.javaspecialists.concurrent.playground.phaser.cojoining.impl.*;

import java.util.function.*;
import java.util.stream.*;

public class TestAll {
    public static void main(String... args) {
        try {
            for (int i = 0; i < 10; i++) {
                test();
                System.out.println();
            }
        } finally {
            CojoinedTasksTester.shutdown();
        }
    }

    private static void test() {
        Stream.<Supplier<Cojoiner>>of(
                NoneCojoiner::new,
                WaitNotifyCojoiner::new//,
//                ConditionAwaitSignalCojoiner::new,
//                CountDownLatchCojoiner::new,
//                VolatileSpinCojoiner::new,
//                PhaserCojoiner::new
        )
                .forEach(CojoinedTasksTester::test);
    }
}
