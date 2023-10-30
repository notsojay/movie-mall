package com.functional;

import java.sql.SQLException;

@FunctionalInterface
public interface UpdateResultProcessor {
    void process(int updateCount) throws SQLException;
}
