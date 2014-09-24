/***
 * Excerpted from "Learn to Program with Minecraft Plugins, CanaryMod Edition",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/ahmine2 for more book information.
***/
package creepercow;

import java.util.List;
import java.util.ArrayList;
import net.canarymod.Canary;
import net.canarymod.api.entity.EntityType;
import net.canarymod.api.world.position.Location;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.entity.living.animal.Cow;
import net.canarymod.api.world.position.Vector3D;
import net.canarymod.api.world.blocks.Block;
import net.canarymod.api.world.blocks.BlockType;
import net.canarymod.api.ai.AIBase;
import com.pragprog.ahmine.ez.EZPlugin;


public class CreeperCowTimer implements AIBase {
  private Cow cow;
  private CreeperCow plugin;
  private boolean running;
  
  CreeperCowTimer(CreeperCow parentPlugin, Cow aCow) {
    cow = aCow;
    plugin = parentPlugin;
    running = true;
  }
  
  public Player getClosestPlayer(Location loc) { //return -1 on failure
    List<Player> list = Canary.getServer().getPlayerList();
    Player closestPlayer = null;
    double minDistance = -1;
    for(int i = 0; i < list.size(); i++) {
      Player p = list.get(i);
      Location ploc = p.getLocation();
      if (Math.abs(ploc.getY() - loc.getY()) < 15) {
        double dist = distance(loc, ploc);
        if (dist < minDistance || minDistance == -1) {
          minDistance = dist;
          closestPlayer = p;
        }
      }
    }
    return closestPlayer;
  }
  //
  // Find the distance on the ground (ignores height)
  // between two Locations
  //
  public double distance(Location loc1, Location loc2) {
    return Math.sqrt(
      Math.pow(loc1.getX() - loc2.getX(), 2) +
      Math.pow(loc1.getZ() - loc2.getZ(), 2)
    );
  }
  
  // Explode yourself
  public void explode() {
    plugin.cowDied(cow); // notify parent
    Location cowLoc = cow.getLocation();
    cow.getWorld().makeExplosion(cow, 
          cowLoc.getX(), cowLoc.getY(), cowLoc.getZ(), 
          3.0f, true);
            
    cow.kill();
    running = false; // this task
  }
    
  // Jump this cow toward the target
  public void jump(Location target) {
    Location cowLoc = cow.getLocation();
    double multFactor = 0.1;
    Vector3D v = new Vector3D(
      (target.getX() - cowLoc.getX()) * multFactor,
      1.0,
      (target.getZ() - cowLoc.getZ()) * multFactor
    );
    cow.moveEntity(v.getX() + (Math.random() * -0.5), 
      v.getY(), 
      v.getZ() + (Math.random() * -0.5)); 
  }  
  
   // Checks if you are ready to begin executing.
  public boolean shouldExecute() {
    return running;
  }
  
  // Checks if you are Continuous, or One-Shot? 
  public boolean isContinuous() {
    return true;
  }
  
  // Checks if you should continue executing (for continuous tasks)
  public boolean continueExecuting() {
    return running;
  }
   
  // Callback to begin executing 
  public void startExecuting() {
  }
  
  // Callback on termination
  public void resetTask() {
    running = false;
    cow.kill();
    cow = null;
    plugin = null;
  }
  
  // Callback to run and execute body of task
  public void updateTask() {
    // In 1.7.10+, you can use cow.isOnGround() instead
    if (EZPlugin.isOnGround(cow)) { // otherwise it's still jumping
      Location cowLoc = cow.getLocation();
      Player p = cow.getWorld().getClosestPlayer(cow, 10000);
      if (p == null) {
        return;
      }
      Location pLoc = p.getLocation();
      double dist = distance(cowLoc, pLoc);
    
      if (dist <= 4) {
        explode();
      } else if (dist <= 200) {
        jump(pLoc);
      }
    }
  }
        
}

