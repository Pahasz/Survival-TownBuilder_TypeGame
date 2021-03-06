package com.mygdx.game.component.buildings;

import com.mygdx.game.component.Component;
import com.mygdx.game.entity.Entity;

public class Building extends Component {
	protected int buildingType;

	/**
	 * Creates a generic building Component.
	 * @param owner The owner of this Component.
	 * @param name The name of this building.
	 * @param type The type of this Component.
	 * @param active If this script is active or not.
	 * @param buildingType The Building type.
	 */
	public Building(Entity owner, String name, int type, boolean active) {
		super(owner, name, type, active);
		
		this.buildingType = type;
	}
	
	/**
	 * Creates a simplified generic building. The building name will be "Building".
	 * @param owner The owner of this Component.
	 * @param buildingType The Building type.
	 */
	public Building(Entity owner, int buildingType){
		this(owner, "Building", buildingType, false);
	}
	
	public int getBuildingType(){
		return this.buildingType;
	}

}
