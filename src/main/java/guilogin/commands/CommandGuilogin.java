package guilogin.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class CommandGuilogin extends CommandBase {

	private Set<ISubCommand> subCommands;

	public CommandGuilogin() {
		subCommands = new HashSet<>();
	}

	public void registerSubCommand(ISubCommand command) {
		subCommands.add(command);
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length < 1)
			return Collections.emptyList();
		else if (args.length == 1) {
			// 子命令列表
			List<String> list = new ArrayList<>();
			subCommands.forEach(c -> {
				String name = c.getName();
				if (name.startsWith(args[0]))
					list.add(name);
			});
			return list;
		} else {
			// 通知子命令
			ISubCommand sc = null;
			String prefix = args[0];
			// 得到子命令
			for (ISubCommand command : subCommands) {
				if (command.getName().startsWith(prefix)) {
					sc = command;
					break;
				}
			}

			if (sc == null)
				return Collections.emptyList();

			return sc.getTabCompletions(server, sender, Arrays.copyOfRange(args, 1, args.length), targetPos);
		}
	}

	private ISubCommand getSubCommandByName(String name) {
		for (ISubCommand c : subCommands) {
			if (c.getName().equals(name))
				return c;
		}
		return null;
	}

	@Override
	public String getName() {
		return "guilogin";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.guilogin.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException("commands.guilogin.usage");

		try {
			getSubCommandByName(args[0]).execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
		} catch (NullPointerException e) {
			sender.sendMessage(new TextComponentTranslation("commands.guilogin.usage.nosuchcommand", args[0]));
		}
	}
}
