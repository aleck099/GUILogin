package guilogin.commands;

import guilogin.GUILogin;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

public class SubCommandSync extends SubCommandBase {
	@Override
	public String getName() {
		return "sync";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		GUILogin.instance.accountMgr.writeToFile();
		sender.sendMessage(new TextComponentTranslation("subcommand.sync.success"));
	}
}
