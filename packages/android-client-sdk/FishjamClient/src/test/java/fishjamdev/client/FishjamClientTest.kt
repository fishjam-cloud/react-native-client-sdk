import android.content.Context
import com.fishjamdev.client.Config
import com.fishjamdev.client.FishjamClient
import com.fishjamdev.client.FishjamClientListener
import fishjam.PeerNotifications
import fishjamdev.client.WebsocketMock
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.membraneframework.rtc.MembraneRTC
import org.membraneframework.rtc.MembraneRTCListener

class FishjamClientTest {
    private val context = mockk<Context>()
    private val webrtcListener = slot<MembraneRTCListener>()
    private val webrtcClient = mockk<MembraneRTC>(relaxed = true)

    private lateinit var websocketMock: WebsocketMock
    private lateinit var fishjamClientListener: FishjamClientListener
    private lateinit var client: FishjamClient

    private val url = "ws://localhost:4000/socket/peer/websocket"
    private val token = "auth"
    private val authRequest =
        PeerNotifications.PeerMessage
            .newBuilder()
            .setAuthRequest(PeerNotifications.PeerMessage.AuthRequest.newBuilder().setToken(token))
            .build()

    private val authenticated =
        PeerNotifications.PeerMessage
            .newBuilder()
            .setAuthenticated(PeerNotifications.PeerMessage.Authenticated.newBuilder())
            .build()

    init {
        mockkObject(MembraneRTC)
        every { MembraneRTC.create(context, capture(webrtcListener), any()) } returns webrtcClient
    }

    @Before fun initMocksAndConnect() {
        websocketMock = WebsocketMock()
        fishjamClientListener = mockk(relaxed = true)
        client = FishjamClient(context, fishjamClientListener)

        client.connect(Config(websocketUrl = url, token = token))
        websocketMock.open()
        verify { fishjamClientListener.onSocketOpen() }
        websocketMock.expect(authRequest)
    }

    @Test fun authenticates() {
        websocketMock.sendToClient(authenticated)
        verify { fishjamClientListener.onAuthSuccess() }
    }

    @Test fun cleansUp() {
        client.cleanUp()
        verify { webrtcClient.disconnect() }
        websocketMock.expectClosed()
        verify { fishjamClientListener.onDisconnected() }
    }

    @Test fun receivesAndSendsMediaEvents() {
        val sdpOfferMediaEvent =
            PeerNotifications.PeerMessage
                .newBuilder()
                .setMediaEvent(PeerNotifications.PeerMessage.MediaEvent.newBuilder().setData("sdpOffer"))
                .build()

        val joinMediaEvent =
            PeerNotifications.PeerMessage
                .newBuilder()
                .setMediaEvent(PeerNotifications.PeerMessage.MediaEvent.newBuilder().setData("join"))
                .build()

        websocketMock.sendToClient(authenticated)

        verify { fishjamClientListener.onAuthSuccess() }

        webrtcListener.captured.onSendMediaEvent("join")

        websocketMock.expect(joinMediaEvent)

        websocketMock.sendToClient(sdpOfferMediaEvent)

        verify { webrtcClient.receiveMediaEvent("sdpOffer") }
    }

    @Test fun callsOnSocketError() {
        websocketMock.error()
        verify { fishjamClientListener.onSocketError(any(), any()) }
    }

    @Test fun callsOnSocketClosed() {
        websocketMock.close()
        verify { fishjamClientListener.onSocketClose(any(), any()) }
    }

    @After fun confirmVerified() {
        confirmVerified(fishjamClientListener)
        confirmVerified(webrtcClient)
        websocketMock.confirmVerified()
    }
}
