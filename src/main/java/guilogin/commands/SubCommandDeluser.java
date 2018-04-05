package guilogin.commands;

import guilogin.GUILogin;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class SubCommandDeluser extends SubCommandBase {
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length < 1)
			return Collections.emptyList();
		return GUILogin.instance.accountMgr.getNamesMatches(args[0]);
	}

	@Override
	public String getName() {
		return "deluser";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException("subcommand.deluser.usage");

		if (GUILogin.instance.accountMgr.deleteAccount(args[0]))
			sender.sendMessage(new TextComponentTranslation("subcommand.deluser.success", args[0]));
		else
			sender.sendMessage(new TextComponentTranslation("subcommand.deluser.notfound", args[0]));
	}
}
