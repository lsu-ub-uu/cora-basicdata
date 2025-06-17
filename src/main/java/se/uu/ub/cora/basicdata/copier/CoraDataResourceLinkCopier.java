/*
 * Copyright 2020, 2023 Uppsala University Library
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

import se.uu.ub.cora.basicdata.data.CoraDataResourceLink;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataResourceLink;
import se.uu.ub.cora.data.copier.DataCopier;

public class CoraDataResourceLinkCopier implements DataCopier {

	private CoraDataResourceLink originalResourceLink;
	private CoraDataResourceLink resourceLinkCopy;

	public CoraDataResourceLinkCopier(DataResourceLink originalResourceLink) {
		this.originalResourceLink = (CoraDataResourceLink) originalResourceLink;
	}

	@Override
	public DataChild copy() {
		resourceLinkCopy = CoraDataResourceLink.withNameInDataAndTypeAndIdAndMimeType(
				originalResourceLink.getNameInData(), null, null, originalResourceLink.getMimeType());
		possiblyCopyRepeatId();

		return resourceLinkCopy;
	}

	private void possiblyCopyRepeatId() {
		if (originalResourceLink.hasRepeatId()) {
			resourceLinkCopy.setRepeatId(originalResourceLink.getRepeatId());
		}
	}

}
