/*
 *
 * Copyright (c) 2017, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2017/01/09
 * Description:
 *
 */

package com.inter3i.sun.api.ota.v1.config;

import org.bson.Document;

public interface IMongoDocConverter {
    Document converBean2Doc(Object commonData);
}
