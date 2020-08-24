/*
 * Copyright 2015, 2019, 2020 Uppsala University Library
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

package se.uu.ub.cora.basicdata.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import se.uu.ub.cora.basicdata.DataMissingException;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroup implements DataGroup {

	private String nameInData;
	private Set<DataAttribute> attributes = new HashSet<>();
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

	public static DataGroup asLinkWithNameInDataAndTypeAndId(String nameInData, String type,
			String id) {
		DataGroup dataGroup = new CoraDataGroup(nameInData);
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordType", type));
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordId", id));
		return dataGroup;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
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

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return getDataAtomicChildrenWithNameInData(childNameInData).collect(Collectors.toList());
	}

	private Stream<DataAtomic> getDataAtomicChildrenWithNameInData(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataAtomic.class::cast);
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		Optional<DataGroup> findFirst = getGroupChildrenWithNameInDataStream(childNameInData)
				.findFirst();
		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataStream(String childNameInData) {
		return getGroupChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataGroup.class::cast);
	}

	private Stream<DataElement> getGroupChildrenStream() {
		return getChildrenStream().filter(isDataGroup);
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		Optional<DataAtomic> findFirst = getDataAtomicChildrenWithNameInData(childNameInData)
				.findFirst();

		if (findFirst.isPresent()) {
			return findFirst.get();
		}
		throw new DataMissingException(
				"DataAtomic not found for childNameInData:" + childNameInData);
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

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String childNameInData) {
		return getGroupChildrenWithNameInDataStream(childNameInData).collect(Collectors.toList());
	}

	@Override
	public boolean hasAttributes() {
		return !attributes.isEmpty();
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
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		return tryToRemoveChild(childNameInData);
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
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		return getChildren().removeIf(filterByNameInData(childNameInData));
	}

	@Override
	public String getNameInData() {
		return nameInData;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
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

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
				.collect(Collectors.toList());

	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataStream(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	private Predicate<DataElement> filterByAttributes(DataAttribute... childAttributes) {
		return dataElement -> dataElementsHasAttributes(dataElement, childAttributes);
	}

	private boolean dataElementsHasAttributes(DataElement dataElement,
			DataAttribute[] childAttributes) {
		Collection<DataAttribute> attributesFromElement = dataElement.getAttributes();
		if (differentNumberOfAttributesInRequestedAndExisting(childAttributes,
				attributesFromElement)) {
			return false;
		}
		return allRequestedAttributesMatchExistingAttributes(childAttributes,
				attributesFromElement);
	}

	private boolean differentNumberOfAttributesInRequestedAndExisting(
			DataAttribute[] childAttributes, Collection<DataAttribute> attributesFromElement) {
		return childAttributes.length != attributesFromElement.size();
	}

	private boolean allRequestedAttributesMatchExistingAttributes(DataAttribute[] childAttributes,
			Collection<DataAttribute> attributesFromElement) {
		for (DataAttribute dataAttribute : childAttributes) {
			if (attributesDoesNotMatch(attributesFromElement, dataAttribute)) {
				return false;
			}
		}
		return true;
	}

	private boolean attributesDoesNotMatch(Collection<DataAttribute> attributesFromElement,
			DataAttribute dataAttribute) {
		return requestedAttributeDoesNotExists(attributesFromElement, dataAttribute);
	}

	private boolean requestedAttributeDoesNotExists(Collection<DataAttribute> attributesFromElement,
			DataAttribute requestedDataAttribute) {
		for (DataAttribute dataAttribute : attributesFromElement) {
			if (sameAttributeNameInData(requestedDataAttribute, dataAttribute)
					&& sameAttributeValue(requestedDataAttribute, dataAttribute)) {
				return false;
			}
		}
		return true;
	}

	private boolean sameAttributeValue(DataAttribute requestedDataAttribute,
			DataAttribute dataAttribute) {
		return dataAttribute.getValue().equals(requestedDataAttribute.getValue());
	}

	private boolean sameAttributeNameInData(DataAttribute requestedDataAttribute,
			DataAttribute dataAttribute) {
		return dataAttribute.getNameInData().equals(requestedDataAttribute.getNameInData());
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
	public void addChildren(Collection<DataElement> dataElements) {
		children.addAll(dataElements);
	}

	@Override
	public List<DataElement> getAllChildrenWithNameInData(String childNameInData) {
		return getChildrenWithNameInData(childNameInData).collect(Collectors.toList());

	}

	private Stream<DataElement> getChildrenWithNameInData(String childNameInData) {
		return getChildrenStream().filter(filterByNameInData(childNameInData))
				.map(DataElement.class::cast);
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {

		Predicate<? super DataElement> childNameInDataMatches = element -> dataElementsNameInDataAndAttributesMatch(
				element, childNameInData, childAttributes);
		return getChildren().removeIf(childNameInDataMatches);

	}

	private boolean dataElementsNameInDataAndAttributesMatch(DataElement element,
			String childNameInData, DataAttribute... childAttributes) {
		return dataElementsNameInDataIs(element, childNameInData)
				&& dataElementsHasAttributes(element, childAttributes);
	}

}
