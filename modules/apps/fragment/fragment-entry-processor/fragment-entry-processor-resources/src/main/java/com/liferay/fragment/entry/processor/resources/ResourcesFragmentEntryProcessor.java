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

package com.liferay.fragment.entry.processor.resources;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.processor.FragmentEntryProcessor;
import com.liferay.fragment.service.FragmentCollectionService;
import com.liferay.fragment.service.FragmentEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	immediate = true, property = "fragment.entry.processor.priority:Integer=4",
	service = FragmentEntryProcessor.class
)
public class ResourcesFragmentEntryProcessor implements FragmentEntryProcessor {

	@Override
	public String processFragmentEntryLinkHTML(
			FragmentEntryLink fragmentEntryLink, String html, String mode,
			Locale locale)
		throws PortalException {

		FragmentEntry fragmentEntry = _fragmentEntryService.fetchFragmentEntry(
			fragmentEntryLink.getFragmentEntryId());

		if (fragmentEntry == null) {
			return html;
		}

		FragmentCollection fragmentCollection =
			_fragmentCollectionService.fetchFragmentCollection(
				fragmentEntry.getFragmentCollectionId());

		while (html.contains(_RESOURCES_PATH)) {
			int index = html.indexOf(_RESOURCES_PATH);

			String delimiter = html.substring(index - 1, index);

			if (Objects.equals(delimiter, StringPool.OPEN_PARENTHESIS)) {
				delimiter = StringPool.CLOSE_PARENTHESIS;
			}

			int lastIndex = html.indexOf(delimiter, index);

			if (lastIndex < 0) {
				break;
			}

			String fileName = html.substring(
				index + _RESOURCES_PATH.length(), lastIndex);

			FileEntry fileEntry =
				PortletFileRepositoryUtil.fetchPortletFileEntry(
					fragmentEntry.getGroupId(),
					fragmentCollection.getResourcesFolderId(), fileName);

			String fileEntryURL = StringPool.BLANK;

			if (fileEntry != null) {
				fileEntryURL = DLUtil.getPreviewURL(
					fileEntry, fileEntry.getFileVersion(), null,
					StringPool.BLANK, false, false);
			}

			html = html.replaceAll(_RESOURCES_PATH + fileName, fileEntryURL);
		}

		return html;
	}

	@Override
	public void validateFragmentEntryHTML(String html) {
	}

	private static final String _RESOURCES_PATH = "../../resources/";

	@Reference
	private FragmentCollectionService _fragmentCollectionService;

	@Reference
	private FragmentEntryService _fragmentEntryService;

}