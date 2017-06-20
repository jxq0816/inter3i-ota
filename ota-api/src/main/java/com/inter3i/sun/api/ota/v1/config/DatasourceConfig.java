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

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

public class DatasourceConfig {
    private static Resource resource = new ClassPathResource("/datasource.properties");
    private static Map configMap=new HashMap();
    private String dbName;
    private String mongoDBIp;
    private int mongoDBPort;
    /**
     * 根据不同的配置 查找缓存对应的数据库表：数据表
     */
    private Map<String, DBAuth> dbAuths = new HashMap<String, DBAuth>(2);

    public static final class DBAuth {
        private String userName;
        private String password;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static DatasourceConfig getConfigByDataSourceName(String dataSourceName) {
        DatasourceConfig rs=(DatasourceConfig)configMap.get(dataSourceName);
        if(rs!=null){
            return rs;
        }
        Properties props = null;
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<Map.Entry<Object, Object>> set = props.entrySet();
        for (Map.Entry<Object, Object> m : set) {
            if(m.getValue().equals(dataSourceName)){
                String key=(String)m.getKey();
                String[] array=key.split("\\.");
                String dataSourceNum=array[array.length-1];
                DatasourceConfig config=getConfig(dataSourceNum);
                configMap.put(dataSourceName,config);
                return config;
            }
        }
        return null;
    }

    public static DatasourceConfig getConfig(String dataSourceNum) {
        String dbNameKey="di.dbName."+dataSourceNum;
        String ipKey="di.mongoDBIp."+dataSourceNum;
        String portKey="di.mongoDBPort."+dataSourceNum;
        String userNameKey="di.dbAuth.userName."+dataSourceNum;
        String passwordKey="di.dbAuth.password."+dataSourceNum;

        synchronized (DatasourceConfig.class) {

            //初始化该配置类
            DatasourceConfig tmp = null;
            try {
                tmp = new DatasourceConfig();
                Properties props = PropertiesLoaderUtils.loadProperties(resource);
                Iterator it = props.keySet().iterator();
                String key;
                while (it.hasNext()) {
                    key = (String) it.next();

                    if(ipKey.equals(key)){
                        tmp.setMongoDBIp((String) props.get(key));
                    } else if(dbNameKey.equals(key)){
                        tmp.setDbName((String) props.get(key));
                    }else if(portKey.equals(key)){
                        tmp.setMongoDBPort(Integer.valueOf((String) props.get(key)));
                    }else if (key.equals(userNameKey)) {
                        //提取用户名
                        String dbName = getKeyInLastSpliteTerm(key, ".");
                        tmp.addAuthUserName4(dbName, (String) props.get(key));
                    } else if (key.equals(passwordKey)) {
                        //提取密码
                        String dbName = getKeyInLastSpliteTerm(key, ".");
                        tmp.addAuthPassword4(dbName, (String) props.get(key));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tmp;
        }
    }

    //获取di.dbAuth.password.3idata 中的 3idata
    private static String getKeyInLastSpliteTerm(String key, String splitChar) {
        if (!key.contains(splitChar)) {
            return null;
        }
        if (key.endsWith(splitChar)) {
            throw new RuntimeException("key can not end with splitChar:[" + splitChar + "].");
        }
        return key.substring(key.lastIndexOf(splitChar) + 1);
    }

    public DBAuth getDBAuthBy(final String dbName) {
        if (!dbAuths.containsKey(dbName)) {
            return null;
        }
        return dbAuths.get(dbName);
    }



    public void addAuthUserName4(final String dbName, final String userName) {
        DBAuth dbAuth = null;
        if (!dbAuths.containsKey(dbName)) {
            dbAuth = new DBAuth();
            dbAuths.put(dbName, dbAuth);
        } else {
            dbAuth = dbAuths.get(dbName);
        }
        dbAuth.setUserName(userName);
    }

    public void addAuthPassword4(final String dbName, final String password) {
        DBAuth dbAuth = null;
        if (!dbAuths.containsKey(dbName)) {
            dbAuth = new DBAuth();
            dbAuths.put(dbName, dbAuth);
        } else {
            dbAuth = dbAuths.get(dbName);
        }
        dbAuth.setPassword(password);
    }
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getMongoDBIp() {
        return mongoDBIp;
    }

    public void setMongoDBIp(String mongoDBIp) {
        this.mongoDBIp = mongoDBIp;
    }


    public int getMongoDBPort() {
        return mongoDBPort;
    }

    public void setMongoDBPort(int mongoDBPort) {
        this.mongoDBPort = mongoDBPort;
    }

}
