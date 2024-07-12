package com.fishjamdev.client

import kotlinx.coroutines.Job

internal class CommandsQueue {
  private var commandsQueue: ArrayDeque<Command> = ArrayDeque()

  fun addCommand(command: Command): Job {
    commandsQueue.add(command)
    if (commandsQueue.size == 1) {
      command.command()
    }
    return command.job
  }

  fun finishCommand() {
    val job = commandsQueue.first().job
    commandsQueue.removeFirst()
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
}
