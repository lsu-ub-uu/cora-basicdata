package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataGroup;

public class CoraDataGroupFactory implements DataGroupFactory {

	@Override
	public DataGroup factorUsingNameInData(String nameInData) {
		return CoraDataGroup.withNameInData(nameInData);
	}

	@Override
	public DataGroup factorAsLinkWithNameInDataTypeAndId(String nameInData, String recordType,
			String recordId) {
		return CoraDataGroup.asLinkWithNameInDataAndTypeAndId(nameInData, recordType, recordId);
	}

}
