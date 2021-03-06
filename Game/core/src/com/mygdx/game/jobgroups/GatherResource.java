package com.mygdx.game.jobgroups;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.component.AIJobController;
import com.mygdx.game.component.Component;
import com.mygdx.game.component.Inventory;
import com.mygdx.game.component.ResourceNode;
import com.mygdx.game.component.Town;
import com.mygdx.game.entity.Entity;
import com.mygdx.game.interfaces.SearchCriteria;
import com.mygdx.game.jobs.Gather;
import com.mygdx.game.jobs.MoveTo;
import com.mygdx.game.jobs.TransferItem;
import com.mygdx.game.utility.Constants;
import com.mygdx.game.utility.Grid;
import com.mygdx.game.utility.ItemBank;

public class GatherResource extends JobGroup{
	ResourceNode resource;
	Town town;

	public GatherResource(AIJobController controller, String name, int type, String resourceName, Town town) {
		super(controller, name, type);
		
		this.town = town;
		
		//Search for the resource we need.
		SearchCriteria neededResource = (e) -> {
			if(e.entityType != Constants.ENTITY_RESOURCE) return false; //First check if the entity is a resource.
			ResourceNode res = e.getComponent(ResourceNode.class); //Cache the ResourceNode script.
			return res.getResourceItem().name.equals(resourceName) && !res.isFull(); //Return if it's the right resource and has room.
		};
		
		//Find the closest Entity that fulfills the lambda expression above.
		Entity closestRes = Grid.getClosestEntityByCriteria(10, this.getController().getEntityOwner(), neededResource);
		
		//Create a lambda checking if the inventory of the entity is not full.
		SearchCriteria notFull = (e) -> ((Inventory)(e.getComponent(Inventory.class))).hasSpace();
		//Get the closest stockpile that's not full.
		Entity closestStockpile = this.findClosestAvailableComponent(town.getBuildingList(Constants.BUILDING_STOCKPILE), notFull);
		
		//If no tree OR stockpile was found, set finished to true and return.
		if(closestRes == null || closestStockpile == null){
			this.setFailed();
			return;
		}
		
		//Get the resource component from the resource
		this.resource = closestRes.getComponent(ResourceNode.class);
		
		//If we can't add a collector, set to finished and return. Otherwise, we have been added as a collector.
		//Think of this extra check as a way to make this thread safe.
		if(!resource.addCollector()){
			this.setFailed();
			return;
		}
		
		//Move to the resource
		this.addJob(new MoveTo("MoveToClosest"+resourceName, 0, this, 
				closestRes.transform.getWorldPosition(), resource.getEntityOwner()));
		
		//Collect the resource
		this.addJob(new Gather(this, resource));
		
		//Move to the closest stockpile to deposit it.
		this.addJob(new MoveTo("MovingToStockpile", 0, this, closestStockpile.transform.getWorldPosition()));
		
		//Transfer the item to the stockpile
		this.addJob(new TransferItem(this, this.controller.getEntityOwner().getComponent(Inventory.class), 
				closestStockpile.getComponent(Inventory.class), ItemBank.getItem(resource.getResourceItem().name), true));
	}
	
	public GatherResource(AIJobController controller, String resourceName, Town town){
		this(controller, "HarvestTreeJobGroup", 0, resourceName, town);
	}
	
	private Entity findClosestAvailableComponent(ArrayList<? extends Component> list, SearchCriteria criteria){
		Vector2 pos = this.controller.getEntityOwner().transform.getWorldPosition(); //Cache the position of the owner.
		float closestDist=9999999999999f;
		Entity closestEntity=null;
		
		for(Component comp : list){
			Entity ent = comp.getEntityOwner();
			Vector2 otherPos = ent.transform.getWorldPosition(); //Cache the local ent's location
			float distance = Math.abs(pos.x - otherPos.x) + Math.abs(pos.y - otherPos.y); //get the distance
			
			//First, a rough distance is checked. Then, we check if the node is full. If not, it is a potential 
			//node to harvest.
			if(distance < closestDist && criteria.withinCriteria(ent)){ 
				closestDist = distance; //Set the distance
				closestEntity = ent; //Set the closest entity
				continue;
			}
		}
		return closestEntity;
	}
}
