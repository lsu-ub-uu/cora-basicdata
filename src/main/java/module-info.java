module se.uu.ub.cora.basicdata {
	requires transitive se.uu.ub.cora.json;
	requires transitive se.uu.ub.cora.data;

	exports se.uu.ub.cora.basicdata;
	exports se.uu.ub.cora.basicdata.converter;
	exports se.uu.ub.cora.basicdata.converter.datatojson;
	exports se.uu.ub.cora.basicdata.converter.jsontodata;

	// provides se.uu.ub.cora.data.DataListFactory with CoraDataListFactory;
	// provides se.uu.ub.cora.data.DataRecordFactory with CoraDataRecordFactory;
	// provides se.uu.ub.cora.data.DataGroupFactory with CoraDataGroupFactory;
	// provides se.uu.ub.cora.data.DataRecordLinkFactory with CoraDataRecordLinkFactory;
	// provides se.uu.ub.cora.data.DataResourceLinkFactory with CoraDataResourceLinkFactory;
	// provides se.uu.ub.cora.data.DataAtomicFactory with CoraDataAtomicFactory;
	// provides se.uu.ub.cora.data.DataAttributeFactory with CoraDataAttributeFactory;
	// provides se.uu.ub.cora.data.converter.JsonToDataConverterFactory
	// with JsonToDataConverterFactoryImp;
	// provides se.uu.ub.cora.data.converter.DataToJsonConverterFactoryCreator
	// with BasicDataToJsonConverterFactoryCreator;
	// provides se.uu.ub.cora.data.copier.DataCopierFactory with CoraDataCopierFactoryImp;
}