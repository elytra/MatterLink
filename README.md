# MatterLink

THIS MOD REQUIRES YOU TO ALSO RUN A MATTERBRIDGE 
https://github.com/42wim/matterbridge

connect matterbridge to MC servers

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

## Downloads

https://github.com/elytra/MatterLink/releases

https://ci.elytradev.com/job/elytra/job/MatterLink/job/master/lastSuccessfulBuild/

## Dependencies

- forgelin: https://minecraft.curseforge.com/projects/shadowfacts-forgelin

## Setup

install matterbridge and try out the basic sample

```
go get github.com/42wim/matterbridge
mv matterbridge-sample.toml matterbridge.tom
matterbridge
```

now you just need to run matterbridge on the server, the default configuration works with the provided sample

and then [RTFM!!!](https://github.com/42wim/matterbridge#configuration)