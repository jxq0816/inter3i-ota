/*
 *
 * Copyright (c) 2016, inter3i.com. All rights reserved.
 * All rights reserved.
 *
 * Author: wangchaochao
 * Created: 2016/12/09
 * Description:
 *
 */

package com.inter3i.sun.persistence.dataimport.base;

import com.inter3i.sun.persistence.Entity;
import lombok.Data;

/**
 * Created by Administrator on 2016/12/9.
 */

@Data
public class DocumentEntity extends Entity {
    private String pageUrl;
    private String originalUrl;
    private String content;
}
