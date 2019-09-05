package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataGroup;

public interface DataGroupFactory {

	DataGroup factorUsingNameInData(String nameInData);

	DataGroup factorAsLinkWithNameInDataTypeAndId(String nameInData, String recordType,
			String recordId);

}
