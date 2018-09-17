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

package com.liferay.structured.content.apio.internal.architect.form;

import com.liferay.apio.architect.form.Form;
import com.liferay.media.object.apio.architect.identifier.MediaObjectIdentifier;

/**
 * @author Javier Gamarra
 */
public class StructuredContentValuesForm {

	public static Form<StructuredContentValuesForm> buildValuesForm(
		Form.Builder<StructuredContentValuesForm> builder) {

		return builder.title(
			__ -> "The structured content values form"
		).description(
			__ -> "This form is used to create the values of a structured form"
		).constructor(
			StructuredContentValuesForm::new
		).addOptionalLinkedModel(
			"mediaObject", MediaObjectIdentifier.class,
			StructuredContentValuesForm::setDocument
		).addOptionalDouble(
			"latitude", StructuredContentValuesForm::setLatitude
		).addOptionalDouble(
			"longitude", StructuredContentValuesForm::setLongitude
		).addOptionalString(
			"name", StructuredContentValuesForm::setName
		).addOptionalString(
			"value", StructuredContentValuesForm::setValue
		).build();
	}

	public Long getDocument() {
		return _document;
	}

	public Double getLatitude() {
		return _latitude;
	}

	public Double getLongitude() {
		return _longitude;
	}

	public String getName() {
		return _name;
	}

	public String getValue() {
		return _value;
	}

	public void setDocument(Long document) {
		_document = document;
	}

	public void setLatitude(Double latitude) {
		_latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		_longitude = longitude;
	}

	public void setName(String name) {
		_name = name;
	}

	}

	public void setValue(String value) {
		_value = value;
	}

	private Long _document;
	private Double _latitude;
	private Double _longitude;
	private String _name;
	private String _value;

}