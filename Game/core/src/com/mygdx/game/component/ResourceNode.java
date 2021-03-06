package com.mygdx.game.component;

import com.mygdx.game.component.Inventory.InventoryItem;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.utility.Events;
import com.mygdx.game.utility.Item;
import com.mygdx.game.utility.events.EventClasses;

public class ResourceNode extends Component {
	private int maxAmount, currentAmount, maxNumCollectors, currNumCollectors;
	private int amountPerTick;
	private float timeToCollect;
	private Item itemToGather;
	
	/**
	 * A component that will hold information about a resource.
	 * @param owner The Entity owner of this component.
	 * @param name The name of this component.
	 * @param type The type of this component.
	 * @param active If this component is active or not.
	 * @param maxAmount The max amount of resources this resource node can have.
	 * @param maxNumCollectors The max number of collectors there can be on this node at one time.
	 * @param timeToCollect The time to collect this resource.
	 * @param amountPerTick The amount of resource per tick.
	 */
	public ResourceNode(Entity owner, String name, int type, boolean active,
			int maxAmount, int maxNumCollectors, float timeToCollect, int amountPerTick, Item itemToGather) {
		super(owner, name, type, false);
		
		this.maxAmount = maxAmount;
		this.currentAmount = this.maxAmount;
		this.maxNumCollectors = maxNumCollectors;
		this.currNumCollectors = 0;
		this.timeToCollect = timeToCollect;
		this.amountPerTick = amountPerTick;
		this.itemToGather = itemToGather;
	}
	

	/**
	 * A component that will hold information about a resource.
	 * @param owner The Entity owner of this component.
	 * @param maxAmount The max amount of resources this resource node can hold.
	 * @param maxNumCollectors The Max amount of collectors that can collect from this node simultaneously.
	 * @param timeToCollect The time for a collector to acquire a resource from this node.
	 * @param amountPerTick The amount collected when a collector has finished collecting.
	 */
	public ResourceNode(Entity owner, int maxAmount, int maxNumCollectors, float timeToCollect, int amountPerTick, Item itemToGather) {
		this(owner, "ResourceNode", 0, false, maxAmount, maxNumCollectors, timeToCollect, amountPerTick, itemToGather);
		
	}
	
	public Item getResourceItem(){
		return this.itemToGather;
	}
	
	public boolean addCollector(){
		if(!isFull()){
			this.currNumCollectors++;
			return true;
		}
		return false;
	}
	
	public void removeCollector(){
		this.currNumCollectors--;
	}
	
	/**
	 * Returns if the current ResourceNode is full (of collectors) or not.
	 * @return True if full, false if not.
	 */
	public boolean isFull(){
		return this.currNumCollectors >= this.maxNumCollectors;
	}
	
	public float getTimeToCollect(){
		return this.timeToCollect;
	}
	
	public int getAmountPerTick(){
		return this.amountPerTick;
	}
	
	/**
	 * Collects the resource and fires a "CollectResource" event;
	 * @param entity The Entity that is collecting this ResourceNoce
	 * @param amount The amount to be collected.
	 * @return An InventoryItem with the Item and quantity that was collected.
	 */
	public InventoryItem collectResourceItem(Entity entity, int amount){
		Events.notify(new EventClasses.CollectedResource(entity, this));
		
		return new InventoryItem(this.itemToGather, this.collectAmount(amount));
	}
	
	/**
	 * A private class to be called when collecting a resource. This will destroy the item if no resources are left.
	 * @param amount The amount to try and collect from this resource.
	 * @return An integer which is the amount that was collected.
	 */
	private int collectAmount(int amount){
		if(this.currentAmount - amount <= 0){
			this.owner.destroy();
			return this.currentAmount;
		}
		this.currentAmount-=amount;
		return amount;
	}
	
}
