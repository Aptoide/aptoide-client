/*
 * Copyright (c) 2016.
 * Modified by marcelo.benites@aptoide.com on 06/05/2016.
 */

package com.aptoide.amethyst.analytics;

import com.aptoide.amethyst.BuildConfig;
import com.seatgeek.sixpack.Alternative;
import com.seatgeek.sixpack.Sixpack;
import com.seatgeek.sixpack.SixpackBuilder;
import com.seatgeek.sixpack.log.LogLevel;
import com.squareup.okhttp.Credentials;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Authenticator;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Route;

public class ABTestManager {

	public static final String APP_VIEW_SECURITY =
			"app-view-security-enhancements";
	private static ABTestManager instance;

	private SixpackBuilder sixpackBuilder;
	private final OkHttpClient httpClient;
	private final String sixpackUrl;
	private final ExecutorService executorService;
	private final Set<ABTest<?>> tests;
	private final Set<ABTest<?>> controlTests;
	private Sixpack sixpack;

	public static ABTestManager getInstance() {
		if (instance == null) {
			instance = new ABTestManager(new SixpackBuilder(), new OkHttpClient.Builder()
					.authenticator(new Authenticator() {

				@Override
				public okhttp3.Request authenticate(Route route, okhttp3.Response response) throws
						IOException {
					return response.request()
							.newBuilder()
							.header("Authorization", Credentials.basic(BuildConfig.SIXPACK_USER,
									BuildConfig.SIXPACK_PASSWORD))
							.build();
				}
			}).build(), BuildConfig.SIXPACK_URL, Executors.newCachedThreadPool());
		}
		return instance;
	}

	private ABTestManager(SixpackBuilder sixpackBuilder, OkHttpClient httpClient, String
			sixpackUrl, ExecutorService executorService) {
		this.sixpackBuilder = sixpackBuilder;
		this.httpClient = httpClient;
		this.sixpackUrl = sixpackUrl;
		this.executorService = executorService;
		this.tests = new HashSet<>();
		this.controlTests = new HashSet<>();
	}

	public void initialize(String clientId) {
		initializeSixpack(clientId);
		registerTests();
		prefetchTests();
	}

	private boolean isInitialized() {
		return sixpack != null;
	}

	private void initializeSixpack(String clientId) {
		sixpack = sixpackBuilder.setSixpackUrl(HttpUrl.parse(sixpackUrl))
				.setHttpClient(httpClient)
				.setClientId(clientId)
				.setLogLevel(BuildConfig.DEBUG ? LogLevel.VERBOSE : LogLevel.NONE)
				.build();
	}

	@SuppressWarnings("unchecked")
	private void registerTests() {
		tests.add(new SixpackABTest(executorService, sixpack.experiment()
				.withName(APP_VIEW_SECURITY)
				.withAlternatives(
						new Alternative("none"),
						new Alternative("warning-pop-up"),
						new Alternative("security-overlay"),
						new Alternative("both"))
				.build(), new SecurityAlternativeParser()));
	}

	private void registerControlTests() {
		if (controlTests.isEmpty()) {
			controlTests.add(new ControlABTest<>(APP_VIEW_SECURITY, SecurityOption.NONE));
		}
	}

	private void prefetchTests() {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				for (ABTest test : tests) {
					test.prefetch();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T> ABTest<T> get(String name) {
		if (isInitialized()) {
			for (ABTest test : tests) {
				if (test.getName().equals(name)) {
					return test;
				}
			}
			throw new IllegalArgumentException("No AB test for name: " + name);
		} else {
			return getControl(name);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> ABTest<T> getControl(String name) {
		synchronized (controlTests) {
			registerControlTests();
			for (ABTest test : controlTests) {
				if (test.getName().equals(name)) {
					return test;
				}
			}
			throw new IllegalArgumentException("No AB test for name: " + name);
		}
	}
}