/*
 * Copyright 2020 Uppsala University Library
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

import java.util.Set;

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.copier.DataCopier;

public class CoraDataResourceLinkCopier implements DataCopier {

	private DataElement dataElement;
	private CoraDataResourceLink resourceLinkCopy;
	private CoraDataResourceLink originalResourceLink;

	public CoraDataResourceLinkCopier(DataElement dataElement) {
		this.dataElement = dataElement;
	}

	@Override
	public DataElement copy() {
		originalResourceLink = (CoraDataResourceLink) dataElement;
		resourceLinkCopy = CoraDataResourceLink.withNameInData(dataElement.getNameInData());
		copyAndAddChildWithNameInData("streamId");
		copyAndAddChildWithNameInData("filename");
		copyAndAddChildWithNameInData("filesize");
		copyAndAddChildWithNameInData("mimeType");
		possiblyCopyRepeatId();
		possiblyCopyAttributes();

		return resourceLinkCopy;
	}

	private void copyAndAddChildWithNameInData(String childNameInData) {
		CoraDataAtomicCopier atomicCopier = CoraDataAtomicCopier
				.usingDataAtomic(originalResourceLink.getFirstChildWithNameInData(childNameInData));
		DataElement atomicChild = atomicCopier.copy();
		resourceLinkCopy.addChild(atomicChild);
	}

	private void possiblyCopyRepeatId() {
		if (originalResourceLink.getRepeatId() != null) {
			resourceLinkCopy.setRepeatId(originalResourceLink.getRepeatId());
		}
	}

	private void possiblyCopyAttributes() {
		Set<DataAttribute> attributes = originalResourceLink.getAttributes();
		for (DataAttribute attribute : attributes) {
			resourceLinkCopy.addAttributeByIdWithValue(attribute.getNameInData(),
					attribute.getValue());
		}
	}

}
