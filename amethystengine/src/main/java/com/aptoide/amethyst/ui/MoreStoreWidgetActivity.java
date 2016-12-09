package com.aptoide.amethyst.ui;

import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.main.HomeTabAdapter;
import com.aptoide.amethyst.fragments.store.BaseWebserviceFragment;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.persistence.DurationInMillis;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MoreStoreWidgetActivity extends MoreActivity {


    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = MoreStoreWidgetFragment.newInstance();
        fragment.setArguments(args);
        return fragment;
    }

    public static class MoreStoreWidgetFragment extends BaseWebserviceFragment {

        private String eventActionUrl;
        private String label;
        private long storeId;
        private boolean isFromHomeBundle;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Bundle args = getArguments();
            eventActionUrl = args.getString(Constants.EVENT_ACTION_URL);
            label = args.getString(Constants.LOCALYTICS_TAG);
            storeId = args.getLong(Constants.STOREID_KEY, -1);
            isFromHomeBundle = args.getBoolean(Constants.HOME_BUNDLES_KEY, false);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        public static Fragment newInstance() {
            return new MoreStoreWidgetFragment();
        }

        @Override
        protected void executeSpiceRequest(boolean useCache) {

            this.useCache = useCache;

            long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;
            spiceManager.execute(
                    AptoideUtils.RepoUtils.buildStoreWidgetRequest(storeId, eventActionUrl, label),
                    getBaseContext() + parseActionUrlIntoCacheKey(eventActionUrl) + "-" + BUCKET_SIZE + "-" + AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false),
                    cacheExpiryDuration,
                    listener);
        }

        @Override
        protected BaseAdapter getAdapter() {
            return new HomeTabAdapter(displayableList, getFragmentManager(), getStoreTheme(),getStoreName(), label, isFromHomeBundle);
        }

        @Override
        protected String getBaseContext() {
            return "GetMoreStoreWidgets";
        }

    }
}
