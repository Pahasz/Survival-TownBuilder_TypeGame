package com.mygdx.game.jobgroups

import com.mygdx.game.component.AIJobController
import com.mygdx.game.component.CharacterNeeds
import com.mygdx.game.component.buildings.ResidenceScala
import com.mygdx.game.entity.Entity
import com.mygdx.game.jobs.MoveTo
import com.mygdx.game.jobs.SleepJob
import com.mygdx.game.component.character.AICharacterCompScala
import com.mygdx.game.component.Component

class SleepScala(controller : AIJobController, name : String, groupType : Int, home : ResidenceScala, needs : CharacterNeeds,
		threshold : Int, val callback : (AICharacterCompScala) => Unit) 
		extends JobGroup(controller, name, groupType) {
	
	def this(controller : AIJobController, home : ResidenceScala, needs : CharacterNeeds, threshold : Int, callback : (AICharacterCompScala) => Unit){
		this(controller, "SleepScala", 0, home, needs, threshold, callback);
	}

	this.addJob(new MoveTo(this, home.getEntityOwner()));
	this.addJob(new SleepJob(this, needs, threshold));
	
	override def destroy() = {
		this.callback((this.getController().getEntityOwner().getComponent(classOf[AICharacterCompScala]))) 
	}
		
}