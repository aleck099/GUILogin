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
				/*显示两次密码输入框？？？不可能！*/
				if (mc.currentScreen instanceof GuiPassword)
					((GuiPassword) mc.currentScreen).close();
				mc.displayGuiScreen(new GuiPassword(mc.currentScreen, ((ServerRequestLoginPacket) packet).extraInfo));
			});
		}
		return null;
	}
}
