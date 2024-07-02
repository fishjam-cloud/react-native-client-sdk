package fishjamdev.client

import com.google.protobuf.GeneratedMessage
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.slot
import io.mockk.verify
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import java.lang.Error

class WebsocketMock {
    private val websocketMock = mockk<WebSocket>(relaxed = true)
    private val clientListener = slot<WebSocketListener>()

    init {
        mockkConstructor(OkHttpClient::class)

        every { anyConstructed<OkHttpClient>().newWebSocket(any(), capture(clientListener)) } returns websocketMock
    }

    fun open() {
        clientListener.captured.onOpen(websocketMock, mockk())
    }

    fun expectClosed() {
        verify { websocketMock.close(1000, null) }
    }

    fun expect(message: GeneratedMessage) {
        val messageBytes = message.toByteArray().toByteString()
        verify(exactly = 1) { websocketMock.send(match<okio.ByteString> { it == messageBytes }) }
    }

    fun sendToClient(message: GeneratedMessage) {
        val messageBytes = message.toByteArray().toByteString()
        clientListener.captured.onMessage(websocketMock, messageBytes)
    }

    fun close() {
        clientListener.captured.onClosed(websocketMock, 1000, "Closed")
    }

    fun error() {
        clientListener.captured.onFailure(websocketMock, Error("Super important error"), mockk())
    }

    fun confirmVerified() {
        io.mockk.confirmVerified(websocketMock)
    }
}
