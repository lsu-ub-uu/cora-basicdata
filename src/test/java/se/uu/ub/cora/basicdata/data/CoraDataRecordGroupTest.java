/*
 * Copyright 2015, 2019, 2025 Uppsala University Library
 * Copyright 2022, 2024 Olov McKie
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordGroupTest {
	private static final AttributeForFilter ATTRIBUTE_FILTER = new AttributeForFilter("someName",
			Set.of("someValue"));

	private static final String TIMESTAMP_FORMAT = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{6}Z";

	private static final String GROUP_NOT_FOUND_FOR_CHILD_NAME_IN_DATA_RECORD_INFO = ""
			+ "Group not found for childNameInData:recordInfo";
	private static final String RECORD_INFO = "recordInfo";
	private DataRecordGroup defaultRecordGroup;
	private DataRecordGroup defaultRecordGroupWithRecordInfo;
	private CoraDataGroup defaultRecordInfo;

	@BeforeMethod
	public void setUp() {
		resetDefaultRecordGroupOnlyGroup();

		defaultRecordGroupWithRecordInfo = CoraDataRecordGroup.withNameInData("someDataGroup");
		defaultRecordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroupWithRecordInfo.addChild(defaultRecordInfo);
	}

	private void resetDefaultRecordGroupOnlyGroup() {
		defaultRecordGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
	}

	private void resetDefaultRecordGroupWithRecordInfo() {
		resetDefaultRecordGroupOnlyGroup();
		defaultRecordInfo = CoraDataGroup.withNameInData(RECORD_INFO);
		defaultRecordGroup.addChild(defaultRecordInfo);
	}

	private void resetDefaultRecordGroupWithRecordInfoAndLink(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		resetDefaultRecordGroupWithRecordInfo();
		var link = CoraDataRecordLink.usingNameInDataAndTypeAndId(nameInData, linkedRecordType,
				linkedRecordId);
		defaultRecordInfo.addChild(link);
	}

	private void resetDefaultRecordGroupWithRecordInfoAndAtomic(String nameInData, String value) {
		resetDefaultRecordGroupWithRecordInfo();
		var atomic = CoraDataAtomic.withNameInDataAndValue(nameInData, value);
		defaultRecordInfo.addChild(atomic);
	}

	@Test
	public void testInit() {
		assertEquals(defaultRecordGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultRecordGroup.getAttributes());
		assertNotNull(defaultRecordGroup.getChildren());
	}

	@Test
	public void testInitWithRepeatId() {
		// TODO: lik till defaultRecordGroup
		assertEquals(defaultRecordGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultRecordGroup.getAttributes());
		assertNotNull(defaultRecordGroup.getChildren());
	}

	@Test
	public void testGroupIsData() {
		assertTrue(defaultRecordGroup instanceof Data);
	}

	@Test
	public void testAddAttribute() {
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "value");
		Collection<DataAttribute> attributes = defaultRecordGroup.getAttributes();
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getNameInData(), "someAttributeName");
		assertEquals(next.getValue(), "value");
	}

	@Test
	public void testAddAttributeWithSameNameInDataOverwrites() {
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "value");
		defaultRecordGroup.addAttributeByIdWithValue("someAttributeName", "someOtherValue");

		Collection<DataAttribute> attributes = defaultRecordGroup.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getValue(), "someOtherValue");
	}

	@Test
	public void testHasAttributes() {
		assertFalse(defaultRecordGroup.hasAttributes());
		defaultRecordGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertTrue(defaultRecordGroup.hasAttributes());
	}

	@Test
	public void testGetAttribute() {
		defaultRecordGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(defaultRecordGroup.getAttribute("attributeId").getValue(), "attributeValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Attribute with id someAttributeId not found.")
	public void testGetAttributeDoesNotExist() {
		defaultRecordGroup.getAttribute("someAttributeId");
	}

	@Test
	public void testAddChild() {
		DataChild dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		defaultRecordGroup.addChild(dataElement);
		List<DataChild> children = defaultRecordGroup.getChildren();
		DataChild childElementOut = children.get(0);
		assertEquals(childElementOut.getNameInData(), "childNameInData");
	}

	@Test
	public void testHasChildren() {
		assertFalse(defaultRecordGroup.hasChildren());
		defaultRecordGroup.addChild(CoraDataGroup.withNameInData("child"));
		assertTrue(defaultRecordGroup.hasChildren());
	}

	@Test
	public void addChildrenEmptyList() {
		defaultRecordGroup.addChildren(Collections.emptyList());
		assertTrue(defaultRecordGroup.getChildren().isEmpty());
	}

	@Test
	public void testAddChildrenAddOneChildNoChildrenBefore() {
		List<DataChild> dataElements = createListWithOneChild();

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 1);
		assertSame(children.get(0), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddOneChildOneChildBefore() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 2);
		assertSame(children.get(1), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddMultipleChildOneChildBefore() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();
		dataElements.add(CoraDataRecordGroup.withNameInData("someGroupChild"));
		dataElements.add(CoraDataAtomic.withNameInDataAndValue("someOtherAtomicChild", "42"));

		defaultRecordGroup.addChildren(dataElements);

		List<DataChild> children = defaultRecordGroup.getChildren();
		assertEquals(children.size(), 4);
		assertSame(children.get(1), dataElements.get(0));
		assertSame(children.get(2), dataElements.get(1));
		assertSame(children.get(3), dataElements.get(2));
	}

	private List<DataChild> createListWithOneChild() {
		DataChild dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		List<DataChild> dataElements = new ArrayList<>();
		dataElements.add(dataElement);
		return dataElements;
	}

	@Test
	public void testContainsChildWithId() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("otherChildId", "otherChildValue"));
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultRecordGroup.addChild(child);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultRecordGroup.addChild(child);
		assertFalse(defaultRecordGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testGetAtomicValue() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		assertEquals(defaultRecordGroup.getFirstAtomicValueWithNameInData("atomicNameInData"),
				"atomicValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Atomic value not found for childNameInData:" + "atomicNameInData_NOT_FOUND")
	public void testExtractAtomicValueNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		defaultRecordGroup.getFirstAtomicValueWithNameInData("atomicNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		DataRecordGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();

		assertEquals(book.getAllDataAtomicsWithNameInData("someChild").size(), 2);
	}

	@Test
	public void testGetAllDataAtomicsWithNameInDataNoResult() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someNameInData");
		List<DataAtomic> aList = dataGroup.getAllDataAtomicsWithNameInData("someNameInData");
		assertEquals(aList.size(), 0);
	}

	private DataRecordGroup createDataGroupWithTwoAtomicChildrenAndOneGroupChild() {
		DataRecordGroup book = CoraDataRecordGroup.withNameInData("book");
		CoraDataAtomic child1 = CoraDataAtomic.withNameInDataAndValue("someChild", "child1");
		child1.setRepeatId("0");
		book.addChild(child1);

		CoraDataAtomic child2 = CoraDataAtomic.withNameInDataAndValue("someChild", "child2");
		child2.setRepeatId("1");
		book.addChild(child2);

		DataGroup child3 = CoraDataGroup.withNameInData("someChild");
		book.addChild(child3);
		return book;
	}

	@Test
	public void testGetFirstDataAtomicWithNameInData() {
		DataRecordGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();
		assertEquals(book.getFirstDataAtomicWithNameInData("someChild"), book.getChildren().get(0));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "DataAtomic not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstDataAtomicWithNameInDataNotFound() {
		defaultRecordGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("someChildNameInData", "atomicValue"));
		defaultRecordGroup.getFirstDataAtomicWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsdWithNameInDataAndAttributesOneMatch() {
		DataRecordGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne",
						CoraDataAttribute.withNameInDataAndValue("otherAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 1);
		DataChild expectedMatchingChild = dataGroup.getChildren().get(2);
		assertSame(atomicsFound.get(0), expectedMatchingChild);
	}

	private DataRecordGroup createDataGroupWithDataAtomicChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");

		CoraDataAtomic childAtomic1 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "1");
		childAtomic1.addAttributeByIdWithValue("oneAttribute", "deafult");
		dataGroup.addChild(childAtomic1);

		CoraDataAtomic childAtomic2 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "2");
		dataGroup.addChild(childAtomic2);
		CoraDataAtomic childAtomic3 = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childOne",
				"value1", "3");
		childAtomic3.addAttributeByIdWithValue("otherAttribute", "alternative");
		dataGroup.addChild(childAtomic3);
		return dataGroup;
	}

	@Test
	public void testGetAllDataAtomicsdWithNameInDataAndAttributesNoMatch() {
		DataRecordGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne", CoraDataAttribute
						.withNameInDataAndValue("nonMatchingAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 0);
	}

	@Test
	public void testGetGroup() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		assertEquals(defaultRecordGroup.getFirstGroupWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstGroupWithNameInDataNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		defaultRecordGroup.getFirstGroupWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		assertEquals(defaultRecordGroup.getFirstChildWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstChildWithNameInDataNotFound() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultRecordGroup.addChild(dataGroup2);
		defaultRecordGroup.getFirstChildWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		addTwoGroupChildrenWithSameNameInData(defaultRecordGroup);

		List<DataGroup> groupsFound = defaultRecordGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 2);
	}

	private void addTwoGroupChildrenWithSameNameInData(DataRecordGroup parentDataGroup) {
		DataGroup dataGroup = CoraDataGroup.withNameInData("childNameInData");
		dataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("firstName", "someName"));
		dataGroup.setRepeatId("0");
		parentDataGroup.addChild(dataGroup);
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataAtomic.withNameInDataAndValue("firstName", "someOtherName"));
		dataGroup2.setRepeatId("1");
		parentDataGroup.addChild(dataGroup2);
	}

	@Test
	public void testGetAllGroupsWithNameInDataNoMatches() {
		defaultRecordGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		List<DataGroup> groupsFound = defaultRecordGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneMatch() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 1);
		assertGroupsFoundAre(groupsFound, child3);
	}

	private void assertGroupsFoundAre(Collection<DataGroup> groupsFound,
			DataGroup... assertedGroups) {
		int i = 0;
		for (DataGroup groupFound : groupsFound) {
			assertEquals(groupFound, assertedGroups[i]);
			i++;
		}
	}

	private DataGroup createTestGroupForAttributesReturnChildGroupWithAttribute(
			DataRecordGroup dataGroup) {
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId3");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		return addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
	}

	private DataGroup addAndReturnDataGroupChildWithNameInData(DataRecordGroup dataGroup,
			String nameInData) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		return child;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInDataAndAttributes(
			DataRecordGroup dataGroup, String nameInData, CoraDataAttribute... attributes) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		for (CoraDataAttribute attribute : attributes) {
			child.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return child;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesTwoMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeValueTwoMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneWrongAttributeNameTwoMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		DataGroup child3 = createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));

		assertEquals(groupsFound.size(), 2);
		assertGroupsFoundAre(groupsFound, child3, child4);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesNoMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value1"));

		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndTwoAttributesOneMatches() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createTestGroupForAttributesReturnChildGroupWithAttribute(dataGroup);
		DataGroup child4 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));
		addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup, "groupId2",
				CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData3", "value2"));

		Collection<DataGroup> groupsFound = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"),
				CoraDataAttribute.withNameInDataAndValue("nameInData2", "value2"));

		assertEquals(groupsFound.size(), 1);
		assertGroupsFoundAre(groupsFound, child4);
	}

	@Test
	public void testRemoveChild() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildMoreThanOneChildExist() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildNotFound() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
		assertFalse(childWasRemoved);
	}

	private DataChild createAndAddAnAtomicChildToDataGroup(DataRecordGroup dataGroup) {
		return createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "childId");
	}

	private DataChild createAndAddAnAtomicChildToDataGroupUsingNameInData(DataRecordGroup dataGroup,
			String nameInData) {
		DataChild child = CoraDataAtomic.withNameInDataAndValue(nameInData, "child value");
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		boolean childWasRemoved = dataGroup.removeAllChildrenWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	private DataChild createAndAddAnAtomicChildWithRepeatIdToDataGroup(DataRecordGroup dataGroup,
			String repeatId) {
		DataChild child = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childId", "child value",
				repeatId);
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWhenOtherChildrenExist() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "someOtherChildId");

		boolean childWasRemoved = dataGroup.removeAllChildrenWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
		assertTrue(dataGroup.containsChildWithNameInData("someOtherChildId"));
	}

	@Test
	public void testRemoveAllChildNotFound() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		assertFalse(dataGroup.removeAllChildrenWithNameInData("childId_NOTFOUND"));
	}

	@Test
	public void testGetAllChildrenWithNameInDataNoChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataNoMatchingChildren() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someOtherChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataOneMatchingAtomicChild() {
		DataRecordGroup dataGroup = CoraDataRecordGroup.withNameInData("someDataGroup");
		CoraDataAtomic atomicChild = createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");

		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
	}

	private CoraDataAtomic createAndAddAtomicChild(DataRecordGroup dataGroup, String nameInData,
			String repeatId) {
		CoraDataAtomic atomicChild = CoraDataAtomic.withNameInDataAndValue(nameInData, "someValue");
		atomicChild.setRepeatId(repeatId);
		dataGroup.addChild(atomicChild);
		return atomicChild;
	}

	@Test
	public void testGetAllChildrenWithNameInDataMultipleMatchesDifferentTypes() {
		CoraDataAtomic atomicChild = createAndAddAtomicChild(defaultRecordGroup,
				"someChildNameInData", "0");
		CoraDataAtomic atomicChild2 = createAndAddAtomicChild(defaultRecordGroup,
				"someChildNameInData", "1");
		CoraDataAtomic atomicChild3 = createAndAddAtomicChild(defaultRecordGroup,
				"someNOTChildNameInData", "2");

		DataGroup dataGroupChild = CoraDataGroup.withNameInData("someChildNameInData");
		defaultRecordGroup.addChild(dataGroupChild);

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
		assertSame(allChildrenWithNameInData.get(1), atomicChild2);
		assertSame(allChildrenWithNameInData.get(2), dataGroupChild);
		assertFalse(allChildrenWithNameInData.contains(atomicChild3));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWrongChildNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");

		DataChildFilter dataChildFilter = createDataChildFilter("NOTchildId");
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);

		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");

		DataChildFilter dataChildFilter = createDataChildFilter("childId", ATTRIBUTE_FILTER);
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);

		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWithWrongAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultRecordGroup.addChild(childDataGroup);

		DataChildFilter dataChildFilter = createDataChildFilter("childId",
				new AttributeForFilter("someName", Set.of("someOtherValue")));
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);
		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");

		DataChildFilter dataChildFilter = createDataChildFilter("childId");
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);
		assertTrue(childWasRemoved);
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");

		defaultRecordGroup.addChild(childDataGroup);

		DataChildFilter dataChildFilter = createDataChildFilter("childId", ATTRIBUTE_FILTER);
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);
		assertTrue(childWasRemoved);
		assertFalse(defaultRecordGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		assertEquals(defaultRecordGroup.getAllChildrenWithNameInData("childId").size(), 2);

		DataChildFilter dataChildFilter = createDataChildFilter("childId", ATTRIBUTE_FILTER);
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);

		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithoutAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		DataChildFilter dataChildFilter = createDataChildFilter("childId");
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);

		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
	}

	private void setUpDataGroupWithTwoChildrenOneWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultRecordGroup.addChild(childDataGroup);
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenNoMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		DataChildFilter dataChildFilter = createDataChildFilter("childId",
				new AttributeForFilter("someNOTName", Set.of("someValue")));
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);

		assertFalse(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 2);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
	}

	@Test
	public void testRemoveChildrenWithAttributesMultipleChildrenTwoMatchesWithAttributes() {
		setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes();

		DataChildFilter dataChildFilter = createDataChildFilter("childId", ATTRIBUTE_FILTER);
		boolean childWasRemoved = defaultRecordGroup
				.removeAllChildrenMatchingFilter(dataChildFilter);
		assertTrue(childWasRemoved);
		assertTrue(defaultRecordGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultRecordGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(2) instanceof CoraDataGroup);

		assertEquals(defaultRecordGroup.getAllChildrenWithNameInData("childOtherId").size(), 1);
	}

	private void setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes() {
		DataGroup childDataGroupWithAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		defaultRecordGroup.addChild(childDataGroupWithAttribute);
		DataGroup childDataGroupWithAttribute2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "1");
		defaultRecordGroup.addChild(childDataGroupWithAttribute2);

		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultRecordGroup, "1");

		DataGroup childDataGroupWithAtttributeOtherName = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childOtherId", "0");
		defaultRecordGroup.addChild(childDataGroupWithAtttributeOtherName);

		DataGroup childDataGroupWithExtraAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		childDataGroupWithExtraAttribute.addAttributeByIdWithValue("someOtherName", "someValue");
		defaultRecordGroup.addChild(childDataGroupWithExtraAttribute);
	}

	private DataGroup createChildGroupWithNameInDataAndRepatIdAndAttributes(String nameInData,
			String repeatId) {
		DataGroup childDataGroup = CoraDataGroup.withNameInData(nameInData);
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		childDataGroup.setRepeatId(repeatId);
		return childDataGroup;
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesNoMatch() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataChildFilter dataChildFilter = createDataChildFilter("someChildNameInData");
		List<DataChild> children = defaultRecordGroup.getAllChildrenMatchingFilter(dataChildFilter);

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesNoMatchNotMatchingNameInData() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataChildFilter dataChildFilter = createDataChildFilter("someOtherChildNameInData",
				ATTRIBUTE_FILTER);
		List<DataChild> children = defaultRecordGroup.getAllChildrenMatchingFilter(dataChildFilter);

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatch() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);
		DataChildFilter dataChildFilter = createDataChildFilter("someChildNameInData",
				ATTRIBUTE_FILTER);
		List<DataChild> children = defaultRecordGroup.getAllChildrenMatchingFilter(dataChildFilter);

		assertEquals(children.size(), 1);

		assertSame(children.get(0), childGroup);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesDataAtomicChild() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		CoraDataAtomic coraDataAtomic = CoraDataAtomic.withNameInDataAndValue("someChildNameInData",
				"someValue");

		defaultRecordGroup.addChild(coraDataAtomic);

		DataChildFilter dataChildFilter = createDataChildFilter("someChildNameInData");
		List<DataChild> children = defaultRecordGroup.getAllChildrenMatchingFilter(dataChildFilter);

		assertEquals(children.size(), 1);
		assertSame(children.get(0), coraDataAtomic);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatchRepeatingGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataGroup childGroup2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "1");
		defaultRecordGroup.addChild(childGroup2);

		DataChildFilter dataChildFilter = createDataChildFilter("someChildNameInData",
				ATTRIBUTE_FILTER);
		List<DataChild> children = defaultRecordGroup.getAllChildrenMatchingFilter(dataChildFilter);

		assertEquals(children.size(), 2);

		assertSame(children.get(0), childGroup);
		assertSame(children.get(1), childGroup2);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMultipleChildrenMatchOneGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultRecordGroup.addChild(childGroup);

		DataGroup childGroupOtherNameInData = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someOtherChildNameInData", "1");
		defaultRecordGroup.addChild(childGroupOtherNameInData);

		DataGroup childGroup2 = CoraDataGroup.withNameInData("someChildNameInData");
		defaultRecordGroup.addChild(childGroup2);

		DataChildFilter someChildNameInDataWithAttributeFilter = createDataChildFilter(
				"someChildNameInData", ATTRIBUTE_FILTER);

		List<DataChild> childrenWithAttributes = defaultRecordGroup
				.getAllChildrenMatchingFilter(someChildNameInDataWithAttributeFilter);

		assertEquals(childrenWithAttributes.size(), 1);
		assertSame(childrenWithAttributes.get(0), childGroup);

		DataChildFilter someChildNameInDataWithoutAttributeFilter = createDataChildFilter(
				"someChildNameInData");
		List<DataChild> childrenWithoutAttributes = defaultRecordGroup
				.getAllChildrenMatchingFilter(someChildNameInDataWithoutAttributeFilter);

		assertEquals(childrenWithoutAttributes.size(), 1);
		assertSame(childrenWithoutAttributes.get(0), childGroup2);
	}

	record AttributeForFilter(String nameInData, Set<String> values) {
	}

	private DataChildFilter createDataChildFilter(String nameInData,
			AttributeForFilter... attributes) {
		DataChildFilter dataChildFilter = se.uu.ub.cora.data.DataProvider
				.createDataChildFilterUsingChildNameInData(nameInData);
		for (AttributeForFilter attribute : attributes) {
			dataChildFilter.addAttributeUsingNameInDataAndPossibleValues(attribute.nameInData,
					attribute.values);
		}
		return dataChildFilter;
	}

	@DataProvider(name = "getLink")
	public Object[][] testCasesForGetLink() {
		GetLink type = new GetLink(() -> defaultRecordGroup.getType(), "type", "recordType");
		GetLink dataDivider = new GetLink(() -> defaultRecordGroup.getDataDivider(), "dataDivider",
				"system");
		GetLink validationType = new GetLink(() -> defaultRecordGroup.getValidationType(),
				"validationType", "validationType");
		GetLink createdBy = new GetLink(() -> defaultRecordGroup.getCreatedBy(), "createdBy",
				"user");
		return new GetLink[][] { { type }, { dataDivider }, { validationType }, { createdBy } };
	}

	record GetLink(Supplier<String> methodToRun, String nameInData, String linkedRecordType) {
	}

	@Test(dataProvider = "getLink")
	public void testGetLinkNoRecordInfo(GetLink testData) {
		resetDefaultRecordGroupOnlyGroup();

		String message = GROUP_NOT_FOUND_FOR_CHILD_NAME_IN_DATA_RECORD_INFO;
		runMethodAssertThrownErrorMessage(testData.methodToRun, message);
	}

	@Test(dataProvider = "getLink")
	public void testGetLinkRecordInfoNoLink(GetLink testData) {
		resetDefaultRecordGroupWithRecordInfo();

		String message = "Child of type: DataRecordLink and name: " + testData.nameInData
				+ " not found as child.";
		runMethodAssertThrownErrorMessage(testData.methodToRun, message);
	}

	@Test(dataProvider = "getLink")
	public void testGetLinkWithValue(GetLink testData) {
		resetDefaultRecordGroupWithRecordInfoAndLink(testData.nameInData, testData.linkedRecordType,
				"someValue");

		assertEquals(testData.methodToRun.get(), "someValue");
	}

	private void runMethodAssertThrownErrorMessage(Supplier<?> methodToRun, String message) {
		try {
			methodToRun.get();
			fail();
		} catch (Exception e) {
			assertThrownErrorMessage(e, message);
		}
	}

	private void assertThrownErrorMessage(Exception e, String message) {
		assertTrue(e instanceof DataMissingException);
		assertEquals(e.getMessage(), message);
	}

	@DataProvider(name = "setLink")
	public Object[][] testCasesForSetLink() {
		SetLink type = new SetLink(typeValue -> defaultRecordGroup.setType(typeValue), "type",
				"recordType");
		SetLink dataDivider = new SetLink(
				dataDividerValue -> defaultRecordGroup.setDataDivider(dataDividerValue),
				"dataDivider", "system");
		SetLink validationType = new SetLink(
				validationTypeValue -> defaultRecordGroup.setValidationType(validationTypeValue),
				"validationType", "validationType");
		SetLink createdBy = new SetLink(
				createdByValue -> defaultRecordGroup.setCreatedBy(createdByValue), "createdBy",
				"user");
		SetLink permissionUnit = new SetLink(
				permissionUnitValue -> defaultRecordGroup.setPermissionUnit(permissionUnitValue),
				"permissionUnit", "permissionUnit");
		return new SetLink[][] { { type }, { dataDivider }, { validationType }, { createdBy },
				{ permissionUnit } };
	}

	record SetLink(Consumer<String> methodToRun, String nameInData, String linkedRecordType) {
	}

	@Test(dataProvider = "setLink")
	public void testSetLinkNoRecordInfo(SetLink testData) {
		resetDefaultRecordGroupOnlyGroup();

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneLinkAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	@Test(dataProvider = "setLink")
	public void testSetLinkRecordInfoNoLink(SetLink testData) {
		resetDefaultRecordGroupWithRecordInfo();

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneLinkAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	@Test(dataProvider = "setLink")
	public void testSetLinkRecordInfoLinkOtherValue(SetLink testData) {
		resetDefaultRecordGroupWithRecordInfoAndLink(testData.nameInData, testData.linkedRecordType,
				"someOtherValue");

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneLinkAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	@Test
	public void testGetPermissionUnit_RecordInfoDoesNotExist() {
		resetDefaultRecordGroupOnlyGroup();

		Optional<String> permissionUnit = defaultRecordGroup.getPermissionUnit();

		assertTrue(permissionUnit.isEmpty());
	}

	@Test
	public void testGetPermissionUnit_UnitLinkDoesNotExist() {
		resetDefaultRecordGroupWithRecordInfo();

		Optional<String> permissionUnit = defaultRecordGroup.getPermissionUnit();

		assertTrue(permissionUnit.isEmpty());
	}

	@Test
	public void testGetPermissionUnit() {
		resetDefaultRecordGroupWithRecordInfo();
		defaultRecordInfo.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("permissionUnit",
				"permissionUnit", "somePermissionUnitId"));

		Optional<String> permissionUnit = defaultRecordGroup.getPermissionUnit();

		assertTrue(permissionUnit.isPresent());
		assertEquals(permissionUnit.get(), "somePermissionUnitId");
	}

	private void assertRecordInfoHasOnlyOneLinkAsSpecifiedInTestData(DataRecordGroup recordGroup,
			SetLink testData) {
		DataGroup recordInfo = recordGroup.getFirstGroupWithNameInData("recordInfo");

		String nameInData = testData.nameInData;
		List<DataChild> allLinkChildren = recordInfo.getAllChildrenWithNameInData(nameInData);
		assertEquals(allLinkChildren.size(), 1);

		DataRecordLink link = recordInfo.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		String linkedRecordType = testData.linkedRecordType;
		assertEquals(link.getLinkedRecordType(), linkedRecordType);
		assertEquals(link.getLinkedRecordId(), "someValue");
	}

	@DataProvider(name = "getAtomic")
	public Object[][] testCasesForGetAtomic() {
		GetAtomic id = new GetAtomic(() -> defaultRecordGroup.getId(), "id");
		GetAtomic tsCreated = new GetAtomic(() -> defaultRecordGroup.getTsCreated(), "tsCreated");

		return new GetAtomic[][] { { id }, { tsCreated } };
	}

	record GetAtomic(Supplier<String> methodToRun, String nameInData) {
	}

	@Test(dataProvider = "getAtomic")
	public void testGetAtomicNoRecordInfo(GetAtomic testData) {
		resetDefaultRecordGroupOnlyGroup();
		String message = GROUP_NOT_FOUND_FOR_CHILD_NAME_IN_DATA_RECORD_INFO;

		runMethodAssertThrownErrorMessage(testData.methodToRun, message);
	}

	@Test(dataProvider = "getAtomic")
	public void testGetAtomicRecordInfoNoLink(GetAtomic testData) {
		resetDefaultRecordGroupWithRecordInfo();
		String message = "Atomic value not found for childNameInData:" + testData.nameInData;

		runMethodAssertThrownErrorMessage(testData.methodToRun, message);
	}

	@Test(dataProvider = "getAtomic")
	public void testGetLinkAtomicValue(GetAtomic testData) {
		resetDefaultRecordGroupWithRecordInfoAndAtomic(testData.nameInData, "someValue");

		assertEquals(testData.methodToRun.get(), "someValue");
	}

	@Test
	public void testSetId() {
		defaultRecordInfo
				.addChild(CoraDataRecordLink.usingNameInDataAndTypeAndId("id", "", "someIdId"));

		defaultRecordGroupWithRecordInfo.setId("someOtherId");

		assertEquals(defaultRecordGroupWithRecordInfo.getId(), "someOtherId");
	}

	@Test
	public void testSetId_NoId() {
		defaultRecordGroupWithRecordInfo.setId("someOtherId");

		assertEquals(defaultRecordGroupWithRecordInfo.getId(), "someOtherId");
		DataAtomic id = (DataAtomic) defaultRecordInfo.getFirstChildWithNameInData("id");
		assertEquals(id.getValue(), "someOtherId");
	}

	@Test
	public void testSetId_NoRecordInfo() {
		defaultRecordGroup.setId("someOtherId");

		assertEquals(defaultRecordGroup.getId(), "someOtherId");
	}

	@DataProvider(name = "setAtomic")
	public Object[][] testCasesForSetAtomic() {
		SetAtomic id = new SetAtomic(idValue -> defaultRecordGroup.setId(idValue), "id");
		SetAtomic tsCreated = new SetAtomic(
				tsCreatedValue -> defaultRecordGroup.setTsCreated(tsCreatedValue), "tsCreated");
		return new SetAtomic[][] { { id }, { tsCreated } };
	}

	record SetAtomic(Consumer<String> methodToRun, String nameInData) {
	}

	@Test(dataProvider = "setAtomic")
	public void testSetAtomicNoRecordInfo(SetAtomic testData) {
		resetDefaultRecordGroupOnlyGroup();

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneAtomicAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	@Test(dataProvider = "setAtomic")
	public void testSetAtomicRecordInfoNoLink(SetAtomic testData) {
		resetDefaultRecordGroupWithRecordInfo();

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneAtomicAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	@Test(dataProvider = "setAtomic")
	public void testSetAtomicRecordInfoLinkOtherValue(SetAtomic testData) {
		resetDefaultRecordGroupWithRecordInfoAndAtomic(testData.nameInData, "someOtherValue");

		testData.methodToRun.accept("someValue");

		assertRecordInfoHasOnlyOneAtomicAsSpecifiedInTestData(defaultRecordGroup, testData);
	}

	private void assertRecordInfoHasOnlyOneAtomicAsSpecifiedInTestData(DataRecordGroup recordGroup,
			SetAtomic testData) {
		DataGroup recordInfo = recordGroup.getFirstGroupWithNameInData("recordInfo");

		String nameInData = testData.nameInData;
		List<DataChild> allLinkChildren = recordInfo.getAllChildrenWithNameInData(nameInData);
		assertEquals(allLinkChildren.size(), 1);

		DataAtomic atomic = recordInfo.getFirstChildOfTypeAndName(DataAtomic.class, nameInData);
		assertEquals(atomic.getValue(), "someValue");
	}

	@Test
	public void testSetTsCreatedToNow() {
		defaultRecordGroup.setTsCreatedToNow();

		String tsCreated = defaultRecordGroup.getTsCreated();
		assertTsTimestampIsInIsoFormatAndCreatedWithinASecond(tsCreated);
	}

	private void assertTsTimestampIsInIsoFormatAndCreatedWithinASecond(String ts) {
		assertTrue(ts.matches(TIMESTAMP_FORMAT));

		int oneSecondInNano = 1000000000;
		Instant parsed = Instant.parse(ts);
		assertTrue(Instant.now().getNano() - parsed.getNano() < oneSecondInNano);
	}

	@Test
	public void testGetLatestUpdatedBy() {
		addUpdatedToDefaultRecordInfo("someOtherUserId", "someOtherTime", "10");
		addUpdatedToDefaultRecordInfo("someUserId", "someTime", "1");

		String userId = defaultRecordGroupWithRecordInfo.getLatestUpdatedBy();

		assertEquals(userId, "someUserId");
	}

	@Test
	public void testGetLatestTsUpdated() {
		addUpdatedToDefaultRecordInfo("someOtherUserId", "someOtherTime", "10");
		addUpdatedToDefaultRecordInfo("someUserId", "someTime", "1");

		String userId = defaultRecordGroupWithRecordInfo.getLatestTsUpdated();

		assertEquals(userId, "someTime");
	}

	private void addUpdatedToDefaultRecordInfo(String userId, String updatedTs, String repeatId) {
		DataGroup updated = CoraDataGroup.withNameInData("updated");
		updated.setRepeatId(repeatId);
		defaultRecordInfo.addChild(updated);
		DataAtomic updatedBy = CoraDataAtomic.withNameInDataAndValue("updatedBy", userId);
		updated.addChild(updatedBy);
		DataAtomic tsUpdated = CoraDataAtomic.withNameInDataAndValue("tsUpdated", updatedTs);
		updated.addChild(tsUpdated);
	}

	@Test
	public void testAddUpdated() {
		defaultRecordGroup.addUpdatedUsingUserIdAndTs("someUserId", "someTsUpdated");

		assertRecordInfoHasLastUpdated(defaultRecordGroup, "someTsUpdated", "someUserId", "0", 1);
	}

	private void assertRecordInfoHasLastUpdated(DataRecordGroup recordGroup, String tsUpdated,
			String updatedBy, String repeatId, int numberOfUpdated) {
		DataGroup lastUpdated = getLastUpdated(recordGroup, numberOfUpdated);

		assertEquals(lastUpdated.getRepeatId(), repeatId);
		assertEquals(lastUpdated.getFirstAtomicValueWithNameInData("tsUpdated"), tsUpdated);

		DataRecordLink lastUpdatedBy = lastUpdated.getFirstChildOfTypeAndName(DataRecordLink.class,
				"updatedBy");
		assertEquals(lastUpdatedBy.getLinkedRecordType(), "user");
		assertEquals(lastUpdatedBy.getLinkedRecordId(), updatedBy);
	}

	private DataGroup getLastUpdated(DataRecordGroup recordGroup, int numberOfUpdated) {
		DataGroup recordInfo = recordGroup.getFirstGroupWithNameInData("recordInfo");
		List<DataChild> updateds = recordInfo.getAllChildrenWithNameInData("updated");

		assertEquals(updateds.size(), numberOfUpdated);

		return (DataGroup) updateds.get(updateds.size() - 1);
	}

	@Test
	public void testAddUpdatedTwoExistsSinceBefore() {
		addUpdatedToDefaultRecordInfo("someOtherUserId", "someOtherTime", "10");
		addUpdatedToDefaultRecordInfo("someUserId", "someTime", "strange1");

		defaultRecordGroupWithRecordInfo.addUpdatedUsingUserIdAndTs("someUserId", "someTsUpdated");

		assertRecordInfoHasLastUpdated(defaultRecordGroupWithRecordInfo, "someTsUpdated",
				"someUserId", "11", 3);
	}

	@Test
	public void testAddUpdatedNow() {
		defaultRecordGroup.addUpdatedUsingUserIdAndTsNow("someUserId");

		String latestTsUpdated = defaultRecordGroup.getLatestTsUpdated();
		assertRecordInfoHasLastUpdated(defaultRecordGroup, latestTsUpdated, "someUserId", "0", 1);
		assertTsTimestampIsInIsoFormatAndCreatedWithinASecond(latestTsUpdated);
	}

	@Test
	public void testAddUpdatedNowTwoExistsSinceBefore() {
		addUpdatedToDefaultRecordInfo("someOtherUserId", "someOtherTime", "10");
		addUpdatedToDefaultRecordInfo("someUserId", "someTime", "strange1");

		defaultRecordGroupWithRecordInfo.addUpdatedUsingUserIdAndTsNow("someUserId");

		String latestTsUpdated = defaultRecordGroupWithRecordInfo.getLatestTsUpdated();
		assertRecordInfoHasLastUpdated(defaultRecordGroupWithRecordInfo, latestTsUpdated,
				"someUserId", "11", 3);
		assertTsTimestampIsInIsoFormatAndCreatedWithinASecond(latestTsUpdated);
	}

	@Test
	public void testGetAllUpdatedNoRecordInfo() {
		String message = GROUP_NOT_FOUND_FOR_CHILD_NAME_IN_DATA_RECORD_INFO;
		runMethodAssertThrownErrorMessage(() -> defaultRecordGroup.getAllUpdated(), message);
	}

	@Test
	public void testGetAllUpdatedRecordInfoNoUpdated() {
		String message = "Child of type: DataGroup and name: updated" + " not found as child.";
		runMethodAssertThrownErrorMessage(() -> defaultRecordGroupWithRecordInfo.getAllUpdated(),
				message);
	}

	@Test
	public void testGetAllUpdatedRecordInfoTwoUpdated() {
		DataGroup updated0 = new CoraDataGroup("updated");
		DataGroup updated1 = new CoraDataGroup("updated");
		defaultRecordInfo.addChild(updated0);
		defaultRecordInfo.addChild(updated1);

		List<DataChild> allUpdated = (List<DataChild>) defaultRecordGroupWithRecordInfo
				.getAllUpdated();

		assertSame(allUpdated.get(0), updated0);
		assertSame(allUpdated.get(1), updated1);
	}

	@Test
	public void testSetUpdatedNoExistsSinceBeforeEmptyListAdded() {
		List<DataChild> list = Collections.emptyList();

		defaultRecordGroup.setAllUpdated(list);

		assertFalse(defaultRecordGroup.containsChildWithNameInData(RECORD_INFO));
	}

	@Test
	public void testSetUpdatedExistsSinceBeforeRemovedOnEmptyList() {
		DataGroup updated0 = new CoraDataGroup("updated");
		DataGroup updated1 = new CoraDataGroup("updated");
		defaultRecordInfo.addChild(updated0);
		defaultRecordInfo.addChild(updated1);

		List<DataChild> list = Collections.emptyList();

		defaultRecordGroupWithRecordInfo.setAllUpdated(list);

		assertFalse(defaultRecordInfo.containsChildWithNameInData("updated"));
	}

	@Test
	public void testSetUpdatedNoExistsSinceBeforeRemovedOnTwoUpdatedNoRecordInfo() {
		DataGroup updated0 = new CoraDataGroup("updated");
		DataGroup updated1 = new CoraDataGroup("updated");

		List<DataChild> list = List.of(updated0, updated1);

		defaultRecordGroup.setAllUpdated(list);

		DataGroup createdRecordInfo = defaultRecordGroup.getFirstGroupWithNameInData("recordInfo");
		List<DataChild> updatedFromRecordInfo = createdRecordInfo
				.getAllChildrenWithNameInData("updated");
		assertSame(updatedFromRecordInfo.get(0), list.get(0));
		assertSame(updatedFromRecordInfo.get(1), list.get(1));
	}

	@Test
	public void testSetUpdatedNoExistsSinceBeforeRemovedOnTwoUpdatedList() {
		DataGroup updated0 = new CoraDataGroup("updated");
		DataGroup updated1 = new CoraDataGroup("updated");
		DataGroup updatedNew0 = new CoraDataGroup("updated");
		DataGroup updatedNew1 = new CoraDataGroup("updated");
		defaultRecordInfo.addChild(updated0);
		defaultRecordInfo.addChild(updated1);

		List<DataChild> list = List.of(updatedNew0, updatedNew1);

		defaultRecordGroupWithRecordInfo.setAllUpdated(list);

		List<DataChild> updatedFromRecordInfo = defaultRecordInfo
				.getAllChildrenWithNameInData("updated");
		assertSame(updatedFromRecordInfo.get(0), list.get(0));
		assertSame(updatedFromRecordInfo.get(1), list.get(1));
		assertEquals(updatedFromRecordInfo.size(), 2);
	}

	@Test
	public void testGetOverwriteProtectionNoRecordInfo() {
		assertTrue(defaultRecordGroup.overwriteProtectionShouldBeEnforced());
	}

	@Test
	public void testGetOverwriteProtectionRecordInfoNoOverwrite() {
		assertTrue(defaultRecordGroupWithRecordInfo.overwriteProtectionShouldBeEnforced());
	}

	@Test
	public void testGetOverwriteProtectionRecordInfoIgnorOverwriteFalse() {
		resetDefaultRecordGroupWithRecordInfoAndAtomic("ignoreOverwriteProtection", "false");

		assertTrue(defaultRecordGroup.overwriteProtectionShouldBeEnforced());
	}

	@Test
	public void testGetOverwriteProtectionRecordInfoIgnorOverwriteTrue() {
		resetDefaultRecordGroupWithRecordInfoAndAtomic("ignoreOverwriteProtection", "true");

		assertFalse(defaultRecordGroup.overwriteProtectionShouldBeEnforced());
	}

	@Test
	public void testRemoveOverwriteProtectionNoRecordInfo() {
		defaultRecordGroup.removeOverwriteProtection();

		assertTrue(defaultRecordGroup.overwriteProtectionShouldBeEnforced());
	}

	@Test
	public void testRemoveOverwriteProtectionRecordInfoIgnorOverwriteTrue() {
		resetDefaultRecordGroupWithRecordInfoAndAtomic("ignoreOverwriteProtection", "true");

		defaultRecordGroup.removeOverwriteProtection();

		assertEquals(defaultRecordInfo.containsChildWithNameInData("ignoreOverwriteProtection"),
				false);
	}

	@Test
	public void testGetTsVisibility() {
		var tsVisibilityAtomic = CoraDataAtomic.withNameInDataAndValue("tsVisibility",
				"someTimeStamp");
		defaultRecordInfo.addChild(tsVisibilityAtomic);

		Optional<String> tsVisibility = defaultRecordGroupWithRecordInfo.getTsVisibility();
		assertTrue(tsVisibility.isPresent());
		assertEquals(tsVisibility.get(), "someTimeStamp");
	}

	@Test
	public void testSetTsVisibilityNow() {
		defaultRecordGroup.setTsVisibilityNow();

		Optional<String> tsVisibility = defaultRecordGroup.getTsVisibility();
		assertTsTimestampIsInIsoFormatAndCreatedWithinASecond(tsVisibility.get());
	}

	@Test
	public void testGetTsVisibilityDoesNotExist() {
		Optional<String> tsVisibility = defaultRecordGroupWithRecordInfo.getTsVisibility();
		assertFalse(tsVisibility.isPresent());
	}

	@Test
	public void testGetVisibility() {
		defaultRecordGroupWithRecordInfo.setVisibility("published");

		Optional<String> visibility = defaultRecordGroupWithRecordInfo.getVisibility();
		assertTrue(visibility.isPresent());
		assertEquals(visibility.get(), "published");
	}

	@Test
	public void testGetVisibilityDoesNotExist() {
		Optional<String> tsVisibility = defaultRecordGroupWithRecordInfo.getVisibility();
		assertTrue(tsVisibility.isEmpty());
	}
}