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

import se.uu.ub.cora.basicdata.data.CoraDataGroup;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.copier.DataCopier;
import se.uu.ub.cora.data.copier.DataCopierFactory;

public class CoraDataGroupCopier implements DataCopier {

	private DataElement dataElement;
	private DataCopierFactory copierFactory;
	private DataGroup originalDataGroup;
	private DataGroup dataGroupCopy;

	private CoraDataGroupCopier(DataElement dataElement, DataCopierFactory copierFactory) {
		this.dataElement = dataElement;
		this.copierFactory = copierFactory;
	}

	public static CoraDataGroupCopier usingDataGroupAndCopierFactory(DataElement dataElement,
			DataCopierFactory copierFactory) {
		return new CoraDataGroupCopier(dataElement, copierFactory);
	}

	@Override
	public DataGroup copy() {
		originalDataGroup = (DataGroup) dataElement;
		dataGroupCopy = CoraDataGroup.withNameInData(originalDataGroup.getNameInData());
		copyChildren();
		possiblyCopyRepeatId();
		possiblyCopyAttributes();

		return dataGroupCopy;
	}

	private void copyChildren() {
		for (DataElement childElement : originalDataGroup.getChildren()) {
			DataCopier dataCopier = copierFactory.factorForDataElement(childElement);
			DataElement copiedElement = dataCopier.copy();
			dataGroupCopy.addChild(copiedElement);
		}
	}

	private void possiblyCopyRepeatId() {
		if (originalDataGroup.getRepeatId() != null) {
			dataGroupCopy.setRepeatId(originalDataGroup.getRepeatId());
		}
	}

	private void possiblyCopyAttributes() {
		Collection<DataAttribute> attributes = originalDataGroup.getAttributes();
		for (DataAttribute attribute : attributes) {
			dataGroupCopy.addAttributeByIdWithValue(attribute.getNameInData(),
					attribute.getValue());
		}
	}

	DataCopierFactory getCopierFactory() {
		return copierFactory;
	}

}
