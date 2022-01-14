/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.config.datasource.pool.creator;

import lombok.SneakyThrows;
import org.apache.shardingsphere.infra.config.datasource.pool.metadata.DataSourcePoolMetaData;
import org.apache.shardingsphere.infra.config.datasource.pool.metadata.DataSourcePoolMetaDataFactory;
import org.apache.shardingsphere.infra.config.datasource.props.DataSourceProperties;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;

import javax.sql.DataSource;
import java.util.Map.Entry;

/**
 * Data source pool creator.
 */
public final class DataSourcePoolCreator {
    
    static {
        ShardingSphereServiceLoader.register(DataSourcePoolMetaData.class);
    }
    
    private final DataSourcePoolMetaData poolMetaData;
    
    public DataSourcePoolCreator(final String dataSourceClassName) {
        poolMetaData = DataSourcePoolMetaDataFactory.newInstance(dataSourceClassName);
    }
    
    /**
     * Create data source.
     * 
     * @param dataSourceProps data source properties
     * @return created data source
     */
    public DataSource createDataSource(final DataSourceProperties dataSourceProps) {
        DataSource result = buildDataSource(dataSourceProps.getDataSourceClassName());
        addPropertySynonym(dataSourceProps);
        DataSourceReflection dataSourceReflection = new DataSourceReflection(result);
        setDefaultFields(dataSourceReflection);
        setConfiguredFields(dataSourceProps, dataSourceReflection);
        dataSourceReflection.addDefaultDataSourceProperties(poolMetaData.getJdbcUrlPropertiesFieldName(), poolMetaData.getJdbcUrlFieldName());
        return result;
    }
    
    @SneakyThrows(ReflectiveOperationException.class)
    private DataSource buildDataSource(final String dataSourceClassName) {
        return (DataSource) Class.forName(dataSourceClassName).getConstructor().newInstance();
    }
    
    private void addPropertySynonym(final DataSourceProperties dataSourceProps) {
        for (Entry<String, String> entry : poolMetaData.getPropertySynonyms().entrySet()) {
            dataSourceProps.addPropertySynonym(entry.getKey(), entry.getValue());
        }
    }
    
    private void setDefaultFields(final DataSourceReflection dataSourceReflection) {
        for (Entry<String, Object> entry : poolMetaData.getDefaultProperties().entrySet()) {
            dataSourceReflection.setField(entry.getKey(), entry.getValue());
        }
    }
    
    private void setConfiguredFields(final DataSourceProperties dataSourceProps, final DataSourceReflection dataSourceReflection) {
        for (Entry<String, Object> entry : dataSourceProps.getAllProperties().entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            if (isValidProperty(fieldName, fieldValue)) {
                dataSourceReflection.setField(fieldName, fieldValue);
            }
        }
    }
    
    private boolean isValidProperty(final String key, final Object value) {
        return !poolMetaData.getInvalidProperties().containsKey(key) || null == value || !value.equals(poolMetaData.getInvalidProperties().get(key));
    }
}
