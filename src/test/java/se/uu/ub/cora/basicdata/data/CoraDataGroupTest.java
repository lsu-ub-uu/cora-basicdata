/*
 * Copyright 2015, 2019, 2022 Uppsala University Library
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataChildFilterSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class CoraDataGroupTest {

	private DataGroup defaultDataGroup;

	@BeforeMethod
	public void setUp() {
		defaultDataGroup = CoraDataGroup.withNameInData("someDataGroup");

	}

	@Test
	public void testInit() {
		assertEquals(defaultDataGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultDataGroup.getAttributes());
		assertNotNull(defaultDataGroup.getChildren());
	}

	@Test
	public void testGroupIsData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("nameInData");
		assertTrue(dataGroup instanceof Data);
	}

	@Test
	public void testInitWithRepeatId() {
		defaultDataGroup.setRepeatId("hrumph");
		assertEquals(defaultDataGroup.getNameInData(), "someDataGroup");
		assertNotNull(defaultDataGroup.getAttributes());
		assertNotNull(defaultDataGroup.getChildren());
		assertEquals(defaultDataGroup.getRepeatId(), "hrumph");
	}

	@Test
	public void testAddAttribute() {
		defaultDataGroup.addAttributeByIdWithValue("someAttributeName", "value");
		Collection<DataAttribute> attributes = defaultDataGroup.getAttributes();
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getNameInData(), "someAttributeName");
		assertEquals(next.getValue(), "value");
	}

	@Test
	public void testAddAttributeWithSameNameInDataOverwrites() {
		defaultDataGroup.addAttributeByIdWithValue("someAttributeName", "value");
		defaultDataGroup.addAttributeByIdWithValue("someAttributeName", "someOtherValue");

		Collection<DataAttribute> attributes = defaultDataGroup.getAttributes();
		assertEquals(attributes.size(), 1);
		DataAttribute next = attributes.iterator().next();
		assertEquals(next.getValue(), "someOtherValue");
	}

	@Test
	public void testHasAttributes() {
		assertFalse(defaultDataGroup.hasAttributes());
		defaultDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertTrue(defaultDataGroup.hasAttributes());
	}

	@Test
	public void testGetAttribute() {
		defaultDataGroup.addAttributeByIdWithValue("attributeId", "attributeValue");
		assertEquals(defaultDataGroup.getAttribute("attributeId").getValue(), "attributeValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Attribute with id someAttributeId not found.")
	public void testGetAttributeDoesNotExist() {
		defaultDataGroup.getAttribute("someAttributeId");
	}

	@Test
	public void testGetAttributeValueNoAttribute() throws Exception {
		Optional<String> attributeValue = defaultDataGroup.getAttributeValue("attributeNameInData");

		assertTrue(attributeValue.isEmpty());
	}

	@Test
	public void testGetAttributeValueAttributeExists() throws Exception {
		defaultDataGroup.addAttributeByIdWithValue("someAttributeName", "someValue");

		Optional<String> attributeValue = defaultDataGroup.getAttributeValue("someAttributeName");

		assertTrue(attributeValue.isPresent());
		assertEquals(attributeValue.get(), "someValue");
	}

	@Test
	public void testAddChild() {
		DataChild dataElement = CoraDataAtomic.withNameInDataAndValue("childNameInData",
				"childValue");
		defaultDataGroup.addChild(dataElement);
		List<DataChild> children = defaultDataGroup.getChildren();
		DataChild childElementOut = children.get(0);
		assertEquals(childElementOut.getNameInData(), "childNameInData");
	}

	@Test
	public void testHasChildren() throws Exception {
		assertFalse(defaultDataGroup.hasChildren());
		defaultDataGroup.addChild(CoraDataGroup.withNameInData("child"));
		assertTrue(defaultDataGroup.hasChildren());
	}

	@Test
	public void addChildrenEmptyList() {
		defaultDataGroup.addChildren(Collections.emptyList());
		assertTrue(defaultDataGroup.getChildren().isEmpty());
	}

	@Test
	public void testAddChildrenAddOneChildNoChildrenBefore() {
		List<DataChild> dataElements = createListWithOneChild();

		defaultDataGroup.addChildren(dataElements);

		List<DataChild> children = defaultDataGroup.getChildren();
		assertEquals(children.size(), 1);
		assertSame(children.get(0), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddOneChildOneChildBefore() {
		defaultDataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();

		defaultDataGroup.addChildren(dataElements);

		List<DataChild> children = defaultDataGroup.getChildren();
		assertEquals(children.size(), 2);
		assertSame(children.get(1), dataElements.get(0));
	}

	@Test
	public void testAddChildrenAddMultipleChildOneChildBefore() {
		defaultDataGroup.addChild(CoraDataAtomic.withNameInDataAndValue("someChild", "someValue"));
		List<DataChild> dataElements = createListWithOneChild();
		dataElements.add(CoraDataGroup.withNameInData("someGroupChild"));
		dataElements.add(CoraDataAtomic.withNameInDataAndValue("someOtherAtomicChild", "42"));

		defaultDataGroup.addChildren(dataElements);

		List<DataChild> children = defaultDataGroup.getChildren();
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
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("otherChildId", "otherChildValue"));
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultDataGroup.addChild(child);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testContainsChildWithIdNotFound() {
		DataChild child = CoraDataAtomic.withNameInDataAndValue("childId", "child value");
		defaultDataGroup.addChild(child);
		assertFalse(defaultDataGroup.containsChildWithNameInData("childId_NOT_FOUND"));
	}

	@Test
	public void testGetAtomicValue() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		assertEquals(defaultDataGroup.getFirstAtomicValueWithNameInData("atomicNameInData"),
				"atomicValue");
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Atomic value not found for childNameInData:" + "atomicNameInData_NOT_FOUND")
	public void testExtractAtomicValueNotFound() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		defaultDataGroup.getFirstAtomicValueWithNameInData("atomicNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsWithNameInData() {
		DataGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();

		assertEquals(book.getAllDataAtomicsWithNameInData("someChild").size(), 2);
	}

	@Test
	public void testGetAllDataAtomicsWithNameInDataNoResult() throws Exception {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someNameInData");
		List<DataAtomic> aList = dataGroup.getAllDataAtomicsWithNameInData("someNameInData");
		assertEquals(aList.size(), 0);
	}

	private DataGroup createDataGroupWithTwoAtomicChildrenAndOneGroupChild() {
		DataGroup book = CoraDataGroup.withNameInData("book");
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
		DataGroup book = createDataGroupWithTwoAtomicChildrenAndOneGroupChild();
		assertEquals(book.getFirstDataAtomicWithNameInData("someChild"), book.getChildren().get(0));
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "DataAtomic not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstDataAtomicWithNameInDataNotFound() {
		defaultDataGroup.addChild(
				CoraDataAtomic.withNameInDataAndValue("someChildNameInData", "atomicValue"));
		defaultDataGroup.getFirstDataAtomicWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllDataAtomicsdWithNameInDataAndAttributesOneMatch() {
		CoraDataGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne",
						CoraDataAttribute.withNameInDataAndValue("otherAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 1);
		DataChild expectedMatchingChild = dataGroup.getChildren().get(2);
		assertSame(atomicsFound.get(0), expectedMatchingChild);
	}

	private CoraDataGroup createDataGroupWithDataAtomicChildren() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");

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
		CoraDataGroup dataGroup = createDataGroupWithDataAtomicChildren();

		List<DataAtomic> atomicsFound = (List<DataAtomic>) dataGroup
				.getAllDataAtomicsWithNameInDataAndAttributes("childOne", CoraDataAttribute
						.withNameInDataAndValue("nonMatchingAttribute", "alternative"));

		assertEquals(atomicsFound.size(), 0);
	}

	@Test
	public void testGetGroup() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultDataGroup.addChild(dataGroup2);
		assertEquals(defaultDataGroup.getFirstGroupWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Group not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstGroupWithNameInDataNotFound() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultDataGroup.addChild(dataGroup2);
		defaultDataGroup.getFirstGroupWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetFirstChildWithNameInData() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultDataGroup.addChild(dataGroup2);
		assertEquals(defaultDataGroup.getFirstChildWithNameInData("childNameInData"), dataGroup2);
	}

	@Test(expectedExceptions = DataMissingException.class, expectedExceptionsMessageRegExp = ""
			+ "Element not found for childNameInData:childNameInData_NOT_FOUND")
	public void testGetFirstChildWithNameInDataNotFound() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		DataGroup dataGroup2 = CoraDataGroup.withNameInData("childNameInData");
		dataGroup2.addChild(CoraDataGroup.withNameInData("grandChildNameInData"));
		defaultDataGroup.addChild(dataGroup2);
		defaultDataGroup.getFirstChildWithNameInData("childNameInData_NOT_FOUND");
	}

	@Test
	public void testGetAllGroupsWithNameInData() {
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));
		addTwoGroupChildrenWithSameNameInData(defaultDataGroup);

		List<DataGroup> groupsFound = defaultDataGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 2);
	}

	private void addTwoGroupChildrenWithSameNameInData(DataGroup parentDataGroup) {
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
		defaultDataGroup
				.addChild(CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue"));

		List<DataGroup> groupsFound = defaultDataGroup
				.getAllGroupsWithNameInData("childNameInData");
		assertEquals(groupsFound.size(), 0);
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesOneMatch() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
			DataGroup dataGroup) {
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId3");
		addAndReturnDataGroupChildWithNameInData(dataGroup, "groupId2");
		DataGroup child3 = addAndReturnDataGroupChildWithNameInDataAndAttributes(dataGroup,
				"groupId2", CoraDataAttribute.withNameInDataAndValue("nameInData", "value1"));
		return child3;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInData(DataGroup dataGroup,
			String nameInData) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		return child;
	}

	private DataGroup addAndReturnDataGroupChildWithNameInDataAndAttributes(DataGroup dataGroup,
			String nameInData, CoraDataAttribute... attributes) {
		DataGroup child = CoraDataGroup.withNameInData(nameInData);
		dataGroup.addChild(child);
		for (CoraDataAttribute attribute : attributes) {
			child.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return child;
	}

	@Test
	public void testGetAllGroupsWithNameInDataAndAttributesTwoMatches() {
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildMoreThanOneChildExist() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertTrue(dataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildNotFound() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		boolean childWasRemoved = dataGroup.removeFirstChildWithNameInData("childId_NOTFOUND");
		assertFalse(childWasRemoved);
	}

	private DataChild createAndAddAnAtomicChildToDataGroup(DataGroup dataGroup) {
		return createAndAddAnAtomicChildToDataGroupUsingNameInData(dataGroup, "childId");
	}

	private DataChild createAndAddAnAtomicChildToDataGroupUsingNameInData(DataGroup dataGroup,
			String nameInData) {
		DataChild child = CoraDataAtomic.withNameInDataAndValue(nameInData, "child value");
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInData() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(dataGroup, "1");
		boolean childWasRemoved = dataGroup.removeAllChildrenWithNameInData("childId");
		assertTrue(childWasRemoved);
		assertFalse(dataGroup.containsChildWithNameInData("childId"));
	}

	private DataChild createAndAddAnAtomicChildWithRepeatIdToDataGroup(DataGroup dataGroup,
			String repeatId) {
		DataChild child = CoraDataAtomic.withNameInDataAndValueAndRepeatId("childId", "child value",
				repeatId);
		dataGroup.addChild(child);
		return child;
	}

	@Test
	public void testRemoveAllChildrenWithNameInDataWhenOtherChildrenExist() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
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
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAnAtomicChildToDataGroup(dataGroup);
		assertFalse(dataGroup.removeAllChildrenWithNameInData("childId_NOTFOUND"));
	}

	@Test
	public void testGetAllChildrenWithNameInDataNoChildren() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());

	}

	@Test
	public void testGetAllChildrenWithNameInDataNoMatchingChildren() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");
		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someOtherChildNameInData");
		assertTrue(allChildrenWithNameInData.isEmpty());

	}

	@Test
	public void testGetAllChildrenWithNameInDataOneMatchingAtomicChild() {
		DataGroup dataGroup = CoraDataGroup.withNameInData("someDataGroup");
		CoraDataAtomic atomicChild = createAndAddAtomicChild(dataGroup, "someChildNameInData", "0");

		List<DataChild> allChildrenWithNameInData = dataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
	}

	private CoraDataAtomic createAndAddAtomicChild(DataGroup dataGroup, String nameInData,
			String repeatId) {
		CoraDataAtomic atomicChild = CoraDataAtomic.withNameInDataAndValue(nameInData, "someValue");
		atomicChild.setRepeatId(repeatId);
		dataGroup.addChild(atomicChild);
		return atomicChild;
	}

	@Test
	public void testGetAllChildrenWithNameInDataMultipleMatchesDifferentTypes() {
		CoraDataAtomic atomicChild = createAndAddAtomicChild(defaultDataGroup,
				"someChildNameInData", "0");
		CoraDataAtomic atomicChild2 = createAndAddAtomicChild(defaultDataGroup,
				"someChildNameInData", "1");
		CoraDataAtomic atomicChild3 = createAndAddAtomicChild(defaultDataGroup,
				"someNOTChildNameInData", "2");

		DataGroup dataGroupChild = CoraDataGroup.withNameInData("someChildNameInData");
		defaultDataGroup.addChild(dataGroupChild);

		List<DataChild> allChildrenWithNameInData = defaultDataGroup
				.getAllChildrenWithNameInData("someChildNameInData");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertSame(allChildrenWithNameInData.get(0), atomicChild);
		assertSame(allChildrenWithNameInData.get(1), atomicChild2);
		assertSame(allChildrenWithNameInData.get(2), dataGroupChild);
		assertFalse(allChildrenWithNameInData.contains(atomicChild3));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWrongChildNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "0");
		boolean childWasRemoved = defaultDataGroup
				.removeAllChildrenWithNameInDataAndAttributes("NOTchildId");
		assertFalse(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "0");
		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesNoMatchWithWrongAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultDataGroup.addChild(childDataGroup);

		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someOtherValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchNoAttributes() {
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "0");
		boolean childWasRemoved = defaultDataGroup
				.removeAllChildrenWithNameInDataAndAttributes("childId");
		assertTrue(childWasRemoved);
	}

	@Test
	public void testRemoveChildrenWithAttributesOneMatchWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultDataGroup.addChild(childDataGroup);
		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertTrue(childWasRemoved);
		assertFalse(defaultDataGroup.containsChildWithNameInData("childId"));
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		assertEquals(defaultDataGroup.getAllChildrenWithNameInData("childId").size(), 2);

		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));

		assertTrue(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultDataGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);

	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenOneMatchWithoutAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		boolean childWasRemoved = defaultDataGroup
				.removeAllChildrenWithNameInDataAndAttributes("childId");
		assertTrue(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultDataGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 1);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
	}

	private void setUpDataGroupWithTwoChildrenOneWithAttributes() {
		DataGroup childDataGroup = CoraDataGroup.withNameInData("childId");
		childDataGroup.addAttributeByIdWithValue("someName", "someValue");
		defaultDataGroup.addChild(childDataGroup);
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "0");
	}

	@Test
	public void testRemoveChildrenWithAttributesTwoChildrenNoMatchWithAttributes() {
		setUpDataGroupWithTwoChildrenOneWithAttributes();

		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someNOTName", "someValue"));
		assertFalse(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultDataGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 2);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataGroup);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
	}

	@Test
	public void testRemoveChildrenWithAttributesMultipleChildrenTwoMatchesWithAttributes() {
		setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes();

		boolean childWasRemoved = defaultDataGroup.removeAllChildrenWithNameInDataAndAttributes(
				"childId", CoraDataAttribute.withNameInDataAndValue("someName", "someValue"));
		assertTrue(childWasRemoved);
		assertTrue(defaultDataGroup.containsChildWithNameInData("childId"));

		List<DataChild> allChildrenWithNameInData = defaultDataGroup
				.getAllChildrenWithNameInData("childId");
		assertEquals(allChildrenWithNameInData.size(), 3);
		assertTrue(allChildrenWithNameInData.get(0) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(1) instanceof CoraDataAtomic);
		assertTrue(allChildrenWithNameInData.get(2) instanceof CoraDataGroup);

		assertEquals(defaultDataGroup.getAllChildrenWithNameInData("childOtherId").size(), 1);
	}

	private void setUpDataGroupWithMultipleChildrenWithAttributesAndWithoutAttributes() {
		DataGroup childDataGroupWithAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		defaultDataGroup.addChild(childDataGroupWithAttribute);
		DataGroup childDataGroupWithAttribute2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "1");
		defaultDataGroup.addChild(childDataGroupWithAttribute2);

		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "0");
		createAndAddAnAtomicChildWithRepeatIdToDataGroup(defaultDataGroup, "1");

		DataGroup childDataGroupWithAtttributeOtherName = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childOtherId", "0");
		defaultDataGroup.addChild(childDataGroupWithAtttributeOtherName);

		DataGroup childDataGroupWithExtraAttribute = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"childId", "0");
		childDataGroupWithExtraAttribute.addAttributeByIdWithValue("someOtherName", "someValue");
		defaultDataGroup.addChild(childDataGroupWithExtraAttribute);
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
		defaultDataGroup.addChild(childGroup);

		List<DataChild> children = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesNoMatchNotMatchingNameInData() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultDataGroup.addChild(childGroup);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");
		List<DataChild> children = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someOtherChildNameInData", attribute);

		assertTrue(children.isEmpty());
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatch() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultDataGroup.addChild(childGroup);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> children = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(children.size(), 1);

		assertSame(children.get(0), childGroup);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesDataAtomicChild() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultDataGroup.addChild(childGroup);

		CoraDataAtomic coraDataAtomic = CoraDataAtomic.withNameInDataAndValue("someChildNameInData",
				"someValue");

		defaultDataGroup.addChild(coraDataAtomic);

		List<DataChild> children = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertEquals(children.size(), 1);
		assertSame(children.get(0), coraDataAtomic);

	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMatchRepeatingGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultDataGroup.addChild(childGroup);

		DataGroup childGroup2 = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "1");
		defaultDataGroup.addChild(childGroup2);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> children = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(children.size(), 2);

		assertSame(children.get(0), childGroup);
		assertSame(children.get(1), childGroup2);
	}

	@Test
	public void testGetAllChildrenWithNameInDataAndAttributesMultipleChildrenMatchOneGroup() {
		DataGroup childGroup = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someChildNameInData", "0");
		defaultDataGroup.addChild(childGroup);

		DataGroup childGroupOtherNameInData = createChildGroupWithNameInDataAndRepatIdAndAttributes(
				"someOtherChildNameInData", "1");
		defaultDataGroup.addChild(childGroupOtherNameInData);

		DataGroup childGroup2 = CoraDataGroup.withNameInData("someChildNameInData");
		defaultDataGroup.addChild(childGroup2);

		CoraDataAttribute attribute = CoraDataAttribute.withNameInDataAndValue("someName",
				"someValue");

		List<DataChild> childrenWithAttributes = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData", attribute);

		assertEquals(childrenWithAttributes.size(), 1);
		assertSame(childrenWithAttributes.get(0), childGroup);

		List<DataChild> childrenWithoutAttributes = defaultDataGroup
				.getAllChildrenWithNameInDataAndAttributes("someChildNameInData");

		assertEquals(childrenWithoutAttributes.size(), 1);
		assertSame(childrenWithoutAttributes.get(0), childGroup2);
	}

	@Test
	public void testGetAllChildrenMatchingFilter_noChildren() throws Exception {
		DataChildFilterSpy childFilter = new DataChildFilterSpy();

		List<DataChild> matchingChildren = defaultDataGroup
				.getAllChildrenMatchingFilter(childFilter);

		childFilter.MCR.assertMethodNotCalled("childMatches");
		assertEquals(matchingChildren.size(), 0);
	}

	@Test
	public void testGetAllChildrenMatchingFilter_oneChild() throws Exception {
		DataChildFilterSpy childFilter = new DataChildFilterSpy();
		DataAtomicSpy atomicChild = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild);

		List<DataChild> matchingChildren = defaultDataGroup
				.getAllChildrenMatchingFilter(childFilter);

		childFilter.MCR.assertParameters("childMatches", 0, atomicChild);
		assertEquals(matchingChildren.size(), 1);
		assertSame(matchingChildren.get(0), atomicChild);
	}

	@Test
	public void testGetAllChildrenMatchingFilter_twoChildMatchesOneDoNot() throws Exception {
		DataAtomicSpy atomicChild = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild);
		DataAtomicSpy atomicChild2 = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild2);
		DataGroupSpy groupChild = new DataGroupSpy();
		defaultDataGroup.addChild(groupChild);
		DataChildFilterSpy childFilter = new DataChildFilterSpy();
		childFilter.MRV.setSpecificReturnValuesSupplier("childMatches",
				(Supplier<Boolean>) () -> false, atomicChild);

		List<DataChild> matchingChildren = defaultDataGroup
				.getAllChildrenMatchingFilter(childFilter);

		childFilter.MCR.assertParameters("childMatches", 0, atomicChild);
		childFilter.MCR.assertParameters("childMatches", 1, atomicChild2);
		childFilter.MCR.assertParameters("childMatches", 2, groupChild);
		assertEquals(matchingChildren.size(), 2);
		assertSame(matchingChildren.get(0), atomicChild2);
		assertSame(matchingChildren.get(1), groupChild);
	}

	@Test
	public void testRemoveAllChildrenMatchingFilter_noChildren() throws Exception {
		DataChildFilterSpy childFilter = new DataChildFilterSpy();

		boolean childRemoved = defaultDataGroup.removeAllChildrenMatchingFilter(childFilter);

		childFilter.MCR.assertMethodNotCalled("childMatches");
		assertFalse(childRemoved);
	}

	@Test
	public void testRemoveAllChildrenMatchingFilter_oneChild() throws Exception {
		DataChildFilterSpy childFilter = new DataChildFilterSpy();
		DataAtomicSpy atomicChild = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild);

		boolean childRemoved = defaultDataGroup.removeAllChildrenMatchingFilter(childFilter);

		childFilter.MCR.assertParameters("childMatches", 0, atomicChild);
		assertTrue(childRemoved);
		assertEquals(defaultDataGroup.getChildren().size(), 0);
	}

	@Test
	public void testRemoveAllChildrenMatchingFilter_twoChildMatchesOneDoNot() throws Exception {
		DataAtomicSpy atomicChild = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild);
		DataAtomicSpy atomicChild2 = new DataAtomicSpy();
		defaultDataGroup.addChild(atomicChild2);
		DataGroupSpy groupChild = new DataGroupSpy();
		defaultDataGroup.addChild(groupChild);
		DataChildFilterSpy childFilter = new DataChildFilterSpy();
		childFilter.MRV.setSpecificReturnValuesSupplier("childMatches",
				(Supplier<Boolean>) () -> false, atomicChild);

		boolean childRemoved = defaultDataGroup.removeAllChildrenMatchingFilter(childFilter);

		assertTrue(childRemoved);
		childFilter.MCR.assertParameters("childMatches", 0, atomicChild);
		childFilter.MCR.assertParameters("childMatches", 1, atomicChild2);
		childFilter.MCR.assertParameters("childMatches", 2, groupChild);
		List<DataChild> childrenLeft = defaultDataGroup.getChildren();
		assertEquals(childrenLeft.size(), 1);
		assertSame(childrenLeft.get(0), atomicChild);
	}

	@Test
	public void testImplementMethods() throws Exception {
		assertTrue(false);
	}
}