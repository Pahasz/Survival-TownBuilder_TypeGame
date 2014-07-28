package com.mygdx.game.utility

import com.mygdx.game.entity.Entity
import java.util.ArrayList
import scala.collection.mutable.ArrayBuffer
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.component.Transform
import com.badlogic.gdx.math.MathUtils
import collection.JavaConversions._

object GridScala {
	private var grid : Array[Array[GridSquareScala]] = _;
	private var squareSize : Int = _;
	private var gridPadding : Int = _;
	
	def initGrid(width : Int, height : Int, padding : Int, sizeOfSquare : Int) = {
		grid = Array.tabulate(3,3){ (x,y) => new GridSquareScala((x-padding)*squareSize,(y-padding)*squareSize, squareSize, squareSize) }
	}
		
	def addEntityToGrid(e : Entity) : GridSquareScala = {
		val transform = e.transform; //Cache the transform
		val xIndex = ((transform.getWorldPosition().x/squareSize) + gridPadding).toInt;
		val yIndex = ((transform.getWorldPosition().y/squareSize) + gridPadding).toInt;
		return this.grid(xIndex)(yIndex).addEntity(e);
	}
	
	def removeEntityFromGrid(e : Entity) = {
		val transform = e.transform; //Cache the transform
		val xIndex = ((transform.getWorldPosition().x/squareSize) + gridPadding).toInt;
		val yIndex = ((transform.getWorldPosition().y/squareSize) + gridPadding).toInt;
		this.grid(xIndex)(yIndex).removeEntity(e);
	}
	
	def updateGridSquare(currSquare : GridSquareScala, entity : Entity) : GridSquareScala = {
		val pos = entity.transform.getWorldPosition();
		val x = (pos.x/squareSize + gridPadding).toInt;
		val y = (pos.y/squareSize + gridPadding).toInt;
		if(this.grid(x)(y).position.x != currSquare.position.x || grid(x)(y).position.y != currSquare.position.y)
			return grid(x)(y);
		return currSquare;
	}
	
		/**
	 * Returns a list of all Entities within the radius specified from the position of the entity passed in.
	 * @param radius The radius of the search (in terms of grid squares).
	 * @param entity The Entity to search from. This will only be used for getting the position of the Entity.
	 * @param criteria The Criteria to satisfy.
	 * @return A list of Entities if Entities were found, an empty ArrayList otherwise.
	 */
	def getEntitiesInRadius(radius : Int, entity : Entity, criteria:(Entity) => Boolean) : ArrayList[Entity] = {
		return getEntitiesInRadiusByCriteria(radius, entity.transform.getWorldPosition(), criteria);
	}
	
	/**
	 * Returns a list of all Entities within the radius specified from the position passed in.
	 * @param radius The radius of the search (in terms of grid squares).
	 * @param entity The Entity to search from. This will only be used for getting the position of the Entity.
	 * @param criteria The Criteria to satisfy.
	 * @return A list of Entities if Entities were found, an empty ArrayList otherwise.
	 */
	def getEntitiesInRadiusByCriteria(radius :Int, position : Vector2, criteria:(Entity) => Boolean) : ArrayList[Entity] = {
		val range = new SearchRange(radius, position);
		
		var list = new ArrayList[Entity](5);
		var x = 0;
		var y = 0;
		
		for(x <- range.startX to range.endX){ //For each X
			for(y <- range.startY to range.endY){ //For each Y
				val entList = grid(x)(y).getEntityListAsBuffer(); //Cache the list.
				for(gridEnt <- entList) //For each Entity in the list from the GridSquare
					if(criteria(gridEnt)) //If the Entity name matches the required name.
						list.add(gridEnt); //Add it to the list.
			}
		}
		return list;
	}
	
