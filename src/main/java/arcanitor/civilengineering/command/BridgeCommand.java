package arcanitor.civilengineering.command;

import arcanitor.civilengineering.CivilEngineering;
import arcanitor.civilengineering.bridge.MessageHandler;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.List;

public class BridgeCommand extends CommandBase {
    private final List<String> aliases;

    public BridgeCommand(){
        aliases = Lists.newArrayList(CivilEngineering.MODID,"bridge","BRIDGE");
    }

    @Override
    @Nonnull
    public String getName() {
        return "bridge";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "bridge <connect|disconnect>";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) /*throws CommandException*/ {
        if (args.length < 1) {
            //throw new WrongUsageException("")
            return;
        }
        String cmd = args[0];
        if (cmd.toLowerCase().equals("connect")) {
            if(!CivilEngineering.incomingMessageThread.isAlive()) {
                CivilEngineering.incomingMessageThread = new Thread(new MessageHandler());
                CivilEngineering.incomingMessageThread.start();
            }
        } else if (cmd.toLowerCase().equals("disconnect")) {
            CivilEngineering.incomingMessageThread.interrupt();
        }
    }


}
