/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.amethyst.webservices;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.webservices.interfaces.v7.IGetListSearchAppsV7WebService;
import com.aptoide.dataprovider.webservices.models.v7.Apiv7ListSearchApps;
import com.aptoide.dataprovider.webservices.models.v7.ListSearchApps;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import retrofit.RetrofitError;

public class GetListSearchAppsv7 extends RetrofitSpiceRequest<ListSearchApps, IGetListSearchAppsV7WebService> {

	private final Apiv7ListSearchApps arguments;

	public GetListSearchAppsv7(Apiv7ListSearchApps arguments) {
		super(ListSearchApps.class, IGetListSearchAppsV7WebService.class);
		this.arguments = arguments;
	}

	@Override
	public ListSearchApps loadDataFromNetwork() throws Exception {
		ListSearchApps listSearchApps = null;
		arguments.access_token = SecurePreferences.getInstance().getString("access_token", null);
		try {
			listSearchApps = getService().listSearchApps(arguments);
		} catch (RetrofitError error) {
			OauthErrorHandler.handle(error);
		}
		return listSearchApps;
	}
}
