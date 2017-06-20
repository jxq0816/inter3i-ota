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

package com.inter3i.sun.api.ota.v1.service.dataimport;

import com.inter3i.sun.api.ota.v1.config.DatasourceConfig;
import com.inter3i.sun.persistence.dataimport.travle.Product;
import com.inter3i.sun.persistence.dataimport.travle.ProductComment;

public interface ITravelDataService {
    public void saveProduct(final Product productfinal, final DatasourceConfig datasourceConfig);

    public void savaProducts(Product.Products productList);

    public void savaProductComment(ProductComment productComment);

    public void saveProductComments(ProductComment.ProductComments productComments);
}
