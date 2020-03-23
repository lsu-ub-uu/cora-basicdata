/*
 * Copyright 2019 Uppsala University Library
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

import java.util.Collection;

import se.uu.ub.cora.basicdata.data.CoraDataRecordLink;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataLink;
import se.uu.ub.cora.data.copier.DataCopier;

public class CoraDataRecordLinkCopier implements DataCopier {

	private DataElement dataElement;
	private CoraDataRecordLink orignialDataRecordLink;
	private CoraDataRecordLink dataRecordLinkCopy;

	public CoraDataRecordLinkCopier(DataElement dataElement) {
		this.dataElement = dataElement;
	}

	@Override
	public DataLink copy() {
		orignialDataRecordLink = (CoraDataRecordLink) dataElement;
		dataRecordLinkCopy = CoraDataRecordLink.withNameInData(dataElement.getNameInData());

		copyAndAddChildWithNameInData("linkedRecordType");
		copyAndAddChildWithNameInData("linkedRecordId");
		possiblyCopyRepeatId();
		possiblyCopyAttributes();
		return dataRecordLinkCopy;
	}

	private void copyAndAddChildWithNameInData(String childNameInData) {
		DataElement linkedRecordTypeCopy = copyChildFromOriginalLinkUsingChildNameInData(
				childNameInData);
		dataRecordLinkCopy.addChild(linkedRecordTypeCopy);
	}

	private DataElement copyChildFromOriginalLinkUsingChildNameInData(String childNameInData) {
		CoraDataAtomicCopier atomicCopier = CoraDataAtomicCopier.usingDataAtomic(
				orignialDataRecordLink.getFirstChildWithNameInData(childNameInData));
		return atomicCopier.copy();
	}

	private void possiblyCopyRepeatId() {
		if (orignialDataRecordLink.getRepeatId() != null) {
			dataRecordLinkCopy.setRepeatId(orignialDataRecordLink.getRepeatId());
		}
	}

	private void possiblyCopyAttributes() {
		Collection<DataAttribute> attributes = orignialDataRecordLink.getAttributes();
		for (DataAttribute attribute : attributes) {
			dataRecordLinkCopy.addAttributeByIdWithValue(attribute.getNameInData(),
					attribute.getValue());
		}
	}

}
