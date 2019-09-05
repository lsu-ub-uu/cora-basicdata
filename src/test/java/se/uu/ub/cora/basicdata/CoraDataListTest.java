/*
 * Copyright 2015 Uppsala University Library
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

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.data.Data;

public class CoraDataListTest {
	@Test
	public void testInit() {
		String containDataOfType = "metadata";
		CoraDataList dataList = CoraDataList.withContainDataOfType(containDataOfType);
		assertEquals(dataList.getContainDataOfType(), "metadata");
	}

	@Test
	public void testAddRecord() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("metadata");
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("dataGroupId");
		CoraDataRecord record = CoraDataRecord.withDataGroup(dataGroup);
		dataList.addData(record);
		List<Data> records = dataList.getDataList();
		assertEquals(records.get(0), record);
	}

	@Test
	public void testAddGroup() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("metadata");
		CoraDataGroup dataGroup = CoraDataGroup.withNameInData("dataGroupId");
		dataList.addData(dataGroup);
		List<Data> groups = dataList.getDataList();
		assertEquals(groups.get(0), dataGroup);
	}

	@Test
	public void testTotalNo() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("metadata");
		dataList.setTotalNo("2");
		assertEquals(dataList.getTotalNumberOfTypeInStorage(), "2");
	}

	@Test
	public void testFromNo() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("metadata");
		dataList.setFromNo("0");
		assertEquals(dataList.getFromNo(), "0");
	}

	@Test
	public void testToNo() {
		CoraDataList dataList = CoraDataList.withContainDataOfType("metadata");
		dataList.setToNo("2");
		assertEquals(dataList.getToNo(), "2");
	}
}
