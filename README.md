# MatterLink

- [Downloads](#downloads)
- [Dependencies](#dependencies)
- [Features](#features)
- [Setup](#setup)

A MatterBridge endpoint for MC servers!

THIS MOD REQUIRES YOU TO ALSO RUN A MATTERBRIDGE RELAY
https://github.com/42wim/matterbridge

Chat with us on IRC: [#matterbridge @ irc.esper.net](irc://irc.esper.net/matterbridge)

## Downloads

[![Github All Releases](https://img.shields.io/github/downloads/elytra/MatterLink/total.svg?style=for-the-badge&label=Github%20Releases&logo=github)](https://github.com/elytra/MatterLink/releases)

[![Jenkins](https://img.shields.io/jenkins/s/https/ci.elytradev.com/job/elytra/job/MatterLink/job/master.svg?style=for-the-badge&label=Jenkins%20Build)](https://ci.elytradev.com/job/elytra/job/MatterLink/job/master/lastSuccessfulBuild/artifact/)

[![Files](https://curse.nikky.moe/api/img/287323/files?logo&style=for-the-badge&version=1.12.2)](https://minecraft.curseforge.com/projects/287323/files)

[![Download](https://curse.nikky.moe/api/img/287323?logo&style=for-the-badge&version=1.12.2)](https://curse.nikky.moe/api/url/287323?version=1.12.2)

[![Download](https://curse.nikky.moe/api/img/287323?logo&style=for-the-badge&version=1.11.2)](https://curse.nikky.moe/api/url/287323?version=1.11.2)

[![Download](https://curse.nikky.moe/api/img/287323?logo&style=for-the-badge&version=1.10.2)](https://curse.nikky.moe/api/url/287323?version=1.10.2)

[![Download](https://curse.nikky.moe/api/img/287323?logo&style=for-the-badge&version=1.7.10)](https://curse.nikky.moe/api/url/287323?version=1.7.10)

## Dependencies

[![Files](https://curse.nikky.moe/api/img/248453/files?logo&style=for-the-badge)](https://minecraft.curseforge.com/projects/248453/files)

## Features

### Custom bridge commands

including passthrough to MC!  
Default commands: `help, tps, list, seed, uptime`

Commands are specified in JSON format as follows:

Passthrough command (executes the configured command from the MC server console)

```json
{
    "alias": "tps",
    "type": "PASSTHROUGH",
    "execute": "forge tps",
    "permLevel": 0,
    "help": "Print server tps",
    "allowArgs": false
}
```

Response command

```json
{
    "alias": "uptime",
    "type": "RESPONSE",
    "response": "{uptime}",
    "permLevel": 1,
    "help": "Print server uptime",
    "allowArgs": false
}
```

### Command permissions

Higher numbers mean more permissions. Configured on a network-by-network basis.  
For IRC, this is your hostmask.  
For Discord, this is your userid (NOT the four-digit number.)

```json
{
  "irc.esper": {
    "~DaMachina@hostname.com":1000
  }
}
```
* Edit and reload the config file without restarting the server!
```
/config <connect|disconnect|reload>
    connect:    Connects the MC chat to the MatterBridge server
    disconnect: Disconnects the chat from the MatterBridge server
    reload:     Disconnects, reloads the config and custom command files, 
                then reconnects.
```

## Setup

Requires the matterbridge config api section to be setup along these lines:

```
[api]
[api.local]
    BindAddress="0.0.0.0:4242" # or listen only to localhost: 127.0.0.1:4242
    #OPTIONAL (no authorization if token is empty)
    Token="mytoken"
    Buffer=1000
    RemoteNickFormat="{NICK}"
    ShowJoinPart = true
```

Now you just need to run MatterBridge on the server, the default configuration works with the provided sample.

Install matterbridge and try out the basic sample:

```
go get github.com/42wim/matterbridge
mv matterbridge-sample.toml matterbridge.toml
matterbridge
```

now start the server with matterlink (and forgelin )in the mods folder

and then [RTFM!!!](https://github.com/42wim/matterbridge#configuration) and configure all your needed gateways, endpoints etc

powered by wishful thinking