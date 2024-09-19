/*
 * Copyright 2015, 2016, 2019, 2020, 2022, 2024 Uppsala University Library
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import se.uu.ub.cora.data.Action;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataRecord;
import se.uu.ub.cora.data.DataRecordGroup;

public final class CoraDataRecord implements DataRecord {
	private static final String SEARCH = "search";
	private DataRecordGroup dataRecordGroup;
	private List<Action> actions = new ArrayList<>();
	private Set<String> readPermissions = new LinkedHashSet<>();
	private Set<String> writePermissions = new LinkedHashSet<>();
	private Set<String> protocols = new LinkedHashSet<>();

	public static CoraDataRecord withDataRecordGroup(DataRecordGroup dataRecordGroup) {
		return new CoraDataRecord(dataRecordGroup);
	}

	private CoraDataRecord(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	@Override
	public void setDataRecordGroup(DataRecordGroup dataRecordGroup) {
		this.dataRecordGroup = dataRecordGroup;
	}

	@Override
	public DataRecordGroup getDataRecordGroup() {
		return dataRecordGroup;
	}

	@Override
	public void addAction(Action action) {
		actions.add(action);
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public Set<String> getReadPermissions() {
		return readPermissions;
	}

	@Override
	public Set<String> getWritePermissions() {
		return writePermissions;
	}

	@Override
	public void addReadPermission(String readPermission) {
		readPermissions.add(readPermission);

	}

	@Override
	public void addWritePermission(String writePermission) {
		writePermissions.add(writePermission);
	}

	@Override
	public void addReadPermissions(Collection<String> readPermissions) {
		this.readPermissions.addAll(readPermissions);
	}

	@Override
	public boolean hasReadPermissions() {
		return !this.readPermissions.isEmpty();
	}

	@Override
	public void addWritePermissions(Collection<String> writePermissions) {
		this.writePermissions.addAll(writePermissions);

	}

	@Override
	public boolean hasWritePermissions() {
		return !this.writePermissions.isEmpty();
	}

	@Override
	public String getType() {
		try {
			return getTypeFromGroup();
		} catch (Exception dmException) {
			throw new DataMissingException("Record type not known");
		}
	}

	private String getTypeFromGroup() {
		DataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData("recordInfo");
		DataGroup linkedTypeGroup = recordInfo.getFirstGroupWithNameInData("type");
		return linkedTypeGroup.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	@Override
	public String getId() {
		try {
			return getIdFromGroup();
		} catch (Exception dmException) {
			throw new DataMissingException("Record id not known");
		}
	}

	private String getIdFromGroup() {
		DataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData("recordInfo");
		return recordInfo.getFirstAtomicValueWithNameInData("id");
	}

	@Override
	public boolean hasActions() {
		return !actions.isEmpty();
	}

	@Override
	public String getSearchId() {
		String type = getType();
		if (SEARCH.equals(type)) {
			return getId();
		} else if (isRecordTypeAndHasSearch(type)) {
			return extractSearchId();
		}
		throw new DataMissingException("No searchId exists");
	}

	private boolean isRecordTypeAndHasSearch(String type) {
		return "recordType".equals(type) && dataRecordGroup.containsChildWithNameInData(SEARCH);
	}

	private String extractSearchId() {
		DataGroup search = dataRecordGroup.getFirstGroupWithNameInData(SEARCH);
		return search.getFirstAtomicValueWithNameInData("linkedRecordId");
	}

	@Override
	public void addProtocol(String protocol) {
		protocols.add(protocol);
	}

	@Override
	public Set<String> getProtocols() {
		return protocols;
	}
}