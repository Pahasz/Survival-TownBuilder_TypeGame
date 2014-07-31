package com.mygdx.game.component.character

import com.mygdx.game.component.buildings.ResidenceScala
import com.mygdx.game.component.Component
import com.mygdx.game.entity.Entity

class CharacterCompScala(owner : Entity, name : String, characterType : Int, active : Boolean) 
	extends Component(owner, name, characterType, active){

	var home : ResidenceScala = _;
	var sleeping : Boolean = false;
	
	override def update(delta : Float) = {
		
	}
	
	def setHome(home : ResidenceScala){
		this.home = home;
		this.home.addToResidence(this);
	}
	
	def getHome() : ResidenceScala = {
		return this.home;
	}
	
	def setSleeping(sleeping : Boolean) = {
		this.sleeping = sleeping;
	}
	
	def isSleeping() : Boolean = {
		return this.sleeping;
	}
}