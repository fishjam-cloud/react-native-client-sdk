import Foundation

enum CommandName {
    case connect
    case join
    case addTrack
    case removeTrack
    case renegotiate
    case leave
}

enum ClientState {
    case created
    case connected
    case joined
}

class Command {
    let commandName: CommandName
    let clientStateAfterCommand: ClientState?
    let execute: () -> Void

    var job = DispatchGroup()

    init(commandName: CommandName, clientStateAfterCommand: ClientState? = nil, execute: @escaping () -> Void = {}) {
        self.commandName = commandName
        self.clientStateAfterCommand = clientStateAfterCommand
        self.execute = execute
    }
}
