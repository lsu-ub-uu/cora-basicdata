/*
 * Copyright 2015, 2019 Uppsala University Library
 * Copyright 2016 Olov McKie
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

package se.uu.ub.cora.basicdata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataPart;

public class CoraDataGroup implements DataGroup, DataElement, DataPart, Data {

	private String nameInData;
	private Map<String, String> attributes = new HashMap<>();
	private List<DataElement> children = new ArrayList<>();
	private String repeatId;
	private Predicate<DataElement> isDataAtomic = dataElement -> dataElement instanceof CoraDataAtomic;
	private Predicate<DataElement> isDataGroup = dataElement -> dataElement instanceof CoraDataGroup;

	public static CoraDataGroup withNameInData(String nameInData) {
		return new CoraDataGroup(nameInData);
	}

	protected CoraDataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	public static CoraDataGroup asLinkWithNameInDataAndTypeAndId(String nameInData, String type,
			String id) {
		CoraDataGroup dataGroup = new CoraDataGroup(nameInData);
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordType", type));
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordId", id));
		return dataGroup;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		return getChildrenStream().anyMatch(filterByNameInData(nameInData));
	}

	private Stream<DataElement> getChildrenStream() {
		return children.stream();
	}

	private Predicate<DataElement> filterByNameInData(String childNameInData) {
		return dataElement -> dataElementsNameInDataIs(dataElement, childNameInData);
	}

	private boolean dataElementsNameInDataIs(DataElement dataElement, String childNameInData) {
		return dataElement.getNameInData().equals(childNameInData);
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String childNameInData) {
		Optional<CoraDataAtomic> optionalFirst = getAtomicChildrenWithNameInData(childNameInData)
				.findFirst();
		return possiblyReturnAtomicChildWithNameInData(childNameInData, optionalFirst);
	}

	private String possiblyReturnAtomicChildWithNameInData(String childNameInData,
			Optional<CoraDataAtomic> optionalFirst) {
		if (optionalFirst.isPresent()) {
			return optionalFirst.get().getValue();
		}
		throw new DataMissingException(
				"Atomic value not found for childNameInData:" + childNameInData);
	}

	private Stream<CoraDataAtomic> getAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataAtomic.class::cast);
	}

	private Stream<DataElement> getAtomicChildrenStream() {
		return getChildrenStream().filter(isDataAtomic);
	}

	public List<CoraDataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return getDataAtomicChildrenWithNameInData(childNameInData).collect(Collectors.toList());
	}

	private Stream<CoraDataAtomic> getDataAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataAtomic.class::cast);
	}

	@Override
	public CoraDataGroup getFirstGroupWithNameInData(String childNameInData) {
		Optional<CoraDataGroup> findFirst = getGroupChildrenWithNameInDataStream(childNameInData)
				.findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	private Stream<CoraDataGroup> getGroupChildrenWithNameInDataStream(String childNameInData) {
		return getGroupChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataGroup.class::cast);
	}

	private Stream<DataElement> getGroupChildrenStream() {
		return getChildrenStream().filter(isDataGroup);
	}

	@Override
	public DataElement getFirstChildWithNameInData(String childNameInData) {
		Optional<DataElement> optionalFirst = possiblyFindFirstChildWithNameInData(childNameInData);
		if (optionalFirst.isPresent()) {
			return optionalFirst.get();
		}
		throw new DataMissingException("Element not found for childNameInData:" + childNameInData);
	}

	private Optional<DataElement> possiblyFindFirstChildWithNameInData(String childNameInData) {
		return getChildrenStream().filter(filterByNameInData(childNameInData)).findFirst();
	}

	public void removeChild(String string) {
		DataElement firstChildWithNameInData = getFirstChildWithNameInData(string);
		children.remove(firstChildWithNameInData);
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String childNameInData) {
		return getGroupChildrenWithNameInDataStream(childNameInData).collect(Collectors.toList());
	}

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		attributes.put(nameInData, value);
	}

	public void removeFirstChildWithNameInData(String childNameInData) {
		boolean childRemoved = tryToRemoveChild(childNameInData);
		if (!childRemoved) {
			throw new DataMissingException(
					"Element not found for childNameInData:" + childNameInData);
		}
	}

	private boolean tryToRemoveChild(String childNameInData) {
		for (DataElement dataElement : getChildren()) {
			if (dataElementsNameInDataIs(dataElement, childNameInData)) {
				getChildren().remove(dataElement);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public Map<String, String> getAttributes() {
		return attributes;
	}

	@Override
	public List<DataElement> getChildren() {
		return children;
	}

	@Override
	public void addChild(DataElement dataElement) {
		children.add(dataElement);
	}

	@Override
	public void setRepeatId(String repeatId) {
		this.repeatId = repeatId;
	}

	@Override
	public String getRepeatId() {
		return repeatId;
	}

	public Collection<CoraDataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			CoraDataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
				.collect(Collectors.toList());

	}

	private Stream<CoraDataGroup> getGroupChildrenWithNameInDataAndAttributes(
			String childNameInData, CoraDataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataStream(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	private Predicate<DataElement> filterByAttributes(CoraDataAttribute... childAttributes) {
		return dataElement -> dataElementsHasAttributes(dataElement, childAttributes);
	}

	private boolean dataElementsHasAttributes(DataElement dataElement,
			CoraDataAttribute[] childAttributes) {
		Map<String, String> attributesFromElement = dataElement.getAttributes();
		if (differentNumberOfAttributesInRequestedAndExisting(childAttributes,
				attributesFromElement)) {
			return false;
		}
		return allRequestedAttributesMatchExistingAttributes(childAttributes,
				attributesFromElement);
	}

	private boolean differentNumberOfAttributesInRequestedAndExisting(
			CoraDataAttribute[] childAttributes, Map<String, String> attributesFromElement) {
		return childAttributes.length != attributesFromElement.size();
	}

	private boolean allRequestedAttributesMatchExistingAttributes(
			CoraDataAttribute[] childAttributes, Map<String, String> attributesFromElement) {
		for (CoraDataAttribute dataAttribute : childAttributes) {
			if (attributesDoesNotMatch(attributesFromElement, dataAttribute)) {
				return false;
			}
		}
		return true;
	}

	private boolean attributesDoesNotMatch(Map<String, String> attributesFromElement,
			CoraDataAttribute dataAttribute) {
		return requestedAttributeDoesNotExists(attributesFromElement, dataAttribute)
				|| requestedAttributeHasDifferentValueAsExisting(attributesFromElement,
						dataAttribute);
	}

	private boolean requestedAttributeDoesNotExists(Map<String, String> attributesFromElement,
			CoraDataAttribute dataAttribute) {
		return !attributesFromElement.containsKey(dataAttribute.getNameInData());
	}

	private boolean requestedAttributeHasDifferentValueAsExisting(
			Map<String, String> attributesFromElement, CoraDataAttribute dataAttribute) {
		return !attributesFromElement.get(dataAttribute.getNameInData())
				.equals(dataAttribute.getValue());
	}

	public String getAttribute(String attributeId) {
		return attributes.get(attributeId);
	}
}
