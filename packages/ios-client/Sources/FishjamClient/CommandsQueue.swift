import Foundation

class CommandsQueue {
    private var commandsQueue = [Command]()
    var clientState = ClientState.created

    func addCommand(_ command: Command) -> DispatchGroup {
        commandsQueue.append(command)
        if commandsQueue.count == 1 {
            command.execute()
        }
        return command.job
    }

    func finishCommand() {
        guard let command = commandsQueue.first else { return }
        let job = command.job
        commandsQueue.removeFirst()
        if let stateAfterCommand = command.clientStateAfterCommand {
            clientState = stateAfterCommand
        }
        job.leave()
        if !commandsQueue.isEmpty {
            commandsQueue.first?.execute()
        }
    }

    func finishCommand(_ commandName: CommandName) {
        if let firstCommand = commandsQueue.first, firstCommand.commandName == commandName {
            finishCommand()
        }
    }

    func finishCommand(_ commandNames: [CommandName]) {
        if let firstCommand = commandsQueue.first, commandNames.contains(firstCommand.commandName) {
            finishCommand()
        }
    }

    func clear(cause: String) {
        commandsQueue.forEach { command in
            command.job.leave()
        }
        commandsQueue.removeAll()
        clientState = .created
    }
}
