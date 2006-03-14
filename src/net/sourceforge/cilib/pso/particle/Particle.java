/*
 * Particle.java
 *
 * Created on January 15, 2003, 8:27 PM
 *
 *
 * Copyright (C) 2003 - 2006 
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science 
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package net.sourceforge.cilib.pso.particle;

import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.problem.Fitness;
import net.sourceforge.cilib.pso.positionupdatestrategies.MemoryNeighbourhoodBestUpdateStrategy;
import net.sourceforge.cilib.pso.positionupdatestrategies.NeighbourhoodBestUpdateStrategy;
import net.sourceforge.cilib.pso.positionupdatestrategies.PositionUpdateStrategy;
import net.sourceforge.cilib.pso.positionupdatestrategies.StandardPositionUpdateStrategy;
import net.sourceforge.cilib.pso.velocityupdatestrategies.StandardVelocityUpdate;
import net.sourceforge.cilib.pso.velocityupdatestrategies.VelocityUpdateStrategy;
import net.sourceforge.cilib.type.types.Type;

/**
 *
 * @author Edwin Peer
 * @author Gary Pampara
 */
public abstract class Particle implements Entity {
	
    public static byte _ciclops_exclude_neighbourhoodBest = 1;
    public static byte _ciclops_exclude_fitness = 1;
    
    protected NeighbourhoodBestUpdateStrategy neighbourhoodBestUpdateStrategy;
    protected PositionUpdateStrategy positionUpdateStrategy;
    protected VelocityUpdateStrategy velocityUpdateStrategy;
    
    /**
     * 
     *
     */
	public Particle() {
		neighbourhoodBestUpdateStrategy = new MemoryNeighbourhoodBestUpdateStrategy();
		positionUpdateStrategy = new StandardPositionUpdateStrategy();
		velocityUpdateStrategy = new StandardVelocityUpdate();
	}

	/**
	 * 
	 * @return
	 */
    public abstract Particle clone();
    
    /**
     * 
     * @return
     */
    public abstract String getId();
    
    /**
     * 
     */
    public abstract void setId(String id);
    
    /**
     * 
     * @param fitness
     */
    public abstract void setFitness(Fitness fitness);

    /**
     * 
     * @return
     */
    public abstract Fitness getFitness();
    
    /**
     * 
     * @return
     */
    public abstract Fitness getBestFitness();
    
    /**
     * 
     * @return
     */
    public abstract int getDimension();
   
    /**
     * Get the position of the <tt>Particle</tt>.
     * @return A <tt>Type</tt> representing the <tt>Particle</tt>'s position.
     */
    public abstract Type getPosition();
    
    /**
     * Get the best position of the <tt>Particle</tt>.
     * @return A <tt>Type</tt> representng the <tt>Particle</tt>'s best position.
     */
    public abstract Type getBestPosition();
    
    /**
     * Get the velocity representation of the <tt>Particle</tt>.
     * @return A <tt>Type</tt> representing the <tt>Particle</tt>'s velocity.
     */
    public abstract Type getVelocity();

    /**
     * 
     * @param particle
     */
    public abstract void setNeighbourhoodBest(Particle particle);
    
    /**
     * Get the current <tt>Particle</tt>'s neighbourhood best.
     * @return The neighbourhood best of the <tt>Particle</tt>
     */
    public abstract Particle getNeighbourhoodBest();

    
    /**
     * Update the position of the <tt>Particle</tt>.
     */
    public abstract void updatePosition();
    
    /**
     * Update the velocity based on the provided <tt>VelocityUpdateStrategy</tt>.
     * @param vu The <tt>VelocityUpdateStrategy</tt> to use.
     */
    public abstract void updateVelocity();
    
    /**
     * 
     * @param decorator
     * @return
     */
    public abstract Particle getDecorator(Class decorator);
       
    /**
     * Get the social best fitness for the particle based on the currently employed
     * <code>NeighbourhoodBestUpdateStrategy</code>
     * 
     * @see net.sourceforge.cilib.pso.positionupdatestrategies.NeighbourhoodBestUpdateStrategy#getSocialBest()
     * @return A <code>Fitness</code> object representing the best social fitness of
     *         the current strategy
	 */
	public Fitness getSocialBestFitness() {
		return neighbourhoodBestUpdateStrategy.getSocialBestFitness(this);
	}
	
	/**
	 * Get the reference to the currently employed <code>NeighbourhoodBestUpdateStrategy</code>
	 * 
	 * @see net.sourceforge.cilib.pso.particle#getNeighbourhoodBestUpdateStrategy()
	 * @return A reference to the current <code>NeighbourhoodBestUpdateStrategy</code> object
	 */
	public NeighbourhoodBestUpdateStrategy getNeighbourhoodBestUpdateStrategy() {
		return this.neighbourhoodBestUpdateStrategy;
	}

	/**
	 * Set the <code>NeighbourhoodBestUpdateStrategy</code> to be used by the <code>Particle</code>
	 * 
	 * @see net.sourceforge.cilib.pso.particle#setNeighbourhoodBestUpdateStrategy(net.sourceforge.cilib.pso.positionupdatestrategies.NeighbourhoodBestUpdateStrategy)
	 * @param neighbourhoodBestUpdateStrategy The <code>NeighbourhoodBestUpdateStrategy</code> to be used
	 */
	public void setNeighbourhoodBestUpdateStrategy(NeighbourhoodBestUpdateStrategy neighbourhoodBestUpdateStrategy) {
		this.neighbourhoodBestUpdateStrategy = neighbourhoodBestUpdateStrategy;
	}

	/**
	 * Get the current <tt>PostionUpdateStrategy</tt> associated with this <tt>Particle</tt>.
	 * @return The currently associated <tt>PositionUpdateStrategy</tt>.
	 */
	public PositionUpdateStrategy getPositionUpdateStrategy() {
		return positionUpdateStrategy;
	}
	
	/**
	 * Set the <tt>PostionUpdateStrategy</tt> for the <tt>Particle</tt>.
	 * @param positionUpdateStrategy The <tt>PositionUpdateStrategy</tt> to use.
	 */
	public void setPositionUpdateStrategy(
			PositionUpdateStrategy positionUpdateStrategy) {
		this.positionUpdateStrategy = positionUpdateStrategy;
	}
	
	
	
	/**
	 * @return Returns the velocityUpdateStrategy.
	 */
	public VelocityUpdateStrategy getVelocityUpdateStrategy() {
		return velocityUpdateStrategy;
	}

	/**
	 * @param velocityUpdateStrategy The velocityUpdateStrategy to set.
	 */
	public void setVelocityUpdateStrategy(
			VelocityUpdateStrategy velocityUpdateStrategy) {
		this.velocityUpdateStrategy = velocityUpdateStrategy;
	}
	

	public int compareTo(Entity o) {
		throw new UnsupportedOperationException("This does not exist --- not supported");
	}
}
