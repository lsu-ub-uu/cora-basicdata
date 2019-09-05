package se.uu.ub.cora.basicdata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.Action;

public class CoraDataResourceLinkTest {

	DataResourceLink resourceLink;

	@BeforeMethod
	public void setUp() {
		resourceLink = DataResourceLink.withNameInData("nameInData");

		CoraDataAtomic streamId = CoraDataAtomic.withNameInDataAndValue("streamId", "myStreamId");
		resourceLink.addChild(streamId);

	}

	@Test
	public void testInit() {
		assertEquals(resourceLink.getNameInData(), "nameInData");
		assertNotNull(resourceLink.getAttributes());
		assertNotNull(resourceLink.getChildren());
		assertEquals(resourceLink.getFirstAtomicValueWithNameInData("streamId"), "myStreamId");
		assertNotNull(resourceLink.getActions());
	}

	@Test
	public void testInitWithRepeatId() {
		resourceLink.setRepeatId("hugh");
		assertEquals(resourceLink.getRepeatId(), "hugh");
	}

	@Test
	public void testAddAction() {
		resourceLink.addAction(Action.READ);

		assertTrue(resourceLink.getActions().contains(Action.READ));
		assertFalse(resourceLink.getActions().contains(Action.DELETE));
		// small hack to get 100% coverage on enum
		Action.valueOf(Action.READ.toString());
	}

	@Test
	public void testFromDataGroup() {
		CoraDataGroup dataGroupResourceLink = createResourceLinkAsDataGroup();

		DataResourceLink dataResourceLink = DataResourceLink.fromDataGroup(dataGroupResourceLink);

		assertCorrectFromDataResourceLink(dataResourceLink);
		assertNull(dataResourceLink.getRepeatId());
	}

	private CoraDataGroup createResourceLinkAsDataGroup() {
		CoraDataGroup dataGroupRecordLink = CoraDataGroup.withNameInData("nameInData");

		CoraDataAtomic fileName = CoraDataAtomic.withNameInDataAndValue("filename", "someFileName");
		dataGroupRecordLink.addChild(fileName);

		CoraDataAtomic streamId = CoraDataAtomic.withNameInDataAndValue("streamId", "someStreamId");
		dataGroupRecordLink.addChild(streamId);
		CoraDataAtomic filesize = CoraDataAtomic.withNameInDataAndValue("filesize", "567");
		dataGroupRecordLink.addChild(filesize);
		CoraDataAtomic mimeType = CoraDataAtomic.withNameInDataAndValue("mimeType", "someMimeType");
		dataGroupRecordLink.addChild(mimeType);
		return dataGroupRecordLink;
	}

	private void assertCorrectFromDataResourceLink(DataResourceLink resourceLink) {
		assertEquals(resourceLink.getNameInData(), "nameInData");

		CoraDataAtomic convertedFileName = (CoraDataAtomic) resourceLink
				.getFirstChildWithNameInData("filename");
		assertEquals(convertedFileName.getValue(), "someFileName");

		CoraDataAtomic convertedStreamId = (CoraDataAtomic) resourceLink
				.getFirstChildWithNameInData("streamId");
		assertEquals(convertedStreamId.getValue(), "someStreamId");

		CoraDataAtomic convertedFilesize = (CoraDataAtomic) resourceLink
				.getFirstChildWithNameInData("filesize");
		assertEquals(convertedFilesize.getValue(), "567");

		CoraDataAtomic convertedMimeType = (CoraDataAtomic) resourceLink
				.getFirstChildWithNameInData("mimeType");
		assertEquals(convertedMimeType.getValue(), "someMimeType");
	}

	@Test
	public void testFromDataGroupWithRepeatId() {
		CoraDataGroup dataGroupResourceLink = createResourceLinkAsDataGroup();
		dataGroupResourceLink.setRepeatId("2");

		DataResourceLink dataResourceLink = DataResourceLink.fromDataGroup(dataGroupResourceLink);

		assertCorrectFromDataResourceLink(dataResourceLink);
		assertEquals(dataResourceLink.getRepeatId(), "2");
	}
}
