package com.github.druid.config;

public class DataSourceContext {

    private static final ThreadLocal<DatasourceName> contextHolder = new ThreadLocal<>();

    public static void setDataSource(DatasourceName value) {
        contextHolder.set(value);
    }

    public static DatasourceName getDataSource() {
        return contextHolder.get();
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }
}
