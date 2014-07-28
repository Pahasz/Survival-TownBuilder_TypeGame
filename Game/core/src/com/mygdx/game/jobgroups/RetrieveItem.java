package com.mygdx.game.jobgroups;

import com.mygdx.game.component.AIJobController;
import com.mygdx.game.component.InventoryScala;
import com.mygdx.game.jobs.Job;
import com.mygdx.game.jobs.MoveTo;
import com.mygdx.game.jobs.TransferItemScala;
import com.mygdx.game.utility.Item;

public class RetrieveItem extends JobGroup {

	public RetrieveItem(AIJobController controller, String name, int type, Item item, int amount, InventoryScala<Item> inventory) {
		super(controller, name, type);
		
		Job moveTo = new MoveTo(this, inventory.getEntityOwner().transform.getWorldPosition());
		this.addJob(moveTo);
		
		Job getItem = new TransferItemScala(this, "Retrieve", 0, inventory, this.controller.getEntityOwner().getComponent(InventoryScala.class), 
				item, amount, false);
		
		this.addJob(getItem);
	}
	
	public RetrieveItem(AIJobController controller, Item item, int amount, InventoryScala<Item> inventory) {
		this(controller, "RetrieveItem", 0, item, amount, inventory);
		
	}

}
