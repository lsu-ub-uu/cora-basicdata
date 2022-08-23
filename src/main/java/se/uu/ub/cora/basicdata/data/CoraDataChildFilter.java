/*
 * Copyright 2022 Uppsala University Library
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;

public class CoraDataChildFilter implements DataChildFilter {

	private String childNameInData;
	private int nrOfChildAttributes;

	private List<FilterAttribute> filterAttributes = new ArrayList<>();

	private CoraDataChildFilter(String childNameInData) {
		this.childNameInData = childNameInData;
	}

	public static CoraDataChildFilter usingNameInData(String childNameInData) {
		return new CoraDataChildFilter(childNameInData);
	}

	@Override
	public void addAttributeUsingNameInDataAndPossibleValues(String attributeName,
			Set<String> possibleValues) {
		FilterAttribute filterAttribute = new FilterAttribute(attributeName, possibleValues);
		filterAttributes.add(filterAttribute);
	}

	@Override
	public boolean childMatches(DataChild dataChild) {
		nrOfChildAttributes = getNumberOfAttributes(dataChild);
		if (childNameMatchesAndHasSameNumberOfAttributes(dataChild)) {
			return hasSameAttributesAndValuesAreInPossibleValues(dataChild.getAttributes());
		}
		return false;
	}

	private boolean hasSameAttributesAndValuesAreInPossibleValues(
			Collection<DataAttribute> attributes) {
		return attributes.stream().allMatch(this::matchAnyFilterAttribute);
	}

	private int getNumberOfAttributes(DataChild child) {
		if (child.hasAttributes()) {
			return child.getAttributes().size();
		}
		return 0;
	}

	private boolean childNameMatchesAndHasSameNumberOfAttributes(DataChild child) {
		return childNameMatches(child) && hasSameNumberOfAttributes();
	}

	private boolean hasSameNumberOfAttributes() {
		return filterAttributes.size() == nrOfChildAttributes;
	}

	private boolean childNameMatches(DataChild child) {
		return child.getNameInData().equals(childNameInData);
	}

	private boolean matchAnyFilterAttribute(DataAttribute dataAttribute) {
		return filterAttributes.stream()
				.anyMatch(filterAttribute -> filterAttribute.attributeMatches(dataAttribute));
	}

	public String onlyForTestGetChildNameInData() {
		return childNameInData;
	}
}
