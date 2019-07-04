package com.dev.view.recyclerview.adapter.base.util;

import com.dev.util.Keep;

/**
 * @author ChayChan
 * @description: ItemProviderException
 * @date 2018/4/12  9:10
 */
@Keep
public class ItemProviderException extends NullPointerException {

    public ItemProviderException(String message) {
        super(message);
    }

}
