package com.functional;

import java.sql.SQLException;

@FunctionalInterface
public interface UpdateCountProcessor {
    void process(int updateCount) throws SQLException;
}
