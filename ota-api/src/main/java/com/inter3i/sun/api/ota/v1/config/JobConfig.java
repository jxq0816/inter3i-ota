/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2017/04/21
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import com.inter3i.sun.api.ota.v1.util.ValidateUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class JobConfig {

    private static Resource resource = new ClassPathResource("/jobconfig.properties");

    public static final String IMPORT="import";
    public static final String SEGMENT="segment";
    /**
     * 入库所有的缓存名称 ---> 数据源
     */
    private static Map<String, String> importCacheNameDsNameMap = new HashMap<>(4);
    /**
     * 入库根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private static Map<String, String> importCacheNameDataTableMap = new HashMap<>(4);

    /**
     * 分词所有的缓存名称 ---> 数据源
     */
    private static Map<String, String> segmentCacheNameDsNameMap = new HashMap<>(4);
    /**
     * 分词根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private static Map<String, String> segmentCacheNameDataTableMap = new HashMap<>(4);

    public static void main(String[] args){
        getConfig();
    }

    public static JobConfig getConfig() {

        synchronized (JobConfig.class) {

            //初始化该配置类
            JobConfig tmp = null;
            try {
                tmp = new JobConfig();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key;
                while (it.hasNext()) {
                    key = (String) it.next();
                    if(key.contains(IMPORT)){
                        if(key.contains("talbename")){
                            tmp.importCacheNameDataTableMap.put(getKeyIn(key), (String) props.get(key));
                        }else if(key.contains("dsname")){
                            tmp.importCacheNameDsNameMap.put(getKeyIn(key), (String) props.get(key));
                        }
                    }
                    if(key.contains(SEGMENT)){
                        if(key.contains("talbename")){
                            tmp.segmentCacheNameDataTableMap.put(getKeyIn(key), (String) props.get(key));
                        }else if(key.contains("dsname")){
                            tmp.segmentCacheNameDsNameMap.put(getKeyIn(key), (String) props.get(key));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmp;
        }
    }

    //获取di.cacheTableMap[cache01] 中的[] 中的key
    private static String getKeyIn(String key) {
        int start=key.indexOf(".",4)+1;
        int end=key.indexOf(".",15);
        String rs=key.substring(start, end);
        return rs;
    }

    public Iterator<String> getAllCacheNames(String jobName) {
        Iterator<String> rs=null;
        if(IMPORT.equals(jobName)){
            rs=importCacheNameDataTableMap.keySet().iterator();
        }
        if(SEGMENT.equals(jobName)){
            rs=segmentCacheNameDataTableMap.keySet().iterator();
        }
        return rs;
    }

    /**
     * 根据cacheName,JobName 查询DataTable
     * @param cacheName
     * @param jobName
     * @return
     */
    public String getDataTableNameBy(String cacheName,String jobName) {

        validateCacheNameDataTables(cacheName,jobName);
        if(IMPORT.equals(jobName)) {
            return importCacheNameDataTableMap.get(cacheName);
        }
        if(SEGMENT.equals(jobName)) {
            return segmentCacheNameDataTableMap.get(cacheName);
        }
        return null;
    }

    private void validateCacheNameDataTables(final String cacheName,String jobName) {
        if(IMPORT.equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.importCacheNameDataTableMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!importCacheNameDataTableMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }

        if(SEGMENT.equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.segmentCacheNameDataTableMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!segmentCacheNameDataTableMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }
    }

    /**
     * 根据cacheName,JobName 查询DataSource
     * @param cacheName
     * @param jobName
     * @return
     */
    public String getDataSourceName(String cacheName,String jobName) {

        validateCacheNameDataTables(cacheName,jobName);
        if(IMPORT.equals(jobName)) {
            return importCacheNameDsNameMap.get(cacheName);
        }
        if(SEGMENT.equals(jobName)) {
            return segmentCacheNameDsNameMap.get(cacheName);
        }
        return null;
    }

    private void validateCacheNameDataSource(final String cacheName,String jobName) {
        if(IMPORT.equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.importCacheNameDsNameMap)) {
                throw new RuntimeException("cache name <===> dataTable mapping is empety!");
            }
            if (!importCacheNameDsNameMap.containsKey(cacheName)) {
                throw new RuntimeException("dataTable name for cacheName:[" + cacheName + "] is null.");
            }
        }

        if(SEGMENT.equals(jobName)){
            if (ValidateUtils.isNullOrEmpt(this.segmentCacheNameDsNameMap)) {
                throw new RuntimeException("cache name <===> dataSource mapping is empety!");
            }
            if (!segmentCacheNameDsNameMap.containsKey(cacheName)) {
                throw new RuntimeException("dataSource name for cacheName:[" + cacheName + "] is null.");
            }
        }
    }

}
