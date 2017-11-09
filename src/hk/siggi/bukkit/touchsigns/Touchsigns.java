/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Sigurdur Helgason [a.k.a. SiggiJG, Siggi-JG, Siggi88, Siggi88088]
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package hk.siggi.bukkit.touchsigns;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Touchsigns extends JavaPlugin implements Listener {

	private ArrayList<TouchsignListener> listeners = new ArrayList<>();
	private HashMap<Plugin, ArrayList<TouchsignListener>> pluginMap = new HashMap<>();

	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void interactWithSign(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Material material = block.getType();
		if (material == Material.WALL_SIGN) {
			boolean rightClick = false;
			Action action = event.getAction();
			if (action == Action.LEFT_CLICK_BLOCK) {
			} else if (action == Action.RIGHT_CLICK_BLOCK) {
				rightClick = true;
			} else {
				return;
			}
			BlockState state = block.getState();
			if (state instanceof Sign) {
				Sign sign = (Sign) state;
				Player player = event.getPlayer();
				org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
				BlockFace facing = signData.getFacing();
				if (facing != event.getBlockFace()) {
					return;
				}
				Location location = player.getLocation();

				boolean cancelled = event.isCancelled();
				boolean sneaking = player.isSneaking();

				double playerX = location.getX();
				double playerY = location.getY() + (sneaking ? 0.54 : 0.62); // add eyes position
				double playerZ = location.getZ();

				double yaw = location.getYaw() * (Math.PI / 180);
				double pitch = location.getPitch() * (Math.PI / 180);

				double normalX = (float) (-Math.sin(yaw) * Math.cos(pitch));
				double normalY = (float) (-Math.sin(pitch));
				double normalZ = (float) (Math.cos(yaw) * Math.cos(pitch));

				if (facing == BlockFace.NORTH || facing == BlockFace.SOUTH) {
					// Z coordinate does not change
					// X coordinate -> X pos on sign (NORTH must flip)
					// Y coordinate -> Y pos on sign
					double signZ;
					if (facing == BlockFace.NORTH) {
						signZ = ((double) block.getZ()) + 1.0 - (1.0 / 8.0);
					} else {
						signZ = ((double) block.getZ()) + (1.0 / 8.0);
					}
					// double multiplier = diffZ / nz;
					double multiplier = (signZ - playerZ) / normalZ;

					double worldX = playerX + (normalX * multiplier);
					double worldY = playerY + (normalY * multiplier);

					double clickX = worldX;
					double clickY = worldY;
					clickX -= Math.floor(clickX);
					clickY -= Math.floor(clickY);
					if (facing == BlockFace.NORTH) {
						clickX = 1.0 - clickX;
					}
					click(block, player, clickX, clickY, rightClick, cancelled);
				} else {
					// X coordinate does not change
					// Z coordinate -> Z pos on sign (EAST must flip)
					// Y coordinate -> Y pos on sign
					double signX;
					if (facing == BlockFace.WEST) {
						signX = ((double) block.getX()) + 1.0 - (1.0 / 8.0);
					} else {
						signX = ((double) block.getX()) + (1.0 / 8.0);
					}
					// double multiplier = diffX / nx;
					double multiplier = (signX - playerX) / normalX;

					double worldZ = playerZ + (normalZ * multiplier);
					double worldY = playerY + (normalY * multiplier);

					double clickX = worldZ;
					double clickY = worldY;
					clickX -= Math.floor(clickX);
					clickY -= Math.floor(clickY);
					if (facing == BlockFace.EAST) {
						clickX = 1.0 - clickX;
					}
					click(block, player, clickX, clickY, rightClick, cancelled);
				}
			}
		}
	}

	private void click(Block block, Player player, double clickX, double clickY, boolean rightClick, boolean cancelled) {
		clickY = (1.0 - clickY - (3.5 / 16.0)) * 2.0;
		TouchsignEvent event = new TouchsignEvent(block, player, clickX, clickY, rightClick, cancelled);
		for (TouchsignListener listener : listeners) {
			listener.touchsign(event);
		}
	}

	/**
	 * Start listening for Touchsign events.
	 * @param listener The listener to add.
	 * @param plugin The plugin that the listener belongs to.
	 */
	public void addListener(TouchsignListener listener, Plugin plugin) {
		if (listeners.contains(listener)) {
			removeListener(listener);
		}
		listeners.add(listener);
		ArrayList<TouchsignListener> theListeners = pluginMap.get(plugin);
		if (theListeners == null) {
			pluginMap.put(plugin, theListeners = new ArrayList<>());
		}
		theListeners.add(listener);
	}

	/**
	 * Stop listening for Touchsign events.
	 * @param listener The listener to remove.
	 */
	public void removeListener(TouchsignListener listener) {
		listeners.remove(listener);
		for (Plugin plugin : pluginMap.keySet()) {
			ArrayList<TouchsignListener> theListeners = pluginMap.get(plugin);
			TouchsignListener listenerList[] = theListeners.toArray(new TouchsignListener[theListeners.size()]);
			for (TouchsignListener listenerInList : listenerList) {
				if (listener == listenerInList) {
					theListeners.remove(listenerInList);
				}
			}
			if (theListeners.isEmpty()) {
				pluginMap.remove(plugin);
			}
		}
	}

	/**
	 * Remove all listeners belonging to a plugin.
	 * @param plugin The plugin to remove all listeners of.
	 */
	public void removeAllListeners(Plugin plugin) {
		ArrayList<TouchsignListener> theListeners = pluginMap.remove(plugin);
		if (theListeners == null) {
			return;
		}
		for (TouchsignListener listener : theListeners) {
			listeners.remove(listener);
		}
	}

	@EventHandler
	public void pluginDisableEvent(PluginDisableEvent event) {
		Plugin plugin = event.getPlugin();
		removeAllListeners(plugin);
	}
}
