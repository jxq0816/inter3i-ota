/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

import java.util.Collection;

public interface Repository<E extends Entity, Q> {
    E findById(String id);

    Iterable<E> findAll();

    Iterable<E> findBy(IQuery<Q> query);

    void insert(E entity);

    void insert(Collection<E> entities);

    void save(E entity);

    void removeById(String id);
}
