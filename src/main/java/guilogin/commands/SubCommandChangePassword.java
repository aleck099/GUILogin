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

public class SubCommandChangePassword extends SubCommandBase {
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)
			return GUILogin.instance.accountMgr.getNamesMatches(args[0]);
		else
			return Collections.emptyList();
	}

	@Override
	public String getName() {
		return "changepassword";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 2)
			throw new WrongUsageException("subcommand.chpw.usage");

		String name = args[0], passwd = args[1];
		sender.sendMessage(new TextComponentTranslation(GUILogin.instance.accountMgr.resetPasswd(name, passwd) ? "subcommand.chpw.success" : "subcommand.chpw.failure"));
	}
}
