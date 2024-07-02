# Fishjam Android Client

Android client library for [Fishjam](https://github.com/fishjam-dev/fishjam).

## Documentation

Documentation is available [here](https://fishjam-dev.github.io/android-client-sdk/).

## Installation

Add jitpack repo to your build.gradle:

```gradle
 allprojects {
  repositories {
   ...
   maven { url 'https://jitpack.io' }
  }
 }
```

Add the dependency:

```gradle
 dependencies {
   implementation 'com.github.fishjam-dev:android-client-sdk:<<version>>'
 }
```

## Usage

Make sure you have:

- Running [Fishjam](https://github.com/fishjam-dev/fishjam) server.
- Created room and token of peer in that room. You can use [dashboard](https://fishjam-dev.github.io/fishjam-dashboard/)
  example to create room and peer token.

You can refer to our minimal example on how to use this library.

## Development

1. Set `FISHJAM_SOCKET_URL` in `~/.gradle/gradle.properties` to your dev backend.
2. Run `ktlint` to format code (if missing, install it with `brew install ktlint`)
3. Run `release-it` to release. Follow the prompts, it should add a commit and a git tag and jitpack should pick it up
   automatically and put the new version in the jitpack repo.

## Contributing

We welcome contributions to this SDK. Please report any bugs or issues you find or feel free to make a pull request with
your own bug fixes and/or features.`

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
