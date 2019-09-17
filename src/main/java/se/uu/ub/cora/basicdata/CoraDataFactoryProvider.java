package se.uu.ub.cora.basicdata;

import se.uu.ub.cora.data.DataAtomicFactory;
import se.uu.ub.cora.data.DataGroupFactory;
import se.uu.ub.cora.data.DataRecordFactory;
import se.uu.ub.cora.data.DataRecordLinkFactory;

public class CoraDataFactoryProvider implements DataFactoryProvider {

	@Override
	public DataRecordFactory getDataRecordFactory() {
		return new CoraDataRecordFactory();
	}

	@Override
	public DataGroupFactory getDataGroupFactory() {
		return new CoraDataGroupFactory();
	}

	@Override
	public DataAtomicFactory getDataAtomicFactory() {
		return new CoraDataAtomicFactory();
	}

	@Override
	public DataRecordLinkFactory getDataRecordLinkFactory() {
		return new CoraDataRecordLinkFactory();
	}

}
