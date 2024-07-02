package org.membraneframework.rtc.test

import android.util.Log
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.membraneframework.rtc.*
import org.membraneframework.rtc.media.LocalAudioTrack
import org.membraneframework.rtc.media.LocalVideoTrack
import org.membraneframework.rtc.media.TrackBandwidthLimit
import org.membraneframework.rtc.media.VideoParameters
import org.membraneframework.rtc.utils.addTransceiver
import org.membraneframework.rtc.utils.createOffer
import org.membraneframework.rtc.utils.getEncodings
import org.membraneframework.rtc.utils.setLocalDescription
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.RtpParameters.Encoding
import org.webrtc.RtpTransceiver
import org.webrtc.SessionDescription
import org.webrtc.VideoTrack

class EndpointConnectionManagerTest {
    private lateinit var manager: PeerConnectionManager
    private lateinit var endpointConnectionMock: PeerConnection

    @Before
    fun createMocks() {
        val endpointConnectionListenerMock = mockk<PeerConnectionListener>(relaxed = true)
        val endpointConnectionFactoryMock = mockk<PeerConnectionFactoryWrapper>(relaxed = true)

        mockkStatic("org.membraneframework.rtc.utils.SuspendableSdpObserverKt")
        mockkStatic("org.membraneframework.rtc.utils.EndpointConnectionUtilsKt")

        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.println(any(), any(), any()) } returns 0

        endpointConnectionMock = mockk(relaxed = true)

        coEvery {
            endpointConnectionMock.createOffer(any<MediaConstraints>())
        } returns Result.success(SessionDescription(SessionDescription.Type.OFFER, "test_description"))
        coEvery {
            endpointConnectionMock.setLocalDescription(any<SessionDescription>())
        } returns Result.success(Unit)

        every { endpointConnectionFactoryMock.createPeerConnection(any(), any()) } returns endpointConnectionMock

        manager = PeerConnectionManager(endpointConnectionListenerMock, endpointConnectionFactoryMock)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun createsOffer() =
        runTest {
            val offer = manager.getSdpOffer(emptyList(), emptyMap(), emptyList())

            assertNotNull(offer)
            assertEquals("test_description", offer.description)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addsAudioTrack() =
        runTest {
            val audioTrack = LocalAudioTrack(mockk(relaxed = true))
            manager.getSdpOffer(emptyList(), emptyMap(), listOf(audioTrack))

            verify(exactly = 1) {
                endpointConnectionMock.addTransceiver(
                    audioTrack.mediaTrack,
                    eq(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY),
                    match { it.size == 1 },
                    withArg {
                        assertEquals("should be just 1 encoding", 1, it.size)
                        assertNull("without rid", it[0].rid)
                    }
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addsVideoTrack() =
        runTest {
            val mediaTrack: VideoTrack = mockk(relaxed = true)

            every { mediaTrack.kind() } returns "video"

            val videoTrack =
                LocalVideoTrack(
                    mediaTrack,
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    VideoParameters.presetFHD169
                )

            manager.getSdpOffer(emptyList(), emptyMap(), listOf(videoTrack))

            verify(exactly = 1) {
                endpointConnectionMock.addTransceiver(
                    videoTrack.rtcTrack(),
                    eq(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY),
                    match { it.size == 1 },
                    withArg {
                        assertEquals("should be just 1 encoding", 1, it.size)
                        assertNull("without rid", it[0].rid)
                    }
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun simulcastConfigIsSet() =
        runTest {
            val videoParameters =
                VideoParameters.presetFHD169.copy(
                    simulcastConfig =
                        SimulcastConfig(
                            true,
                            listOf(TrackEncoding.H, TrackEncoding.L)
                        )
                )

            val mediaTrack: VideoTrack = mockk(relaxed = true)

            every { mediaTrack.kind() } returns "video"

            val videoTrack =
                LocalVideoTrack(
                    mediaTrack,
                    mockk(relaxed = true),
                    mockk(relaxed = true),
                    videoParameters
                )

            manager.getSdpOffer(emptyList(), emptyMap(), listOf(videoTrack))

            verify(exactly = 1) {
                endpointConnectionMock.addTransceiver(
                    videoTrack.rtcTrack(),
                    eq(RtpTransceiver.RtpTransceiverDirection.SEND_ONLY),
                    any(),
                    withArg {
                        assertEquals("Should be 3 encodings", 3, it.size)

                        assertEquals("first encoding should have rid=l", "l", it[0].rid)
                        assertTrue("l encoding should be active", it[0].active)
                        assertEquals("l layer should be 4x smaller", it[0].scaleResolutionDownBy, 4.0)

                        assertEquals("first encoding should have rid=m", "m", it[1].rid)
                        assertFalse("m encoding should not be active", it[1].active)
                        assertEquals("m layer should be 2x smaller", it[1].scaleResolutionDownBy, 2.0)

                        assertEquals("third encoding should have rid=h", "h", it[2].rid)
                        assertTrue("h encoding should be active", it[2].active)
                        assertEquals("h layer should have original size", it[2].scaleResolutionDownBy, 1.0)
                    }
                )
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun setTrackBandwidth() =
        runTest {
            val h = Encoding("h", true, 1.0)
            val m = Encoding("m", true, 2.0)
            val l = Encoding("l", true, 4.0)

            every { endpointConnectionMock.senders } returns
                listOf(
                    mockk(relaxed = true) {
                        every { parameters } returns
                            mockk(relaxed = true) {
                                every { track()?.id() } returns "dummy_track"
                            }
                    },
                    mockk(relaxed = true) {
                        every { parameters } returns
                            mockk(relaxed = true) {
                                every { track()?.id() } returns "track_id"
                                every { getEncodings() } returns
                                    listOf(
                                        h,
                                        m,
                                        l
                                    )
                            }
                    }
                )
            manager.getSdpOffer(emptyList(), emptyMap(), emptyList())
            assertNull("layers have no maxBitrateBps", h.maxBitrateBps)
            manager.setTrackBandwidth("track_id", TrackBandwidthLimit.BandwidthLimit(1000))
            assertEquals("h layer has correct maxBitrateBps", 780190, h.maxBitrateBps)
            assertEquals("m layer has correct maxBitrateBps", 195047, m.maxBitrateBps)
            assertEquals("l layer has correct maxBitrateBps", 48761, l.maxBitrateBps)
        }
}
