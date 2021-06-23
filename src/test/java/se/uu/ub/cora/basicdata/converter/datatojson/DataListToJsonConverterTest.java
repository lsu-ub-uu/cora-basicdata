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

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.basicdata.data.CoraDataList;
import se.uu.ub.cora.basicdata.mcr.MethodCallRecorder;
import se.uu.ub.cora.data.Data;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.converter.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;

public class DataListToJsonConverterTest {

	private DataToJsonConverterFactorySpy converterFactory;
	private DataListToJsonConverter recordListToJsonConverter;
	private JsonBuilderFactorySpy builderFactory;
	private CoraDataList dataList;

	@BeforeMethod
	public void beforeMethod() throws Exception {
		converterFactory = new DataToJsonConverterFactorySpy();
		builderFactory = new JsonBuilderFactorySpy();
		dataList = createDataList();
		recordListToJsonConverter = DataListToJsonConverter
				.usingJsonFactoryForDataList(converterFactory, builderFactory, dataList);

	}

	@Test
	public void testRecordListConverterImplementsDataToJsonConverter() throws Exception {
		assertTrue(recordListToJsonConverter instanceof DataToJsonConverter);
	}

	@Test
	public void testToJsonObjectBuilderRootBuilderAndListBuilderCreatedWithBuilderFactory()
			throws Exception {
		JsonObjectBuilder returnedObjectBuilder = recordListToJsonConverter.toJsonObjectBuilder();
		JsonObjectBuilderSpy rootWrappingBuilder = getRootWrappingBuilderFromSpy();

		assertSame(returnedObjectBuilder, rootWrappingBuilder);

		JsonObjectBuilderSpy recordListBuilder = getRecordListBuilderFromSpy();
		rootWrappingBuilder.MCR.assertParameters("addKeyJsonObjectBuilder", 0, "dataList",
				recordListBuilder);
	}

