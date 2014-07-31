package com.mygdx.game.jobgroups

import java.util.ArrayList
import scala.collection.JavaConversions._
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.component.AIJobController
import com.mygdx.game.component.Component
import com.mygdx.game.component.InventoryScala
import com.mygdx.game.component.ResourceNode
import com.mygdx.game.component.TownScala
import com.mygdx.game.entity.Entity
import com.mygdx.game.jobs.MoveTo
import com.mygdx.game.jobs.TransferItemScala
import com.mygdx.game.utility.Constants
import com.mygdx.game.utility.GridScala
import com.mygdx.game.utility.Item
import com.mygdx.game.utility.ItemBank
import com.mygdx.game.jobs.GatherScala

class GatherResourceScala(controller : AIJobController, name : String, groupType : Int, val resourceName : String, val town : TownScala) 
	extends JobGroup(controller, name, groupType){
	
	def this(controller : AIJobController, resourceName : String, town : TownScala){
		this(controller, "GatherResource "+resourceName, 0, resourceName, town);
	}
	
	val neededResource : Entity => Boolean = (e : Entity) => {
		if(e.entityType != Constants.ENTITY_RESOURCE)
			false //First check if the entity is a resource.
		else{
			val res : ResourceNode = e.getComponent(classOf[ResourceNode]); //Cache the ResourceNode script.
			println();
			(res.getResourceItem().name == resourceName && !res.isFull()); //Return if it's the right resource and has room.
		}
	}
	
	Init();
	
	def Init() : Unit = {
		//Find the closest Entity that fulfills the lambda expression above.
		val closestRes = GridScala.getClosestEntityByCriteria(10, this.getController().getEntityOwner(), neededResource);
		
		//Create a lambda checking if the inventory of the entity is not full.
		val notFull = (ent : Entity) => ((ent.getComponent(classOf[InventoryScala[Item]]))).hasSpace();
		
		//Get the closest stockpile that's not full.
		val closestStockpile = this.findClosestAvailableComponent(town.getBuildingList(Constants.BUILDING_STOCKPILE), notFull);
		
		//If no tree OR stockpile was found, set finished to true and return.
		if(closestRes == null || closestStockpile == null){
			this.setFailed();
			return;
		}
		
		//Get the resource component from the resource
		val resource : ResourceNode = closestRes.getComponent(classOf[ResourceNode]);
		
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
		this.addJob(new GatherScala(this, resource));
		
		//Move to the closest stockpile to deposit it.
		this.addJob(new MoveTo("MovingToStockpile", 0, this, closestStockpile.transform.getWorldPosition()));
		
		//Transfer the item to the stockpile
		this.addJob(new TransferItemScala(this, this.controller.getEntityOwner().getComponent(classOf[InventoryScala[Item]]), 
				closestStockpile.getComponent(classOf[InventoryScala[Item]]), ItemBank.getItem(resource.getResourceItem().name), 99999999, true));
	}
	
	private def findClosestAvailableComponent[T <: Component : ClassManifest](list : ArrayList[T], criteria : (Entity) => Boolean) : Entity = {
		val pos = this.controller.getEntityOwner().transform.getWorldPosition(); //Cache the position of the owner.
		var closestDist : Float = 9999999999999f;
		var closestEntity : Entity = null;
		
		for(comp <- list){
			val ent = comp.getEntityOwner();
			val otherPos = ent.transform.getWorldPosition(); //Cache the local ent's location
			val distance : Float = Math.abs(pos.x - otherPos.x) + Math.abs(pos.y - otherPos.y); //get the distance
			
			//First, a rough distance is checked. Then, we check if the node is full. If not, it is a potential 
			//node to harvest.
			if(distance < closestDist && criteria(ent)){ 
				closestDist = distance; //Set the distance
				closestEntity = ent; //Set the closest entity
			}
		}
		return closestEntity;
	}

}