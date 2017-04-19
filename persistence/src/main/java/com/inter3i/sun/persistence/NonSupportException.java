/*
 *   Copyright (c) 2016, inter3i.com. All rights reserved.
 *
 *   Author: dq
 *   Created: 11/18/16 6:01 PM
 *   Description:
 *
 */

package com.inter3i.sun.persistence;

public class NonSupportException extends RuntimeException {

    private static final long serialVersionUID = 178751939980700153L;

    public NonSupportException() {
    }

    public NonSupportException(String message) {
        super(message);
    }

    public NonSupportException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonSupportException(Throwable cause) {
        super(cause);
    }

    public NonSupportException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
