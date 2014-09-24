/***
 * Excerpted from "Learn to Program with Minecraft Plugins, CanaryMod Edition",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/ahmine2 for more book information.
***/
package cowshooter;

import net.canarymod.api.entity.EntityType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.entity.living.animal.Cow;
import net.canarymod.api.world.position.Vector3D;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.ai.AIBase;

public class CowTask implements AIBase {
    
    private Cow cow;
    private boolean running;
    
    private boolean isStopped() {
      Location loc = cow.getLocation();
      Block b = cow.getWorld().getBlockAt((int)loc.getX(),
        (int)loc.getY()-2, (int)loc.getZ());
      return b.getType() != BlockType.Air;
    }
    
    public CowTask(Cow myCow) {
        cow = myCow;
        running = true;
    }
    
    /*
     * Checks if you are ready to begin executing.
     */
    public boolean shouldExecute() {
      return running;
    } 
    
    /* 
     * Checks if you should continue executing (for continuous tasks)
     */
    public boolean continueExecuting() {
      return running;
    }  
    
    /* 
     * Checks if you are Continuous, or One-Shot? 
     */
    public boolean isContinuous() {
      return true;
    }
     
    /* 
     * Callback to begin executing 
     */
    public void startExecuting() {
    }
    
    /* 
     * Callback on termination 
     */
    public void resetTask() {
      cow = null; // Free up for GC
    } 
    
    /* 
     * Callback to run.
     */
    public void updateTask() {
      if (isStopped()) { 
        Location loc = cow.getLocation();
        cow.setHealth(0);
        cow.kill();       
        cow.getWorld().makeExplosion(cow, 
          loc.getX(), loc.getY(), loc.getZ(), 
          1.0f, true);
        running = false;
        resetTask();
      } else {
        cow.setFireTicks(100);
        cow.setHealth((float)cow.getMaxHealth());
      }
    }
}
