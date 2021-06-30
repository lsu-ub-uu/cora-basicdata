/*
 * Copyright 2015, 2019, 2021 Uppsala University Library
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

package se.uu.ub.cora.basicdata.converter.datatojson;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataAttribute;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataList;
import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.basicdata.mcr.MethodCallRecorder;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;

public class BasicDataToJsonConverterFactoryTest {
	private DataToJsonConverterFactory converterFactory;
	private JsonBuilderFactorySpy builderFactory;
	private CoraDataGroup coraDataGroup;
	private CoraDataAtomic dataAtomic;
	private CoraDataAttribute dataAttribute;
	private CoraDataRecordLink dataRecordLink;
	private CoraDataResourceLink dataResourceLink;
	private String recordUrl;
	private String baseUrl;

	@BeforeMethod
	public void beforeMethod() {
		createConvertibles();
		builderFactory = new JsonBuilderFactorySpy();
		converterFactory = BasicDataToJsonConverterFactory.usingBuilderFactory(builderFactory);
		baseUrl = "some/url/";
		recordUrl = "some/url/type/id";
	}

	private void createConvertibles() {
		coraDataGroup = CoraDataGroup.withNameInData("groupNameInData");
		dataAtomic = CoraDataAtomic.withNameInDataAndValue("atomicNameInData", "atomicValue");
		dataAttribute = CoraDataAttribute.withNameInDataAndValue("attributeNameInData",
				"attributeValue");
		dataRecordLink = CoraDataRecordLink.withNameInData("recordLinkNameInData");
		dataResourceLink = CoraDataResourceLink.withNameInData("recordLinkNameInData");
	}

	@Test
	public void testDataList() throws Exception {
		CoraDataList coraDataList = CoraDataList.withContainDataOfType("someType");
		DataListToJsonConverter dataToJsonConverter = (DataListToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataList);
		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.dataList, coraDataList);

	}

	@Test
	public void testRecordNoUrl() throws Exception {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataRecord);
		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertTrue(dataToJsonConverter.actionsConverter instanceof RecordActionsToJsonConverterImp);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.baseUrl, null);
		assertSame(dataToJsonConverter.dataRecord, coraDataRecord);
	}

	@Test
	public void testDependenciesOfActionConverterNoUrl() throws Exception {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataRecord);
		RecordActionsToJsonConverterImp actionsConverter = (RecordActionsToJsonConverterImp) dataToJsonConverter.actionsConverter;
		assertSame(actionsConverter.converterFactory, converterFactory);
		assertSame(actionsConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.baseUrl, null);
	}

	@Test
	public void testRecordWithUrl() throws Exception {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingBaseUrlAndConvertible(baseUrl, coraDataRecord);
		assertSame(dataToJsonConverter.converterFactory, converterFactory);
		assertTrue(dataToJsonConverter.actionsConverter instanceof RecordActionsToJsonConverterImp);
		assertSame(dataToJsonConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.baseUrl, baseUrl);
		assertSame(dataToJsonConverter.dataRecord, coraDataRecord);
	}

	@Test
	public void testDependenciesOfActionConverterWithUrl() throws Exception {
		CoraDataRecord coraDataRecord = CoraDataRecord.withDataGroup(null);
		DataRecordToJsonConverter dataToJsonConverter = (DataRecordToJsonConverter) converterFactory
				.factorUsingBaseUrlAndConvertible(baseUrl, coraDataRecord);
		RecordActionsToJsonConverterImp actionsConverter = (RecordActionsToJsonConverterImp) dataToJsonConverter.actionsConverter;
		assertSame(actionsConverter.converterFactory, converterFactory);
		assertSame(actionsConverter.builderFactory, builderFactory);
		assertSame(dataToJsonConverter.baseUrl, baseUrl);
	}

	@Test
	public void testDataGroupNoUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);

	}

	@Test
	public void testDataAtomicNoUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) converterFactory
				.factorUsingConvertible(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAttributeNoUrl() {
		DataAttributeToJsonConverter dataToJsonConverter = (DataAttributeToJsonConverter) converterFactory
				.factorUsingConvertible(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testRecordLinkNoUrl() throws Exception {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertFalse(dataToJsonConverter instanceof DataRecordLinkToJsonConverter);
	}

	@Test
	public void testDataResourceLinkNoUrl() throws Exception {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertFalse(dataToJsonConverter instanceof DataResourceLinkToJsonConverter);
	}

	// TODO: Implement Converter for DataList add test
	// TODO: Implement Converter for DataRecord add test

	@Test
	public void testDataGroupWithUrl() {
		DataGroupToJsonConverter dataToJsonConverter = (DataGroupToJsonConverter) converterFactory
				.factorUsingConvertible(coraDataGroup);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAtomicWithUrl() {
		DataAtomicToJsonConverter dataToJsonConverter = (DataAtomicToJsonConverter) converterFactory
				.factorUsingConvertible(dataAtomic);

		JsonBuilderFactory jsonBuilderFactory = dataToJsonConverter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testDataAttributeWithUrl() {
		DataAttributeToJsonConverter converter = (DataAttributeToJsonConverter) converterFactory
				.factorUsingConvertible(dataAttribute);

		JsonBuilderFactory jsonBuilderFactory = converter.factory;
		assertSame(jsonBuilderFactory, builderFactory);
	}

	@Test
	public void testRecordLinkWithUrl() throws Exception {
		DataRecordLinkToJsonConverter converter = (DataRecordLinkToJsonConverter) converterFactory
				.factorUsingBaseUrlAndRecordUrlAndConvertible(baseUrl, recordUrl, dataRecordLink);

		JsonBuilderFactory jsonBuilderFactory = converter.jsonBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.converterFactory, converterFactory);
		assertEquals(converter.baseURL, baseUrl);
	}

	@Test
	public void testRectorDownToRecordLink() throws Exception {

		converterFactory.factorUsingBaseUrlAndRecordUrlAndConvertible(baseUrl, recordUrl,
				dataRecordLink);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataRecordLink);

		assertTrue(converter instanceof DataRecordLinkToJsonConverter);
	}

	@Test
	public void testDataResourceLinkWithUrl() throws Exception {
		DataResourceLinkToJsonConverter converter = (DataResourceLinkToJsonConverter) converterFactory
				.factorUsingBaseUrlAndRecordUrlAndConvertible(baseUrl, recordUrl, dataResourceLink);

		JsonBuilderFactory jsonBuilderFactory = converter.resourceLinkBuilderFactory;
		assertSame(jsonBuilderFactory, builderFactory);
		assertSame(converter.converterFactory, converterFactory);
		assertEquals(converter.recordURL, recordUrl);
	}

	@Test
	public void testCallFactorUsingBaseUrlAndConvertible() throws Exception {

	}

	@Test
	public void testRectorDownToRecordLink2() throws Exception {

		converterFactory.factorUsingBaseUrlAndConvertible(baseUrl, dataRecordLink);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataRecordLink);

		assertTrue(converter instanceof DataGroupToJsonConverter);
	}

	@Test
	public void testGenerateLinksForResourceWithoutRecordUrlSetShouldReturnDataGroup()
			throws Exception {

		converterFactory.factorUsingBaseUrlAndConvertible(baseUrl, dataResourceLink);
		DataToJsonConverter converter = converterFactory.factorUsingConvertible(dataResourceLink);

		assertTrue(converter instanceof DataGroupToJsonConverter);
		assertFalse(converter instanceof DataResourceLinkToJsonConverter);
	}

	@Test
	public void testFactorUsingBaseUrlAndConvertibleUsesFactorUsingConvertible() throws Exception {
		BasicDataToJsonConverterFactoryForTest forTest = new BasicDataToJsonConverterFactoryForTest();

		DataToJsonConverter converter = forTest.factorUsingBaseUrlAndConvertible(baseUrl,
				dataRecordLink);

		assertEquals(forTest.baseUrl, baseUrl);
		forTest.MCR.assertParameters("factorUsingConvertible", 0, dataRecordLink);
		forTest.MCR.assertReturn("factorUsingConvertible", 0, converter);

	}

	@Test
	public void testFactorUsingRecordUrlAndConvertibleUsesFactorUsingConvertible()
			throws Exception {
		BasicDataToJsonConverterFactoryForTest forTest = new BasicDataToJsonConverterFactoryForTest();
		DataToJsonConverter converter = forTest
				.factorUsingBaseUrlAndRecordUrlAndConvertible(baseUrl, recordUrl, dataRecordLink);

		assertEquals(forTest.baseUrl, baseUrl);
		assertEquals(forTest.recordUrl, recordUrl);
		forTest.MCR.assertParameters("factorUsingConvertible", 0, dataRecordLink);
		forTest.MCR.assertReturn("factorUsingConvertible", 0, converter);
	}

	class BasicDataToJsonConverterFactoryForTest extends BasicDataToJsonConverterFactory {
		BasicDataToJsonConverterFactoryForTest() {
			super(null);
		}

		MethodCallRecorder MCR = new MethodCallRecorder();

		@Override
		public DataToJsonConverter factorUsingConvertible(Convertible convertible) {
			MCR.addCall("convertible", convertible);
			DataToJsonConverter converter = new DataToJsonConverterSpy();
			MCR.addReturned(converter);
			return converter;
		}
	}
}
