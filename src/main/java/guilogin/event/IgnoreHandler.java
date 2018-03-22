package guilogin.event;

import guilogin.GUILogin;
import guilogin.db.PlayerInfo;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IgnoreHandler {

/*
	private void ignore(PlayerEvent e) {
		if (FAQ.instance.notLoggedins.containsKey(e.getEntityPlayer().getName())) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerInteract(PlayerInteractEvent e) {
		ignore(e);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerAttack(AttackEntityEvent e) {
		ignore(e);
		if (e.getTarget() instanceof EntityPlayerMP && FAQ.instance.notLoggedins.containsKey(e.getTarget().getName()))
			e.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerFillBucket(FillBucketEvent e) {
		ignore(e);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerItemPickup(EntityItemPickupEvent e) {
		ignore(e);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerXPPickup(PlayerPickupXpEvent event) {
		ignore(event);
	}

	public void playerUseHoe(UseHoeEvent event) {
		ignore(event);
	}
	*/

	// =========================================================================================================================
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerEvent(PlayerEvent event) {
		if (!event.isCancelable())
			return;

		if (event.getEntityPlayer() instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntityPlayer();
			if (GUILogin.instance.notLoggedins.containsKey(player.getName())) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerHurt(LivingHurtEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof EntityPlayerMP && GUILogin.instance.notLoggedins.containsKey(entity.getName()))
			event.setCanceled(true);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void playerChat(ServerChatEvent event) {
		if (!(event.getPlayer() instanceof FakePlayer) && GUILogin.instance.notLoggedins.containsKey(event.getUsername())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void DropItems(ItemTossEvent event) {
		if (!(event.getPlayer() instanceof FakePlayer) && GUILogin.instance.notLoggedins.containsKey(event.getPlayer().getName())) {
			ItemStack itemStack = event.getEntityItem().getItem();
			event.setCanceled(true);
			event.getPlayer().inventory.addItemStackToInventory(itemStack);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void EntityEvents(BlockEvent.PlaceEvent event) {
		if (GUILogin.instance.notLoggedins.containsKey(event.getPlayer().getName())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void EntityEvents(BlockEvent.BreakEvent event) {
		if (GUILogin.instance.notLoggedins.containsKey(event.getPlayer().getName())) {
			event.setCanceled(true);
		}
	}


	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void PlayerMovement(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP && (GUILogin.instance.notLoggedins.containsKey(event.getEntity().getName()))) {
			EntityPlayerMP player = (EntityPlayerMP) event.getEntity();

			PlayerInfo info = GUILogin.instance.notLoggedins.get(player.getName());
			if (info == null)
				return;
			/*
			玩家移动了？
			 */
			if (player.posX != info.X || player.posY != info.Y || player.posZ != info.Z)
				player.setPositionAndUpdate(info.X, info.Y, info.Z);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onCommand(CommandEvent event) {
		ICommandSender sender = event.getSender();
		if (sender instanceof EntityPlayerMP  && (GUILogin.instance.notLoggedins.containsKey(sender.getName()))) {
			event.setCanceled(true);
		}
	}
}
