/*
 * Copyright 2016 Nikolay Kucheriaviy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ne1c.rainbowmvp;

import android.content.Context;
import android.support.v4.content.Loader;

import com.ne1c.rainbowmvp.base.BasePresenter;

public class PresenterLoader<P extends BasePresenter> extends Loader<P> {
    private P mPresenter;

    private String mTagPresenter;

    public PresenterLoader(Context context, String tagPresenter) {
        super(context);

        mTagPresenter = tagPresenter;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mPresenter != null) {
            deliverResult(mPresenter);
            return;
        }

        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        mPresenter = (P) PresenterFactory.getInstance().create(mTagPresenter);
        deliverResult(mPresenter);
    }

    @Override
    protected void onReset() {
        mPresenter.onDestroy();
        mPresenter = null;
    }
}
