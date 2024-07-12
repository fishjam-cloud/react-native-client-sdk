package com.fishjamdev.client

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

internal enum class CommandName {
  CONNECT,
  JOIN,
  ADD_TRACK,
  REMOVE_TRACK,
  RENEGOTIATE,
  LEAVE,
}

internal data class Command(val commandName: CommandName, val command: () -> Unit = {}) {
  val job: CompletableJob = Job()
}
