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

package com.inter3i.sun.api.ota.v1.service.impl.dataimport;

import com.inter3i.sun.api.ota.v1.config.ImportDataConfig;
import com.inter3i.sun.api.ota.v1.config.MongoDBServerConfig;
import com.inter3i.sun.api.ota.v1.controller.dataimport.travel.TravelDataImportController;
import com.inter3i.sun.api.ota.v1.service.dataimport.ITravelDataService;
import com.inter3i.sun.persistence.Repository;
import com.inter3i.sun.persistence.dataimport.travle.Product;
import com.inter3i.sun.persistence.dataimport.travle.ProductComment;
import com.inter3i.sun.persistence.impl.MongoRepository;
import com.inter3i.sun.persistence.impl.MongoRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TravelDataService implements ITravelDataService {
    private static final Logger logger = LoggerFactory.getLogger(TravelDataImportController.class);


    public void saveProduct(Product product, final MongoDBServerConfig serverConfig) {
        MongoRepositoryFactory factory = MongoRepositoryFactory.fromDefaultDb(serverConfig.getDbName());
        MongoRepository<Product> repository = (MongoRepository) factory.createRepository(Product.class, ImportDataConfig.TABLE_NAME_PRODUCT);
        repository.save(product);
    }

    public void savaProducts(Product.Products productList) {

    }

    public void savaProductComment(ProductComment productComment) {

    }

    public void saveProductComments(ProductComment.ProductComments productComments) {

    }

}
