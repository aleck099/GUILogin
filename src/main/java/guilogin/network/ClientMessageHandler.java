package guilogin.network;

import guilogin.gui.GuiPassword;
import guilogin.network.packets.AbstractPacket;
import guilogin.network.packets.ServerRequestLoginPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientMessageHandler implements IMessageHandler<LoginMessage, IMessage> {
	@Override
	public IMessage onMessage(LoginMessage message, MessageContext ctx) {
		AbstractPacket packet = message.getPacket();
		if (packet instanceof ServerRequestLoginPacket) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft mc = Minecraft.getMinecraft();
				mc.displayGuiScreen(new GuiPassword(mc.currentScreen, ((ServerRequestLoginPacket) packet).extraInfo));
			});
		}
		return null;
	}
}
