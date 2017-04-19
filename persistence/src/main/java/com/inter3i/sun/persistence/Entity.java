/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

import java.io.Serializable;

public abstract class Entity implements Serializable, Cloneable {
    private static final long serialVersionUID = 8788619824667101571L;

    public static final String ID = "id";

    private String id;

    public Entity() {
    }

    public Entity(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}