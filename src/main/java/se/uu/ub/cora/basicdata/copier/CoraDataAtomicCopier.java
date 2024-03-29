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
package se.uu.ub.cora.basicdata.copier;

import java.util.Collection;

import se.uu.ub.cora.basicdata.data.CoraDataAtomic;
import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.copier.DataCopier;

public class CoraDataAtomicCopier implements DataCopier {

	private DataChild dataElement;

	private CoraDataAtomicCopier(DataChild dataElement) {
		this.dataElement = dataElement;
	}

	public static CoraDataAtomicCopier usingDataAtomic(DataChild dataElement) {
		return new CoraDataAtomicCopier(dataElement);
	}

	@Override
	public DataChild copy() {
		CoraDataAtomic dataAtomic = (CoraDataAtomic) dataElement;
		CoraDataAtomic dataAtomicCopy = CoraDataAtomic
				.withNameInDataAndValue(dataAtomic.getNameInData(), dataAtomic.getValue());
		possiblySetRepeatId(dataAtomic, dataAtomicCopy);
		possiblyCopyAttributes(dataAtomic, dataAtomicCopy);
		return dataAtomicCopy;
	}

	private void possiblySetRepeatId(DataAtomic dataAtomic, DataAtomic dataAtomicCopy) {
		if (dataAtomic.getRepeatId() != null) {
			dataAtomicCopy.setRepeatId(dataAtomic.getRepeatId());
		}
	}

	private void possiblyCopyAttributes(DataAtomic dataAtomic, DataAtomic dataAtomicCopy) {
		Collection<DataAttribute> attributes = dataAtomic.getAttributes();
		for (DataAttribute attribute : attributes) {
			dataAtomicCopy.addAttributeByIdWithValue(attribute.getNameInData(),
					attribute.getValue());
		}
	}

}
