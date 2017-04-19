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

package com.inter3i.sun.persistence.dataimport.travle;

import com.inter3i.sun.persistence.dataimport.base.DocumentEntity;
import lombok.Data;

import java.util.List;

@Data
public class Product extends DocumentEntity {
    private String productNo;
    private String topicCategory;
    private String packageType;


    @Data
    public static class Products {
        private List<Product> products;

    }
}
