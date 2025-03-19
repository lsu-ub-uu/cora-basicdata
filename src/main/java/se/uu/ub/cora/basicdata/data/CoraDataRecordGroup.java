/*
 * Copyright 2022, 2024 Olov McKie
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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataMissingException;
import se.uu.ub.cora.data.DataRecordGroup;
import se.uu.ub.cora.data.DataRecordLink;

public class CoraDataRecordGroup extends CoraDataGroup implements DataRecordGroup {

	private static final String IGNORE_OVERWRITE_PROTECTION = "ignoreOverwriteProtection";
	private static final String UPDATED = "updated";
	private static final String SYSTEM_RECORD_TYPE = "system";
	private static final String VALIDATION_TYPE = "validationType";
	private static final String ID = "id";
	private static final String DATA_DIVIDER = "dataDivider";
	private static final String TYPE = "type";
	private static final String RECORD_INFO = "recordInfo";
	private static final String TS_VISIBILITY = "tsVisibility";
	private static final String VISIBILITY = "visibility";

	public static CoraDataRecordGroup withNameInData(String nameInData) {
		return new CoraDataRecordGroup(nameInData);
	}

	protected CoraDataRecordGroup(String nameInData) {
		super(nameInData);
	}

	@Override
	public String getType() {
		return getFirstLinkedRecordIdWithNameInDataFromRecordInfo(TYPE);
	}

	@Override
	public void setType(String type) {
		var child = CoraDataRecordLink.usingNameInDataAndTypeAndId(TYPE, "recordType", type);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public String getId() {
		return getFirstAtomicValueWithNameInDataFromRecordInfo(ID);
	}

	@Override
	public void setId(String id) {
		var child = CoraDataAtomic.withNameInDataAndValue(ID, id);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public String getDataDivider() {
		return getFirstLinkedRecordIdWithNameInDataFromRecordInfo(DATA_DIVIDER);
	}

	@Override
	public void setDataDivider(String dataDivider) {
		var child = CoraDataRecordLink.usingNameInDataAndTypeAndId(DATA_DIVIDER, SYSTEM_RECORD_TYPE,
				dataDivider);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public String getValidationType() {
		return getFirstLinkedRecordIdWithNameInDataFromRecordInfo(VALIDATION_TYPE);
	}

	@Override
	public void setValidationType(String validationType) {
		var child = CoraDataRecordLink.usingNameInDataAndTypeAndId(VALIDATION_TYPE, VALIDATION_TYPE,
				validationType);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	private String getFirstAtomicValueWithNameInDataFromRecordInfo(String nameInData) {
		DataGroup recordInfo = getRecordInfo();
		return recordInfo.getFirstAtomicValueWithNameInData(nameInData);
	}

	private String getFirstLinkedRecordIdWithNameInDataFromRecordInfo(String nameInData) {
		DataGroup recordInfo = getRecordInfo();
		DataRecordLink typeLink = recordInfo.getFirstChildOfTypeAndName(DataRecordLink.class,
				nameInData);
		return typeLink.getLinkedRecordId();
	}

	private DataGroup getRecordInfo() {
		return getFirstGroupWithNameInData(RECORD_INFO);
	}

	private void replaceAllChildrenInRecordInfoWithChild(DataChild child) {
		ensureRecordInfoExists();
		DataGroup recordInfo = getRecordInfo();
		String nameInData = child.getNameInData();
		CoraDataChildFilter filter = CoraDataChildFilter.usingNameInData(nameInData);
		recordInfo.removeAllChildrenMatchingFilter(filter);
		recordInfo.addChild(child);
	}

	private void ensureRecordInfoExists() {
		if (!containsChildWithNameInData(RECORD_INFO)) {
			addChild(CoraDataGroup.withNameInData(RECORD_INFO));
		}
	}

	@Override
	public String getCreatedBy() {
		return getFirstLinkedRecordIdWithNameInDataFromRecordInfo("createdBy");
	}

	@Override
	public void setCreatedBy(String userId) {
		var child = CoraDataRecordLink.usingNameInDataAndTypeAndId("createdBy", "user", userId);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public String getTsCreated() {
		return getFirstAtomicValueWithNameInDataFromRecordInfo("tsCreated");
	}

	@Override
	public void setTsCreated(String tsCreated) {
		var child = CoraDataAtomic.withNameInDataAndValue("tsCreated", tsCreated);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public void setTsCreatedToNow() {
		setTsCreated(getNowAsIso8601());
	}

	private String getNowAsIso8601() {
		DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendInstant(6).toFormatter();
		return formatter.format(Instant.now());
	}

	@Override
	public String getLatestUpdatedBy() {
		DataGroup lastUpdated = getLastUpdated();
		return lastUpdated.getFirstAtomicValueWithNameInData("updatedBy");
	}

	private DataGroup getLastUpdated() {
		DataGroup recordInfo = getRecordInfo();
		List<DataChild> updateds = recordInfo.getAllChildrenWithNameInData(UPDATED);
		return (DataGroup) updateds.get(updateds.size() - 1);
	}

	@Override
	public String getLatestTsUpdated() {
		DataGroup lastUpdated = getLastUpdated();
		return lastUpdated.getFirstAtomicValueWithNameInData("tsUpdated");
	}

	@Override
	public void addUpdatedUsingUserIdAndTs(String userId, String tsUpdated) {
		ensureRecordInfoExists();
		DataGroup recordInfo = getRecordInfo();
		CoraDataGroup updated = CoraDataGroup.withNameInData(UPDATED);
		updated.setRepeatId(calcultateRepeatId(recordInfo));
		recordInfo.addChild(updated);

		var tsUpdatedChild = CoraDataAtomic.withNameInDataAndValue("tsUpdated", tsUpdated);
		updated.addChild(tsUpdatedChild);

		var updatedBy = CoraDataRecordLink.usingNameInDataAndTypeAndId("updatedBy", "user", userId);
		updated.addChild(updatedBy);
	}

	private String calcultateRepeatId(DataGroup recordInfo) {
		List<DataGroup> updatedList = recordInfo.getAllGroupsWithNameInData(UPDATED);
		if (updatedList.isEmpty()) {
			return "0";
		}
		return calculateRepeatId(updatedList);
	}

	private String calculateRepeatId(List<DataGroup> updatedList) {
		List<Integer> repeatIds = getAllCurrentRepeatIds(updatedList);

		Integer max = Collections.max(repeatIds);
		return String.valueOf(max + 1);
	}

	private List<Integer> getAllCurrentRepeatIds(List<DataGroup> updatedList) {
		List<Integer> repeatIds = new ArrayList<>(updatedList.size());
		for (DataGroup updated : updatedList) {
			repeatIds.add(getValueAsString0IfProblem(updated));
		}
		return repeatIds;
	}

	private Integer getValueAsString0IfProblem(DataGroup updated) {
		try {
			return Integer.valueOf(updated.getRepeatId());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	@Override
	public void addUpdatedUsingUserIdAndTsNow(String userId) {
		addUpdatedUsingUserIdAndTs(userId, getNowAsIso8601());
	}

	@Override
	public List<DataChild> getAllUpdated() {
		DataGroup recordInfo = getRecordInfo();
		List<DataChild> allUpdated = recordInfo.getAllChildrenWithNameInData("updated");
		throwErrorIfEmptyList(allUpdated);
		return allUpdated;
	}

	private void throwErrorIfEmptyList(List<DataChild> allUpdated) {
		if (allUpdated.isEmpty()) {
			throw new DataMissingException(
					"Child of type: DataGroup and name: updated not found as child.");
		}
	}

	@Override
	public void setAllUpdated(Collection<DataChild> updatedList) {
		ensureRecordInfoExistsIfUpdatedListNotEmpty(updatedList);
		replaceExistingUpdatedWithNewOnesIfPossible(updatedList);
	}

	private void replaceExistingUpdatedWithNewOnesIfPossible(Collection<DataChild> updatedList) {
		if (containsChildWithNameInData(RECORD_INFO)) {
			replaceExistingUpdatedWithNewOnes(updatedList);
		}
	}

	private void replaceExistingUpdatedWithNewOnes(Collection<DataChild> updatedList) {
		DataGroup recordInfo = getRecordInfo();
		recordInfo.removeAllChildrenWithNameInData("updated");
		for (DataChild updatedChild : updatedList) {
			recordInfo.addChild(updatedChild);
		}
	}

	private void ensureRecordInfoExistsIfUpdatedListNotEmpty(Collection<DataChild> updatedList) {
		if (!updatedList.isEmpty()) {
			ensureRecordInfoExists();
		}
	}

	@Override
	public boolean overwriteProtectionShouldBeEnforced() {
		return !ignoreOverwriteProtectionIsSetToTrue();
	}

	private boolean ignoreOverwriteProtectionIsSetToTrue() {
		return containsChildWithNameInData(RECORD_INFO)
				&& getRecordInfo().containsChildWithNameInData(IGNORE_OVERWRITE_PROTECTION)
				&& getRecordInfo().getFirstAtomicValueWithNameInData(IGNORE_OVERWRITE_PROTECTION)
						.equals("true");
	}

	@Override
	public void removeOverwriteProtection() {
		if (containsChildWithNameInData(RECORD_INFO)) {
			getRecordInfo().removeAllChildrenWithNameInData(IGNORE_OVERWRITE_PROTECTION);
		}
	}

	@Override
	public void setTsVisibility(String tsVisibility) {
		var child = CoraDataAtomic.withNameInDataAndValue(TS_VISIBILITY, tsVisibility);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public void setTsVisibilityNow() {
		setTsVisibility(getNowAsIso8601());
	}

	@Override
	public Optional<String> getTsVisibility() {
		return possiblyGetAtomicValueFromRecordInfo(TS_VISIBILITY);
	}

	private Optional<String> possiblyGetAtomicValueFromRecordInfo(String nameInData) {
		if (containsChildWithNameInData(RECORD_INFO)) {
			DataGroup recordInfo = getRecordInfo();
			if (recordInfo.containsChildWithNameInData(nameInData)) {
				return Optional.of(recordInfo.getFirstAtomicValueWithNameInData(nameInData));
			}
		}
		return Optional.empty();
	}

	@Override
	public void setVisibility(String visibility) {
		var child = CoraDataAtomic.withNameInDataAndValue(VISIBILITY, visibility);
		replaceAllChildrenInRecordInfoWithChild(child);
	}

	@Override
	public Optional<String> getVisibility() {
		return possiblyGetAtomicValueFromRecordInfo(VISIBILITY);
	}

}
