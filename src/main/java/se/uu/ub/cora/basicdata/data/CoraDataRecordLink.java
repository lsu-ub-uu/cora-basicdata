/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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
import java.util.List;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataElement;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;

public final class CoraDataRecordLink extends CoraDataGroup implements DataRecordLink {

	private List<Action> actions = new ArrayList<>();

	private CoraDataRecordLink(String nameInData) {
		super(nameInData);
	}

	public static CoraDataRecordLink fromDataGroup(DataGroup dataGroup) {
		return new CoraDataRecordLink(dataGroup);
	}

	private CoraDataRecordLink(DataGroup dataGroup) {
		super(dataGroup.getNameInData());
		addLinkedRecordTypeAndId(dataGroup);
		setRepeatId(dataGroup.getRepeatId());
	}

	private void addLinkedRecordTypeAndId(DataGroup dataGroup) {
		DataElement linkedRecordType = dataGroup.getFirstChildWithNameInData("linkedRecordType");
		addChild(linkedRecordType);
		DataElement linkedRecordId = dataGroup.getFirstChildWithNameInData("linkedRecordId");
		addChild(linkedRecordId);
	}

	public static CoraDataRecordLink withNameInData(String nameInData) {
		return new CoraDataRecordLink(nameInData);
	}

	public static CoraDataRecordLink asLinkWithNameInDataAndTypeAndId(String nameInData,
			String type, String id) {
		CoraDataRecordLink dataRecordLink = new CoraDataRecordLink(nameInData);
		dataRecordLink.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordType", type));
		dataRecordLink.addChild(CoraDataAtomic.withNameInDataAndValue("linkedRecordId", id));
		return dataRecordLink;
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);
	}

	/**
	 * @deprecated use {@linkplain #hasReadAction()} instead
	 */
	@Override
	@Deprecated
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public boolean hasReadAction() {
		return actions.contains(Action.READ);
	}

	@Override
	public String getLinkedRecordType() {
		return super.getFirstAtomicValueWithNameInData("linkedRecordType");
	}

	@Override
	public String getLinkedRecordId() {
		return super.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

}
