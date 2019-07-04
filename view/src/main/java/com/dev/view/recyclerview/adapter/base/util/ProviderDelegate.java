package com.dev.view.recyclerview.adapter.base.util;

import android.util.SparseArray;

import com.dev.util.Keep;
import com.dev.view.recyclerview.adapter.base.provider.BaseItemProvider;

/**
 * https://github.com/chaychan
 * @author ChayChan
 * @date 2018/3/21  11:04
 */
@Keep
public class ProviderDelegate {

    private SparseArray<BaseItemProvider> mItemProviders = new SparseArray<>();

    public void registerProvider(BaseItemProvider provider){
        if (provider == null){
            throw new ItemProviderException("ItemProvider can not be null");
        }

        int viewType = provider.viewType();

        if (mItemProviders.get(viewType) == null){
            mItemProviders.put(viewType,provider);
        }
    }

    public SparseArray<BaseItemProvider> getItemProviders(){
        return mItemProviders;
    }

}
