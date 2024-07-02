# Fishjam iOS Client

[Fishjam](https://github.com/fishjam-dev/fishjam) Client library for iOS apps written in Swift.

## Components

The repository consists of 3 separate components:

- `FishjamClient` - Fishjam client fully compatible with `Fishjam`, responsible for exchanging media events and
  receiving media streams which then are presented to the user
- `FishjamClientDemo` - Demo application utilizing `Fishjam` client
- `MembraneRTC` - iOS WebRTC client

### Example App

Really simple App allowing to test `Fishjam client` functionalities. It consist of 2 screens:

- Joining screen where user passes peer token followed by join button click
- Room's screen consisting of set of control buttons and an area where participants' videos get displayed

## Documentation

API documentation is available [here](https://fishjam-dev.github.io/ios-client-sdk/documentation/fishjamclient/).

## Installation

Add FishjamClient dependency to your project.

## Developing

1. Run `./scripts/init.sh` in the main directory to install swift-format and release-it and set up git hooks
2. Edit `Debug.xcconfig` to set backend url in development.
3. Run `release-it` to release. Follow the prompts, it should update version in podspec, make a commit and tag and push
   the new version.

## Contributing

We welcome contributions to iOS Client SDK. Please report any bugs or issues you find or feel free to make a pull
request with your own bug fixes and/or features.

## Fishjam Ecosystem

|             |                                                                                                                                                                                                                                                      |
| ----------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Client SDKs | [React](https://github.com/fishjam-dev/react-client-sdk), [React Native](https://github.com/fishjam-dev/react-native-client-sdk), [iOS](https://github.com/fishjam-dev/ios-client-sdk), [Android](https://github.com/fishjam-dev/android-client-sdk) |
| Server SDKs | [Elixir](https://github.com/fishjam-dev/elixir_server_sdk), [Python](https://github.com/fishjam-dev/python-server-sdk), [OpenAPI](https://fishjam-dev.github.io/fishjam-docs/for_developers/api_reference/rest_api)                                  |
| Services    | [Videoroom](https://github.com/fishjam-dev/fishjam-videoroom) - an example videoconferencing app written in elixir <br/> [Dashboard](https://github.com/fishjam-dev/fishjam-dashboard) - an internal tool used to showcase Fishjam's capabilities    |
| Resources   | [Fishjam Book](https://fishjam-dev.github.io/book/) - theory of the framework, [Docs](https://fishjam-dev.github.io/fishjam-docs/), [Tutorials](https://github.com/fishjam-dev/fishjam-clients-tutorials)                                            |
| Membrane    | Fishjam is based on [Membrane](https://membrane.stream/), [Discord](https://discord.gg/nwnfVSY)                                                                                                                                                      |
| Compositor  | [Compositor](https://github.com/membraneframework/membrane_video_compositor_plugin) - Membrane plugin to transform video                                                                                                                             |
| Protobufs   | If you want to use Fishjam on your own, you can use our [protobufs](https://github.com/fishjam-dev/protos)                                                                                                                                           |

## Copyright and License

Copyright 2023, [Software Mansion](https://swmansion.com/?utm_source=git&utm_medium=readme&utm_campaign=fishjam)

[![Software Mansion](https://logo.swmansion.com/logo?color=white&variant=desktop&width=200&tag=membrane-github)](https://swmansion.com/?utm_source=git&utm_medium=readme&utm_campaign=fishjam)

Licensed under the [Apache License, Version 2.0](LICENSE)
