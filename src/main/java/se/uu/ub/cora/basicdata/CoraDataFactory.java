/*
 * Copyright 2019 Uppsala University Library
 * Copyright 2022 Olov McKie
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

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.basicdata.data.CoraDataAttribute;
import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.basicdata.data.CoraDataList;
import se.uu.ub.cora.basicdata.data.CoraDataRecord;
import se.uu.ub.cora.basicdata.data.CoraDataRecordGroup;
import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataFactory;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataList;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class CoraDataFactory implements DataFactory {

	@Override
	public DataList factorListUsingNameOfDataType(String nameOfDataType) {
		return CoraDataList.withContainDataOfType(nameOfDataType);
	}

	@Override
	public DataRecord factorRecordUsingDataGroup(DataGroup dataGroup) {
		return CoraDataRecord.withDataGroup(dataGroup);
	}

	@Override
	public DataRecordGroup factorRecordGroupUsingNameInData(String nameInData) {
		return CoraDataRecordGroup.withNameInData(nameInData);
	}

	@Override
	public DataRecordGroup factorRecordGroupFromDataGroup(DataGroup dataGroup) {
		CoraDataRecordGroup recordGroup = CoraDataRecordGroup
				.withNameInData(dataGroup.getNameInData());
		recordGroup.addChildren(dataGroup.getChildren());
		for (DataAttribute attribute : dataGroup.getAttributes()) {
			recordGroup.addAttributeByIdWithValue(attribute.getNameInData(), attribute.getValue());
		}
		return recordGroup;
	}

	@Override
	public DataGroup factorGroupUsingNameInData(String nameInData) {
		return CoraDataGroup.withNameInData(nameInData);
	}

	@Override
	public DataRecordLink factorRecordLinkUsingNameInData(String nameInData) {
		return CoraDataRecordLink.withNameInData(nameInData);
	}

	@Override
	public DataRecordLink factorRecordLinkUsingNameInDataAndTypeAndId(String nameInData,
			String recordType, String recordId) {
		return CoraDataRecordLink.asLinkWithNameInDataAndTypeAndId(nameInData, recordType,
				recordId);
	}

	@Override
	public DataResourceLink factorResourceLinkUsingNameInData(String nameInData) {
		return CoraDataResourceLink.withNameInData(nameInData);
	}

	@Override
	public DataAtomic factorAtomicUsingNameInDataAndValue(String nameInData, String value) {
		return CoraDataAtomic.withNameInDataAndValue(nameInData, value);
	}

	@Override
	public DataAtomic factorAtomicUsingNameInDataAndValueAndRepeatId(String nameInData,
			String value, String repeatId) {
		return CoraDataAtomic.withNameInDataAndValueAndRepeatId(nameInData, value, repeatId);
	}

	@Override
	public DataAttribute factorAttributeUsingNameInDataAndValue(String nameInData, String value) {
		return CoraDataAttribute.withNameInDataAndValue(nameInData, value);
	}

}
