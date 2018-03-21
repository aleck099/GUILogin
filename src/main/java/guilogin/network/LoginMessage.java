package guilogin.network;

import guilogin.network.packets.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.io.*;

public class LoginMessage implements IMessage {

	private AbstractPacket packet;

	public LoginMessage(AbstractPacket packet) {
		this.packet = packet;
	}

	public LoginMessage() {
	}

	public AbstractPacket getPacket() {
		return packet;
	}

	public void setPacket(AbstractPacket pa) {
		this.packet = pa;
	}

	@Override
	public void fromBytes(ByteBuf rawBuf) {
		int len = rawBuf.readableBytes();
		byte[] buf = new byte[len];
		rawBuf.readBytes(buf);

		ObjectInputStream iStream;
		try {
			iStream = new ObjectInputStream(new ByteArrayInputStream(buf));
			Object o = iStream.readObject();
			if (o instanceof AbstractPacket)
				packet = (AbstractPacket) o;
			iStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		try {
			ObjectOutputStream os = new ObjectOutputStream(oStream);
			os.writeObject(packet);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		buf.writeBytes(oStream.toByteArray());
	}
}
