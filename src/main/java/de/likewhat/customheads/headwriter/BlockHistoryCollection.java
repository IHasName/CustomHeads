package de.likewhat.customheads.headwriter;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.util.BlockVector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BlockHistoryCollection {

    private final ConcurrentHashMap<BlockVector, MaterialData> locations;
    private World world;
    private boolean finished = false;

    public BlockHistoryCollection() {
        locations = new ConcurrentHashMap<>();
    }

    /**
     * Adds a Block to the History
     * @param block The Blocks to add to the History
     * @throws java.lang.IllegalStateException when Block Collection is closed with finish()
     * @throws java.lang.IllegalArgumentException when the Block's World doesn't match the first Block's World
     */
    public void addBlock(Block block) {
        checkFinished();
        if(world == null) {
            world = block.getWorld();
        } else {
            if(block.getWorld() != world) {
                throw new IllegalArgumentException("Can't add Block that's not in the same World as the first (expected " + world.getName() + " but got " + block.getWorld().getName() + ")");
            }
        }
        locations.put(new BlockVector(block.getX(), block.getY(), block.getZ()), block.getState().getData());
    }

    /**
     * Rolls back the Blocks contained in the History
     */
    public void rollbackBlocks() {
        for (Map.Entry<BlockVector, MaterialData> entry : locations.entrySet()) {
            BlockVector vector = entry.getKey();
            MaterialData materialData = entry.getValue();
            Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
            if (materialData.getItemType() == Material.AIR) {
                block.setType(Material.AIR);
                continue; // Won't need to update the Block any further
            }
            if (block.getType() != materialData.getItemType()) {
                if(materialData.getItemType() == Material.BED) {
                    block.setType(Material.AIR);
                    continue; // Beds got changed in later Versions so it won't update correctly
                }
                block.setType(materialData.getItemType(), false);
            }
            BlockState state = block.getState();
            state.setData(materialData);
            if(!checkMultiBlocks(materialData, block)) {
                state.update();
            }
        }
        locations.clear();
    }

    // TODO: Add other Material Data
    private boolean checkMultiBlocks(MaterialData materialData, Block sourceBlock) {
        if(materialData instanceof Door) {
            Door door = (Door) materialData;
            Block relativeBlock = sourceBlock.getRelative(door.isTopHalf() ? BlockFace.DOWN : BlockFace.UP);
            if(relativeBlock.getType() == Material.AIR) {
                relativeBlock.setType(materialData.getItemType(), false);
                BlockState otherDoorState = relativeBlock.getState();
                door.setTopHalf(!door.isTopHalf());
                System.out.println(door.isTopHalf());
                otherDoorState.setData(door);
                return true;
            }
        }
        return false;
    }

    /**
     * Close the Block Collection to prevent any new additions to the History
     */
    public void finish() {
        checkFinished();
        finished = true;
    }

    private void checkFinished() {
        if(finished) {
            throw new IllegalStateException("Block History already finished");
        }
    }
}
