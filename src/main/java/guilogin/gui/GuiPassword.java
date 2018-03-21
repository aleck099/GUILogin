package guilogin.gui;

import guilogin.GUILogin;
import guilogin.network.LoginMessage;
import guilogin.network.packets.ClientLoginPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.io.IOException;

public class GuiPassword extends GuiScreen {

	private GuiScreen parent;
	private String extraInfo;

	/*TODO 改成密码Field*/
	private GuiTextField psField;
	private int BUTTON_ID = 0;

	public GuiPassword(GuiScreen parent, String info) {
		this.parent = parent;
		this.extraInfo = info;
	}

	@Override
	public void initGui() {
		psField = new GuiTextField(0, fontRenderer, (width / 2) - 100, (int)(height * 0.4), 200, 50);
		psField.setMaxStringLength(128);
		psField.setFocused(true);
		psField.setCanLoseFocus(false);

		buttonList.add(new GuiButton(BUTTON_ID, width / 2 - 20, (int)(height * 0.7), "确定"));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == BUTTON_ID) {
			/*
			关掉Gui，发送登录信息
			 */
			String passwd = psField.getText();
			Minecraft.getMinecraft().displayGuiScreen(parent);
			LoginMessage msg = new LoginMessage();
			msg.setPacket(new ClientLoginPacket(passwd));
			GUILogin.netWrapper.sendToServer(new LoginMessage());
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		psField.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);

		/*
		绘制服务器的额外信息
		比如 密码错误 输入密码 注册 等等
		 */
		drawCenteredString(fontRenderer, extraInfo, (int)(width * 0.5), (int)(height * 0.1), 0xffff00);
	}
}
