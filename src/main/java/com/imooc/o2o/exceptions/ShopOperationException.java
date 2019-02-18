package com.imooc.o2o.exceptions;

/**
 * Created by pc on 2019/2/1.
 */
public class ShopOperationException extends RuntimeException {
    private static final long serialVersionUID = 2361446884822298905L;

    public ShopOperationException(String msg){
        super(msg);
    }
}
