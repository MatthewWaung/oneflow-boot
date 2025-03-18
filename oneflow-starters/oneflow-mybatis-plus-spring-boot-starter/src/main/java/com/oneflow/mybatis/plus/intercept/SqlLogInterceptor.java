package com.oneflow.mybatis.plus.intercept;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.oneflow.mybatis.plus.props.MybatisPlusProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;


@Slf4j
@Component
@RequiredArgsConstructor
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})
})
public class SqlLogInterceptor implements Interceptor {

    private final MybatisPlusProperties properties;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        // SQL日志支持动态关闭
        if (!properties.isSqlLog()) {
            return invocation.proceed();
        }

        long start = System.currentTimeMillis();
        Object proceed = invocation.proceed();
        long end = System.currentTimeMillis();
        String printSql = null;

        try {
            printSql = generateSql(invocation);
        } catch (Exception e) {
            log.error("print Sql exception: ", e);
        } finally {
            log.info("执行sql：[ {} ], sql执行耗时: [{} ms]", printSql, (end - start));
        }

        return proceed;
    }

    private String generateSql(Invocation invocation) {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = statementHandler.getBoundSql();

        Configuration configuration = mappedStatement.getConfiguration();

        // 获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 参数映射信息
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        String sql = boundSql.getSql();
        sql = sql.replaceAll("\\s+", " ").replaceAll("\\n", " ");

        if (!ObjectUtils.isEmpty(parameterObject) && !ObjectUtils.isEmpty(parameterMappings)) {
            // TypeHandlerRegistry是Mybatis用来管理TypeHandler的注册器，用于Java类型和JDBC类型之间的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                for (ParameterMapping param : parameterMappings) {
                    String propertyName = param.getProperty();
                    // 基于当前的参数对象创建一个新的 MetaObject，确保 paramMetaObject 能够正确识别参数对象的属性和 getter 方法，从而允许 sql 中的 ? 被正确替换为参数值
                    MetaObject paramMetaObject = configuration.newMetaObject(parameterObject);

                    if (paramMetaObject.hasGetter(propertyName)) {
                        Object obj = paramMetaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        sql = sql.replaceFirst("\\?", "missing");
                    }
                }
            }
        }

        return sql;
    }

    private String getParameterValue(Object object) {
        String value = "";
        if (object == null) {
            value = "null";
        } else if (object instanceof String) {
            value = "'" + object + "'";
        } else if (object instanceof Date) {
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + format.format((Date) object) + "'";
        } else {
            value = object.toString();
        }
        return value;
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
