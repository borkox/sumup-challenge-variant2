package com.bmarkov.challenge;

import org.apache.camel.cdi.Main;

/**
 * A Camel Application
 */
public class MainApp {

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.run(args);
        main.close();
    }

}

