# MatterLink

A MatterBridge endpoint for MC servers!

THIS MOD REQUIRES YOU TO ALSO RUN A MATTERBRIDGE RELAY
https://github.com/42wim/matterbridge

requires api section to be setup along these lines 

```
[api]
[api.local]
    BindAddress="0.0.0.0:4242" # or listen only to localhost: 127.0.0.1:4242

    #Bearer token used for authentication
    #curl -H "Authorization: Bearer testtoken" http://localhost:4343/api/messages
    
    #OPTIONAL (no authorization if token is empty)
    Token="mytoken"
    
    Buffer=1000
    
    RemoteNickFormat="{NICK}"
    
    ShowJoinPart = true
```

## Features

* Individually configurable relaying of player deaths, achievements/advancements, server join, and server leave
* Configurable bridge commands sent from chat to MC:
```
help:       Lists all commands with no arguments, 
            or displays help for a command
players:    Lists online players
uptime:     Print server uptime
```
* Edit config settings without restarting the server!
```
/config <connect|disconnect|reload>
Connect or disconnect the bridge, 
or cycle the connection and reload the config file
```
* Pass through commands to MC! Fully configurable. 
```
# MC commands that can be executed through the bridge
# Separate bridge command and MC command with '=', 
# separate multiple values with spaces
#  [default: [tps=forge tps]]
S:commandMapping <
    tps=forge tps
 >
```
This default example allows you to run `/forge tps` on the server by typing `$tps` in the chat
(replace $ with whatever you've configured as the command prefix). 

**WARNING: There is *NO* permissions checking of any kind for command passthrough!
Do not configure passthrough for any commands you would not be comfortable
with anyone on your IRC/Discord/etc. executing!**
 

## Downloads

https://github.com/elytra/MatterLink/releases

https://ci.elytradev.com/job/elytra/job/MatterLink/job/master/lastSuccessfulBuild/ - may be unstable

## Dependencies

- forgelin: https://minecraft.curseforge.com/projects/shadowfacts-forgelin

## Setup

Install matterbridge and try out the basic sample:

```
go get github.com/42wim/matterbridge
mv matterbridge-sample.toml matterbridge.tom
matterbridge
```

Now you just need to run MatterBridge on the server, the default configuration works with the provided sample.

and then [RTFM!!!](https://github.com/42wim/matterbridge#configuration)