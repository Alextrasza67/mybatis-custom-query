package com.github.druid.datasource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 可以在系统运行过程中，动态注册datasource
 * 代码基于 {@link org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource} 进行扩展
 */
public class CustomRoutingDataSource extends AbstractDataSource implements InitializingBean {

    @Nullable
    private Map<Object, Object> targetDataSources;

    @Nullable
    private Object defaultTargetDataSource;

    private boolean lenientFallback = true;

    private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();

    @Nullable
    private Map<Object, DataSource> resolvedDataSources;

    @Nullable
    private DataSource resolvedDefaultDataSource;

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    /**
     * Specify the map of target DataSources, with the lookup key as key.
     * The mapped value can either be a corresponding {@link javax.sql.DataSource}
     * instance or a data source name String (to be resolved via a
     * {@link #setDataSourceLookup DataSourceLookup}).
     * <p>The key can be of arbitrary type; this class implements the
     * generic lookup process only. The concrete key representation will
     * be handled by {@link #resolveSpecifiedLookupKey(Object)} and
     * {@link #determineCurrentLookupKey()}.
     */
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        this.targetDataSources = targetDataSources;
    }

    /**
     * Specify the default target DataSource, if any.
     * <p>The mapped value can either be a corresponding {@link javax.sql.DataSource}
     * instance or a data source name String (to be resolved via a
     * {@link #setDataSourceLookup DataSourceLookup}).
     * <p>This DataSource will be used as target if none of the keyed
     * {@link #setTargetDataSources targetDataSources} match the
     * {@link #determineCurrentLookupKey()} current lookup key.
     */
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
    }

    /**
     * Specify whether to apply a lenient fallback to the default DataSource
     * if no specific DataSource could be found for the current lookup key.
     * <p>Default is "true", accepting lookup keys without a corresponding entry
     * in the target DataSource map - simply falling back to the default DataSource
     * in that case.
     * <p>Switch this flag to "false" if you would prefer the fallback to only apply
     * if the lookup key was {@code null}. Lookup keys without a DataSource
     * entry will then lead to an IllegalStateException.
     * @see #setTargetDataSources
     * @see #setDefaultTargetDataSource
     * @see #determineCurrentLookupKey()
     */
    public void setLenientFallback(boolean lenientFallback) {
        this.lenientFallback = lenientFallback;
    }

    /**
     * Set the DataSourceLookup implementation to use for resolving data source
     * name Strings in the {@link #setTargetDataSources targetDataSources} map.
     * <p>Default is a {@link JndiDataSourceLookup}, allowing the JNDI names
     * of application server DataSources to be specified directly.
     */
    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        this.dataSourceLookup = (dataSourceLookup != null ? dataSourceLookup : new JndiDataSourceLookup());
    }


    @Override
    public void afterPropertiesSet() {
        if (this.targetDataSources == null) {
            throw new IllegalArgumentException("Property 'targetDataSources' is required");
        }

        readWriteLock.writeLock().lock();
        try {
            this.resolvedDataSources = new HashMap<>();
            this.targetDataSources.forEach((key, value) -> {
                Object lookupKey = resolveSpecifiedLookupKey(key);
                DataSource dataSource = resolveSpecifiedDataSource(value);
                this.resolvedDataSources.put(lookupKey, dataSource);
            });
        } finally {
            readWriteLock.writeLock().unlock();
        }

        if (this.defaultTargetDataSource != null) {
            this.resolvedDefaultDataSource = resolveSpecifiedDataSource(this.defaultTargetDataSource);
        }
    }

    /**
     * Resolve the given lookup key object, as specified in the
     * {@link #setTargetDataSources targetDataSources} map, into
     * the actual lookup key to be used for matching with the
     * {@link #determineCurrentLookupKey() current lookup key}.
     * <p>The default implementation simply returns the given key as-is.
     * @param lookupKey the lookup key object as specified by the user
     * @return the lookup key as needed for matching
     */
    protected Object resolveSpecifiedLookupKey(Object lookupKey) {
        return lookupKey;
    }

    /**
     * Resolve the specified data source object into a DataSource instance.
     * <p>The default implementation handles DataSource instances and data source
     * names (to be resolved via a {@link #setDataSourceLookup DataSourceLookup}).
     * @param dataSource the data source value object as specified in the
     * {@link #setTargetDataSources targetDataSources} map
     * @return the resolved DataSource (never {@code null})
     * @throws IllegalArgumentException in case of an unsupported value type
     */
    protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
        if (dataSource instanceof DataSource) {
            return (DataSource) dataSource;
        }
        else if (dataSource instanceof String) {
            return this.dataSourceLookup.getDataSource((String) dataSource);
        }
        else {
            throw new IllegalArgumentException(
                    "Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
            return determineTargetDataSource().getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return determineTargetDataSource().getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }
        return determineTargetDataSource().unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return (iface.isInstance(this) || determineTargetDataSource().isWrapperFor(iface));
    }

    /**
     * Retrieve the current target DataSource. Determines the
     * {@link #determineCurrentLookupKey() current lookup key}, performs
     * a lookup in the {@link #setTargetDataSources targetDataSources} map,
     * falls back to the specified
     * {@link #setDefaultTargetDataSource default target DataSource} if necessary.
     * @see #determineCurrentLookupKey()
     */
    protected DataSource determineTargetDataSource() {
        Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
        Object lookupKey = determineCurrentLookupKey();

        readWriteLock.readLock().lock();
        DataSource dataSource;
        try {
            dataSource = this.resolvedDataSources.get(lookupKey);
        } finally {
            readWriteLock.readLock().unlock();
        }

        if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
            dataSource = this.resolvedDefaultDataSource;
        }
        if (dataSource == null) {
            throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
        }
        return dataSource;
    }

    /**
     * Determine the current lookup key. This will typically be
     * implemented to check a thread-bound transaction context.
     * <p>Allows for arbitrary keys. The returned key needs
     * to match the stored lookup key type, as resolved by the
     * {@link #resolveSpecifiedLookupKey} method.
     */
    @Nullable
    protected Object determineCurrentLookupKey() {
        return DataSourceContext.getDataSource();
    }


    /**
     * dynamic add datasource
     * @param _lookupKey
     * @param _dataSource
     */
    public void registerDataSource(Object _lookupKey, Object _dataSource){
        readWriteLock.writeLock().lock();
        try {
            if(this.resolvedDataSources == null){
                this.resolvedDataSources = new HashMap<>();
            }

            Object lookupKey = resolveSpecifiedLookupKey(_lookupKey);
            DataSource dataSource = resolveSpecifiedDataSource(_dataSource);
            this.resolvedDataSources.put(lookupKey, dataSource);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
