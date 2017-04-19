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

import com.inter3i.sun.persistence.Entity;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

public class Convert extends AbstractHttpMessageConverter<Entity> {
    protected boolean supports(Class<?> aClass) {
        return false;
    }

    protected Entity readInternal(Class<? extends Entity> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    protected void writeInternal(Entity entity, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

    }
}
