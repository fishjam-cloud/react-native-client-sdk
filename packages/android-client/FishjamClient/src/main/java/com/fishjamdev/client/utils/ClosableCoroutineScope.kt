package com.fishjamdev.client.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

internal class ClosableCoroutineScope(
  context: CoroutineContext
) : Closeable,
  CoroutineScope {
  override val coroutineContext: CoroutineContext = context

  override fun close() {
    coroutineContext.cancel()
  }
}
