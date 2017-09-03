package com.repackage.exception;

/**
 * Created by Shinelon on 2017/5/26.
 */
public class XmlParseException extends Exception {
    private static final long serialVersionUID = 1L;

    public XmlParseException() {
        super();
    }

    public XmlParseException(String message) {
        super(message);
    }

    public XmlParseException(Throwable cause) {
        super(cause);
    }

    public XmlParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
