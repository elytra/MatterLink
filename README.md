# MatterLink

matterbridge for MC servers

https://github.com/42wim/matterbridge

requires api section to be setup along these lines 

```
[api]
[api.local]
    BindAddress="0.0.0.0:4343" # or listen only to localhost

    #Bearer token used for authentication
    #curl -H "Authorization: Bearer testtoken" http://localhost:4343/api/messages
    
    #OPTIONAL (no authorization if token is empty)
    Token="testtoken"
    
    Buffer=1000
    
    RemoteNickFormat="{NICK}"
    
    ShowJoinPart = true
```

values you need to remember are obviously the IP, port and token
MatterLInk will need them in the configuration

## Downloads

https://ci.elytradev.com/job/elytra/job/MatterLink/job/master/