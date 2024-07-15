package com.fishjamdev.client

import kotlinx.coroutines.Job

internal class CommandsQueue {
  private var commandsQueue: ArrayDeque<Command> = ArrayDeque()
  var clientState: ClientState = ClientState.CREATED

  fun addCommand(command: Command): Job {
    commandsQueue.add(command)
    if (commandsQueue.size == 1) {
      command.command()
    }
    return command.job
  }

  fun finishCommand() {
    val command = commandsQueue.first()
    val job = command.job
    commandsQueue.removeFirst()
    if(command.clientStateAfterCommand != null) {
      clientState = command.clientStateAfterCommand
    }
    job.complete()
    // TODO: make it iterative, not recursive?
    if (commandsQueue.isNotEmpty()) {
      commandsQueue.first().command()
    }
  }

  fun finishCommand(commandName: CommandName) {
    if (commandsQueue.isNotEmpty() && commandsQueue.first().commandName == commandName) {
      finishCommand()
    }
  }

  fun clear() {
    clientState = ClientState.CREATED
    commandsQueue.clear()
  }
}
