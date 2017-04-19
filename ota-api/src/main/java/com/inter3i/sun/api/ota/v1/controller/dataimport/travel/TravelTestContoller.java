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

package com.inter3i.sun.api.ota.v1.controller.dataimport.travel;

import com.inter3i.sun.persistence.Repository;
import com.inter3i.sun.persistence.dataimport.travle.Product;
import com.inter3i.sun.persistence.impl.MongoRepository;
import com.inter3i.sun.persistence.impl.MongoRepositoryFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("/dataImport")
//@EnableAutoConfiguration
public class TravelTestContoller {

    ///getParameters() or getParts()
    // multipart/form-data or application/x-www-form-url-encoded

    //curl -X POST -H "Expect:" --data-binary @"test.jpg" "http://localhost:7001/upload"

    //不通过参数方式，直接通过contentType 来告诉服务端直接读取 二进制流 curl http://localhost:7001/upload -H "Content-Type: image/jpeg" --data-binary @test.jpg
    //通过参数 image来指定图片:    curl http://localhost:8080/upload -F image=@test.jpg


    @RequestMapping("/datatest")
    public String test() {
        return "hello wangcc";
    }

    @RequestMapping("/datatest1")
    public String test1() {
        return "hello wangcc 1";
    }

   /* @RequestMapping(value = "/testPost", method = RequestMethod.POST)
    public void testPost(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) {

    }*/

//    @RequestMapping(value = "/testPost", method = RequestMethod.POST)
//    public void testPost(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) {
//
//    }


    //@GetMapping(value = "/login/{name}/{pwd}", method = RequestMethod.POST)
    @RequestMapping(value = "/login/{name}/{pwd}", method = RequestMethod.POST)
    public
    @ResponseBody
    Product login(@PathVariable String name, @PathVariable String pwd, @RequestBody Product product) {
/*
        int age = loginBean.getAge();
*/

        /*Product product = new Product();
        product.setPackageType();
        product.setProductNo( );
        product.setTopicCategory();*/

        MongoRepositoryFactory factory = MongoRepositoryFactory.fromDefaultDb("3idata");
        String collectName = "product";

        MongoRepository<Product> repository = (MongoRepository) factory.createRepository(Product.class, collectName);

        repository.save(product);

        //repository.insert(product);

        if (name.equals("admin") && pwd.equals("admin")) {
            return product;
        } else {
            return product;
        }
    }

}
