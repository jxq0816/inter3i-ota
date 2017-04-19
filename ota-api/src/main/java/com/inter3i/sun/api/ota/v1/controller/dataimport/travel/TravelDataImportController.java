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

package com.inter3i.sun.api.ota.v1.controller.dataimport.travel;

import com.inter3i.sun.api.ota.v1.service.ServiceFactory;
import com.inter3i.sun.api.ota.v1.service.dataimport.ITravelDataService;
import com.inter3i.sun.persistence.dataimport.ResponseBean;
import com.inter3i.sun.persistence.dataimport.travle.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController("/travel/account")
public class TravelDataImportController {
    private static final Logger logger = LoggerFactory.getLogger(TravelDataImportController.class);

    private final ITravelDataService travelDataService;

    TravelDataImportController() {
        super();
        this.travelDataService = ServiceFactory.travelDataService();
    }

    /**
     * 添加多个旅游产品
     *
     * @param products
     * @return
     */
    @RequestMapping(value = "/products", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseBean importProducts(@RequestBody Product.Products products) {
        ResponseBean responseData = new ResponseBean();
        try {
            //travelDataService.
        } catch (Exception e) {
            logger.error("Import documents for travel exception:[" + e.getMessage() + "].", e);
            responseData.setSuccess(false);
            responseData.setErrorMsg("Import documents for travel exception:[" + e.getMessage() + "].");
        } finally {
            return responseData;
        }
    }

    /**
     * 添加单个旅游产品
     *
     * @param product
     * @return
     */
    @RequestMapping(value = "/product", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseBean importProduct(@RequestBody Product product) {
        ResponseBean responseData = new ResponseBean();
        try {

        } catch (Exception e) {
            logger.error("Import document for travel exception:[" + e.getMessage() + "].", e);
            responseData.setSuccess(false);
            responseData.setErrorMsg("Import document for travel exception:[" + e.getMessage() + "].");
        } finally {
            return responseData;
        }
    }

    @RequestMapping(value = "/products/comments", method = RequestMethod.PUT)
    public
    @ResponseBody
    ResponseBean importProductCommnets(@RequestBody Product product) {
        ResponseBean responseData = new ResponseBean();
        try {

        } catch (Exception e) {
            logger.error("Import document for travel exception:[" + e.getMessage() + "].", e);
            responseData.setSuccess(false);
            responseData.setErrorMsg("Import document for travel exception:[" + e.getMessage() + "].");
        } finally {
            return responseData;
        }
    }
}
