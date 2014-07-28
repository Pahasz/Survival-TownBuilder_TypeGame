package com.mygdx.game.component

import com.mygdx.game.entity.Entity
import scala.collection.mutable.ArrayBuffer
import com.mygdx.game.component.character.AICharacterCompScala
import com.mygdx.game.utility.Timer
import com.mygdx.game.component.buildings.Building
import com.mygdx.game.component.character.AICharacterComp
import com.mygdx.game.component.character.AICharacterCompScala
import scala.collection.mutable.HashMap
import java.util.ArrayList

class TownScala(owner : Entity, name : String, compType : Int, active : Boolean) 
	extends Component(owner, name, compType, active){

	private var maxPopulation : Int = 0;
	private var citizenList : ArrayBuffer[AICharacterCompScala] = new ArrayBuffer(5);
	
	private val sortDemandsTimer : Timer = new Timer(10);
	
	private var buildingMap : HashMap[Int, ArrayBuffer[Building]] = new HashMap();
	//private var supplyMap : HashMap[String, Supply] = new HashMap();
	//this.itemStatsList = new ArrayList<ItemStats>(); //Init the ItemStats list.
	
	/*
	 for(Item item : ItemBank.getItemsAsList()){
			ItemStats stats = new ItemStats(item); //Create the ItemStats
			this.itemStatsList.add(stats); //Add the item stats
			//Link the supply in the item stats to the supplyMap
			this.supplyMap.put(item.name, this.itemStatsList.get(this.itemStatsList.size()-1).supply); 
		}
	 */
	
	@Override
	override def update(delta : Float) = {
		/*
		if(this.sortDemandsTimer.done()){
			//First, update all demands.
			for(stats : ItemStats <- this.itemStatsList)
				stats.update(delta);
			
			//Sort the demands based on the demand.
			Collections.sort(this.itemStatsList, new CompareDemand());
			
			this.sortDemandsTimer.restart(); //Restart the timer.
		}
		*/
	}
	
	/**
	 * Sets the maximum population of the Town.
	 * @param population The Maximum population this town can hold.
	 */
	def setMaxPopulation(population : Int){
		this.maxPopulation = population;
	}
	
	/**
	 * Gets the maximum population of this Town.
	 * @return An integer which is the maximum population of the Town.
	 */
	def getMaxPopulation() : Int = {
		return this.maxPopulation;
	}
	
	/**
	 * Gets the current population of the Town.
	 * @return An integer which is the current population of the Town.
	 */
	def getCurrPopulation() : Int = {
		return this.citizenList.size;
	}
	
	/**
	 * Adds a citizen to the town's population if there is room. This will also assign the citizen worker the Town object.
	 * @param citizen The Citizen to make part of this town.
	 * @return True if the citizen was able to be added, false otherwise.
	 */
	def addToPopulation(citizen : AICharacterCompScala) : Boolean = {
		if(this.citizenList.size < this.maxPopulation){
			citizen.assignTownOwner(this);
			this.citizenList += citizen;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes a citizen from the population. This will also set the citizen's Town reference to null.
	 * @param citizen The Citizen to remove from the town.
	 */
	def removeFromPopulation(citizen : AICharacterCompScala){
		citizen.assignTownOwner(null);
		this.citizenList -= citizen;
	}
	
	/**
	 * Adds a building to the building list of this Town.
	 * @param buildingComponent The BuildingComponent to add. This represents the building entity.
	 */
	def addBuilding(buildingComponent : Building) = {
		//If this building type does not exist.
		if(!this.buildingMap.contains(buildingComponent.getBuildingType())){
			val list : ArrayBuffer[Building] = new ArrayBuffer[Building]();
			this.buildingMap.put(buildingComponent.getBuildingType(), list);
		}
		
		this.buildingMap.get(buildingComponent.getBuildingType()).asInstanceOf[ArrayBuffer[Building]] += buildingComponent;
	}
	
	/**
	 * Gets a list of buildings of the requested type from the building list of the Town.
	 * @param type An integer which is the type of building to retrieve.
	 * @return An ArrayList of BuildingComponents which are the type requested. If the type was not found, an empty ArrayList is returned.
	 */
	def getBuildingList(buildingType : Int):ArrayList[Building] = {
		//If the key doesn't exist, return an empty list.
		if(!this.buildingMap.contains(buildingType)) return new ArrayList[Building](0);
		return this.buildingMap.get(buildingType).asInstanceOf[ArrayList[Building]]; //Otherwise, return the list found.
	}
	
	/**
	 * Gets a list of buildings of the requested type from the building list of the Town.
	 * @param type An integer which is the type of building to retrieve.
	 * @return An ArrayList of BuildingComponents which are the type requested. If the type was not found, an empty ArrayList is returned.
	 */
	private def getBuildingListAsBuffer(buildingType : Int):ArrayBuffer[Building] = {
		//If the key doesn't exist, return an empty list.
		if(!this.buildingMap.contains(buildingType)) return new ArrayBuffer[Building](0);
		return this.buildingMap.get(buildingType).asInstanceOf[ArrayBuffer[Building]]; //Otherwise, return the list found.
	}
	
	
	def getBuildingByCriteria(buildingType : Int, criteria : (Building) => Boolean):Building = {
		for(building : Building <- this.getBuildingListAsBuffer(buildingType)){
			if(criteria(building))
				return building;
		}
		
		return null;
	}
}