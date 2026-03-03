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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
	private Map<String, List<DataChild>> childrenByNameInDataMap = new HashMap<>();
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
		return childrenByNameInDataMap.containsKey(nameInData);
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			Optional<DataAtomic> optionalFirst = getAtomicChildrenWithNameInData(childNameInData)
					.findFirst();
			if (optionalFirst.isPresent()) {
				return optionalFirst.get().getValue();
			}
		}
		throw new DataMissingException(
				"Atomic value not found for childNameInData:" + childNameInData);
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInData(String childNameInData) {
		return getChildrenWithNameInData(childNameInData, isDataAtomic, DataAtomic.class);
	}

	private <T extends DataChild> Stream<T> getChildrenWithNameInData(String childNameInData,
			Predicate<DataChild> filter, Class<T> classType) {
		return getGenericChildrenStream(childNameInData, filter).map(classType::cast);
	}

	private Stream<DataChild> getGenericChildrenStream(String childNameInData,
			Predicate<DataChild> filter) {
		return childrenByNameInDataMap.get(childNameInData).stream().filter(filter);
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			return getAtomicChildrenWithNameInData(childNameInData).toList();
		}
		return Collections.emptyList();
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			Optional<DataGroup> findFirst = getFirstGroupWithNameInDataFromMap(childNameInData);
			if (findFirst.isPresent()) {
				return findFirst.get();
			}
		}
		throw new DataMissingException("Group not found for childNameInData:" + childNameInData);
	}

	private Optional<DataGroup> getFirstGroupWithNameInDataFromMap(String childNameInData) {
		return getGroupChildrenWithNameInDataStream(childNameInData).findFirst();
	}

	private Stream<DataGroup> getGroupChildrenWithNameInDataStream(String childNameInData) {
		return getChildrenWithNameInData(childNameInData, isDataGroup, DataGroup.class);
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			Optional<DataAtomic> findFirst = getAtomicChildrenWithNameInData(childNameInData)
					.findFirst();
			if (findFirst.isPresent()) {
				return findFirst.get();
			}
		}
		throw new DataMissingException(
				"DataAtomic not found for childNameInData:" + childNameInData);
	}

	@Override
	public DataChild getFirstChildWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			Optional<DataChild> optionalFirst = possiblyFindFirstChildWithNameInData(
					childNameInData);
			if (optionalFirst.isPresent()) {
				return optionalFirst.get();
			}
		}
		throw new DataMissingException("Element not found for childNameInData:" + childNameInData);
	}

	private Optional<DataChild> possiblyFindFirstChildWithNameInData(String childNameInData) {
		return childrenByNameInDataMap.get(childNameInData).stream().findFirst();
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			return getGroupChildrenWithNameInDataStream(childNameInData).toList();
		}
		return Collections.emptyList();
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
		if (containsChildWithNameInData(childNameInData)) {
			DataChild removeFirst = childrenByNameInDataMap.get(childNameInData).removeFirst();
			makeSureMapHasNoEmptyListForName(childNameInData);
			return children.remove(removeFirst);
		}
		return false;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			List<DataChild> removeChildren = childrenByNameInDataMap.remove(childNameInData);
			return children.removeAll(removeChildren);
		}
		return false;
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
		childrenByNameInDataMap.computeIfAbsent(dataElement.getNameInData(), _ -> new ArrayList<>())
				.add(dataElement);
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
		dataElements.stream().forEach(this::addChild);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String childNameInData) {
		if (containsChildWithNameInData(childNameInData)) {
			return childrenByNameInDataMap.get(childNameInData);
		}
		return Collections.emptyList();

	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {

		Predicate<? super DataChild> childNameInDataMatches = element -> attributesMatch(element,
				childAttributes);
		return removeMatchingChildren(childNameInData, childNameInDataMatches);

	}

	private boolean attributesMatch(DataChild element, DataAttribute... childAttributes) {
		return dataElementsHasAttributes(element, childAttributes);
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		Predicate<? super DataChild> childNameInDataMatches = element -> attributesMatch(element,
				childAttributes);
		return filterChildren(childNameInData, childNameInDataMatches);
	}

	@Override
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		if (containsChildWithNameInData(childNameInData)) {
			return getAtomicChildrenWithNameInDataAndAttributes(childNameInData, childAttributes)
					.toList();
		}
		return Collections.emptyList();
	}

	private Stream<DataAtomic> getAtomicChildrenWithNameInDataAndAttributes(String childNameInData,
			DataAttribute... childAttributes) {
		return getAtomicChildrenWithNameInData(childNameInData)
				.filter(filterByAttributes(childAttributes));
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		String childNameInData = childFilter.getNameInData();
		return filterChildren(childNameInData, childFilter::childMatches);
	}

	private List<DataChild> filterChildren(String childNameInData,
			Predicate<? super DataChild> predicate) {
		if (containsChildWithNameInData(childNameInData)) {
			return childrenByNameInDataMap.get(childNameInData).stream().filter(predicate).toList();
		}
		return Collections.emptyList();
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		return removeMatchingChildren(childFilter.getNameInData(), childFilter::childMatches);
	}

	private boolean removeMatchingChildren(String childNameInData,
			Predicate<? super DataChild> filter) {
		if (containsChildWithNameInData(childNameInData)) {
			childrenByNameInDataMap.get(childNameInData).removeIf(filter);
			makeSureMapHasNoEmptyListForName(childNameInData);
			return children.removeIf(filter);
		}
		return false;
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> type, String name) {
		if (childrenByNameInDataMap.containsKey(name)) {
			List<DataChild> childWithNameInData = childrenByNameInDataMap.get(name);
			return childWithNameInData.stream().anyMatch(type::isInstance);
		}
		return false;
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
		if (childrenByNameInDataMap.containsKey(name)) {
			return childrenByNameInDataMap.get(name).stream().map(type::cast).findFirst();
		}
		return Optional.empty();
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
		if (childrenByNameInDataMap.containsKey(name)) {
			return getChildrenOfTypeAndNameFromMap(type, name);
		}
		return Collections.emptyList();
	}

	private <T extends DataChild> List<T> getChildrenOfTypeAndNameFromMap(Class<T> type,
			String name) {
		return childrenByNameInDataMap.get(name).stream().filter(filterByType(type)).map(type::cast)
				.toList();
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> type,
			String name) {
		Optional<T> optionalFirst = getOptionalFirstChildOfTypeAndName(type, name);
		if (optionalFirst.isPresent()) {
			childrenByNameInDataMap.get(name).remove(optionalFirst.get());
			makeSureMapHasNoEmptyListForName(name);
			return children.remove(optionalFirst.get());
		}
		return false;
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> type, String name) {
		if (childrenByNameInDataMap.containsKey(name)) {
			List<T> childrenToRemove = getChildrenOfTypeAndNameFromMap(type, name);
			childrenByNameInDataMap.get(name).removeAll(childrenToRemove);
			makeSureMapHasNoEmptyListForName(name);
			return children.removeAll(childrenToRemove);
		}
		return false;
	}

	private void makeSureMapHasNoEmptyListForName(String name) {
		if (childrenByNameInDataMap.get(name).isEmpty()) {
			childrenByNameInDataMap.remove(name);
		}
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
