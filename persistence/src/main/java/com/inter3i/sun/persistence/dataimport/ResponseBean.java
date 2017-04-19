/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2016/12/12
 * Description:
 *
 */

package com.inter3i.sun.persistence.dataimport;

import lombok.Data;


@Data
public class ResponseBean {
    private boolean success = true;
    private String errorCode;
    private String errorMsg;
}
