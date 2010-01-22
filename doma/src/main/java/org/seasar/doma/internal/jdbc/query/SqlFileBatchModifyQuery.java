/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.doma.internal.jdbc.query;

import static org.seasar.doma.internal.util.AssertionUtil.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.seasar.doma.internal.expr.ExpressionEvaluator;
import org.seasar.doma.internal.expr.Value;
import org.seasar.doma.internal.jdbc.sql.NodePreparedSqlBuilder;
import org.seasar.doma.internal.jdbc.sql.PreparedSql;
import org.seasar.doma.jdbc.Config;
import org.seasar.doma.jdbc.SqlExecutionSkipCause;
import org.seasar.doma.jdbc.SqlFile;
import org.seasar.doma.jdbc.SqlKind;

/**
 * @author taedium
 * 
 */
public abstract class SqlFileBatchModifyQuery<E> implements BatchModifyQuery {

    protected final Class<E> elementClass;

    protected final SqlKind kind;

    protected Config config;

    protected String sqlFilePath;

    protected String parameterName;

    protected String callerClassName;

    protected String callerMethodName;

    protected SqlFile sqlFile;

    protected boolean optimisticLockCheckRequired;

    protected boolean executable;

    protected SqlExecutionSkipCause sqlExecutionSkipCause = SqlExecutionSkipCause.BATCH_TARGET_NONEXISTENT;

    protected int queryTimeout;

    protected int batchSize;

    protected List<E> elements;

    protected E currentEntity;

    protected List<PreparedSql> sqls;

    public SqlFileBatchModifyQuery(Class<E> elementClass, SqlKind kind) {
        assertNotNull(elementClass, kind);
        this.elementClass = elementClass;
        this.kind = kind;
    }

    public void prepare() {
        assertNotNull(config, sqlFilePath, parameterName, callerClassName,
                callerMethodName, elements, sqls);
        Iterator<E> it = elements.iterator();
        if (it.hasNext()) {
            executable = true;
            sqlExecutionSkipCause = null;
            currentEntity = it.next();
            prepareSqlFile();
            prepareOptions();
            prepareSql();
        } else {
            return;
        }
        while (it.hasNext()) {
            currentEntity = it.next();
            prepareSql();
        }
        assertEquals(elements.size(), sqls.size());
    }

    protected void prepareSqlFile() {
        sqlFile = config.getSqlFileRepository().getSqlFile(sqlFilePath,
                config.getDialect());
    }

    protected void prepareOptions() {
        if (queryTimeout <= 0) {
            queryTimeout = config.getQueryTimeout();
        }
        if (batchSize <= 0) {
            batchSize = config.getBatchSize();
        }
    }

    protected void prepareSql() {
        Value value = new Value(elementClass, currentEntity);
        ExpressionEvaluator evaluator = new ExpressionEvaluator(Collections
                .singletonMap(parameterName, value), config.getDialect()
                .getExpressionFunctions());
        NodePreparedSqlBuilder sqlBuilder = new NodePreparedSqlBuilder(config,
                kind, sqlFile.getPath(), evaluator);
        PreparedSql sql = sqlBuilder.build(sqlFile.getSqlNode());
        sqls.add(sql);
    }

    @Override
    public void complete() {
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setSqlFilePath(String sqlFilePath) {
        this.sqlFilePath = sqlFilePath;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public void setElements(Iterable<E> elements) {
        assertNotNull(elements);
        this.elements = new ArrayList<E>();
        for (E element : elements) {
            this.elements.add(element);
        }
        this.sqls = new ArrayList<PreparedSql>(this.elements.size());
    }

    public void setCallerClassName(String callerClassName) {
        this.callerClassName = callerClassName;
    }

    public void setCallerMethodName(String callerMethodName) {
        this.callerMethodName = callerMethodName;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public PreparedSql getSql() {
        return sqls.get(0);
    }

    @Override
    public String getClassName() {
        return callerClassName;
    }

    @Override
    public String getMethodName() {
        return callerMethodName;
    }

    @Override
    public List<PreparedSql> getSqls() {
        return sqls;
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public boolean isOptimisticLockCheckRequired() {
        return optimisticLockCheckRequired;
    }

    @Override
    public boolean isAutoGeneratedKeysSupported() {
        return false;
    }

    @Override
    public boolean isExecutable() {
        return executable;
    }

    @Override
    public SqlExecutionSkipCause getSqlExecutionSkipCause() {
        return sqlExecutionSkipCause;
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public String toString() {
        return sqls.toString();
    }

}
