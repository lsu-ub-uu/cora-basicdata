/**
 * Copyright 2015, 2016, 2023 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.basicdata.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataResourceLink;

public final class CoraDataResourceLink implements DataResourceLink {

	private static final String MIME_TYPE_GENERIC = "application/octet-stream";
	private static final String NOT_YET_IMPLEMENTED = "Not yet implemented.";

	private List<Action> actions = new ArrayList<>();
	private String nameInData;
	private String mimeType;
	private String repeatId;

	public static CoraDataResourceLink withNameInData(String nameInData) {
		return new CoraDataResourceLink(nameInData, MIME_TYPE_GENERIC);
	}

	public static CoraDataResourceLink withNameInDataAndMimeType(String nameInData,
			String mimeType) {
		return new CoraDataResourceLink(nameInData, mimeType);
	}

	private CoraDataResourceLink(String nameInData, String mimeType) {
		this.nameInData = nameInData;
		this.mimeType = mimeType;
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public boolean hasReadAction() {
		return actions.contains(Action.READ);
	}

	@Override
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String getMimeType() {
		return mimeType;
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	@Override
	public boolean hasRepeatId() {
		return repeatId != null && !"".equals(repeatId);
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

	@Override
	public boolean hasAttributes() {
		return false;
	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		throw new UnsupportedOperationException(NOT_YET_IMPLEMENTED);
	}

}
