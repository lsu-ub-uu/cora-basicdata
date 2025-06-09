/*
 * Copyright 2015, 2019, 2020, 2022 Uppsala University Library
 * Copyright 2016, 2023 Olov McKie
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
import java.util.function.Predicate;
import java.util.stream.Stream;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;

public class CoraDataGroup implements DataGroup {

	private String nameInData;
	private Set<DataAttribute> attributes = new HashSet<>();
	private List<DataChild> children = new ArrayList<>();
	private String repeatId;
	private Predicate<DataChild> isDataAtomic = CoraDataAtomic.class::isInstance;
	private Predicate<DataChild> isDataGroup = CoraDataGroup.class::isInstance;

	public static CoraDataGroup withNameInData(String nameInData) {
		return new CoraDataGroup(nameInData);
	}

	protected CoraDataGroup(String nameInData) {
		this.nameInData = nameInData;
	}

	@Deprecated
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
		return children.stream().anyMatch(filterByNameInData(nameInData));
	}

	private Predicate<DataChild> filterByNameInData(String childNameInData) {
		return dataElement -> dataElementsNameInDataIs(dataElement, childNameInData);
	}

	private boolean dataElementsNameInDataIs(DataChild dataElement, String childNameInData) {
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

	private Stream<DataChild> getAtomicChildrenStream() {
		return children.stream().filter(isDataAtomic);
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		return getDataAtomicChildrenWithNameInData(childNameInData).toList();
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

	private Stream<DataChild> getGroupChildrenStream() {
		return children.stream().filter(isDataGroup);
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
	public DataChild getFirstChildWithNameInData(String childNameInData) {
		Optional<DataChild> optionalFirst = possiblyFindFirstChildWithNameInData(childNameInData);
		if (optionalFirst.isPresent()) {
			return optionalFirst.get();
		}
		throw new DataMissingException("Element not found for childNameInData:" + childNameInData);
	}

	private Optional<DataChild> possiblyFindFirstChildWithNameInData(String childNameInData) {
		return children.stream().filter(filterByNameInData(childNameInData)).findFirst();
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String childNameInData) {
		return getGroupChildrenWithNameInDataStream(childNameInData).toList();
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
		attributes.removeIf(attr -> nameInData.equals(attr.getNameInData()));
	}

	@Override
	public boolean removeFirstChildWithNameInData(String childNameInData) {
		return tryToRemoveChild(childNameInData);
	}

	private boolean tryToRemoveChild(String childNameInData) {
		for (DataChild dataElement : children) {
			if (dataElementsNameInDataIs(dataElement, childNameInData)) {
				children.remove(dataElement);
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
	public List<DataChild> getChildren() {
		return children;
	}

	@Override
	public void addChild(DataChild dataElement) {
		children.add(dataElement);
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
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
				.toList();

	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getGroupChildrenWithNameInDataStream(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	private Predicate<DataChild> filterByAttributes(DataAttribute... childAttributes) {
		return dataElement -> dataElementsHasAttributes(dataElement, childAttributes);
	}

	private boolean dataElementsHasAttributes(DataChild dataElement,
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
	public void addChildren(Collection<DataChild> dataElements) {
		children.addAll(dataElements);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String childNameInData) {
		return getChildrenWithNameInData(childNameInData).toList();

	}

	private Stream<DataChild> getChildrenWithNameInData(String childNameInData) {
		return children.stream().filter(filterByNameInData(childNameInData))
				.map(DataChild.class::cast);
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {

		Predicate<? super DataChild> childNameInDataMatches = element -> dataElementsNameInDataAndAttributesMatch(
				element, childNameInData, childAttributes);
		return removeMatchingChildren(childNameInDataMatches);

	}

	private boolean dataElementsNameInDataAndAttributesMatch(DataChild element,
			String childNameInData, DataAttribute... childAttributes) {
		return dataElementsNameInDataIs(element, childNameInData)
				&& dataElementsHasAttributes(element, childAttributes);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		Predicate<? super DataChild> childNameInDataMatches = element -> dataElementsNameInDataAndAttributesMatch(
				element, childNameInData, childAttributes);
		return filterChildren(childNameInDataMatches);
	}

	@Override
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		return getAtomicChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
				.toList();
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getAtomicChildrenWithNameInDataStream(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInDataStream(String childNameInData) {
		return getAtomicChildrenStream().filter(filterByNameInData(childNameInData))
				.map(CoraDataAtomic.class::cast);
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		return filterChildren(childFilter::childMatches);
	}

	private List<DataChild> filterChildren(Predicate<? super DataChild> predicate) {
		return children.stream().filter(predicate).toList();
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		return removeMatchingChildren(childFilter::childMatches);
	}

	private boolean removeMatchingChildren(Predicate<? super DataChild> filter) {
		return children.removeIf(filter);
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> type, String name) {
		return children.stream().filter(filterByNameInData(name)).anyMatch(type::isInstance);
	}

	@Override
	public <T extends DataChild> T getFirstChildOfTypeAndName(Class<T> type, String name) {
		Optional<T> optionalFirst = getOptionalFirstChildOfTypeAndName(type, name);
		if (optionalFirst.isPresent()) {
			return optionalFirst.get();
		}
		throw new DataMissingException("Child of type: " + type.getSimpleName() + " and name: "
				+ name + " not found as child.");
	}

	private <T extends DataChild> Optional<T> getOptionalFirstChildOfTypeAndName(Class<T> type,
			String name) {
		return children.stream().filter(filterByNameInData(name)).map(type::cast).findFirst();
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfType(Class<T> type) {
		return children.stream().filter(filterByType(type)).map(type::cast).toList();
	}

	private <T> Predicate<DataChild> filterByType(Class<T> type) {
		return type::isInstance;
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfTypeAndName(Class<T> type, String name) {
		return children.stream().filter(filterByNameInData(name).and(filterByType(type)))
				.map(type::cast).toList();
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> type,
			String name) {
		Optional<T> optionalFirst = getOptionalFirstChildOfTypeAndName(type, name);
		if (optionalFirst.isPresent()) {
			return children.remove(optionalFirst.get());
		}
		return false;
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> type, String name) {
		return children.removeAll(getChildrenOfTypeAndName(type, name));
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
