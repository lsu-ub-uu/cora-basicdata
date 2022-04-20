/*
 * Copyright 2015, 2016 Uppsala University Library
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
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataResourceLink;

public final class CoraDataResourceLink extends CoraDataGroup implements DataResourceLink {

	private List<Action> actions = new ArrayList<>();

	public static CoraDataResourceLink withNameInData(String nameInData) {
		return new CoraDataResourceLink(nameInData);
	}

	private CoraDataResourceLink(String nameInData) {
		super(nameInData);
	}

	public static CoraDataResourceLink fromDataGroup(DataGroup dataGroup) {
		return new CoraDataResourceLink(dataGroup);
	}

	private CoraDataResourceLink(DataGroup dataGroup) {
		super(dataGroup.getNameInData());
		addResourceLinkChildren(dataGroup);
		setRepeatId(dataGroup.getRepeatId());
	}

	private void addResourceLinkChildren(DataGroup dataGroup) {
		DataChild streamId = dataGroup.getFirstChildWithNameInData("streamId");
		addChild(streamId);
		DataChild fileName = dataGroup.getFirstChildWithNameInData("filename");
		addChild(fileName);
		DataChild fileSize = dataGroup.getFirstChildWithNameInData("filesize");
		addChild(fileSize);
		DataChild mimeType = dataGroup.getFirstChildWithNameInData("mimeType");
		addChild(mimeType);
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public boolean hasReadAction() {
		return actions.contains(Action.READ);
	}

	@Override
	public void setStreamId(String streamId) {
		super.addChild(CoraDataAtomic.withNameInDataAndValue("streamId", streamId));
	}

	@Override
	public String getStreamId() {
		return super.getFirstAtomicValueWithNameInData("streamId");
	}

	@Override
	public void setFileName(String fileName) {
		super.addChild(CoraDataAtomic.withNameInDataAndValue("filename", fileName));
	}

	@Override
	public String getFileName() {
		return super.getFirstAtomicValueWithNameInData("filename");
	}

	@Override
	public void setFileSize(String fileSize) {
		super.addChild(CoraDataAtomic.withNameInDataAndValue("filesize", fileSize));
	}

	@Override
	public String getFileSize() {
		return super.getFirstAtomicValueWithNameInData("filesize");
	}

	@Override
	public void setMimeType(String mimeType) {
		super.addChild(CoraDataAtomic.withNameInDataAndValue("mimeType", mimeType));
	}

	@Override
	public String getMimeType() {
		return super.getFirstAtomicValueWithNameInData("mimeType");
	}
}
