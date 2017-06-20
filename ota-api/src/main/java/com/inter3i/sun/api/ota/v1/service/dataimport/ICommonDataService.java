/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: Administrator
 * Created: 2016/12/12
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.service.dataimport;

import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.config.StoreDataConfig;
import com.inter3i.sun.persistence.dataimport.CommonData;

import java.net.UnknownHostException;

public interface ICommonDataService {
    void savaCommonData(final String cacheName, final CommonData commonDatas, final MongoDBServerConfig serverConfig,final StoreDataConfig storeDataConfig) throws UnknownHostException;
}
