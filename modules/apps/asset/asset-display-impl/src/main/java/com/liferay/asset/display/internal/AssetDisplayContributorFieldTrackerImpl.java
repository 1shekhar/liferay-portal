/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.asset.display.internal;

import com.liferay.asset.display.contributor.AssetDisplayContributorField;
import com.liferay.asset.display.contributor.AssetDisplayContributorFieldTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Jürgen Kappler
 */
@Component(
	immediate = true, service = AssetDisplayContributorFieldTracker.class
)
public class AssetDisplayContributorFieldTrackerImpl
	implements AssetDisplayContributorFieldTracker {

	@Deactivate
	public void deactivate() {
		_assetListContributorFields.clear();
	}

	@Override
	public List<AssetDisplayContributorField> getAssetDisplayContributorFields(
		String className) {

		List<AssetDisplayContributorField> assetDisplayContributorFields =
			_assetListContributorFields.get(className);

		if (assetDisplayContributorFields != null) {
			return assetDisplayContributorFields;
		}

		return Collections.emptyList();
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		service = AssetDisplayContributorField.class,
		target = "(model.class.name=*)"
	)
	protected void addAssetDisplayContributorField(
		AssetDisplayContributorField assetDisplayContributorField,
		Map<String, Object> properties) {

		String className = (String)properties.get("model.class.name");

		List<AssetDisplayContributorField> assetDisplayContributorFields =
			new ArrayList<>();

		if (_assetListContributorFields.containsKey(className)) {
			assetDisplayContributorFields = _assetListContributorFields.get(
				className);
		}

		assetDisplayContributorFields.add(assetDisplayContributorField);

		_assetListContributorFields.put(
			className, assetDisplayContributorFields);
	}

	protected void removeAssetDisplayContributorField(
		AssetDisplayContributorField assetDisplayContributorField,
		Map<String, Object> properties) {

		String className = (String)properties.get("model.class.name");

		List<AssetDisplayContributorField> assetDisplayContributorFields =
			_assetListContributorFields.get(className);

		if (assetDisplayContributorFields != null) {
			assetDisplayContributorFields.remove(assetDisplayContributorField);
		}
	}

	private final Map<String, List<AssetDisplayContributorField>>
		_assetListContributorFields = new ConcurrentHashMap<>();

}