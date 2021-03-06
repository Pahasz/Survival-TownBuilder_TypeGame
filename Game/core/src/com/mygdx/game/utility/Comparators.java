package com.mygdx.game.utility;

import java.util.Comparator;

import com.mygdx.game.component.Town.ItemStats;

public class Comparators {
	
	public static class CompareDemand implements Comparator<ItemStats> {
	    @Override
	    public int compare(ItemStats stats1, ItemStats stats2) {
	    	if(stats1.demand.demand > stats2.demand.demand) return 1;
	    	else return -1;
	    }
	    
	}

}
