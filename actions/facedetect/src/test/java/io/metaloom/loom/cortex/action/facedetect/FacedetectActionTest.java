package io.metaloom.loom.cortex.action.facedetect;

import static io.metaloom.cortex.action.api.ProcessableMediaMeta.FACES;
import static io.metaloom.cortex.action.api.ProcessableMediaMeta.FACE_CLUSTERS;
import static io.metaloom.cortex.action.api.ProcessableMediaMeta.SHA_512;
import static io.metaloom.loom.test.assertj.LoomWorkerAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.metaloom.cortex.action.api.ActionResult;
import io.metaloom.cortex.action.api.ProcessableMedia;
import io.metaloom.cortex.action.common.settings.ProcessorSettings;
import io.metaloom.cortex.action.media.AbstractWorkerTest;
import io.metaloom.cortex.action.media.LoomClientMock;
import io.metaloom.loom.client.grpc.LoomGRPCClient;
import io.metaloom.loom.cortex.action.facedetect.FacedetectAction;
import io.metaloom.loom.cortex.action.facedetect.FacedetectActionSettings;
import io.metaloom.loom.cortex.action.facedetect.cluster.ClusterResult;
import io.metaloom.loom.test.TestEnvHelper;
import io.metaloom.loom.test.Testdata;
import io.metaloom.video.facedetect.Face;

public class FacedetectActionTest extends AbstractWorkerTest {

	@Test
	public void testVideo() throws IOException {
		FacedetectAction action = mockAction();
		Testdata data = TestEnvHelper.prepareTestdata("action-test");
		ProcessableMedia media = media(data.sampleVideo3Path());
		ActionResult result = action.process(media);
		assertThat(result).isProcessed();
		assertThat(media).hasXAttr(1).hasXAttr(SHA_512);
		List<Face> faces = media.get(FACES);
		ClusterResult cluster = media.get(FACE_CLUSTERS, ClusterResult.class);
		assertNotNull(cluster);
		assertEquals(4, cluster.size(), "There should be four clusters since the video contains four persons");
		System.out.println("Faces: " + faces.size());
	}

	@Test
	public void testImage() throws IOException {
		FacedetectAction action = mockAction();
		Testdata data = TestEnvHelper.prepareTestdata("action-test");
		ProcessableMedia media = media(data.sampleImage1Path());
		ActionResult result = action.process(media);
		assertThat(result).isProcessed();
		assertThat(media).hasXAttr(1).hasXAttr(SHA_512);
		List<Face> faces = media.get(FACES);
		System.out.println("Faces: " + faces.size());
	}

	public FacedetectAction mockAction() throws FileNotFoundException {
		LoomGRPCClient client = LoomClientMock.mockGrpcClient();
		return new FacedetectAction(client, new ProcessorSettings(),
			new FacedetectActionSettings().setMinFaceHeightFactor(0.05f).setVideoScaleSize(512));
	}
}
