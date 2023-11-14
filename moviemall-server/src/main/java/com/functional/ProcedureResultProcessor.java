package com.functional;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface ProcedureResultProcessor<T> {
    T process(CallableStatement stmt, boolean hadResults) throws SQLException;
}
