/**
 * Copyright 2015, 2016, 2023, 2025 Uppsala University Library
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataResourceLink;

public final class CoraDataResourceLink implements DataResourceLink {

	private Set<DataAttribute> attributes = new HashSet<>();
	private List<Action> actions = new ArrayList<>();
	private String nameInData;
	private String mimeType;
	private String repeatId;
	private String type;
	private String id;

	public static CoraDataResourceLink withNameInDataAndTypeAndIdAndMimeType(String nameInData,
			String type, String id, String mimeType) {
		return new CoraDataResourceLink(nameInData, type, id, mimeType);
	}

	private CoraDataResourceLink(String nameInData, String type, String id, String mimeType) {
		this.nameInData = nameInData;
		this.type = type;
		this.id = id;
		this.mimeType = mimeType;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getId() {
		return id;
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
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public boolean hasReadAction() {
		return actions.contains(Action.READ);
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
		possiblyRemovePreviouslyStoredAttribute(nameInData);
		attributes.add(CoraDataAttribute.withNameInDataAndValue(nameInData, value));
	}

	private void possiblyRemovePreviouslyStoredAttribute(String nameInData) {
		attributes.removeIf(attr -> nameInData.equals(attr.getNameInData()));
	}

	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}

	@Override
	public DataAttribute getAttribute(String attributeId) {
		for (DataAttribute dataAttribute : attributes) {
			if (dataAttribute.getNameInData().equals(attributeId)) {
				return dataAttribute;
			}
		}
		throw new DataMissingException("Attribute with id " + attributeId + " not found.");
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public Optional<String> getAttributeValue(String nameInData) {
		for (DataAttribute dataAttribute : attributes) {
			if (dataAttribute.getNameInData().equals(nameInData)) {
				return Optional.of(dataAttribute.getValue());
			}
		}
		return Optional.empty();
	}

}
