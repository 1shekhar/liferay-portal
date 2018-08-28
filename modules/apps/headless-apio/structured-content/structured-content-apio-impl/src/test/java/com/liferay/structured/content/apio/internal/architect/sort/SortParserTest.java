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

package com.liferay.structured.content.apio.internal.architect.sort;

import com.liferay.structured.content.apio.architect.sort.Sort;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class SortParserTest {

	@Test
	public void testGetSortAsc() {
		Optional<Sort.SortField> sortOptional = _sortParser.getSortField(
			"field:asc");

		Assert.assertTrue(sortOptional.isPresent());

		Sort.SortField sortField = sortOptional.get();

		Assert.assertEquals("field", sortField.getFieldName());

		Assert.assertTrue(sortField.isAscending());
	}

	@Test
	public void testGetSortBadSyntax() {
		AbstractThrowableAssert exception = Assertions.assertThatThrownBy(
			() -> _sortParser.getSortField("field:desc:another")
		).isInstanceOf(
			RuntimeException.class
		);

		exception.hasMessageStartingWith("Unable to parse sort expression");
	}

	@Test
	public void testGetSortDesc() {
		Optional<Sort.SortField> sortOptional = _sortParser.getSortField(
			"field:desc");

		Assert.assertTrue(sortOptional.isPresent());

		Sort.SortField sortField = sortOptional.get();

		Assert.assertEquals("field", sortField.getFieldName());

		Assert.assertTrue(!sortField.isAscending());
	}

	@Test
	public void testGetSortNoOrder() {
		Optional<Sort.SortField> sortOptional = _sortParser.getSortField("field");

		Assert.assertTrue(sortOptional.isPresent());

		Sort.SortField sortField = sortOptional.get();

		Assert.assertEquals("field", sortField.getFieldName());

		Assert.assertTrue(sortField.isAscending());
	}

	@Test
	public void testGetSortNull() {
		Optional<Sort.SortField> sortOptional = _sortParser.getSortField(null);

		Assert.assertTrue(!sortOptional.isPresent());
	}

	@Test
	public void testIsAscAnotherValue() {
		Assert.assertTrue(_sortParser.isAscending("reverse"));
	}

	@Test
	public void testIsAscAscLower() {
		Assert.assertTrue(_sortParser.isAscending("asc"));
	}

	@Test
	public void testIsAscAscLowerAndUpper() {
		Assert.assertTrue(_sortParser.isAscending("aSC"));
	}

	@Test
	public void testIsAscAscUpper() {
		Assert.assertTrue(_sortParser.isAscending("ASC"));
	}

	@Test
	public void testIsAscDescLower() {
		Assert.assertTrue(!_sortParser.isAscending("desc"));
	}

	@Test
	public void testIsAscDescLowerAndUpper() {
		Assert.assertTrue(!_sortParser.isAscending("dESC"));
	}

	@Test
	public void testIsAscDescUpper() {
		Assert.assertTrue(!_sortParser.isAscending("DESC"));
	}

	@Test
	public void testIsAscEmpty() {
		Assert.assertTrue(_sortParser.isAscending(""));
	}

	@Test
	public void testIsAscNull() {
		Assert.assertTrue(_sortParser.isAscending(null));
	}

	@Test
	public void testSortEmpty() {
		List<Sort.SortField> sortFields = _sortParser.parse("");

		Assert.assertEquals(
			"No sort keys should be obtained: " + sortFields, 0, sortFields.size());
	}

	@Test
	public void testSortOneField() {
		List<Sort.SortField> sortFields = _sortParser.parse("field1");

		Assert.assertEquals(
			"One sort field should be obtained: " + sortFields, 1, sortFields.size());

		Sort.SortField sortField = sortFields.get(0);

		Assert.assertEquals("field1", sortField.getFieldName());
	}

	@Test
	public void testSortOnlyComma() {
		List<Sort.SortField> sortFields = _sortParser.parse(",");

		Assert.assertEquals(
			"No sort fields should be obtained: " + sortFields, 0, sortFields.size());
	}

	@Test
	public void testSortTwoFields() {
		List<Sort.SortField> sortFields = _sortParser.parse("field1,field2");

		Assert.assertEquals(
			"Two sort fields should be obtained: " + sortFields, 2,
			sortFields.size());

		Sort.SortField sortField = sortFields.get(0);

		Assert.assertEquals("field1", sortField.getFieldName());

		Assert.assertTrue(sortField.isAscending());

		Sort.SortField sortField2 = sortFields.get(1);

		Assert.assertEquals("field2", sortField2.getFieldName());

		Assert.assertTrue(sortField2.isAscending());
	}

	@Test
	public void testSortTwoFieldsAscAndDesc() {
		List<Sort.SortField> sortFields = _sortParser.parse(
			"field1:asc,field2:desc");

		Assert.assertEquals(
			"Two sort fields should be obtained: " + sortFields, 2,
			sortFields.size());

		Sort.SortField sortField = sortFields.get(0);

		Assert.assertEquals("field1", sortField.getFieldName());

		Assert.assertTrue(sortField.isAscending());

		Sort.SortField sortField2 = sortFields.get(1);

		Assert.assertEquals("field2", sortField2.getFieldName());

		Assert.assertTrue(!sortField2.isAscending());
	}

	@Test
	public void testSortTwoFieldsDefaultAndDesc() {
		List<Sort.SortField> sortFields = _sortParser.parse("field1,field2:desc");

		Assert.assertEquals(
			"Two sort fields should be obtained: " + sortFields, 2,
			sortFields.size());

		Sort.SortField sortField = sortFields.get(0);

		Assert.assertEquals("field1", sortField.getFieldName());

		Assert.assertTrue(sortField.isAscending());

		Sort.SortField sortField2 = sortFields.get(1);

		Assert.assertEquals("field2", sortField2.getFieldName());

		Assert.assertTrue(!sortField2.isAscending());
	}

	private static final SortParserImpl _sortParser = new SortParserImpl();

}