	/**
 * Returns the closest Entity within a radius of the Entity passed in. This Entity must satisfy the SearchCriteria passed in.
 * This will use breadth-first search style (but not the actual technique) to search for the closest valid Entity.
 * @param radius The maximum radius to find an Entity.
 * @param entity The Entity we are searching from. This is used to get the starting position of the search.
 * @param criteria The SearchCriteria that must be satisfied.
 * @return The closest valid Entity if found, null otherwise.
 */
def getClosestEntityByCriteria(radius : Int, entity : Entity, criteria:(Entity) => Boolean) : Entity = {
	return getClosestEntityByCriteria(radius, entity.transform.getWorldPosition(), criteria);
}

/**
 * Returns the closest Entity within a radius of the Entity passed in. This Entity must satisfy the SearchCriteria passed in.
 * This will use breadth-first search style (but not the actual technique) to search for the closest valid Entity.
 * @param radius The maximum radius to find an Entity.
 * @param position The starting position of the search.
 * @param criteria The SearchCriteria that must be satisfied.
 * @return The closest valid Entity if found, null otherwise.
 */
def getClosestEntityByCriteria(radius : Int, position : Vector2, criteria:(Entity) => Boolean) : Entity = {
	var currRadius = 0;
	var range :SearchRange = null;
	
	var list = new ArrayList[Entity]();
	var closestEntity : Entity = null;
	
	while(closestEntity == null && currRadius <= radius){
		range = new SearchRange(currRadius, position);
		
		for(x <- range.startX to range.endX){ //For each X
			for(y <- range.startY to range.endY){ //For each Y
				if((y == range.startY || y == range.endY) || (x == range.startX || x == range.endX)){
					list.addAll(grid(x)(y).getEntityList());
				}
			}
		}
		
		closestEntity = closestEntityFunc(criteria, list, range);
		list.clear();
		currRadius+=1;
	}
	
	return closestEntity;
}

private def closestEntityFunc(criteria:(Entity) => Boolean, list : ArrayList[Entity], range : SearchRange) : Entity = {
	var closestEntity : Entity = null;
	var closestDist : Float = 9999999999999f;
	
	for(ent <- list){
		if(criteria(ent)){
			val otherPos = ent.transform.getWorldPosition(); //Cache the local ent's location
			val distance = Math.abs(range.entPos.x - otherPos.x) + Math.abs(range.entPos.y - otherPos.y).toFloat; //get the distance
			if(distance < closestDist){ //If the computed distance is less than the last closest
				closestDist = distance; //Set the distance
				closestEntity = ent; //Set the closest entity
			}
		}
	}
	
	return closestEntity;
}
	
	private class SearchRange(radius : Int, position : Vector2){
		var startX, startY, endX, endY : Int = _;
		var entPos : Vector2 = _;
		
		this.entPos = position;
		this.startX = ((this.entPos.x/squareSize + gridPadding) - radius).toInt;
		this.startY = ((this.entPos.y/squareSize + gridPadding) - radius).toInt;
		this.endX = ((this.entPos.x/squareSize + gridPadding) + radius).toInt;
		this.endY = ((this.entPos.y/squareSize + gridPadding) + radius).toInt;
		
		this.startX = MathUtils.clamp(startX, 0, grid.length-1);
		this.startY = MathUtils.clamp(startY, 0, grid.length-1);
		this.endX = MathUtils.clamp(endX, 0, grid.length-1);
		this.endY = MathUtils.clamp(endY, 0, grid.length-1);
	}
	
	class GridSquareScala(x : Int, y : Int, val width : Int, val height : Int) {
		val position = new Vector2(x,y);
		
		private def entityList : ArrayBuffer[Entity] = new ArrayBuffer[Entity](5);
		
		def addEntity(e : Entity) : GridSquareScala = {
			entityList += e;
			return this;
		}
		
		def removeEntity(e : Entity):Boolean = {
			val index = this.entityList.indexOf(e);
			val entToReturn = this.entityList.remove(index);
			if(entToReturn == null)
				return false;
			return true;
		}
		
		def getEntityList() : ArrayList[Entity] = {
			return this.entityList.asInstanceOf[ArrayList[Entity]];
		}
		
		def getEntityListAsBuffer() : ArrayBuffer[Entity] = {
			return this.entityList;
		}
	}
}