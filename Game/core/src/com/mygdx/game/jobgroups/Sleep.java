package com.mygdx.game.jobgroups;

import com.mygdx.game.component.AIJobController;
import com.mygdx.game.component.CharacterNeeds;
import com.mygdx.game.component.buildings.ResidenceScala;
import com.mygdx.game.component.character.AICharacterComp;
import com.mygdx.game.interfaces.AICharacterCompCallback;
import com.mygdx.game.jobs.MoveTo;
import com.mygdx.game.jobs.SleepJob;

public class Sleep extends JobGroup {
	AICharacterCompCallback callback;

	public Sleep(AIJobController controller, String name, int type,
			ResidenceScala home, CharacterNeeds needs, int threshold,
			AICharacterCompCallback callback) {
		super(controller, name, type);

		this.addJob(new MoveTo(this, home.getEntityOwner()));
		this.addJob(new SleepJob(this, needs, threshold));
		
		this.callback = callback;
	}

	public Sleep(AIJobController controller, ResidenceScala home,
			CharacterNeeds needs, int threshold, AICharacterCompCallback callback) {
		this(controller, "Sleeping", 0, home, needs, threshold, callback);
	}
	
	@Override
	public void destroy(){
		this.callback.callback(this.getController().getEntityOwner().getComponent(AICharacterComp.class));
	}

}