	private JsonObjectBuilderSpy getRecordListBuilderFromSpy() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 0);
	}

	private JsonObjectBuilderSpy getRootWrappingBuilderFromSpy() {
		return (JsonObjectBuilderSpy) builderFactory.MCR.getReturnValue("createObjectBuilder", 1);
	}

	@Test
	public void testToJsonObjectBuilderBasicListInfoAdded() throws Exception {
		recordListToJsonConverter.toJsonObjectBuilder();
		JsonObjectBuilderSpy recordListBuilder = getRecordListBuilderFromSpy();
		recordListBuilder.MCR.assertParameters("addKeyString", 0, "totalNo",
				dataList.getTotalNumberOfTypeInStorage());
		recordListBuilder.MCR.assertParameters("addKeyString", 1, "fromNo", dataList.getFromNo());
		recordListBuilder.MCR.assertParameters("addKeyString", 2, "toNo", dataList.getToNo());
		recordListBuilder.MCR.assertParameters("addKeyString", 3, "containDataOfType",
				dataList.getContainDataOfType());
	}

	@Test
	public void testToJsonObjectBuilderDataBuilderCreatedWithBuilderFactoryAndAddedToListBuilder()
			throws Exception {
		recordListToJsonConverter.toJsonObjectBuilder();
		JsonObjectBuilderSpy recordListBuilder = getRecordListBuilderFromSpy();
		JsonArrayBuilderSpy dataArrayBuilder = getDataArrayBuilderFromSpy();
		recordListBuilder.MCR.assertParameters("addKeyJsonArrayBuilder", 0, "data",
				dataArrayBuilder);
	}

	private JsonArrayBuilderSpy getDataArrayBuilderFromSpy() {
		return (JsonArrayBuilderSpy) builderFactory.MCR.getReturnValue("createArrayBuilder", 0);
	}

	@Test
	public void testToJsonObjectBuilderNoConvertersCreatedAndNothingAddedToDataIfNoRecordsInList()
			throws Exception {
		dataList.getDataList().clear();

		recordListToJsonConverter.toJsonObjectBuilder();
		JsonArrayBuilderSpy dataArrayBuilder = getDataArrayBuilderFromSpy();
		dataArrayBuilder.MCR.assertMethodNotCalled("addJsonObjectBuilder");
	}

	@Test
	public void testToJsonObjectBuilderConvertersCreatedFromFactoryAndResultAddedToDataForEachRecordInList()
			throws Exception {
		recordListToJsonConverter.toJsonObjectBuilder();

		List<Data> listOfData = dataList.getDataList();

		converterFactory.MCR.assertParameters("factorUsingConvertible", 0, listOfData.get(0));
		assertCorrectFactoryAndConvertAndAddingToDataForDataNumberFromList(0);
		assertCorrectFactoryAndConvertAndAddingToDataForDataNumberFromList(1);

		converterFactory.MCR.assertNumberOfCallsToMethod("factorUsingConvertible",
				listOfData.size());
	}

	private void assertCorrectFactoryAndConvertAndAddingToDataForDataNumberFromList(
			int listNumber) {
		JsonArrayBuilderSpy dataArrayBuilder = getDataArrayBuilderFromSpy();
		DataToJsonConverterSpy recordConverterSpy1 = (DataToJsonConverterSpy) converterFactory.MCR
				.getReturnValue("factorUsingConvertible", listNumber);
		recordConverterSpy1.MCR.assertMethodWasCalled("toJsonObjectBuilder");
		var recordBuilder1 = recordConverterSpy1.MCR.getReturnValue("toJsonObjectBuilder", 0);
		dataArrayBuilder.MCR.assertParameters("addJsonObjectBuilder", listNumber, recordBuilder1);
	}

	@Test
	public void testToJson() throws Exception {

		DataListToJsonConverterForTest recordListConverterForTest = new DataListToJsonConverterForTest(
				converterFactory, builderFactory, dataList);

		String json = recordListConverterForTest.toJson();

		recordListConverterForTest.MCR.methodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy jsonBuilder = (JsonObjectBuilderSpy) recordListConverterForTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		jsonBuilder.MCR.methodWasCalled("toJsonFormattedPrettyString");
		jsonBuilder.MCR.assertReturn("toJsonFormattedPrettyString", 0, json);
	}

	@Test
	public void testToJsonCompactFormat() throws Exception {

		DataListToJsonConverterForTest recordListConverterForTest = new DataListToJsonConverterForTest(
				converterFactory, builderFactory, dataList);

		String json = recordListConverterForTest.toJsonCompactFormat();

		recordListConverterForTest.MCR.methodWasCalled("toJsonObjectBuilder");
		JsonObjectBuilderSpy jsonBuilder = (JsonObjectBuilderSpy) recordListConverterForTest.MCR
				.getReturnValue("toJsonObjectBuilder", 0);
		jsonBuilder.MCR.methodWasCalled("toJsonFormattedString");
		jsonBuilder.MCR.assertReturn("toJsonFormattedString", 0, json);
	}

	class DataListToJsonConverterForTest extends DataListToJsonConverter {
		MethodCallRecorder MCR = new MethodCallRecorder();

		DataListToJsonConverterForTest(DataToJsonConverterFactorySpy converterFactory,
				JsonBuilderFactorySpy builderFactory, CoraDataList dataList) {
			super(converterFactory, builderFactory, dataList);
		}

		@Override
		public JsonObjectBuilder toJsonObjectBuilder() {
			MCR.addCall();
			JsonObjectBuilderSpy objectBuilder = new JsonObjectBuilderSpy();
			MCR.addReturned(objectBuilder);
			return objectBuilder;
		}
	}

	private CoraDataList createDataList() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("place");
		DataRecord dataRecord = new DataRecordSpy();
		dataList.addData(dataRecord);
		DataRecord dataRecord2 = new DataRecordSpy();
		dataList.addData(dataRecord2);
		dataList.setTotalNo("111");
		dataList.setFromNo("1");
		dataList.setToNo("100");
		return dataList;
	}

}
