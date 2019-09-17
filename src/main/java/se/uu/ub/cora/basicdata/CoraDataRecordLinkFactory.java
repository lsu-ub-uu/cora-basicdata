package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLinkFactory;

public class CoraDataRecordLinkFactory implements DataRecordLinkFactory {

	@Override
	public DataGroup factorUsingNameInData(String nameInData) {
		return CoraDataRecordLink.withNameInData(nameInData);
	}

}
