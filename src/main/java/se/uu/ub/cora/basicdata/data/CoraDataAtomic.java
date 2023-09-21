/*
 * Copyright 2015, 2022 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataMissingException;

public final class CoraDataAtomic implements DataAtomic {

	private String nameInData;
	private String value;
	private String repeatId;
	private Set<DataAttribute> attributes = new HashSet<>();

	public static CoraDataAtomic withNameInDataAndValue(String nameInData, String value) {
		return new CoraDataAtomic(nameInData, value);
	}

	public static CoraDataAtomic withNameInDataAndValueAndRepeatId(String nameInData, String value,
			String repeatId) {
		return new CoraDataAtomic(nameInData, value, repeatId);
	}

	private CoraDataAtomic(String nameInData, String value) {
		this.nameInData = nameInData;
		this.value = value;
	}

	public CoraDataAtomic(String nameInData, String value, String repeatId) {
		this.nameInData = nameInData;
		this.value = value;
		this.repeatId = repeatId;
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
		Iterator<DataAttribute> iterator = attributes.iterator();
		while (iterator.hasNext()) {
			possiblyRemoveAttribute(iterator, nameInData);
		}
	}

	private void possiblyRemoveAttribute(Iterator<DataAttribute> iterator, String nameInData) {
		DataAttribute next = iterator.next();
		if (next.getNameInData().equals(nameInData)) {
			iterator.remove();
		}
	}

	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		return attributes;
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
	public String getValue() {
		return value;
	}

	@Override
	public boolean hasRepeatId() {
		return repeatId != null && !"".equals(repeatId);
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
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
