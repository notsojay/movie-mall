package com.utils;

import java.io.IOException;
import java.util.logging.*;

public class LogUtil {
    private static Logger logger = null;

    public static synchronized Logger getLogger() {
        if (logger == null) {
            try {
                logger = Logger.getLogger("PerformanceLogger");
                FileHandler fh = new FileHandler("/home/ubuntu/ts_n_tj.log", true);
                logger.addHandler(fh);
                fh.setFormatter(new SimpleFormatter());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logger;
    }
}
