package org.seasar.doma.criteria.query

import java.lang.reflect.Method
import org.seasar.doma.FetchType
import org.seasar.doma.jdbc.Config
import org.seasar.doma.jdbc.PreparedSql
import org.seasar.doma.jdbc.SelectOptions
import org.seasar.doma.jdbc.SqlLogType

class SelectQuery(
    private val config: Config,
    private val sql: PreparedSql,
    private val className: String,
    private val methodName: String
) : org.seasar.doma.jdbc.query.SelectQuery {

    override fun getConfig(): Config {
        return config
    }

    override fun getSql(): PreparedSql {
        return sql
    }

    override fun getClassName(): String {
        return className
    }

    override fun getMethodName(): String {
        return methodName
    }

    override fun getFetchType(): FetchType {
        return FetchType.EAGER
    }

    override fun getFetchSize(): Int {
        return config.fetchSize
    }

    override fun getMaxRows(): Int {
        return config.maxRows
    }

    override fun getQueryTimeout(): Int {
        return config.queryTimeout
    }

    override fun comment(sql: String?): String {
        throw UnsupportedOperationException()
    }

    override fun getSqlLogType(): SqlLogType {
        throw UnsupportedOperationException()
    }

    override fun isResultEnsured(): Boolean {
        return false
    }

    override fun isResultMappingEnsured(): Boolean {
        return false
    }

    override fun isResultStream(): Boolean {
        return false
    }

    override fun prepare() {
        throw UnsupportedOperationException()
    }

    override fun complete() {
        throw UnsupportedOperationException()
    }

    override fun getOptions(): SelectOptions {
        throw UnsupportedOperationException()
    }

    override fun getMethod(): Method {
        throw UnsupportedOperationException()
    }
}
