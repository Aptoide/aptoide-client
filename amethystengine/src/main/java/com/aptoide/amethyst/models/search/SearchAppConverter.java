/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 25/05/2016.
 */

package com.aptoide.amethyst.models.search;

import com.aptoide.dataprovider.webservices.models.v7.SearchItem;
import com.aptoide.dataprovider.webservices.models.v7.ViewItem;
import com.aptoide.models.ApkSuggestionJson;
import com.aptoide.models.displayables.SearchApp;
import com.aptoide.models.displayables.SponsoredSearchApp;

import java.util.ArrayList;
import java.util.List;

public class SearchAppConverter {

	private final int bucketSize;

	public SearchAppConverter(int bucketSize) {
		this.bucketSize = bucketSize;
	}

	public List<SearchApp> convert(List<SearchItem> searchItems, int offset, boolean fromSubscribedStore) {
		final List<SearchApp> myStoresApps = new ArrayList<>();

		int position = 0;
		for (SearchItem searchItem: searchItems) {
			position++;
			myStoresApps.add(convert(searchItem, fromSubscribedStore, offset + position));
		}
		return myStoresApps;
	}

	private SearchApp convert(SearchItem searchItem, boolean fromSubscribedStore, int position) {
		return new SearchApp(bucketSize,
				fromSubscribedStore,
				position,
				searchItem.name,
				searchItem.store != null? searchItem.store.name: "",
				searchItem.packageName,
				searchItem.file != null? searchItem.file.vername : "",
				searchItem.file != null && searchItem.file.vercode != null? searchItem.file.vercode.intValue() : 0,
				searchItem.file != null? searchItem.file.md5sum: "",
				searchItem.updated,
				searchItem.file != null && searchItem.file.malware != null? (ViewItem.File.Malware.TRUSTED.equals(searchItem.file.malware.rank)? 2 : 0) : 0,
				searchItem.icon,
				searchItem.hasVersions,
				searchItem.stats != null && searchItem.stats.rating != null? searchItem.stats.rating.avg: 0,
				searchItem.store != null && searchItem.store.appearance != null? searchItem.store.appearance.theme: "",
				searchItem.stats != null && searchItem.stats.downloads != null? searchItem.stats.downloads.longValue(): 0);
	}

	public SponsoredSearchApp convert(ApkSuggestionJson searchItem) {
		final ApkSuggestionJson.Partner partner = searchItem.getAds().get(0).getPartner();
		return new SponsoredSearchApp(bucketSize,
				searchItem.getAds().get(0).getInfo().getAd_id(),
				searchItem.getAds().get(0).getInfo().getCpc_url(),
				searchItem.getAds().get(0).getInfo().getCpi_url(),
				(partner != null? partner.getPartnerInfo().getName(): null),
				(partner != null? partner.getPartnerData().getClick_url(): null),
				searchItem.getAds().get(0).getData().description,
				searchItem.getAds().get(0).getData().downloads,
				searchItem.getAds().get(0).getData().icon,
				searchItem.getAds().get(0).getData().id,
				searchItem.getAds().get(0).getData().md5sum,
				searchItem.getAds().get(0).getData().name,
				searchItem.getAds().get(0).getData().packageName,
				searchItem.getAds().get(0).getData().repo,
				searchItem.getAds().get(0).getData().vercode,
				searchItem.getAds().get(0).getData().vername);
	}
}
