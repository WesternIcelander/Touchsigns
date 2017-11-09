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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TouchsignEvent {

	private final Block block;
	private final Player player;
	private final double clickX;
	private final double clickY;
	private final boolean rightClick;
	private final boolean cancelled;
	private final int line;

	TouchsignEvent(Block block, Player player, double clickX, double clickY, boolean rightClick, boolean cancelled) {
		this.block = block;
		this.player = player;
		this.clickX = clickX;
		this.clickY = clickY;
		this.rightClick = rightClick;
		this.cancelled = cancelled;
		int lineClicked;
		if (clickY < 0.280) {
			lineClicked = 0;
		} else if (clickY < 0.488) {
			lineClicked = 1;
		} else if (clickY < 0.696) {
			lineClicked = 2;
		} else {
			lineClicked = 3;
		}
		line = lineClicked;
	}

	/**
	 * Get the sign the player clicked on
	 *
	 * @return Block that the player clicked on.
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * Get the player that clicked on the sign.
	 *
	 * @return Player that clicked on the sign
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the X coordinate on the sign that the player clicked on. The leftmost
	 * part of the sign is 0, and the rightmost part of the sign is 1.
	 *
	 * @return
	 */
	public double getClickX() {
		return clickX;
	}

	/**
	 * Get the Y coordinate on the sign that the player clicked on. The top of
	 * the sign is 0, and the bottom of the sign is 1.
	 *
	 * @return
	 */
	public double getClickY() {
		return clickY;
	}
	
	/**
	 * Check to see if the player right clicked on the sign.
	 * @return true if the player right clicked, false if left clicked.
	 */
	public boolean isRightClick() {
		return rightClick;
	}

	/**
	 * Check to see if the original PlayerInteractEvent that caused this event
	 * was cancelled by another plugin.
	 *
	 * @return true if the PlayerInteractEvent causing this event was cancelled.
	 */
	public boolean wasCancelled() {
		return cancelled;
	}

	/**
	 * Get which line of the sign the player clicked on. The top line is 0, the
	 * bottom line is 3. (Reminder: All signs have 4 lines!)
	 *
	 * @return the line that the player clicked on.
	 */
	public int getLine() {
		return line;
	}
}
